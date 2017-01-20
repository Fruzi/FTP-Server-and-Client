package bgu.spl171.net.impl.packets;

/**
 * Data Packet
 */
public class DATAPacket extends Packet {

    private short packetSize;
    private short blockNum;
    private byte[] data;

    /**
     * Constructor
     * @param packetSize This packet's data array size
     * @param blockNum This data packet's number in the current data transmission
     * @param data The actual data
     */
    public DATAPacket(short packetSize, short blockNum, byte[] data) {
        opCode = DATA;
        this.packetSize = packetSize;
        this.blockNum = blockNum;
        this.data = data;
    }

    /**
     * Getter for the packet size
     * @return the size of the data array
     */
    public short getPacketSize() {
        return packetSize;
    }

    /**
     * Getter for this packet's number
     * @return this packet's block number in the current data transmission
     */
    public short getBlockNum() {
        return blockNum;
    }

    /**
     * Getter for this packet's actual data
     * @return the packet's byte array
     */
    public byte[] getData() {
        return data;
    }
}
