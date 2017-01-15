#ifndef LOGRQPACKET
#define LOGRQPACKET
#include "Packet.h"
#include <string>

class LOGRQPacket : public Packet {
private:
	std::string userName_;
public:
	LOGRQPacket(const std::string& userName);
	std::string getUserName() const;
};

#endif
