#ifndef DELRQPACKET
#define DELRQPACKET
#include "Packet.h"
#include <string>

class DELRQPacket : public Packet {
private:
	std::string fileName_;
public:
	DELRQPacket(const std::string& fileName);
	std::string getFileName() const;
};

#endif