#include "PacketEncoderDecoder.h"
#include <iterator>
#include <iostream>

PacketEncoderDecoder::PacketEncoderDecoder() : opCode_(-1), packetSize_(-1), blockNum_(-1), errorCode_(-1), data_(), delOrAdd_(-1), shortByteArr_(), message() {
}


PacketEncoderDecoder::~PacketEncoderDecoder() {
}

std::vector<char> PacketEncoderDecoder::encode(const Packet& packet) {
	opCode_ = packet.getOpcode();
	std::vector<char> result(shortToBytes(opCode_));
	
	if (opCode_ == Rrq) {
		encodeRRQPacket(packet, result);
	} else if (opCode_ == Wrq) {
		encodeWRQPacket(packet, result);
	} else if (opCode_ == Data) {
		encodeDATAPacket(packet, result);
	} else if (opCode_ == Ack) {
		encodeACKPacket(packet, result);
	} else if (opCode_ == Error) {
		encodeERRORPacket(packet, result);
	} else if (opCode_ == Logrq) {
		encodeLOGRQPacket(packet, result);
	} else if (opCode_ == Delrq) {
		encodeDELRQPacket(packet, result);
	}
	resetFields();
	return result;
}

void PacketEncoderDecoder::encodeRRQPacket(const Packet& packet, std::vector<char>& result) const {
	const RRQPacket& rrqPacket(dynamic_cast<const RRQPacket&>(packet));
	std::string fileName = rrqPacket.getFileName();
	std::copy(fileName.begin(), fileName.end(), std::back_inserter(result));
	result.push_back('\0');
}

void PacketEncoderDecoder::encodeWRQPacket(const Packet& packet, std::vector<char>& result) const {
	const WRQPacket& wrqPacket(dynamic_cast<const WRQPacket&>(packet));
	std::string fileName = wrqPacket.getFileName();
	std::copy(fileName.begin(), fileName.end(), std::back_inserter(result));
	result.push_back('\0');
}

void PacketEncoderDecoder::encodeDATAPacket(const Packet& packet, std::vector<char>& result) {
	const DATAPacket& dataPacket(dynamic_cast<const DATAPacket&>(packet));
	packetSize_ = dataPacket.getPacketSize();
	blockNum_ = dataPacket.getBlockNum();
	data_ = dataPacket.getData();
	std::vector<char> packetSizeBytes = shortToBytes(packetSize_);
	std::vector<char> blockNumBytes = shortToBytes(blockNum_);
	result.insert(result.end(), packetSizeBytes.begin(), packetSizeBytes.end());
	result.insert(result.end(), blockNumBytes.begin(), blockNumBytes.end());
	result.insert(result.end(), data_.begin(), data_.end());
	data_.clear();
}

void PacketEncoderDecoder::encodeACKPacket(const Packet& packet, std::vector<char>& result) {
	const ACKPacket& ackPacket(dynamic_cast<const ACKPacket&>(packet));
	blockNum_ = ackPacket.getBlockNum();
	std::vector<char> blockNumBytes = shortToBytes(blockNum_);
	result.insert(result.end(), blockNumBytes.begin(), blockNumBytes.end());
}

void PacketEncoderDecoder::encodeERRORPacket(const Packet& packet, std::vector<char>& result) {
	const ERRORPacket& errorPacket(dynamic_cast<const ERRORPacket&>(packet));
	errorCode_ = errorPacket.getErrorCode();
	std::vector<char> errorCodeBytes = shortToBytes(errorCode_);
	std::string errorMessage = errorPacket.getErrorMessage();
	result.insert(result.end(), errorCodeBytes.begin(), errorCodeBytes.end());
	std::copy(errorMessage.begin(), errorMessage.end(), std::back_inserter(result));
	result.push_back('\0');
}

void PacketEncoderDecoder::encodeLOGRQPacket(const Packet& packet, std::vector<char>& result) const {
	const LOGRQPacket& logrqPacket(dynamic_cast<const LOGRQPacket&>(packet));
	std::string userName = logrqPacket.getUserName();
	std::copy(userName.begin(), userName.end(), std::back_inserter(result));
	result.push_back('\0');
}

void PacketEncoderDecoder::encodeDELRQPacket(const Packet& packet, std::vector<char>& result) const {
	const DELRQPacket& delrqPacket(dynamic_cast<const DELRQPacket&>(packet));
	std::string fileName = delrqPacket.getFileName();
	std::copy(fileName.begin(), fileName.end(), std::back_inserter(result));
	result.push_back('\0');
}

