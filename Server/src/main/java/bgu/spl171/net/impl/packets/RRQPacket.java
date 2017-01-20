package bgu.spl171.net.impl.packets;

/**
 * Read Request Packet
 */
public class RRQPacket extends Packet {

    private String fileName;

    /**
     * Constructor
     * @param fileName The name of the file to be read and sent to a client
     */
    public RRQPacket(String fileName) {
        opCode = RRQ;
        this.fileName = fileName;
    }

    /**
     * Getter for the file name
     * @return the file name
     */
    public String getFileName() {
        return fileName;
    }
}
