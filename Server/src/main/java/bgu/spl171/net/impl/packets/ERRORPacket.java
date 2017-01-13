package bgu.spl171.net.impl.packets;

/**
 * Created by Uzi the magnanimous, breaker of code and leader of IDES. He who has tamed the java beast and crossed the narrow C(++).
 on this, 1/12/2017 the day of reckoning.
 */
public class ERRORPacket extends Packet {
    private short errorCode;
    private String errMsg;
    private byte delimiter = '\0';

    public ERRORPacket(short errorCode, String errMsg) {
        opCode=5;
        this.errorCode = errorCode;
        this.errMsg = errMsg;
    }

    public short getErrorCode() {
        return errorCode;
    }

    public String getErrMsg() {
        return errMsg;
    }
}
