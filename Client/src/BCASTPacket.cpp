#include "BCASTPacket.h"

BCASTPacket::BCASTPacket(char delOrAdd, const std::string & fileName) : Packet(BCAST), delOrAdd_(delOrAdd), fileName_(fileName) {}

char BCASTPacket::getDelOrAdd() const {
	return delOrAdd_;
}

std::string BCASTPacket::getFileName() const {
	return fileName_;
}
