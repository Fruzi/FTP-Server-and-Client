package bgu.spl171.net.impl.TFTP;

import bgu.spl171.net.api.bidi.BidiMessagingProtocol;
import bgu.spl171.net.api.bidi.Connections;
import bgu.spl171.net.impl.packets.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;

//@TODO make sure the error message sent is of the lowest possible value

/**
 * Created by Uzi the magnanimous, breaker of code and loader of IDEs. He who has tamed the java beast and crossed the narrow C(++).
 * on this, 1/12/2017 the day of reckoning.
 */

public class PacketProtocol implements BidiMessagingProtocol<Packet> {
    private static ConcurrentMap<String, Integer> loggedUsers;
    private final static Object locKey = new Object();


    private final File filesLocation = new File ("Files");
    private final File tempFiles = new File ("Files/Temp");
    private final short MAX_DATA_SIZE=512;


    private FileInputStream fileInputStream;
    private ByteArrayInputStream byteSteam;
    private File fileBeingReceived = null;
    private boolean sendingFileData = false;
    private boolean sendingDIRQ = false;
    private short blockNum=1;
    private short lastAck =0;
    private boolean finishedSendingPacks=false;

    private boolean terminateMe;
    private int ownerID;
    private String userName;
    private Connections<Packet> connections;

    public PacketProtocol() {
        terminateMe = false;
    }

    @Override
    public void start(int connectionId, Connections<Packet> connections) {
        ownerID = connectionId;
        this.connections = connections;
    }

    @Override
    public void process(Packet msg) {
        short opCode = msg.getOpCode();
        switch(opCode){
            case Packet.RRQ:
                processRRQ(((RRQPacket)msg).getFileName());
                break;

            case Packet.WRQ:
                processWRQ(((WRQPacket)msg).getFileName());
                break;

            case Packet.DATA:
                processDATA((DATAPacket)msg);
                break;

            case Packet.ACK:
                processACK((ACKPack)msg);
                break;

            case Packet.ERROR:
                processERROR();
                break;

            case Packet.DIRQ:
                processDIRQ();
                break;

            case Packet.LOGRQ:
                processLOGRQ(((LOGRQPacket)msg).getUserName());
                break;

            case Packet.DELRQ:
                processDELRQ(((DELRQPacket)msg).getFileName());
                break;

            case Packet.DISC:
                processDISC();
                break;

            default:
                sendError((short)4, "Illegal OP Code");
                break;
        }
    }

    private void processRRQ(String fileName){
        if (!isLoggedIn()){
            sendError((short)6, "Not Logged In!!");
            return;
        }
        File reqFile = new File(filesLocation + "/" + fileName);
        if(!reqFile.exists()) {
            sendError((short) 1, "File Not Found");
            return;
        }
        try {
            fileInputStream = new FileInputStream(reqFile);
        }
        catch(IOException e){
            e.printStackTrace();
        }
        sendingFileData =true;
        sendFileData();
    }

