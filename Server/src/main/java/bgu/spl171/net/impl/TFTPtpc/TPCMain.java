package bgu.spl171.net.impl.TFTPtpc;

import bgu.spl171.net.impl.TFTP.PacketEncoderDecoder;
import bgu.spl171.net.impl.TFTP.PacketProtocol;
import bgu.spl171.net.srv.Server;

public class TPCMain {

    public static void main(String[] args) {

        Server.threadPerClient(Integer.parseInt(args[0]),
                PacketProtocol::new,
                PacketEncoderDecoder::new)
                .serve();
    }
}
