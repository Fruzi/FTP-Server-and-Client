package bgu.spl171.net.impl.TFTPreactor;

import bgu.spl171.net.impl.TFTP.PacketEncoderDecoder;
import bgu.spl171.net.impl.TFTP.PacketProtocol;
import bgu.spl171.net.srv.Server;

public class ReactorMain {

    public static void main(String[] args) {

        Server.reactor(Runtime.getRuntime().availableProcessors(),
                Integer.parseInt(args[0]),
                PacketProtocol::new,
                PacketEncoderDecoder::new)
                .serve();
    }
}
