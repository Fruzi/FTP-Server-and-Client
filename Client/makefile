CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude
LDFLAGS:=-lboost_system -lboost_locale -lboost_thread

all: TFTPclient
	g++ -o bin/TFTPclient bin/TFTPclient.o bin/connectionHandler.o bin/ServerTask.o bin/PacketEncoderDecoder.o bin/Packet.o $(LDFLAGS) 

test: TFTPTest
	g++ -o bin/TFTPTest bin/Packet.o bin/PacketEncoderDecoder.o bin/EncdecTest.o $(LDFLAGS)
	
TFTPclient: bin/TFTPclient.o bin/connectionHandler.o bin/ServerTask.o bin/PacketEncoderDecoder.o bin/Packet.o

TFTPTest: bin/Packet.o bin/PacketEncoderDecoder.o bin/EncdecTest.o

bin/TFTPclient.o: src/TFTPclient.cpp
	g++ $(CFLAGS) -o bin/TFTPclient.o src/TFTPclient.cpp

bin/connectionHandler.o: src/connectionHandler.cpp
	g++ $(CFLAGS) -o bin/connectionHandler.o src/connectionHandler.cpp

bin/ServerTask.o: src/ServerTask.cpp
	g++ $(CFLAGS) -o bin/ServerTask.o src/ServerTask.cpp

bin/PacketEncoderDecoder.o: src/PacketEncoderDecoder.cpp
	g++ $(CFLAGS) -o bin/PacketEncoderDecoder.o src/PacketEncoderDecoder.cpp
	
bin/Packet.o: src/Packet.cpp
	g++ $(CFLAGS) -o bin/Packet.o src/Packet.cpp

bin/EncdecTest.o: src/EncdecTest.cpp
	g++ $(CFLAGS) -o bin/EncdecTest.o src/EncdecTest.cpp
	
.PHONY: clean
clean:
	rm -f bin/*
