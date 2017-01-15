#include "DATAPacket.h"

DATAPacket::DATAPacket(short packetSize, short blockNum, char * data) : Packet(DATA), packetSize_(packetSize), blockNum_(blockNum), data_(data) {}

short DATAPacket::getPacketSize() const {
	return packetSize_;
}

short DATAPacket::getBlockNum() const {
	return blockNum_;
}

char * DATAPacket::getData() const {
	return data_;
}
