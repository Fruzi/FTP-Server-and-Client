package bgu.spl171.net.impl.packets;

/**
 * Created by Uzi the magnanimous, breaker of code and leader of IDES. He who has tamed the java beast and crossed the narrow C(++).
 on this, 1/12/2017 the day of reckoning.
 */
public class Packet {
    public Packet() {
        this.opCode = 5;
    }

    protected short opCode;
    public short getOpCode(){
        return opCode;
    }
}