    private void sendFileData() {
        if (!isLoggedIn()){
            sendError((short)6, "Not Logged In!!");
            return;
        }
        try {
            DATAPacket dataPacket = createDataPacket(false);
            connections.send(ownerID, dataPacket);
            if (dataPacket.getPacketSize()!=MAX_DATA_SIZE) {
                finishedSendingPacks=true;
                sendingFileData = false;
                fileInputStream.close();
                fileInputStream=null;
            }
            blockNum++;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processWRQ(String fileName){
        if (!isLoggedIn()){
            sendError((short)6, "Not Logged In!!");
            return;
        }
        fileBeingReceived =new File(tempFiles +"/"+ fileName);
        //process if the file was already
        synchronized (locKey) {
            File[] listOfFiles = filesLocation.listFiles();
            if (listOfFiles != null) {
                for (File file : listOfFiles) {
                    if (file.getName().equals(fileName)) {
                        fileBeingReceived = null;
                        sendError((short) 5, "File already exists");
                        return;
                    }
                }
            }
            try {
                if (fileBeingReceived.createNewFile()) {
                    sendACK((short) 0);
                } else {
                    fileBeingReceived = null;
                    sendError((short) 5, "File already exists");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void processDATA(DATAPacket msg){
        if (!isLoggedIn()){
            sendError((short)6, "Not Logged In!!");
            return;
        }
        if (fileBeingReceived ==null){
            sendError((short)0, "Didn't receive a WRQ");
            return;
        }
        int packetSize=msg.getPacketSize();
        short blockNum=msg.getBlockNum();
        byte[] data=msg.getData();
        try{
            FileOutputStream output = new FileOutputStream(fileBeingReceived, true);
            output.write(data);
            output.close();
            sendACK(blockNum);
            if (packetSize!=MAX_DATA_SIZE){
                Path source = fileBeingReceived.toPath().toAbsolutePath();
                Path destination = (new File(filesLocation +"/" + fileBeingReceived.getName())).toPath().toAbsolutePath();
                Files.move(source,destination,ATOMIC_MOVE);
                broadcast((byte)1, fileBeingReceived.getName());
                fileBeingReceived=null;
            }
        }
        catch (IOException e){
            e.printStackTrace();
            sendError((short)3, "Insufficient memory in server");
        }
    }

    private void processACK(ACKPack msg){
        if (!isLoggedIn()){
            sendError((short)6, "Not Logged In!!");
            return;
        }
        lastAck=msg.getBlockNum();
        if (lastAck!=blockNum-1){
            sendError((short)0,"Incorrect block number");
            return;
        }
        if (finishedSendingPacks){
            blockNum=1;
            finishedSendingPacks=false;
        }
        else if (sendingFileData){
            sendFileData();
        }
        else if (sendingDIRQ){
            sendDIRQ();
        }
        else {
            sendError((short)0, "Why you ackin' so cray-cray?");
        }
    }

    private void processERROR(){
        sendingFileData =false;
        sendingDIRQ=false;
        if (fileBeingReceived!=null){
            fileBeingReceived.delete();
            fileBeingReceived=null;
        }
        try {
            fileInputStream.close();
            byteSteam.close();
            byteSteam=null;
            fileInputStream=null;
            blockNum=1;
            lastAck=0;

        }
        catch (IOException e){
            e.printStackTrace();
        }
    }


    private void processDIRQ(){
        if (!isLoggedIn()){
            sendError((short)6, "Not Logged In!!");
            return;
        }
        String[] fileList = filesLocation.list((file, name) -> (new File(file+ "/" + name)).isFile());
        if (fileList==null || fileList.length==0){
            DATAPacket dataPacket = new DATAPacket((short)0,(short)1,null);
            connections.send(ownerID,dataPacket);
            return;
        }
        String bigStringOfNames = "";
        for (String fileName : fileList){
            bigStringOfNames +=(fileName+'\0');
        }
        byte[] stringOfNamesInBytes = bigStringOfNames.getBytes();
        byteSteam = new ByteArrayInputStream(stringOfNamesInBytes);
        sendingDIRQ=true;
        sendDIRQ();
    }

    private void sendDIRQ(){
        if (!isLoggedIn()){
            sendError((short)6, "Not Logged In!!");
            return;
        }
        DATAPacket dataPacket=createDataPacket(true);
        connections.send(ownerID,dataPacket);
        if (dataPacket.getPacketSize()!=MAX_DATA_SIZE){
            finishedSendingPacks=true;
            sendingDIRQ=false;
            try {
                byteSteam.close();
                byteSteam=null;
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
        blockNum++;
    }


    private void processLOGRQ(String userName){
        if (isLoggedIn()){
            sendError((short)7, "User already logged in");
            return;
        }
        if (userName==null){
            sendError((short)0, "Must supply a user name, FOOL");
        }
        loggedUsers.putIfAbsent(userName, ownerID);
        this.userName=userName;
        sendACK((short)0);
    }

    private void processDELRQ(String fileName){
        if (!isLoggedIn()){
            sendError((short)6, "Not Logged In!!");
            return;
        }
        File file = new File(filesLocation + "/" + fileName);
        if (!file.exists()){
            sendError((short)1, "File not found – DELRQ of non-existing file");
            return;
        }
        if (file.delete()) {
            sendACK((short) 0);
            broadcast((byte) 0, fileName);
        } else {
            sendError((short) 2, " Access violation – File cannot be written, read or deleted");
        }
    }

    private void processDISC() {
        if (!isLoggedIn()){
            sendError((short)6, "Not Logged In!!");
            return;
        }
        loggedUsers.remove(userName);
        sendACK((short)0);
        terminateMe = true;
    }

    private boolean isLoggedIn(){
        if (loggedUsers==null){
            loggedUsers=new ConcurrentHashMap<>();
            return false;
        }
        for (int userID: loggedUsers.values()) {
            if (userID==ownerID) {return true;}
        }
        return false;
    }

    private void sendError(short errorNum, String errorMsg){
        ERRORPacket errorPacket = new ERRORPacket(errorNum, errorMsg);
        connections.send(ownerID,errorPacket);
    }

    private void broadcast(Byte delOrAdd, String filename){
        BCASTPacket bcastPacket = new BCASTPacket(delOrAdd, filename);
        for (Integer connectionID : loggedUsers.values()){
            connections.send(connectionID,bcastPacket);
        }
    }

    private void sendACK(short index){
        ACKPack ackPack = new ACKPack(index);
        connections.send(ownerID,ackPack);
    }

    private DATAPacket createDataPacket(boolean dirq){
        byte[] data = new byte[MAX_DATA_SIZE];
        int size=0;
        try {
            if (dirq) {
                size = byteSteam.read(data, 0, MAX_DATA_SIZE);
            }
            else {
                size = fileInputStream.read(data,0, MAX_DATA_SIZE);
            }
        } catch (java.lang.Exception exception) {
            exception.printStackTrace();
        }

        if (size!=MAX_DATA_SIZE){
            byte[] littleData = new byte[size];
            System.arraycopy(data,0,littleData,0,size);
            return new DATAPacket((short)size,blockNum,littleData);
        }
        return new DATAPacket((short)size,blockNum,data);
    }


    @Override
    public boolean shouldTerminate() {
        return terminateMe;
    }
}
