#include "Packet.h"

Packet::Packet(short opCode) : opCode_(opCode) {}

short Packet::getOpcode() const {
	return opCode_;
}

Packet::~Packet() {}
