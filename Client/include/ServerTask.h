#ifndef SERVERTASK
#define SERVERTASK

#include "connectionHandler.h"
#include "PacketEncoderDecoder.h"
#include <fstream>

class ServerTask {
private:
	ConnectionHandler* handler_;
	PacketEncoderDecoder encDec_;
	char receivingData_; // -1 - not receiving data, 0 - expecting file, 1 - expecting list of file names
	short blockNum_;
	bool sendingData_;
	bool sentDisc_;
	bool finalBlockSent_;
	//std::vector<char> downloadDataCollection_;
	//std::vector<char> uploadDataCollection_;
	std::vector<char> dataCollection_;
	std::string currentQueriedFile_;
	std::ifstream fileInputStream_;

	void printFilenamesFromData();
	void writeToDisc(const std::vector<char>& toWrite) const;
	//void readFromDisc();
	std::vector<char> readFromDisc();
	bool sendPacket(const Packet& packet);
	bool sendDataFromDisc();

	void finishedReadingData();

	bool handleIncomingData(DATAPacket * packet);

public:
	ServerTask(ConnectionHandler* handler);
	void runServerInput();
	void runKeyboardInput();
	virtual ~ServerTask();
};

#endif
