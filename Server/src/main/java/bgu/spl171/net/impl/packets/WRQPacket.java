package bgu.spl171.net.impl.packets;

/**
 * Write Request Packet
 */
public class WRQPacket extends Packet {

    private String fileName;

    /**
     * Constructor
     * @param fileName The name of the file to be written to the server by a client
     */
    public WRQPacket(String fileName) {
        opCode = WRQ;
        this.fileName = fileName;
    }

    /**
     * Getter for the file name
     * @return the file name
     */
    public String getFileName(){
        return fileName;
    }
}
