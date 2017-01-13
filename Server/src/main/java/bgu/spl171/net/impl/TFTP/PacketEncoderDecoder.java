package bgu.spl171.net.impl.TFTP;

import bgu.spl171.net.api.MessageEncoderDecoder;
import bgu.spl171.net.impl.echo.LineMessageEncoderDecoder;
import bgu.spl171.net.impl.packets.*;

/**
 * Created by Uzi the magnanimous, breaker of code and leader of IDES. He who has tamed the java beast and crossed the narrow C(++).
 * on this, 1/12/2017 the day of reckoning.
 */
public class PacketEncoderDecoder implements MessageEncoderDecoder<Packet> {

    private LineMessageEncoderDecoder messageEncDec;

    private short opCode = -1;
    private byte[] shortByteArr;
    private int shortByteArrIndex;
    private short dataPacketSize = -1;
    private byte[] dataBytes;
    private int dataBytesIndex;
    private short blockNumber = -1;
    private short errorCode = -1;
    private byte deletedAdded = -1;

    @Override
    public Packet decodeNextByte(byte nextByte) {
        if (opCode == -1) {
            opCode = decodeShortPacketSegment(nextByte, opCode);
            if (opCode == Packet.DIRQ) {
                resetFields();
                return new DIRQPacket();
            }
            if (opCode == Packet.DISC) {
                resetFields();
                return new DISCPacket();
            }
        } else {
            if (isStringMessagePacket())
                return decodeMessagePacket(nextByte);
            if (opCode == Packet.DATA)
                return decodeDataPacket(nextByte);
            if (opCode == Packet.ACK)
                return decodeACKPacket(nextByte);
            if (opCode == Packet.ERROR)
                return decodeERRORPacket(nextByte);
            if (opCode == Packet.BCAST)
                return decodeBCASTPacket(nextByte);
        }
        return null;
    }

    private short decodeShortPacketSegment(byte nextByte, short value) {
        if (shortByteArr == null) {
            shortByteArr = new byte[2];
            shortByteArrIndex = 0;
        }
        shortByteArr[shortByteArrIndex++] = nextByte;
        if (shortByteArrIndex == shortByteArr.length) {
            value = bytesToShort(shortByteArr);
            shortByteArrIndex = 0;
        }
        return value;
    }

    private String decodeStringMessage(byte nextByte) {
        if (messageEncDec == null)
            messageEncDec = new LineMessageEncoderDecoder();
        return messageEncDec.decodeNextByte(nextByte);
    }

    private boolean isStringMessagePacket() {
        return (opCode == Packet.RRQ || opCode == Packet.WRQ || opCode == Packet.LOGRQ || opCode == Packet.DELRQ);
    }

    private Packet decodeMessagePacket(byte nextByte) {
        String message = decodeStringMessage(nextByte);
        if (message != null) {
            return newStringMessagePacket(message);
        }
        return null;
    }

    private DATAPacket decodeDataPacket(byte nextByte) {
        if (dataPacketSize == -1) {
            dataPacketSize = decodeShortPacketSegment(nextByte, dataPacketSize);
        } else {
            if (dataBytes == null) {
                dataBytes = new byte[dataPacketSize];
                dataBytesIndex = 0;
            }
            if (blockNumber == -1) {
                blockNumber = decodeShortPacketSegment(nextByte, blockNumber);
            } else {
                dataBytes[dataBytesIndex++] = nextByte;
                if (dataBytesIndex == dataBytes.length) {
                    DATAPacket packet = new DATAPacket(dataPacketSize, blockNumber, dataBytes);
                    resetFields();
                    dataBytes = null;
                    return packet;
                }
            }
        }
        return null;
    }

    private ACKPack decodeACKPacket(byte nextByte) {
        //if (blockNumber == -1) {
            blockNumber = decodeShortPacketSegment(nextByte, blockNumber);
            if (blockNumber != -1) {
                ACKPack result = new ACKPack(blockNumber);
                resetFields();
                return result;
            }
        //}
        return null;
    }

    private BCASTPacket decodeBCASTPacket(byte nextByte) {
        if (deletedAdded == -1) {
            deletedAdded = nextByte;
        } else {
            String message = decodeStringMessage(nextByte);
            if (message != null) {
                BCASTPacket result = new BCASTPacket(deletedAdded, message);
                resetFields();
                return result;
            }
        }
        return null;
    }

    private ERRORPacket decodeERRORPacket(byte nextByte) {
        if (errorCode == -1) {
            errorCode = decodeShortPacketSegment(nextByte, errorCode);
        } else {
            String message = decodeStringMessage(nextByte);
            if (message != null) {
                ERRORPacket result = new ERRORPacket(errorCode, message);
                resetFields();
                return result;
            }
        }
        return null;
    }

    private Packet newStringMessagePacket(String message) {
        short tempOpCode = opCode;
        resetFields();
        switch (tempOpCode) {
            case Packet.RRQ:
                return new RRQPacket(message);
            case Packet.WRQ:
                return new WRQPacket(message);
            case Packet.LOGRQ:
                return new LOGRQPacket(message);
            case Packet.DELRQ:
                return new DELRQPacket(message);
        }
        return null;
    }

    private void resetFields() {
        opCode = -1;
        dataPacketSize = -1;
        blockNumber = -1;
        errorCode = -1;
        deletedAdded = -1;
    }

    private short bytesToShort(byte[] byteArr)
    {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }

    private byte[] shortToBytes(short num)
    {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }

    @Override
    public byte[] encode(Packet message) {
        //@TODO
        return new byte[0];
    }
}
