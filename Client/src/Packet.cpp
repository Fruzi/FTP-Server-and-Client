#include "Packet.h"

Packet::Packet(short opCode) : opCode_(opCode) {}

short Packet::getOpcode() const {
	return opCode_;
}

Packet::~Packet() {}

RRQPacket::RRQPacket(const std::string & fileName) : Packet(Rrq), fileName_(fileName) {}

std::string RRQPacket::getFileName() const {
	return fileName_;
}

WRQPacket::WRQPacket(const std::string & fileName) : Packet(Wrq), fileName_(fileName) {}

std::string WRQPacket::getFileName() const {
	return fileName_;
}

DATAPacket::DATAPacket(short packetSize, short blockNum, const std::vector<char>& data) : Packet(Data), packetSize_(packetSize), blockNum_(blockNum), data_(data) {}

short DATAPacket::getPacketSize() const {
	return packetSize_;
}

short DATAPacket::getBlockNum() const {
	return blockNum_;
}

std::vector<char> DATAPacket::getData() const {
	return data_;
}

ACKPacket::ACKPacket(short blockNum) : Packet(Ack), blockNum_(blockNum) {}

short ACKPacket::getBlockNum() const {
	return blockNum_;
}

ERRORPacket::ERRORPacket(short errorCode, const std::string & errorMessage) : Packet(Error), errorCode_(errorCode), errorMessage_(errorMessage) {}

short ERRORPacket::getErrorCode() const {
	return errorCode_;
}

std::string ERRORPacket::getErrorMessage() const {
	return errorMessage_;
}

DIRQPacket::DIRQPacket() : Packet(Dirq) {}

LOGRQPacket::LOGRQPacket(const std::string & userName) : Packet(Logrq), userName_(userName) {}

std::string LOGRQPacket::getUserName() const {
	return userName_;
}

DELRQPacket::DELRQPacket(const std::string & fileName) : Packet(Delrq), fileName_(fileName) {}

std::string DELRQPacket::getFileName() const {
	return fileName_;
}

BCASTPacket::BCASTPacket(char delOrAdd, const std::string & fileName) : Packet(Bcast), delOrAdd_(delOrAdd), fileName_(fileName) {}

char BCASTPacket::getDelOrAdd() const {
	return delOrAdd_;
}

std::string BCASTPacket::getFileName() const {
	return fileName_;
}

DISCPacket::DISCPacket() : Packet(Disc) {}

