//
// Created by alonam on 1/15/17.
//

#include "../include/EncdecTest.h"



int main (int argc, char *argv[]) {



    /* * * * * * * * * * * * * * * * * * * * * * * * * * *
                   TESTING THE ENCODER DECODER
                   /* * * * * * * * * * * * * * * * * * * * * * * * * * * */

    // Instructions: 1. Change the names I used for your names.
    //               2. Import the thing you need
    //               3. Remove the "//" from the packet you want to test, and run it.
    //                  You can activate decode and then encode in order to see that you receive the same output as you started.
    //               *. Some of the tests are not relevant - You need to encode just: data, ack, bcast, and error.
    PacketEncoderDecoder encdec;
    EncdecTest test;
  test.testDataDecode(encdec); // 3
	test.testDataEncode(encdec); // 3
  test.testACKDecode(encdec); // 4
 test.testACKEncode(encdec); // 4
  test.testErrorDecode(encdec); // 5
	test.testErrorEncode(encdec); // 5
 	test.testBCastDecode(encdec); // 9
	test.testDISCEncode(encdec);
	test.testKeyboardDecode(encdec);
}


char* EncdecTest::vecToArr(vector<char>& v){
    // Get a char pointer to the data in the vector
    char* buf = &v[0]; // For example , vec[1011,.... , 9101], so buf -> 1011 (Later, i'll send it with the size, so it will know when to finish)
    return buf;
}


vector<char> EncdecTest::arrToVec(char* c){
    int size=sizeof(c);
    vector<char> v (c, c+size);
    return v;
}


//
//void EncdecTest::testDataDecode (PacketEncoderDecoder& encdec){
//    vector<char> b = {0,3,0,2,1,5,1,2}; // 0,2 is the packetSize(2), 1,5 is the blockNum(261)
//    char* x = (char*)03021512;
//    //cout << b << endl;
//    cout << b.size() << endl;
//    cout << "Vector info " << endl;
//    cout << b[6] << endl;
//
//    // bytesToShort({0,5})=(short)5, bytesToShort({1,5})=(short)261
//    Packet* res=nullptr;
//    cout<< "Before decoding, the Arr is"<< endl;
//    printArr(b);
//    cout << "FLAG DataDecode 1" << endl;
//    for (auto i=0; i<b.size(); i++) {
//        char tmp = b.at(i);
//        cout << tmp << " Is the fucking nextByte " << endl;
//        res = encdec.decodeNextByte(tmp);
//    }
//    cout << "FLAG DataDecode 2" << endl;
//    DATAPacket *res1= dynamic_cast<DATAPacket*>(res);
//    cout << "FLAG DataDecode 3" << endl;
//    short opcode=res1->getOpcode();
//    short packetSize=res1->getPacketSize();
//    short blockNum=res1->getBlockNum();
//    vector<char> dataBytes=res1->getData();
//    cout << "FLAG DataDecode 4" << endl;
//    cout<< "After decoding the arr, we've got a packet!"<<endl;
//    cout<<"The opcode is " << opcode << " The packetSize is " << packetSize <<"  and the blockNum is " << blockNum<<endl;
//    cout<<"The data is "<<endl;
//    printArr(dataBytes);
//}

void EncdecTest::testKeyboardDecode(PacketEncoderDecoder& encdec) {
	cout << "testKeyboardDecode (DELRQ)" << endl;
	string input;
	std::getline(std::cin, input);
	Packet* res = encdec.decodeFromKeyboardInput(input);
	if (res == nullptr || res->getOpcode() != 8) {
		cout << "Fail" << endl;
		return;
	}
	cout << dynamic_cast<DELRQPacket*>(res)->getFileName() << endl;


}

void EncdecTest::testDataDecode (PacketEncoderDecoder& encdec){
    vector<char> b = {0,3,0,5,1,5,1,2,3,4,5}; // 0,5 is the packetSize(5), 1,5 is the blockNum(261)
    // bytesToShort({0,5})=(short)5, bytesToShort({1,5})=(short)261
    Packet* res=nullptr;
    cout<< "Before decoding, the Arr is"<< endl;
    printArr(b);
    cout << "FLAG DataDecode 1" << endl;
    for (auto i=0; i<b.size(); i++)
        res=encdec.decodeNextByte(b[i]);
    cout << "FLAG DataDecode 2" << endl;
    DATAPacket *res1= dynamic_cast<DATAPacket*>(res);
    cout << "FLAG DataDecode 3" << endl;
    short opcode=res1->getOpcode();
    short packetSize=res1->getPacketSize();
    short blockNum=res1->getBlockNum();
    vector<char> dataBytes=res1->getData();
    cout << "FLAG DataDecode 4" << endl;
    cout<< "After decoding the arr, we've got a packet!"<<endl;
    cout<<"The opcode is " << opcode << " The packetSize is " << packetSize <<"  and the blockNum is " << blockNum<<endl;
    //cout<<"The data is "<<endl;
    //printArr(dataBytes);
	cout << (dataBytes == vector<char>({1, 2, 3, 4, 5})) << endl;

	delete res;
}

