#include "ERRORPacket.h"

ERRORPacket::ERRORPacket(short errorCode, const std::string & errorMessage) : Packet(ERROR), errorCode_(errorCode), errorMessage_(errorMessage) {}

short ERRORPacket::getErrorCode() const {
	return errorCode_;
}

std::string ERRORPacket::getErrorMessage() const {
	return errorMessage_;
}
