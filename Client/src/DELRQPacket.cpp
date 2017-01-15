#include "DELRQPacket.h"

DELRQPacket::DELRQPacket(const std::string & fileName) : Packet(DELRQ), fileName_(fileName) {}

std::string DELRQPacket::getFileName() const {
	return fileName_;
}