Packet* PacketEncoderDecoder::decodeFromKeyboardInput(const std::string& input) {
	auto spaceIndex = input.find_first_of(' ');
	if (spaceIndex == std::string::npos) {
		if (input == "DIRQ") {
			return new DIRQPacket();
		}
		if (input == "DISC") {
			return new DISCPacket();
		}
	} else {
		std::string type = input.substr(0, spaceIndex);
		std::string argument = input.substr(spaceIndex + 1, std::string::npos);
		if (type == "RRQ") {
			return new RRQPacket(argument);
		}
		if (type == "WRQ") {
			return new WRQPacket(argument);
		}
		if (type == "LOGRQ") {
			return new LOGRQPacket(argument);
		}
		if (type == "DELRQ") {
			return new DELRQPacket(argument);
		}
	}
	return nullptr;
}

Packet* PacketEncoderDecoder::decodeNextByte(char nextByte) {
	if (opCode_ == -1) {
		opCode_ = decodeShortPacketSegment(nextByte, opCode_);
	} else {
		if (opCode_ == Data) {
			return decodeDATAPacket(nextByte);
		}
		if (opCode_ == Ack) {
			return decodeACKPacket(nextByte);
		}
		if (opCode_ == Error) {
			return decodeERRORPacket(nextByte);
		}
		if (opCode_ == Bcast) {
			return decodeBCASTPacket(nextByte);
		}
	}
	return nullptr;
}

short PacketEncoderDecoder::decodeShortPacketSegment(short nextByte, short value) {
	if (shortByteArr_.size() < 2) {
		shortByteArr_.push_back(nextByte);
		if (shortByteArr_.size() == 2) {
			value = bytesToShort(shortByteArr_);
			shortByteArr_.clear();
		}
	}
	return value;
}

std::string PacketEncoderDecoder::decodeStringMessage(char nextByte) {
	std::string result;
	data_.push_back(nextByte);
	if (nextByte == '\0') {
		result = std::string(data_.begin(), data_.end());
		data_.clear();
	}
	return result;
}

DATAPacket* PacketEncoderDecoder::decodeDATAPacket(char nextByte) {
	if (packetSize_ == -1) {
		packetSize_ = decodeShortPacketSegment(nextByte, packetSize_);
	} else if (blockNum_ == -1) {
		blockNum_ = decodeShortPacketSegment(nextByte, blockNum_);
		if (blockNum_ != -1 && packetSize_ == 0) {
			DATAPacket* packet = new DATAPacket(packetSize_, blockNum_, data_);
			resetFields();
			return packet;
		}
	} else {
		data_.push_back(nextByte);
		if (data_.size() == packetSize_) {
			DATAPacket* packet = new DATAPacket(packetSize_, blockNum_, data_);
			resetFields();
			return packet;
		}
	}
	return nullptr;
}

ACKPacket* PacketEncoderDecoder::decodeACKPacket(char nextByte) {
	blockNum_ = decodeShortPacketSegment(nextByte, blockNum_);
	if (blockNum_ != -1) {
		ACKPacket* result = new ACKPacket(blockNum_);
		resetFields();
		return result;
	}
	return nullptr;
}

ERRORPacket* PacketEncoderDecoder::decodeERRORPacket(char nextByte) {
	if (errorCode_ == -1) {
		errorCode_ = decodeShortPacketSegment(nextByte, errorCode_);
	} else {
		std::string message = decodeStringMessage(nextByte);
		if (!message.empty()) {
			ERRORPacket* result = new ERRORPacket(errorCode_, message);
			resetFields();
			return result;
		}
	}
	return nullptr;
}

BCASTPacket* PacketEncoderDecoder::decodeBCASTPacket(char nextByte) {
	if (delOrAdd_ == -1) {
		delOrAdd_ = nextByte;
	} else {
		std::string message = decodeStringMessage(nextByte);
		if (!message.empty()) {
			BCASTPacket* result = new BCASTPacket(delOrAdd_, message);
			resetFields();
			return result;
		}
	}
	return nullptr;
}

void PacketEncoderDecoder::resetFields() {
	opCode_ = -1;
	packetSize_ = -1;
	blockNum_ = -1;
	errorCode_ = -1;
	delOrAdd_ = -1;
	data_.clear();
	shortByteArr_.clear();
}


short PacketEncoderDecoder::bytesToShort(const std::vector<char>& bytesArr) {
	short result = (bytesArr[0] & 0xff) << 8;
	result += bytesArr[1] & 0xff;
	return result;
}

std::vector<char> PacketEncoderDecoder::shortToBytes(short num) {
	std::vector<char> bytes;
	bytes.push_back((num >> 8) & 0xFF);
	bytes.push_back(num & 0xFF);
	return bytes;
}