void EncdecTest::testDataEncode (PacketEncoderDecoder& encdec){
    vector<char> b = {1,2,3,4,5};
    DATAPacket packet(5, 261, b);
    vector<char> res = encdec.encode(packet);
    cout<<"Encoding the packet " << packet.getOpcode() << " is the Opcode "<< packet.getPacketSize() << " is the packetSize " << packet.getBlockNum() << " is the Block Num " <<endl;
    //cout<<"The data arr is " <<endl;
    //printArr(b);
    //cout<<"Output: "<<endl;

    //printArr(*res); // Should be {0,3,0,5,1,5,1,2,3,4,5}
    cout<<"The output should be {0,3,0,5,1,5,1,2,3,4,5}"<<endl;
	if (res == vector<char>({0, 3, 0, 5, 1, 5, 1, 2, 3, 4, 5 }))
		cout << "success!" << endl;
}

void EncdecTest::testDISCEncode (PacketEncoderDecoder& encdec){
	DISCPacket packet;
    vector<char> res = encdec.encode(packet);
    cout<<"Encoding the packet " << packet.getOpcode() << " is the Opcode"<<endl;
    //cout<<"Output: "<<endl;

    //printArr(*res); // Should be {0,10}
    cout<<"The output should be {0,10}"<<endl;
	if (res == vector<char>({ 0,10 }))
		cout << "success!" << endl;

}

void EncdecTest::testBCastDecode (PacketEncoderDecoder& encdec){
    vector<char> b = {0,9,1,66,67,97,115,116,83,116,114,0};
    // popString({66,67,97,115,116,83,116,114})=(String)"BCastStr"
    Packet* res=nullptr;
    //cout<<"Before decoding, the Arr is"<<endl;
    //printArr(b);
    for (auto i=0; i<b.size(); i++)
        res=encdec.decodeNextByte(b[i]);
    BCASTPacket *res1= dynamic_cast<BCASTPacket*>(res);
    short opcode=res1->getOpcode();
    short deleted_or_added=res1->getDelOrAdd();
    string Filename=res1->getFileName();
    cout<<"After decoding the arr, we've got a packet!"<<endl;
    cout<<"The opcode is " << opcode << " the deleted_or_added is " << deleted_or_added <<"  and the Filename is " << Filename<<endl;

}

void EncdecTest::testBCastEncode (PacketEncoderDecoder& encdec){
	BCASTPacket packet(1, "BCastStr");
    vector<char> res = encdec.encode(packet);
    cout<<"Encoding the packet " << packet.getOpcode() << " is the Opcode " << packet.getDelOrAdd() << " is the deleted_or_added code " << packet.getFileName()<<endl;
    //cout<<"Output: "<<endl;

    //printArr(*res); // Should be {0,9,1,66,67,97,115,116,83,116,114,0}
	if (res == vector<char>({ 0,9,1,66,67,97,115,116,83,116,114,0 }))
		cout << "success!" << endl;

}

void EncdecTest::testDELRQEncode (PacketEncoderDecoder& encdec){
    DELRQPacket packet("Dana");
    vector<char> res = encdec.encode(packet);
    cout<<"Encoding the packet " << packet.getOpcode() << " Opcode " << packet.getFileName()<<endl;
    cout<<"Output: "<<endl;

    //printArr(res); // Should be {0,8,68,97,110,97,0}
    cout<<"The output should be {0,8,68,97,110,97,0}"<<endl;

}

void EncdecTest::testLOGRQEncode (PacketEncoderDecoder& encdec){
    LOGRQPacket packet("Dana");
    vector<char> res = encdec.encode(packet);
    cout<<"Encoding the packet " << packet.getOpcode() << " Opcode " << packet.getUserName()<<endl;
    cout<<"Output: "<<endl;

    //printArr(res); // Should be {0,7,68,97,110,97,0}
    cout<<"The output should be {0,7,68,97,110,97,0}"<<endl;


}

void EncdecTest::testDIRQEncode (PacketEncoderDecoder& encdec){
    DIRQPacket packet;
    vector<char> res = encdec.encode(packet);
    cout<<"Encoding the packet " << packet.getOpcode() << " is the Opcode"<<endl;
    cout<<"Output: "<<endl;

   // printArr(res); // Should be {0,6}
    cout<<"The output should be {0,6}"<<endl;

}


