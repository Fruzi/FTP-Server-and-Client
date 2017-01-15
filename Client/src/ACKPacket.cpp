#include "ACKPacket.h"

ACKPacket::ACKPacket(short blockNum) : Packet(ACK), blockNum_(blockNum) {}

short ACKPacket::getBlockNum() const {
	return blockNum_;
}
