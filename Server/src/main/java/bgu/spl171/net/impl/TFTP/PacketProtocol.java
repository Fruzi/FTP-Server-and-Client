package bgu.spl171.net.impl.TFTP;

import bgu.spl171.net.api.bidi.BidiMessagingProtocol;
import bgu.spl171.net.api.bidi.Connections;
import bgu.spl171.net.impl.packets.Packet;

/**
 * Created by Uzi the magnanimous, breaker of code and loader of IDEs. He who has tamed the java beast and crossed the narrow C(++).
 * on this, 1/12/2017 the day of reckoning.
 */
public class PacketProtocol<T> implements BidiMessagingProtocol<Packet> {
    private final T fileDatabase;

    private boolean terminateMe;
    private int ownerID;
    private Connections<Packet> connections;

    //"T arg" is the file database
    public PacketProtocol(T fileDatabase) {
        this.fileDatabase = fileDatabase;
        terminateMe = false;
    }

    @Override
    public void start(int connectionId, Connections<Packet> connections) {
        ownerID = connectionId;
        this.connections = connections;
    }

    //Use the file database here (like in NewsFeed)
    @Override
    public void process(Packet msg) {
    //TODO create a T msg and end func with send/broadcast for connections
    }

    @Override
    public boolean shouldTerminate() {
        return terminateMe;
    }
}
