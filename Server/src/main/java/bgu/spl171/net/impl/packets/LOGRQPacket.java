package bgu.spl171.net.impl.packets;

/**
 * Created by Uzi the magnanimous, breaker of code and leader of IDES. He who has tamed the java beast and crossed the narrow C(++).
 on this, 1/12/2017 the day of reckoning.
 */
public class LOGRQPacket extends Packet {
    private String userName;
    private byte delimiter = '\0';

    public LOGRQPacket(String userName) {
        opCode=7;
        this.userName = userName;
    }
}
