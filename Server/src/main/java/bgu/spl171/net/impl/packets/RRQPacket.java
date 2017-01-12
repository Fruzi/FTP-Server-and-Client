package bgu.spl171.net.impl.packets;

/**
 * Created by Uzi the magnanimous, breaker of code and leader of IDES. He who has tamed the java beast and crossed the narrow C(++).
 on this, 1/12/2017 the day of reckoning.
 */
public class RRQPacket extends Packet {
    private String fileName;
    private byte end = '\0';

    public RRQPacket(String fileName) {
        opCode=1;
        this.fileName = fileName;
    }
}
