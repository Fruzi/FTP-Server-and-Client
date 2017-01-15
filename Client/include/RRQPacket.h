#ifndef RRQPACKET
#define RRQPACKET

#include "Packet.h"
#include <string>

class RRQPacket : public Packet {
private:
	std::string fileName_;
public:
	RRQPacket(const std::string& fileName);
	std::string getFileName() const;
};

#endif
