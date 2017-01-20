package bgu.spl171.net.impl.packets;

/**
 * Error Packet
 */
public class ERRORPacket extends Packet {

    private short errorCode;
    private String errMsg;

    /**
     * Constructor
     * @param errorCode The error code
     * @param errMsg The error message
     */
    public ERRORPacket(short errorCode, String errMsg) {
        opCode = ERROR;
        this.errorCode = errorCode;
        this.errMsg = errMsg;
    }

    /**
     * Getter for the error code
     * @return the error code
     */
    public short getErrorCode() {
        return errorCode;
    }

    /**
     * Getter for the error message
     * @return the error message
     */
    public String getErrMsg() {
        return errMsg;
    }
}
