package bgu.spl171.net.impl.packets;

/**
 * Created by Uzi the magnanimous, breaker of code and leader of IDES. He who has tamed the java beast and crossed the narrow C(++).
 on this, 1/12/2017 the day of reckoning.
 */
public class dataPacket extends Packet {
    private short packetSize;
    private short blockNum;
    public static final int MAX_DATA_SIZE = 512;
    private byte[] data;

    public dataPacket(short packetSize, short blockNum, byte[] data) {
        opCode = 3;
        this.packetSize = packetSize;
        this.blockNum = blockNum;
        data = new byte[packetSize];
        //might want to change to shallow copy
        System.arraycopy(data,0,this.data,0,packetSize);
    }
}
