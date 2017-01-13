package bgu.spl171.net.impl.packets;

/**
 * Created by Uzi the magnanimous, breaker of code and leader of IDES. He who has tamed the java beast and crossed the narrow C(++).
 on this, 1/12/2017 the day of reckoning.
 */
public class BCASTPacket extends Packet {
    private byte delOrAdd;
    private String fileName;
    private byte delimiter = '\0';

    public BCASTPacket(byte delOrAdd, String fileName) {
        opCode=9;
        this.delOrAdd = delOrAdd;
        this.fileName = fileName;
    }

    public byte getDelOrAdd() {
        return delOrAdd;
    }

    public String getFileName() {
        return fileName;
    }
}
