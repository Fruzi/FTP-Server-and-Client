package bgu.spl171.net.impl.TFTP;

import bgu.spl171.net.api.bidi.BidiMessagingProtocol;
import bgu.spl171.net.api.bidi.Connections;
import bgu.spl171.net.impl.packets.*;

/**
 * Created by Uzi the magnanimous, breaker of code and loader of IDEs. He who has tamed the java beast and crossed the narrow C(++).
 * on this, 1/12/2017 the day of reckoning.
 */
public class PacketProtocol implements BidiMessagingProtocol<Packet> {
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
    //TODO create a T msg and end func with send/broadcast for handler
        short opCode = msg.getOpCode();
        switch(opCode){
            case 1:
                processRRQ((RRQPacket)msg);
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

    private void processRRQ(RRQPacket msg){
        String fileName=msg.getFileName();
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
        int blockNum=msg.getBlockNum();
    }

    private void processERROR(ERRORPacket msg){
        int errorCode=msg.getErrorCode();
        String errorMsg=msg.getErrMsg();
    }

    private void processDIRQ(DIRQPacket msg){

    }

    private void processLOGRQ(LOGRQPacket msg){

    }

    private void processDELRQ(DELRQPacket msg){

    }

    private void processBCAST(BCASTPacket msg){
        byte delOrAdd=msg.getDelOrAdd();
        String fileName=msg.getFileName();
    }

    private void processDISC(DISCPacket msg){

    }

    @Override
    public boolean shouldTerminate() {
        return terminateMe;
    }
}
