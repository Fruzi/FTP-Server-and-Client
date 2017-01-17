#include <ServerTask.h>
#include <memory>
#include "Packet.h"

ServerTask::ServerTask(const ConnectionHandler& handler) : handler_(handler), encDec_(), receivingDirq_(false), lastBlockSent_(0), sendingData_(false), sentDisc_(false) {}

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
				case Rrq:

					break;
				case Wrq:
					sendingData_ = true;
					break;
				case Dirq:
					receivingDirq_ = true;
					break;
				case Logrq:

					break;
				case Delrq:
					
					break;

				case Disc:
					sentDisc_ = true;
					break;
				default:

					break;
				}
				std::vector<char> encodedPacket = encDec_.encode(packet.get());

				if (!handler_.sendBytes(&encodedPacket[0], encodedPacket.size())) {
					return;
				}
			}
		}
	}
}

void ServerTask::runServerInput() {
	while (true) {
		char in;
		if (!handler_.getBytes(&in, 1)) {
			return;
		}
		Packet* message = encDec_.decodeNextByte(in);
		if (message) {
			short opCode = message->getOpcode();
			std::cout << "Packet with opcode " << opCode << " received" << std::endl;
			switch (opCode) {
			case Data:
				if (receivingDirq_) {
					
				} else {
					
				}

				break;
			case Ack:
				if (sentDisc_) {
					
				} else {
					
				}

				break;
			case Error:
				ERRORPacket* error = dynamic_cast<ERRORPacket*>(message);
				std::cout << "Error " << error->getErrorCode() << std::endl;
				break;
			case Bcast:
				BCASTPacket* bcast = dynamic_cast<BCASTPacket*>(message);
				std::cout << "BCAST " << bcast->getDelOrAdd() << " " << bcast->getFileName() << std::endl;
				break;
			}
		}
	}
}

ServerTask::~ServerTask() {}
