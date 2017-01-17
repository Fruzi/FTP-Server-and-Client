#ifndef SERVERTASK
#define SERVERTASK


#include "connectionHandler.h"
#include "PacketEncoderDecoder.h"

class ServerTask {
private:
	ConnectionHandler handler_;
	PacketEncoderDecoder encDec_;
	bool receivingDirq_; //if false then its a file
	short lastBlockSent_;
	bool sendingData_;
	bool sentDisc_;
public:
	ServerTask(const ConnectionHandler& handler);
	void runServerInput();
	void runKeyboardInput();
	virtual ~ServerTask();
};

#endif
