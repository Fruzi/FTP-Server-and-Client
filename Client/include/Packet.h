#ifndef PACKET
#define PACKET

enum opCodes {
	RRQ = 1,
	WRQ,
	DATA,
	ACK,
	ERROR,
	DIRQ,
	LOGRQ,
	DELRQ,
	BCAST,
	DISC
};

class Packet {
protected:
	Packet(short opCode);
	short opCode_;
public:
	short getOpcode() const;
	virtual ~Packet();
};

#endif