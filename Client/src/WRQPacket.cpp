#include "WRQPacket.h"

WRQPacket::WRQPacket(const std::string & fileName) : Packet(WRQ), fileName_(fileName) {}

std::string WRQPacket::getFileName() const {
	return fileName_;
}
