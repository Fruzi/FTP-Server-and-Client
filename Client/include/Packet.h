#ifndef PACKET
#define PACKET
#include <vector>
#include <string>

enum opCodes {
	Rrq = 1,
	Wrq,
	Data,
	Ack,
	Error,
	Dirq,
	Logrq,
	Delrq,
	Bcast,
	Disc
};

class Packet {
protected:
	Packet(short opCode);
	short opCode_;
public:
	short getOpcode() const;
	virtual ~Packet();
};

class RRQPacket : public Packet {
private:
	std::string fileName_;
public:
	RRQPacket(const std::string& fileName);
	std::string getFileName() const;
};

class WRQPacket : public Packet {
private:
	std::string fileName_;
public:
	WRQPacket(const std::string& fileName);
	std::string getFileName() const;
};

class DATAPacket : public Packet {
private:
	short packetSize_;
	short blockNum_;
	std::vector<char> data_;
public:
	DATAPacket(short packetSize, short blockNum, const std::vector<char>& data);
	short getPacketSize() const;
	short getBlockNum() const;
	std::vector<char> getData() const;

	static const int MAX_DATA_SIZE = 512;
};

class ACKPacket : public Packet {
private:
	short blockNum_;
public:
	ACKPacket(short blockNum);
	short getBlockNum() const;
};

class ERRORPacket : public Packet {
private:
	short errorCode_;
	std::string errorMessage_;
public:
	ERRORPacket(short errorCode, const std::string& errorMessage);
	short getErrorCode() const;
	std::string getErrorMessage() const;
};

class DIRQPacket : public Packet {
public:
	DIRQPacket();
};

class LOGRQPacket : public Packet {
private:
	std::string userName_;
public:
	LOGRQPacket(const std::string& userName);
	std::string getUserName() const;
};

class DELRQPacket : public Packet {
private:
	std::string fileName_;
public:
	DELRQPacket(const std::string& fileName);
	std::string getFileName() const;
};

class BCASTPacket : public Packet {
private:
	char delOrAdd_;
	std::string fileName_;
public:
	BCASTPacket(char delOrAdd, const std::string& fileName);
	char getDelOrAdd() const;
	std::string getFileName() const;
};

class DISCPacket : public Packet {
public:
	DISCPacket();
};

#endif
