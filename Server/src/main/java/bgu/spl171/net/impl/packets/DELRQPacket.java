package bgu.spl171.net.impl.packets;

/**
 * Delete Request Packet
 */
public class DELRQPacket extends Packet {

    private String fileName;

    /**
     * Constructor
     * @param fileName The name of the file to be deleted by the server
     */
    public DELRQPacket(String fileName) {
        opCode = DELRQ;
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
