package bgu.spl171.net.impl.TFTP;

import bgu.spl171.net.api.bidi.BidiMessagingProtocol;
import bgu.spl171.net.api.bidi.Connections;
import bgu.spl171.net.impl.packets.*;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Uzi the magnanimous, breaker of code and loader of IDEs. He who has tamed the java beast and crossed the narrow C(++).
 * on this, 1/12/2017 the day of reckoning.
 */
public class PacketProtocol implements BidiMessagingProtocol<Packet> {
    public static Map<Integer, String> loggedUsers;

    //for sending data to user and waiting for ack of last pack
    private ByteArrayInputStream byteSteam;
    private FileInputStream fileStream;
    private boolean sendingReadData = false;
    private boolean sendingDIRQ = false;
    private boolean sendingWriteData = false;
    private boolean recievingDIRQ = false;
    private boolean recivingWriteData;
    private File generatedFile;
    private short blockNum=1;
    private int lastAck =0;

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
                String fileName=((RRQPacket) msg).getFileName();
                File reqFile = new File("./Files/" + fileName);
                if(!reqFile.exists()){
                    //@TODO error message
                    return;
                }
                try {
                    fileStream = new FileInputStream(reqFile);
                }
                catch (IOException e){
                    e.printStackTrace();
                 }
                sendingReadData =true;
                processRRQ();
                break;

                case Packet.WRQ:
                    processWRQ((WRQPacket)msg);
                    break;

            case Packet.DATA:
                processDATA((DATAPacket)msg);
                break;

            case Packet.ACK:
                processACK((ACKPack)msg);
                break;

            case Packet.ERROR:
                processERROR((ERRORPacket)msg);
                break;

            case Packet.DIRQ:
                File listPath = new File("./Files");
                File[] fileList = listPath.listFiles();
                if (fileList==null){
                    DATAPacket dataPacket = new DATAPacket((short)0,(short)0,null);
                    connections.send(ownerID,dataPacket);
                    return;
                }
                String nameList = "";
                for (File file : fileList){
                    nameList+=(file.getName()+'\0');
                }
                byte[] fileListInBytes=nameList.getBytes();
                byteSteam = new ByteArrayInputStream(fileListInBytes);
                sendingDIRQ=true;
                processDIRQ();
                break;

            case Packet.LOGRQ:
                processLOGRQ((LOGRQPacket)msg);
                break;

            case Packet.DELRQ:
                processDELRQ((DELRQPacket)msg);
                break;

            case Packet.BCAST:
                processBCAST((BCASTPacket)msg);
                break;

            case Packet.DISC:
                processDISC();
                break;
        }
    }

    private void processRRQ() {
        if (!isLoggedIn()){
            //@TODO error message
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

    private void processWRQ(WRQPacket msg){
        if (!isLoggedIn()){
            //@TODO error message
            return;
        }
        generatedFile=new File(msg.getFileName());
        try{
            if (generatedFile.createNewFile()){
                recivingWriteData=true;
                ACKPack ackPack = new ACKPack((short)0);
                connections.send(ownerID,ackPack);
            }
            else {
                generatedFile=null;
                //@TODO error message
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private void processDATA(DATAPacket msg){
        if (!isLoggedIn()){
            //@TODO error message
            return;
        }
        int packetSize=msg.getPacketSize();
        int blockNum=msg.getBlockNum();
        byte[] data=msg.getData();
        if (packetSize!=512){
            //this is the last packet
        }
        //I am either the server receiving data from a WRQ or the client receiving either DIRQ or RRQ
        if (recivingWriteData){
            //the server getting WRQ
        }
        if (recievingDIRQ){
            //client getting Dir info
        }
        else {
            //client receiving RRQ
        }
    }

    private void processACK(ACKPack msg){
        if (!isLoggedIn()){
            //@TODO error message
            return;
        }
        lastAck=msg.getBlockNum();
        //I am the server and received acknowledgment, therefor can send more data
        if (sendingReadData && lastAck==blockNum-1){
            processRRQ();
        }
        if (sendingDIRQ && lastAck==blockNum-1){
            processDIRQ();
        }
        //I am the client and received acknowledgment, therefor can send more data
        if (sendingWriteData && lastAck==blockNum-1){
            //send more data using data packs
        }
        //add something for the client waiting on the ack for dis
    }

    private void processERROR(ERRORPacket msg){
        //clients only recieve those, no need to check if logged in
        int errorCode=msg.getErrorCode();
        String errorMsg=msg.getErrMsg();
        System.out.println("Error " + errorCode);
    }

    private void processDIRQ(){
        if (!isLoggedIn()){
            //@TODO error message
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
            loggedUsers=new HashMap<>();
            return false;
        }
        for (int userID: loggedUsers.keySet()) {
            if (userID==ownerID) {return true;}
        }
        return false;
    }

    private void processLOGRQ(LOGRQPacket msg){
        if (isLoggedIn()){
            //@TODO error message
            return;
        }
        String userName = msg.getUserName();
        loggedUsers.put(ownerID,userName);
        ACKPack ackPack = new ACKPack((short)0);
        connections.send(ownerID,ackPack);
    }

    private void processDELRQ(DELRQPacket msg){
        if (!isLoggedIn()){
            //@TODO error message
            return;
        }
        String fileName = msg.getFileName();
        try{

            File file = new File("./Files/" + fileName);

            if(file.delete()){
                ACKPack ackPack = new ACKPack((short)0);
                connections.send(ownerID, ackPack);
                BCASTPacket bcastPacket = new BCASTPacket((byte)0, fileName);
               broadcast(bcastPacket);
            }else{
                //@TODO actual error handling
                short errorNum = 0; //placeholder
                ERRORPacket errorPacket = new ERRORPacket(errorNum, "Nope"); //placeholder
                connections.send(ownerID, errorPacket);
            }

        }catch(Exception e){

            e.printStackTrace();

        }
    }

    private void broadcast(BCASTPacket msg){
        for (Integer connectionID : loggedUsers.keySet()){
            connections.send(connectionID,msg);
        }
    }

    private void processBCAST(BCASTPacket msg){ //only users receive these packets
        byte delOrAdd=msg.getDelOrAdd();
        String delOrAddPrint;
        if (delOrAdd==0)
            {delOrAddPrint="del";}
        else
            {delOrAddPrint="add";}
        String fileName=msg.getFileName();
        System.out.println("BCAST " + delOrAddPrint + " " + fileName);
    }

    private void processDISC() {
        if (!isLoggedIn()){
            //@TODO error message
            return;
        }
        loggedUsers.remove(ownerID);
        ACKPack ackPack = new ACKPack((short)0);
        connections.send(ownerID,ackPack);
    }


    @Override
    public boolean shouldTerminate() {
        return terminateMe;
    }
}
