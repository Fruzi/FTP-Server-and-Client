//
// Created by alonam on 1/15/17.
//

#ifndef CLIENTDANA_ENCDECTEST_H
#define CLIENTDANA_ENCDECTEST_H
#include "../include/PacketEncoderDecoder.h"
using namespace std;
#include <string>
#include <iostream>


class EncdecTest {

public:
    PacketEncoderDecoder* encdec = new PacketEncoderDecoder();
     char* vecToArr(vector<char>& v);
     vector<char> arrToVec(char* c);
     void testDataDecode (PacketEncoderDecoder& encdec);
     void testDataEncode (PacketEncoderDecoder& encdec);
     void testDISCDecode (PacketEncoderDecoder& encdec);
     void testDISCEncode (PacketEncoderDecoder& encdec);
     void testBCastDecode (PacketEncoderDecoder& encdec);
     void testBCastEncode (PacketEncoderDecoder& encdec);
     void testDELRQDecode (PacketEncoderDecoder& encdec);
     void testDELRQEncode (PacketEncoderDecoder& encdec);
     void testLOGRQDecode (PacketEncoderDecoder& encdec);
     void testLOGRQEncode (PacketEncoderDecoder& encdec);
     void testDIRQDecode (PacketEncoderDecoder& encdec);
     void testDIRQEncode (PacketEncoderDecoder& encdec);
     void testErrorDecode (PacketEncoderDecoder& encdec);
     void testErrorEncode (PacketEncoderDecoder& encdec);
     void testRRQDecode (PacketEncoderDecoder& encdec);
     void testRRQEncode (PacketEncoderDecoder& encdec);
     void testWRQDecode (PacketEncoderDecoder& encdec);
     void testWRQEncode (PacketEncoderDecoder& encdec);
     void testACKDecode (PacketEncoderDecoder& encdec);
     void testACKEncode (PacketEncoderDecoder& encdec);
	 void testKeyboardDecode (PacketEncoderDecoder& encdec);
	 void printArr(vector<char> b);
     short bytesToShort(vector<char> byteArr);
     vector<char> shortToBytes(short num);
     vector<char> popTwoFirstBytes(vector<char> dataToDecode);
     string popString(vector<char> bytes);

};


#endif //CLIENTDANA_ENCDECTEST_H