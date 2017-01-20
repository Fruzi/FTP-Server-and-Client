package bgu.spl171.net.impl.packets;

/**
 * Acknowledge Packet
 */
public class ACKPack extends Packet {

    private short blockNum;

    /**
     * Constructor
     * @param blockNum The data block number confirmed by this ACK Packet
     */
    public ACKPack(short blockNum) {
        opCode = ACK;
        this.blockNum = blockNum;
    }

    /**
     * Getter for the packet's block number
     * @return this packet's block number
     */
    public short getBlockNum() {
        return blockNum;
    }
}
