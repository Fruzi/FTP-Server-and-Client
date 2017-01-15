#ifndef BCASTPACKET
#define BCASTPACKET
#include "Packet.h"
#include <string>

class BCASTPacket : public Packet {
private:
	char delOrAdd_;
	std::string fileName_;
public:
	BCASTPacket(char delOrAdd, const std::string& fileName);
	char getDelOrAdd() const;
	std::string getFileName() const;
};

#endif
