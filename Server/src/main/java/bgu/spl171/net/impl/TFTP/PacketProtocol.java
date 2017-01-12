package bgu.spl171.net.impl.TFTP;

import bgu.spl171.net.api.bidi.BidiMessagingProtocol;
import bgu.spl171.net.api.bidi.Connections;

/**
 * Created by Uzi the magnanimous, breaker of code and loader of IDEs. He who has tamed the java beast and crossed the narrow C(++).
 * on this, 1/12/2017 the day of reckoning.
 */
public class PacketProtocol<Packet> implements BidiMessagingProtocol<Packet> {
    private boolean terminateMe;
    private int ownerID;
    private Connections<Packet> connections;

    public PacketProtocol() {
        terminateMe = false;
    }

    @Override
    public void start(int connectionId, Connections<Packet> connections) {
        ownerID = connectionId;
        this.connections = connections;
    }

    @Override
    public void process(Packet msg) {
    //TODO create a T msg and end func with send/broadcast for handler
    }

    @Override
    public boolean shouldTerminate() {
        return terminateMe;
    }
}
