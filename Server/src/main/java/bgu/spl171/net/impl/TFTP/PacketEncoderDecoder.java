package bgu.spl171.net.impl.TFTP;

import bgu.spl171.net.api.MessageEncoderDecoder;
import bgu.spl171.net.impl.packets.*;

/**
 * Implementation of the MessageEncoderDecoder used to encode and decode data transmitted through TCP/IP communication.
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
                return decodeDATAPacket(nextByte);
            if (opCode == Packet.ACK)
                return decodeACKPack(nextByte);
            if (opCode == Packet.ERROR)
                return decodeERRORPacket(nextByte);
            if (opCode == Packet.BCAST)
                return decodeBCASTPacket(nextByte);
        }
        return null;
    }

    @Override
    public byte[] encode(Packet message) {
        if (message == null)
            return null;
        byte[] res = null;
        byte[] opCodeBytes = encodeOpCode(message);
        if (opCode == Packet.DIRQ || opCode == Packet.DISC) {
            res = opCodeBytes;
        } else if (isStringMessagePacket()) {
            res = encodeMessagePacket(message, opCodeBytes);
        } else if (opCode == Packet.DATA) {
            res = encodeDATAPacket(message, opCodeBytes);
        } else if (opCode == Packet.ACK) {
            res = encodeACKPack(message, opCodeBytes);
        } else if (opCode == Packet.ERROR) {
            res = encodeERRORPacket(message, opCodeBytes);
        } else if (opCode == Packet.BCAST) {
            res = encodeBCASTPacket(message, opCodeBytes);
        }
        resetFields();
        return res;
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

    private DATAPacket decodeDATAPacket(byte nextByte) {
        if (dataPacketSize == -1) {
            dataPacketSize = decodeShortPacketSegment(nextByte, dataPacketSize);
        } else {
            if (dataBytes == null) {
                dataBytes = new byte[dataPacketSize];
                dataBytesIndex = 0;
            }
            if (blockNumber == -1) {
                blockNumber = decodeShortPacketSegment(nextByte, blockNumber);
                if (blockNumber != -1 && dataPacketSize == 0) {
                    DATAPacket packet = new DATAPacket(dataPacketSize, blockNumber, dataBytes);
                    resetFields();
                    dataBytes = null;
                    return packet;
                }
            } else {
                dataBytes[dataBytesIndex++] = nextByte;
                if (dataBytesIndex == dataPacketSize) {
                    DATAPacket packet = new DATAPacket(dataPacketSize, blockNumber, dataBytes);
                    resetFields();
                    dataBytes = null;
                    return packet;
                }
            }
        }
        return null;
    }

    private ACKPack decodeACKPack(byte nextByte) {
        blockNumber = decodeShortPacketSegment(nextByte, blockNumber);
        if (blockNumber != -1) {
            ACKPack result = new ACKPack(blockNumber);
            resetFields();
            return result;
        }
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

    private String getStrData(Packet message) {
        switch (message.getOpCode()) {
            case Packet.RRQ:
                return ((RRQPacket)message).getFileName();
            case Packet.WRQ:
                return ((WRQPacket)message).getFileName();
            case Packet.LOGRQ:
                return ((LOGRQPacket)message).getUserName();
            case Packet.DELRQ:
                return ((DELRQPacket)message).getFileName();
            case Packet.ERROR:
                return ((ERRORPacket)message).getErrMsg();
            case Packet.BCAST:
                return ((BCASTPacket)message).getFileName();
        }
        return null;
    }

    private byte[] encodeOpCode(Packet message) {
        opCode = message.getOpCode();
        return shortToBytes(opCode);
    }

    private byte[] encodeStrData(Packet message) {
        String strData = getStrData(message);
        if (messageEncDec == null)
            messageEncDec = new LineMessageEncoderDecoder();
        return messageEncDec.encode(strData);
    }

    private byte[] encodePacketSize(Packet message) {
        dataPacketSize = ((DATAPacket)message).getPacketSize();
        return shortToBytes(dataPacketSize);
    }

    private byte[] encodeBlockNumber(Packet message) {
        blockNumber = opCode == Packet.DATA ? ((DATAPacket)message).getBlockNum()
                : ((ACKPack)message).getBlockNum();
        return shortToBytes(blockNumber);
    }

    private byte[] encodeErrorCode(Packet message) {
        errorCode = ((ERRORPacket)message).getErrorCode();
        return shortToBytes(errorCode);
    }

    private byte[] encodeMessagePacket(Packet message, byte[] opCodeBytes) {
        byte[] strDataBytes = encodeStrData(message);
        byte[] res = new byte[strDataBytes.length + 2];
        System.arraycopy(opCodeBytes, 0, res, 0, opCodeBytes.length);
        System.arraycopy(strDataBytes, 0, res, 2, strDataBytes.length);
        return res;
    }

    private byte[] encodeDATAPacket(Packet message, byte[] opCodeBytes) {
        byte[] packetSizeBytes = encodePacketSize(message);
        byte[] blockNumberBytes = encodeBlockNumber(message);
        dataBytes = ((DATAPacket)message).getData();
        byte[] res = new byte[dataBytes.length + 6];
        System.arraycopy(opCodeBytes, 0, res, 0, opCodeBytes.length);
        System.arraycopy(packetSizeBytes, 0, res, 2, packetSizeBytes.length);
        System.arraycopy(blockNumberBytes, 0, res, 4, blockNumberBytes.length);
        System.arraycopy(dataBytes, 0, res, 6, dataBytes.length);
        return res;
    }

    private byte[] encodeACKPack(Packet message, byte[] opCodeBytes) {
        byte[] res = new byte[4];
        byte[] blockNumberBytes = encodeBlockNumber(message);
        System.arraycopy(opCodeBytes, 0, res, 0, opCodeBytes.length);
        System.arraycopy(blockNumberBytes, 0, res, 2, blockNumberBytes.length);
        return res;
    }
    private byte[] encodeERRORPacket(Packet message, byte[] opCodeBytes) {
        byte[] errorCodeBytes = encodeErrorCode(message);
        byte[] strDataBytes = encodeStrData(message);
        byte[] res = new byte[strDataBytes.length + 4];
        System.arraycopy(opCodeBytes, 0, res, 0, opCodeBytes.length);
        System.arraycopy(errorCodeBytes, 0, res, 2, errorCodeBytes.length);
        System.arraycopy(strDataBytes, 0, res, 4, strDataBytes.length);
        return res;
    }

    private byte[] encodeBCASTPacket(Packet message, byte[] opCodeBytes) {
        byte[] strDataBytes = encodeStrData(message);
        byte[] res = new byte[strDataBytes.length + 3];
        System.arraycopy(opCodeBytes, 0, res, 0, opCodeBytes.length);
        res[2] = ((BCASTPacket)message).getDelOrAdd();
        System.arraycopy(strDataBytes, 0, res, 3, strDataBytes.length);
        return res;
    }

    private void resetFields() {
        opCode = -1;
        dataPacketSize = -1;
        blockNumber = -1;
        errorCode = -1;
        deletedAdded = -1;
        dataBytes = null;
        shortByteArr = null;
        shortByteArrIndex = 0;
        dataBytesIndex = 0;
    }

    private short bytesToShort(byte[] byteArr) {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }

    private byte[] shortToBytes(short num) {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }


}
