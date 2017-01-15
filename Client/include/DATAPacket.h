#ifndef DATAPACKET
#define DATAPACKET

#include "Packet.h"

class DATAPacket : public Packet {
private:
	short packetSize_;
	short blockNum_;
	char* data_;
public:
	DATAPacket(short packetSize, short blockNum, char* data);
	short getPacketSize() const;
	short getBlockNum() const;
	char* getData() const;

	static const int MAX_DATA_SIZE = 512;
};

#endif