void EncdecTest::testErrorDecode (PacketEncoderDecoder& encdec){
    vector<char> b = {0,5,14,20 ,69,114,114,111,114,32,75,97,112,97,114,97 ,0};
    // bytesToShort({14,20})=(short)3604, and popString({69,114,114,111,114,32,75,97,112,97,114,97})=(String)"Error Kapara"
    Packet* res=nullptr;
    //cout<<"Before decoding, the Arr is"<<endl;
    //printArr(b);
    for (auto i=0; i<b.size(); i++)
        res=encdec.decodeNextByte(b[i]);
    ERRORPacket *res1= dynamic_cast<ERRORPacket*>(res);
    short opcode=res1->getOpcode();
    short errorCode=res1->getErrorCode();
    string errorMsg=res1->getErrorMessage();
    cout<<"After decoding the arr, we've got a packet!"<<endl;
    cout<<"The opcode is " << opcode << " The Error code is " << errorCode <<"  and the error messege is " << errorMsg<<endl;

}

void EncdecTest::testErrorEncode (PacketEncoderDecoder& encdec){
    cout << "I'm at the Error encode" << endl;
	ERRORPacket packet(3604, "Error Kapara");
    vector<char> res = encdec.encode(packet);
    cout<<"Encoding the packet " << packet.getOpcode() << " is the Opcode " << packet.getErrorCode() <<" is the error code " << packet.getErrorMessage()<<endl;
    //cout<<"Output: "<<endl;

    //printArr(*res); // Should be {0,5,14,20 ,69,114 ,114,111,114,32,75,97,112,97,114,97 ,0}
    cout<<"The output should be {0,5,14,20,69,114,114,111,114,32,75,97,112,97,114,97,0}"<<endl;
	if (res == vector<char>({ 0,5,14,20,69,114,114,111,114,32,75,97,112,97,114,97,0 }))
		cout << "success!" << endl;

}

void EncdecTest::testRRQEncode (PacketEncoderDecoder& encdec){
    RRQPacket packet("Dana");
    vector<char> res = encdec.encode(packet);
    cout<<"Encoding the packet " << packet.getOpcode() << " Opcode " << packet.getFileName()<<endl;
    cout<<"Output: "<<endl;

    //printArr(res); // Should be {0,1,68,97,110,97,0}
    cout<<"The output should be {0,1,68,97,110,97,0}"<<endl;

}

void EncdecTest::testWRQEncode (PacketEncoderDecoder& encdec){
    WRQPacket packet("Dana");
    vector<char> res = encdec.encode(packet);
    cout<<"Encoding the packet " << packet.getOpcode() << " Opcode " << packet.getFileName()<<endl;
    cout<<"Output: "<<endl;

    //printArr(res); // Should be {0,2,68,97,110,97,0}
    cout<<"The output should be {0,2,68,97,110,97,0}"<<endl;

}

void EncdecTest::testACKDecode (PacketEncoderDecoder& encdec){
    vector<char> b = {0,4,14,20}; // bytesToShort({14,20})=(short)3604
    Packet* res=nullptr;
    //cout<<"Before decoding, the Arr is"<<endl;
    //printArr(b);
    for (auto i=0; i<b.size(); i++)
        res=encdec.decodeNextByte(b[i]);
	ACKPacket *res1= dynamic_cast<ACKPacket*>(res);
    short opcode=res1->getOpcode();
    short blockNum=res1->getBlockNum();
    cout<<"After decoding the arr, we've got a packet!"<<endl;
    cout<<"The opcode is " << opcode <<" and the blockNum is " << blockNum<<endl;

}

void EncdecTest::testACKEncode (PacketEncoderDecoder& encdec){
	ACKPacket packet(3604); // bytesToShort({14,20})=(short)3604
    vector<char> res = encdec.encode(packet);
    cout<<"Encoding the packet " << packet.getOpcode() << " Opcode " << packet.getBlockNum()<<endl;
    //cout<<"Output: "<<endl;

    //printArr(*res); // Should be {0,2,68,97,110,97,0}
    cout<<"The output should be {0,4,14,20}"<<endl;
	if (res == vector<char>({0, 4, 14, 20}))
		cout << "success!" << endl;

}


void EncdecTest::printArr(vector<char> b){
    //	System.out.print("Output: ");
    string s(b.begin(), b.end());
   
    cout << s <<endl;
}


short EncdecTest::bytesToShort(vector<char> byteArr)
{
    short result = (short)((byteArr[0] & 0xff) << 8);
    result += (short)(byteArr[1] & 0xff);
    return result;
}

vector<char> EncdecTest::shortToBytes(short num)
{
    vector<char> bytesArr;
    bytesArr.push_back( ((num >> 8) & 0xFF) );
    bytesArr.push_back(  (num & 0xFF)  );
    return bytesArr;

}

vector<char> EncdecTest::popTwoFirstBytes(vector<char> dataToDecode){
    dataToDecode.erase(dataToDecode.begin());
    dataToDecode.erase(dataToDecode.begin());
    return dataToDecode;
}


string EncdecTest::popString(vector<char> bytes) {

    std::string s(bytes.begin(), bytes.end());
    return s;
    //notice that we explicitly requesting that the string will be decoded from UTF-8
    //this is not actually required as it is the default encoding in java.

}

