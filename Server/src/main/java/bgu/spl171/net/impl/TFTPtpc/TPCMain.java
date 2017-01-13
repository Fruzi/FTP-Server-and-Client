package bgu.spl171.net.impl.TFTPtpc;

import bgu.spl171.net.impl.TFTP.PacketEncoderDecoder;
import bgu.spl171.net.impl.TFTP.PacketProtocol;
import bgu.spl171.net.srv.Server;

/**
 * Created by Uzi the magnanimous, breaker of code and leader of IDES. He who has tamed the java beast and crossed the narrow C(++).
 on this, 1/12/2017 the day of reckoning.
 */
public class TPCMain {

    public static void main(String[] args) {
        //@TODO: Add a file database as a shared server object, like in NewsFeedServerMain,
        //@TODO: and send it to the protocol constructor
        Object fileDatabase = new Object(); //PLACEHOLDER

        Server.threadPerClient(Integer.parseInt(args[0]),
                () -> new PacketProtocol<>(fileDatabase),
                PacketEncoderDecoder::new)
                .serve();
    }
}
