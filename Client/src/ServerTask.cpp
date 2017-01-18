#include <ServerTask.h>
#include <memory>
#include "Packet.h"

ServerTask::ServerTask(ConnectionHandler* handler) : handler_(handler), encDec_(), receivingData_(-1), lastBlockSent_(0), sendingData_(false), sentDisc_(false) {}

void ServerTask::runKeyboardInput() {
	const short bufsize = 1024;
	while (true) {
		char buf[bufsize];
		std::cin.getline(buf, bufsize);
		std::string input(buf);
		if (input.length() > 0) {
			std::unique_ptr<Packet> packet(PacketEncoderDecoder::decodeFromKeyboardInput(input));
			if (packet) {
				short opCode = packet->getOpcode();
				switch (opCode) {
				case Rrq: {
					receivingData_ = 0;
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
				std::vector<char> encodedPacket = encDec_.encode(packet.get());

				if (!handler_->sendBytes(&encodedPacket[0], encodedPacket.size())) {
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
			std::cout << "Packet with opcode " << opCode << " received" << std::endl << std::flush;
			switch (opCode) {
			case Data: {
				DATAPacket* packet = dynamic_cast<DATAPacket*>(message);
				std::cout << std::to_string(receivingData_) << std::endl << std::flush;
				if (receivingData_ == 0) {
					//write to disc
				} else if (receivingData_ == 1) {
					std::list<std::string> fileList = getStringsFromDIRQ(*packet);
					for (std::string fileName : fileList) {
						std::cout << fileName << std::endl << std::flush;
					}
				} else {
					//sendError
				}

				break;
			} case Ack: {
				ACKPacket* ack = dynamic_cast<ACKPacket*>(message);
				std::cout << "ACK " << ack->getBlockNum() << std::endl;

				if (sentDisc_) {

				} else {

				}

				break;
			} case Error: {
				ERRORPacket* error = dynamic_cast<ERRORPacket*>(message);
				std::cout << "Error " << error->getErrorCode() << std::endl;
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

std::list<std::string> ServerTask::getStringsFromDIRQ(const DATAPacket & packet) {
	std::list<std::string> fileList;
	char delimiter = '\0';
	std::vector<char> packetData = packet.getData();
	std::vector<char> currentString;
	for (char byte : packetData) {
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
