#ifndef ERRORPACKET
#define ERRORPACKET
#include "Packet.h"
#include <string>

class ERRORPacket : public Packet {
private:
	short errorCode_;
	std::string errorMessage_;
public:
	ERRORPacket(short errorCode, const std::string& errorMessage);
	short getErrorCode() const;
	std::string getErrorMessage() const;
};

#endif
