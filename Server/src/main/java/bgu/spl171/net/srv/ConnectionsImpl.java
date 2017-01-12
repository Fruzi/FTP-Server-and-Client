package bgu.spl171.net.srv;

import bgu.spl171.net.api.bidi.Connections;
import bgu.spl171.net.srv.bidi.ConnectionHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Uzi the Magnanimous, breaker of code and loader of IDEs. He who has tamed the java beast and crossed the narrow C(++).
 * on this day, 1/12/2017, as was foretold.
 */
public class ConnectionsImpl<T> implements Connections<T> {

    private Map<Integer, ConnectionHandler<T>> connectionMap;

    @Override
    public boolean send(int connectionId, T msg) {
        ConnectionHandler<T> handler = connectionMap.get(connectionId);
        if (handler == null)
            return false;
        handler.send(msg);
        return true;
    }

    @Override
    public void broadcast(T msg) {
        for (ConnectionHandler<T> handler : connectionMap.values()) {
            handler.send(msg);
        }
    }

    @Override
    public void disconnect(int connectionId) {
        ConnectionHandler<T> handler = connectionMap.get(connectionId);
        if (handler != null) {
            try {
                handler.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            connectionMap.remove(connectionId);
        }
    }

    public void add(int connectionId, ConnectionHandler<T> handler) {
        if (connectionMap == null) {
            connectionMap = new HashMap<>();
        }
        connectionMap.put(connectionId, handler);
    }
}
