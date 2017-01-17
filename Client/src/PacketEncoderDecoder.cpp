#include "PacketEncoderDecoder.h"
#include <iterator>
#include <locale>

PacketEncoderDecoder::PacketEncoderDecoder() : opCode_(-1), packetSize_(-1), blockNum_(-1), errorCode_(-1), data_(), delOrAdd_(-1), shortByteArr_(), message() {
}


PacketEncoderDecoder::~PacketEncoderDecoder() {
}

std::vector<char> PacketEncoderDecoder::encode(Packet* packet) {
	opCode_ = packet->getOpcode();
	std::vector<char> result(shortToBytes(opCode_));
	
	if (opCode_ == Rrq) {
		std::vector<char> bytes = encodeRRQPacket(packet);
		result.insert(result.end(), bytes.begin(), bytes.end());
	} else if (opCode_ == Wrq) {
		std::vector<char> bytes = encodeWRQPacket(packet);
		result.insert(result.end(), bytes.begin(), bytes.end());
	} else if (opCode_ == Data) {
		std::vector<char> bytes = encodeDATAPacket(packet);
		result.insert(result.end(), bytes.begin(), bytes.end());
	} else if (opCode_ == Ack) {
		std::vector<char> bytes = encodeACKPacket(packet);
		result.insert(result.end(), bytes.begin(), bytes.end());
	} else if (opCode_ == Error) {
		std::vector<char> bytes = encodeERRORPacket(packet);
		result.insert(result.end(), bytes.begin(), bytes.end());
	} else if (opCode_ == Logrq) {
		std::vector<char> bytes = encodeLOGRQPacket(packet);
		result.insert(result.end(), bytes.begin(), bytes.end());

	} else if (opCode_ == Delrq) {
		std::vector<char> bytes = encodeDELRQPacket(packet);
		result.insert(result.end(), bytes.begin(), bytes.end());

	}
	resetFields();
	return result;
}

std::vector<char> PacketEncoderDecoder::encodeRRQPacket(Packet* packet) const {
	std::vector<char> bytes;
	std::string fileName = dynamic_cast<RRQPacket*>(packet)->getFileName();
	std::copy(fileName.begin(), fileName.end(), std::back_inserter(bytes));
	bytes.push_back('\0');
	return bytes;
}

std::vector<char> PacketEncoderDecoder::encodeWRQPacket(Packet* packet) const {
	std::vector<char> bytes;
	std::string fileName = dynamic_cast<WRQPacket*>(packet)->getFileName();
	std::copy(fileName.begin(), fileName.end(), std::back_inserter(bytes));
	bytes.push_back('\0');
	return bytes;
}

std::vector<char> PacketEncoderDecoder::encodeDATAPacket(Packet* packet) {
	std::vector<char> bytes;
	packetSize_ = dynamic_cast<DATAPacket*>(packet)->getPacketSize();
	blockNum_ = dynamic_cast<DATAPacket*>(packet)->getBlockNum();
	data_ = dynamic_cast<DATAPacket*>(packet)->getData();
	std::vector<char> packetSizeBytes = shortToBytes(packetSize_);
	std::vector<char> blockNumBytes = shortToBytes(blockNum_);
	bytes.insert(bytes.end(), packetSizeBytes.begin(), packetSizeBytes.end());
	bytes.insert(bytes.end(), blockNumBytes.begin(), blockNumBytes.end());
	bytes.insert(bytes.end(), data_.begin(), data_.end());
	data_.clear();
	return bytes;
}

std::vector<char> PacketEncoderDecoder::encodeACKPacket(Packet* packet) {
	std::vector<char> bytes;
	blockNum_ = dynamic_cast<ACKPacket*>(packet)->getBlockNum();
	std::vector<char> blockNumBytes = shortToBytes(blockNum_);
	bytes.insert(bytes.end(), blockNumBytes.begin(), blockNumBytes.end());
	return bytes;
}

std::vector<char> PacketEncoderDecoder::encodeERRORPacket(Packet* packet) {
	std::vector<char> bytes;
	errorCode_ = dynamic_cast<ERRORPacket*>(packet)->getErrorCode();
	std::vector<char> errorCodeBytes = shortToBytes(errorCode_);
	std::string errorMessage = dynamic_cast<ERRORPacket*>(packet)->getErrorMessage();
	bytes.insert(bytes.end(), errorCodeBytes.begin(), errorCodeBytes.end());
	std::copy(errorMessage.begin(), errorMessage.end(), std::back_inserter(bytes));
	bytes.push_back('\0');
	return bytes;
}

std::vector<char> PacketEncoderDecoder::encodeLOGRQPacket(Packet* packet) const {
	std::vector<char> bytes;
	std::string userName = dynamic_cast<LOGRQPacket*>(packet)->getUserName();
	std::copy(userName.begin(), userName.end(), std::back_inserter(bytes));
	bytes.push_back('\0');
	return bytes;
}

std::vector<char> PacketEncoderDecoder::encodeDELRQPacket(Packet* packet) const {
	std::vector<char> bytes;
	std::string fileName = dynamic_cast<DELRQPacket*>(packet)->getFileName();
	std::copy(fileName.begin(), fileName.end(), std::back_inserter(bytes));
	bytes.push_back('\0');
	return bytes;
}

Packet* PacketEncoderDecoder::decodeFromKeyboardInput(const std::string& input) {
	auto spaceIndex = findFirstSpace(input);
	if (spaceIndex == -1) {
		if (input == "DIRQ") {
			return new DIRQPacket();
		}
		if (input == "DISC") {
			return new DISCPacket();
		}
	} else {
		std::string type = input.substr(0, spaceIndex);
		std::string argument = input.substr(spaceIndex + 1);
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

int PacketEncoderDecoder::findFirstSpace(const std::string& input) {
	std::locale loc;
	for (size_t i = 0; i < input.length(); i++) {
		if (std::isspace(input.at(i), loc)) {
			return i;
		}
	}
	return -1;
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
	if (nextByte == '\0') {
		result = std::string(data_.begin(), data_.end());
		data_.clear();
	}
	data_.push_back(nextByte);
	return result;
}

DATAPacket* PacketEncoderDecoder::decodeDATAPacket(char nextByte) {
	if (packetSize_ == -1) {
		packetSize_ = decodeShortPacketSegment(nextByte, packetSize_);
	} else if (blockNum_ == -1) {
		blockNum_ = decodeShortPacketSegment(nextByte, blockNum_);
	} else {
		data_.push_back(nextByte);
		if (data_.size() == packetSize_) {
			DATAPacket* packet = new DATAPacket(packetSize_, blockNum_, data_);
			resetFields();
			data_.clear();
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
}


short PacketEncoderDecoder::bytesToShort(std::vector<char> bytesArr) {
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
