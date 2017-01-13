package bgu.spl171.net.impl.TFTP;

import bgu.spl171.net.api.MessageEncoderDecoder;
import bgu.spl171.net.impl.packets.Packet;

/**
 * Created by Uzi the magnanimous, breaker of code and leader of IDES. He who has tamed the java beast and crossed the narrow C(++).
 * on this, 1/12/2017 the day of reckoning.
 */
public class PacketEncoderDecoder implements MessageEncoderDecoder<Packet> {


    @Override
    public Packet decodeNextByte(byte nextByte) {
        return null;
    }

    @Override
    public byte[] encode(Packet message) {
        return new byte[0];
    }
}
