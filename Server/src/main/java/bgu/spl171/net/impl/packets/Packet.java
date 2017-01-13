package bgu.spl171.net.impl.packets;

import bgu.spl171.net.impl.rci.Command;

/**
 * Created by Uzi the magnanimous, breaker of code and leader of IDES. He who has tamed the java beast and crossed the narrow C(++).
 on this, 1/12/2017 the day of reckoning.
 */
public abstract class Packet <T> {
    protected short opCode;
    public short getOpCode(){
        return opCode;
    }
}
