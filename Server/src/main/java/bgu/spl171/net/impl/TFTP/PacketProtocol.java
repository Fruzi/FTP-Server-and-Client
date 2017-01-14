package bgu.spl171.net.impl.TFTP;

import bgu.spl171.net.api.bidi.BidiMessagingProtocol;
import bgu.spl171.net.api.bidi.Connections;
import bgu.spl171.net.impl.packets.*;
import bgu.spl171.net.srv.bidi.ConnectionHandler;

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
    private File fileBeingSent;
    private boolean sendingData=false;
    private int index=0;
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
            case 1:
                String fileName=((RRQPacket) msg).getFileName();
                File reqFile = new File("./Files/" + fileName);
                fileBeingSent=reqFile;
                sendingData=true;
                processRRQ(reqFile);
                break;
            case 2:
                processWRQ((WRQPacket)msg);
                break;
            case 3:
                processDATA((DATAPacket)msg);
                break;
            case 4:
                processACK((ACKPack)msg);
                break;
            case 5:
                processERROR((ERRORPacket)msg);
                break;
            case 6:
                processDIRQ((DIRQPacket)msg);
                break;
            case 7:
                processLOGRQ((LOGRQPacket)msg);
                break;
            case 8:
                processDELRQ((DELRQPacket)msg);
                break;
            case 9:
                processBCAST((BCASTPacket)msg);
                break;
            case 10:
                processDISC((DISCPacket)msg);
                break;
        }
    }

    private void processRRQ(File reqFile){
        if(!reqFile.exists()){
            //@TODO error message
            return;
        }

        long length = reqFile.length();

        try {
            if (index<length) {
                InputStream in = new FileInputStream(reqFile);
                byte[] data = new byte[512];

                int size = in.read(data, index, index + 512);
                DATAPacket dataPacket = new DATAPacket((short) size, blockNum, data);
                connections.send(ownerID, dataPacket);

                if (index>=length){
                    index=0;
                    blockNum=1;
                    sendingData=false;
                    fileBeingSent=null;
                }
                else {
                    index += 512;
                    blockNum++;
                }
                in.close();
            }
        }
         catch (IOException e){
             e.printStackTrace();
         }
    }

    private void processWRQ(WRQPacket msg){
        String fileName=msg.getFileName();

    }

    private void processDATA(DATAPacket msg){
        int packetSize=msg.getPacketSize();
        int blockNum=msg.getBlockNum();
        byte[] data=msg.getData();

    }

    private void processACK(ACKPack msg){
        lastAck=blockNum=msg.getBlockNum();
        if (sendingData && lastAck==this.blockNum-1){
            processRRQ(fileBeingSent);
        }
    }

    private void processERROR(ERRORPacket msg){
        int errorCode=msg.getErrorCode();
        String errorMsg=msg.getErrMsg();
    }

    private void processDIRQ(DIRQPacket msg){

    }

    private void processLOGRQ(LOGRQPacket msg){
        String userName = msg.getUserName();
        if (loggedUsers==null){
            loggedUsers=new HashMap<>();
        }
        for (String loggedUser: loggedUsers.values()) {
            if (loggedUser.equals(userName)){
                //@TODO create error
                return;
            }
        }
        loggedUsers.put(ownerID,userName);
    }

    private void processDELRQ(DELRQPacket msg){
        String fileName = msg.getFileName();
        try{

            File file = new File("./Files/" + fileName);

            if(file.delete()){
                ACKPack ackPack = new ACKPack((short)0);
                connections.send(ownerID, ackPack);
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

    private void processBCAST(BCASTPacket msg){
        byte delOrAdd=msg.getDelOrAdd();
        String fileName=msg.getFileName();
    }

    private void processDISC(DISCPacket msg) {

    }


    @Override
    public boolean shouldTerminate() {
        return terminateMe;
    }
}
