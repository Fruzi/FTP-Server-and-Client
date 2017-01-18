#include <ServerTask.h>
#include <memory>
#include "Packet.h"

ServerTask::ServerTask(ConnectionHandler* handler) : handler_(handler), encDec_(), receivingData_(-1), lastBlockSent_(0), sendingData_(false), sentDisc_(false), sentWrq_(false), dataCollection_(), currentQueriedFile_(), fileInputStream_() {}

void ServerTask::runKeyboardInput() {
	const short bufsize = 1024;
	while (!sentDisc_) {
		char buf[bufsize];
		std::cin.getline(buf, bufsize);
		std::string input(buf);
		if (input.length() > 0) {
			Packet* packet(PacketEncoderDecoder::decodeFromKeyboardInput(input));
			if (packet) {
				short opCode = packet->getOpcode();
				switch (opCode) {
				case Rrq: {
					RRQPacket* rrq = dynamic_cast<RRQPacket*>(packet);
					receivingData_ = 0;
					currentQueriedFile_ = rrq->getFileName();
					break;
				} case Wrq: {
					sendingData_ = true;
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
				std::vector<char> encodedPacket = encDec_.encode(packet);
				delete packet;
				if (!handler_->sendBytes(&encodedPacket[0], encodedPacket.size())) {
					return;
				}
			}
		}
	}
}

void ServerTask::runServerInput() {
	while (!sentDisc_) {
		char in;
		if (!handler_->getBytes(&in, 1)) {
			return;
		}
		Packet* message = encDec_.decodeNextByte(in);
		if (message) {
			short opCode = message->getOpcode();
			std::cout << "Packet with opcode " << opCode << " received" << std::endl << std::flush;
			switch (opCode) {
			case Data: {
				if (receivingData_ == -1) {
					//send error and return
					delete message;
					return;
				}
				DATAPacket* packet = dynamic_cast<DATAPacket*>(message);
				std::vector<char> currentPacketData = packet->getData();
				if (receivingData_ == 0) {
					//write to disc
					std::ofstream fileOutputStream;
					fileOutputStream.open(currentQueriedFile_, std::ios::out);
					fileOutputStream.open(currentQueriedFile_, std::ios::app);
					fileOutputStream << &currentPacketData[0];
					fileOutputStream.close();
				} else {
					dataCollection_.insert(dataCollection_.end(), currentPacketData.begin(), currentPacketData.end());

				}
				if (packet->getPacketSize() < DATAPacket::MAX_DATA_SIZE) {
					 if (receivingData_ == 0) {
						 std::cout << "RRQ " << currentQueriedFile_ << " complete" << std::endl << std::flush;
					} else {
						 std::list<std::string> fileList = getStringsFromDIRQ(currentPacketData);
						for (std::string fileName : fileList) {
							std::cout << fileName << std::endl << std::flush;
						}
						dataCollection_.clear();
					}
					receivingData_ = -1;
				}
				Packet* ackResponse = new ACKPacket(packet->getBlockNum());
				std::vector<char> encodedPacket = encDec_.encode(ackResponse);
				if (!handler_->sendBytes(&encodedPacket[0], encodedPacket.size())) {
					return;
				}
				delete ackResponse;
				break;
			} case Ack: {
				ACKPacket* ack = dynamic_cast<ACKPacket*>(message);
				std::cout << "ACK " << ack->getBlockNum() << std::endl;

				if (sentWrq_) {

				}
				break;
			} case Error: {
				ERRORPacket* error = dynamic_cast<ERRORPacket*>(message);
				std::cout << "Error " << error->getErrorCode() << " " << error->getErrorMessage() << std::endl;
				break;
			} case Bcast: {
				BCASTPacket* bcast = dynamic_cast<BCASTPacket*>(message);
				std::cout << "BCAST " << bcast->getDelOrAdd() << " " << bcast->getFileName() << std::endl;
				break;
			} default: {

				break;
			}
			}
		}
	}
}

std::list<std::string> ServerTask::getStringsFromDIRQ(const std::vector<char>& dataArr) {
	std::list<std::string> fileList;
	char delimiter = '\0';
	std::vector<char> currentString;
	for (char byte : dataArr) {
		if (byte == delimiter) {
			std::string str(currentString.begin(), currentString.end());
			fileList.push_back(str);
			currentString.clear();
		} else {
			currentString.push_back(byte);
		}
	}
	return fileList;
}

ServerTask::~ServerTask() {
	delete handler_;
}
