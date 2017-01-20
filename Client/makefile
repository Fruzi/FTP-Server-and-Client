PROGRAM = TFTPclient

SRCS = Packet.cpp \
	connectionHandler.cpp \
	PacketEncoderDecoder.cpp \
	ServerTask.cpp

SRCDIR = src
BINDIR = bin

OBJS = $(SRCS:%.cpp=$(BINDIR)/%.o)

CC = g++
CFLAGS =-Wall -Weffc++ -g -std=c++11
LFLAGS =-lboost_system -lboost_locale -lboost_thread

INCLUDES = -Iinclude
LIBS =

.PHONY: all
all: $(PROGRAM)

$(PROGRAM): $(OBJS) $(BINDIR)/TFTPclient.o
	$(CC) $(LIBS) -o $(BINDIR)/$(PROGRAM) $+ $(LFLAGS)

$(BINDIR)/%.o: $(SRCDIR)/%.cpp
	@mkdir -p $(BINDIR)
	$(CC) -c $(CFLAGS) $(INCLUDES) -o $@ $<

.PHONY: clean
clean:
	rm -rf $(BINDIR)/*
