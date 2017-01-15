#ifndef WRQPACKET
#define WRQPACKET

#include "Packet.h"
#include <string>

class WRQPacket : public Packet {
private:
	std::string fileName_;
public:
	WRQPacket(const std::string& fileName);
	std::string getFileName() const;
};

#endif