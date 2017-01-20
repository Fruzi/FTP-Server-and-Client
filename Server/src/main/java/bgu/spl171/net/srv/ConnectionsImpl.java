package bgu.spl171.net.srv;

import bgu.spl171.net.api.bidi.Connections;
import bgu.spl171.net.srv.bidi.ConnectionHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of the Connections interface, used to keep track of all currently connected clients.
 * It is used to send data to clients via their ConnectionHandlers and disconnect clients.
 * @param <T> the type of data used for communication in the TCP/IP protocol.
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

    /**
     * Adds a newly-connected client to this server's collection of clients.
     * @param connectionId The ID of the new client
     * @param handler The client's ConnectionHandle instance
     */
    public void add(int connectionId, ConnectionHandler<T> handler) {
        if (connectionMap == null) {
            connectionMap = new ConcurrentHashMap<>();
        }
        connectionMap.put(connectionId, handler);
    }
}
