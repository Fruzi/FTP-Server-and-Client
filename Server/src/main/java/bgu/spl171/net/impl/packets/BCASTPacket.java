package bgu.spl171.net.impl.packets;

/**
 * Broadcast Packet
 */
public class BCASTPacket extends Packet {

    private byte delOrAdd;
    private String fileName;

    /**
     * Constructor
     * @param delOrAdd Indicates if the file has been deleted or added
     * @param fileName The name of the file that has been removed or added to the server
     */
    public BCASTPacket(byte delOrAdd, String fileName) {
        opCode = BCAST;
        this.delOrAdd = delOrAdd;
        this.fileName = fileName;
    }

    /**
     * Getter for the deleted/added indicator
     * @return the deleted/added indicator
     */
    public byte getDelOrAdd() {
        return delOrAdd;
    }

    /**
     * Getter for the file name
     * @return the file name
     */
    public String getFileName() {
        return fileName;
    }
}
