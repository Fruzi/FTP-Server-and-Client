#ifndef PACKETENCODERDECODER
#define PACKETENCODERDECODER
#include "Packet.h"
#include <string>
#include <vector>
#include <list>

class PacketEncoderDecoder {
private:
	short opCode_;
	short packetSize_;
	short blockNum_;
	short errorCode_;
	std::vector<char> data_;
	char delOrAdd_;
	std::vector<char> shortByteArr_;
	std::string message;

	void resetFields();

	static short bytesToShort(std::vector<char> bytesArr);
	static std::vector<char> shortToBytes(short num);
	
	void encodeRRQPacket(Packet* packet, std::vector<char>& result) const;
	void encodeWRQPacket(Packet* packet, std::vector<char>& result) const;
	void encodeDATAPacket(Packet* packet, std::vector<char>& result);
	void encodeACKPacket(Packet* packet, std::vector<char>& result);
	void encodeERRORPacket(Packet* packet, std::vector<char>& result);
	void encodeLOGRQPacket(Packet* packet, std::vector<char>& result) const;
	void encodeDELRQPacket(Packet* packet, std::vector<char>& result) const;

	short decodeShortPacketSegment(short nextByte, short value);
	std::string decodeStringMessage(char nextByte);
	DATAPacket* decodeDATAPacket(char nextByte);
	ACKPacket* decodeACKPacket(char nextByte);
	ERRORPacket* decodeERRORPacket(char nextByte);
	BCASTPacket* decodeBCASTPacket(char nextByte);

public:
	PacketEncoderDecoder();
	virtual ~PacketEncoderDecoder();

	std::vector<char> encode(Packet* packet);
	static Packet* decodeFromKeyboardInput(const std::string& input);
	Packet* decodeNextByte(char nextByte);
};

#endif
