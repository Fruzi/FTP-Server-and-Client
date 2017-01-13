package bgu.spl171.net.impl.packets;

/**
 * Created by Uzi the magnanimous, breaker of code and leader of IDES. He who has tamed the java beast and crossed the narrow C(++).
 on this, 1/12/2017 the day of reckoning.
 */
public abstract class Packet <T> {

    //Opcodes
    public static final short RRQ = 1;
    public static final short WRQ = 2;
    public static final short DATA = 3;
    public static final short ACK = 4;
    public static final short ERROR = 5;
    public static final short DIRQ = 6;
    public static final short LOGRQ = 7;
    public static final short DELRQ = 8;
    public static final short BCAST = 9;
    public static final short DISC = 10;

    protected short opCode;

    public short getOpCode(){
        return opCode;
    }
}
