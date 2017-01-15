#include "LOGRQPacket.h"

LOGRQPacket::LOGRQPacket(const std::string & userName) : Packet(LOGRQ), userName_(userName) {}

std::string LOGRQPacket::getUserName() const {
	return userName_;
}
