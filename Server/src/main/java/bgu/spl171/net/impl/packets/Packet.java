package bgu.spl171.net.impl.packets;

/**
 * An abstract package used to transmit data in this TFTP communication
 */
public abstract class Packet {

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

    /**
     * Getter for the packet's opcode
     * @return this packet's opcode
     */
    public short getOpCode(){
        return opCode;
    }
}
