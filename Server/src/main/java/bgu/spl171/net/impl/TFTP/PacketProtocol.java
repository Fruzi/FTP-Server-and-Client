package bgu.spl171.net.impl.TFTP;

import bgu.spl171.net.api.bidi.BidiMessagingProtocol;
import bgu.spl171.net.api.bidi.Connections;
import bgu.spl171.net.impl.packets.*;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//@TODO make sure the error message sent is of the lowest possible value

/**
 * Created by Uzi the magnanimous, breaker of code and loader of IDEs. He who has tamed the java beast and crossed the narrow C(++).
 * on this, 1/12/2017 the day of reckoning.
 */
public class PacketProtocol implements BidiMessagingProtocol<Packet> {
    private static Map<Integer, String> loggedUsers;

    private final File filesLocation = new File("./Files/");
    private final File tempFiles = new File("./Files/Temp/");


    //for sending data to user and waiting for ack of last pack
    private ByteArrayInputStream byteSteam;
    private FileInputStream fileStream;
    private boolean sendingReadData = false;
    private boolean sendingDIRQ = false;
    private File fileBeingReceived;
    private short blockNum=1;
    private short lastAck =0;

    private boolean terminateMe;
    private int ownerID;
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
                processLOGRQ((LOGRQPacket)msg);
                break;

            case Packet.DELRQ:
                processDELRQ((DELRQPacket)msg);
                break;

            case Packet.DISC:
                processDISC();
                break;

            default:
                sendError((short)4, "Illegal OP Code");
        }
    }

    private void processRRQ(String fileName){
        File reqFile = new File(filesLocation + fileName);
        if(!reqFile.exists()){
            sendError((short)1, "File Not Found");
            return;
        }
        try {
            fileStream = new FileInputStream(reqFile);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        sendingReadData =true;
        sendFileData();
    }

    private void sendFileData() {
        if (!isLoggedIn()){
            sendError((short)6, "Not Logged In!!");
            return;
        }
        byte[] data = new byte[512];
        try {
            int size = fileStream.read(data, 0, 512);
            DATAPacket dataPacket = new DATAPacket((short) size, blockNum, data);
            connections.send(ownerID, dataPacket);
            if (size!=512) {
                blockNum = 1;
                sendingReadData = false;
                fileStream.close();
            } else {
                blockNum++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processWRQ(String fileName){
        if (!isLoggedIn()){
            sendError((short)6, "Not Logged In!!");
            return;
        }
        fileBeingReceived =new File(tempFiles + fileName);
        //process if the file was already uploaded
        for (File file : filesLocation.listFiles()){
            if (file.getName().equals(fileName)) {
                fileBeingReceived =null;
                sendError((short)5, "File already exists");
            }
        }
        try{
            if (fileBeingReceived.createNewFile()){
                sendACK((short)0);
            }
            else {
                fileBeingReceived =null;
                sendError((short)5, "File already exists");
            }
        }
        catch (IOException e){
            e.printStackTrace();
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
        }
        catch (IOException e){
            e.printStackTrace();
        }
        sendACK(blockNum);
        if (packetSize!=512){
            broadcast((byte)1, fileBeingReceived.getName());
        }
    }

    private void processACK(ACKPack msg){
        if (!isLoggedIn()){
            sendError((short)6, "Not Logged In!!");
            return;
        }
        lastAck=msg.getBlockNum();
        if (sendingReadData && lastAck==blockNum-1){
            sendFileData();
        }
        if (sendingDIRQ && lastAck==blockNum-1){
            sendDIRQ();
        }
    }

    //only errors we can recieve from clients are related to him unable to receive data
    private void processERROR(){
        sendingReadData=false;
        sendingDIRQ=false;
    }


    private void processDIRQ(){
        if (!isLoggedIn()){
            sendError((short)6, "Not Logged In!!");
            return;
        }
        File listPath = new File("./Files");
        String[] fileList = listPath.list((file, name) -> file.isFile());
        if (fileList==null){
            //no files in server, no point in opening the byteStream, etc
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
        byte[] data = new byte[512];
        int size = byteSteam.read(data,0,512);
        DATAPacket dataPacket = new DATAPacket((short)size,blockNum,data);
        connections.send(ownerID,dataPacket);
        if (size!=512){
            blockNum=1;
            sendingDIRQ=false;
            try {
                byteSteam.close();
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
        else{
            blockNum++;
        }
    }

    private boolean isLoggedIn(){
        if (loggedUsers==null){
            loggedUsers=new ConcurrentHashMap<>();
            return false;
        }
        for (int userID: loggedUsers.keySet()) {
            if (userID==ownerID) {return true;}
        }
        return false;
    }

    private void processLOGRQ(LOGRQPacket msg){
        if (isLoggedIn()){
            sendError((short)7, "User already logged in");
            return;
        }
        String userName = msg.getUserName();
        loggedUsers.put(ownerID,userName);
        sendACK((short)0);
    }

    private void processDELRQ(DELRQPacket msg){
        if (!isLoggedIn()){
            sendError((short)6, "Not Logged In!!");
            return;
        }
        String fileName = msg.getFileName();
        try{

            File file = new File("./Files/" + fileName);

            if(file.delete()){
                sendACK((short)0);
                broadcast((byte)0,fileName);
            }else{
                sendError((short)1, "File not found");
            }

        }catch(Exception e){

            e.printStackTrace();

        }
    }



    /*
    only users receive these packets
    private void processBCAST(BCASTPacket msg){
        byte delOrAdd=msg.getDelOrAdd();
        String delOrAddPrint;
        if (delOrAdd==0)
            {delOrAddPrint="del";}
        else
            {delOrAddPrint="add";}
        String fileName=msg.getFileName();
        System.out.println("BCAST " + delOrAddPrint + " " + fileName);
    }
    */

    private void processDISC() {
        if (!isLoggedIn()){
            sendError((short)6, "Not Logged In!!");
            return;
        }
        loggedUsers.remove(ownerID);
        sendACK((short)0);
        connections.disconnect(ownerID);
    }

    private void sendError(short errorNum, String errorMsg){
        ERRORPacket errorPacket = new ERRORPacket(errorNum, errorMsg);
        connections.send(ownerID,errorPacket);
    }

    private void broadcast(Byte delOrAdd, String filename){
        BCASTPacket bcastPacket = new BCASTPacket(delOrAdd, filename);
        for (Integer connectionID : loggedUsers.keySet()){
            connections.send(connectionID,bcastPacket);
        }
    }

    private void sendACK(short index){
        ACKPack ackPack = new ACKPack(index);
        connections.send(ownerID,ackPack);
    }


    @Override
    public boolean shouldTerminate() {
        return terminateMe;
    }
}
