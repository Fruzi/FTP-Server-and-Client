#include "RRQPacket.h"

RRQPacket::RRQPacket(const std::string & fileName) : Packet(RRQ), fileName_(fileName) {}

std::string RRQPacket::getFileName() const {
	return fileName_;
}
