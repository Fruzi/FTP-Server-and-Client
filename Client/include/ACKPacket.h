#ifndef ACKPACKET
#define ACKPACKET

#include "Packet.h"

class ACKPacket : public Packet {
private:
	short blockNum_;
public:
	ACKPacket(short blockNum);
	short getBlockNum() const;
};

#endif
