#include <ServerTask.h>
#include <memory>
#include "Packet.h"

ServerTask::ServerTask(ConnectionHandler* handler) : handler_(handler), encDec_(), receivingData_(-1), blockNum_(1), sendingData_(false), sentDisc_(false), finalBlockSent_(false), dataCollection_(), currentQueriedFile_(), fileInputStream_() {}

void ServerTask::runKeyboardInput() {
	const short bufsize = 1024;
	while (true) {
		char buf[bufsize];
		std::cin.getline(buf, bufsize);
		std::string input(buf);
		if (input.length() > 0) {
			Packet* packet(PacketEncoderDecoder::decodeFromKeyboardInput(input));
			if (packet) {
				short opCode = packet->getOpcode();
				switch (opCode) {
				case Rrq: {
					const RRQPacket rrq(dynamic_cast<const RRQPacket&>(*packet));
					receivingData_ = 0;
					currentQueriedFile_ = rrq.getFileName();
					break;
				} case Wrq: {
					const WRQPacket wrq(dynamic_cast<const WRQPacket&>(*packet));
					sendingData_ = true;
					finalBlockSent_ = false;
					currentQueriedFile_ = wrq.getFileName();
					fileInputStream_.open(currentQueriedFile_, std::ios::binary);
					break;
				} case Dirq: {
					receivingData_ = 1;
					break;
				} case Disc: {
					sentDisc_ = true;
					break;
				} default: {

					break;
				}
				}
				bool success = sendPacket(*packet);
				delete packet;
				if (!success) {
					return;
				}
			}
		}
	}
}

void ServerTask::runServerInput() {
	while (true) {
		char in;
		if (!handler_->getBytes(&in, 1)) {
			return;
		}
		Packet* message = encDec_.decodeNextByte(in);
		if (message) {
			short opCode = message->getOpcode();
			switch (opCode) {
			case Data: {
				if (receivingData_ == -1) {
					sendPacket(ERRORPacket(0, ""));
					delete message;
					return;
				}
				bool success = handleIncomingData(dynamic_cast<DATAPacket&>(*message));
				if (!success) {
					delete message;
					return;
				}
				break;
			} case Ack: {
				ACKPacket ack(dynamic_cast<ACKPacket&>(*message));
				if (ack.getBlockNum() != blockNum_ - 1) {
					sendPacket(ERRORPacket(0, ""));
					delete message;
					continue;
				}
				std::cout << "ACK " << ack.getBlockNum() << std::endl << std::flush;
				if (finalBlockSent_) {
					std::cout << "WRQ " << currentQueriedFile_ << " complete" << std::endl << std::flush;
					blockNum_ = 1;
					finalBlockSent_ = false;
				}
				if (sentDisc_) {
					delete message;
					return;
				}
				if (sendingData_) {
					if (!sendDataFromDisc()) {
						delete message;
						return;
					}
				}
				break;
			} case Error: {
				ERRORPacket error(dynamic_cast<ERRORPacket&>(*message));
				std::cout << "Error " << error.getErrorCode() << std::endl << std::flush;
				sendingData_ = false;
				receivingData_ = -1;
				fileInputStream_.close();
				break;
			} case Bcast: {
				BCASTPacket bcast(dynamic_cast<BCASTPacket&>(*message));
				std::string deloradd = bcast.getDelOrAdd() == 0 ? "del" : "add";
				std::cout << "BCAST " << deloradd << " " << bcast.getFileName() << std::endl << std::flush;
				break;
			} default: {

				break;
			}
			}
			delete message;
		}
	}
}

void ServerTask::printFilenamesFromData() {
	char delimiter = '\0';
	std::vector<char> currentString;
	for (char byte : dataCollection_) {
		if (byte == delimiter) {
			std::cout << std::string(currentString.begin(), currentString.end()) << std::endl << std::flush;
			currentString.clear();
		} else {
			currentString.push_back(byte);
		}
	}
}

void ServerTask::writeToDisc(const std::vector<char>& toWrite) const {
	std::ofstream fileOutputStream;
	fileOutputStream.open(currentQueriedFile_, std::ios::out | std::ios::binary | std::ios::app);
	fileOutputStream.write(&toWrite[0], toWrite.size());
	fileOutputStream.flush();
	fileOutputStream.close();
}

std::vector<char> ServerTask::readFromDisc() {
	char bytes[DATAPacket::MAX_DATA_SIZE];
	fileInputStream_.read(bytes, sizeof(bytes));

	std::streamsize amountRead = fileInputStream_.gcount();

	std::vector<char> bytesRead(bytes, bytes + amountRead);
	
	return bytesRead;
}

bool ServerTask::sendPacket(const Packet& packet) {
	std::vector<char> encodedPacket = encDec_.encode(packet);
	return handler_->sendBytes(&encodedPacket[0], encodedPacket.size());
}

bool ServerTask::sendDataFromDisc() {
	std::vector<char> bytesRead(readFromDisc());
	
	DATAPacket dataPacket(bytesRead.size(), blockNum_++, bytesRead);
	if (!sendPacket(dataPacket)) {
		return false;
	}

	if (fileInputStream_.gcount() < DATAPacket::MAX_DATA_SIZE) {
		finishedReadingData();
	}

	return true;
}

void ServerTask::finishedReadingData() {
	fileInputStream_.clear();
	fileInputStream_.seekg(0);
	fileInputStream_.close();
	sendingData_ = false;
	finalBlockSent_ = true;
	//blockNum_ = 1;
	currentQueriedFile_.clear();
}

bool ServerTask::handleIncomingData(const DATAPacket& packet) {
	const std::vector<char> currentPacketData = packet.getData();
	if (receivingData_ == 0) {
		std::ofstream fileOutputStream;
		writeToDisc(currentPacketData);
	} else {
		dataCollection_.insert(dataCollection_.end(), currentPacketData.begin(), currentPacketData.end());
	}
	ACKPacket ackResponse(packet.getBlockNum());
	bool success = sendPacket(ackResponse);
	if (packet.getPacketSize() < DATAPacket::MAX_DATA_SIZE) {
		if (receivingData_ == 0) {
			std::cout << "RRQ " << currentQueriedFile_ << " complete" << std::endl << std::flush;
			currentQueriedFile_.clear();
		} else {
			printFilenamesFromData();
			dataCollection_.clear();
		}
		receivingData_ = -1;
	}
	
	return success;
}

ServerTask::~ServerTask() {
	delete handler_;
}
