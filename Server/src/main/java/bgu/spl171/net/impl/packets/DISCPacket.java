package bgu.spl171.net.impl.packets;

/**
 * Disconnect Packet
 */
public class DISCPacket extends Packet {

    /**
     * Constructor
     */
    public DISCPacket() {
        opCode = DISC;
    }
}
