#ifndef SERVERTASK
#define SERVERTASK

#include "connectionHandler.h"
#include "PacketEncoderDecoder.h"

class ServerTask {
private:
	ConnectionHandler* handler_;
	PacketEncoderDecoder encDec_;
	char receivingData_; // -1 - not receiving data, 0 - expecting file, 1 - expecting list of file names
	short lastBlockSent_;
	bool sendingData_;
	bool sentDisc_;
	static std::list<std::string> getStringsFromDIRQ(const DATAPacket& packet);

public:
	ServerTask(ConnectionHandler* handler);
	void runServerInput();
	void runKeyboardInput();
	virtual ~ServerTask();
};

#endif
