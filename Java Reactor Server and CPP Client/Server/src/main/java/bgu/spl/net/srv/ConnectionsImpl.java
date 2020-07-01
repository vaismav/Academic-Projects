package bgu.spl.net.srv;



import java.util.concurrent.ConcurrentHashMap;


public class ConnectionsImpl<T> implements Connections<T> {
    private ConcurrentHashMap<ConnectionHandler, Integer> mapByHandler=new ConcurrentHashMap<>();
    private ConcurrentHashMap<Integer, ConnectionHandler> mapByConnectionId=new ConcurrentHashMap<>();
    private int nextId=0;
    private Object broadcastLock=new Object();
    private Object connectionIdLock=new Object();




    public <T> int connect(ConnectionHandler<T> handler) {
        int output;
        synchronized (connectionIdLock) {
            output = nextId;
            mapByConnectionId.put(nextId, handler);
            mapByHandler.put(handler, nextId);
            nextId=nextId+1;
        }
       return output;
    }


    @Override
    public boolean send(int connectionId, T msg) {

        synchronized (mapByConnectionId.get(connectionId)) {    //preventing the handler from disconnecting while sending event
            ConnectionHandler<T> handler=mapByConnectionId.get(connectionId);
            if (handler!=null) {
                handler.send(msg);
                return true;
            }
        }
        return false;
    }

    @Override
    public void broadcast(T msg) {
        synchronized (broadcastLock){    //So no one will disconnect while iterating
            for (ConnectionHandler handler: mapByHandler.keySet()) {
                handler.send(msg);
            }
        }
    }

    @Override
    public void disconnect(int connectionId) {
        synchronized (broadcastLock) {
            ConnectionHandler handler = mapByConnectionId.get(connectionId);
            if (handler != null) {
                synchronized (mapByConnectionId.get(connectionId)) {    //Lock the handler of this clinet to prevent sending message to disconectted client
                    Integer id=mapByHandler.get(handler);
                    mapByHandler.remove(handler);
                    mapByConnectionId.remove(id);
                }
            }
        }

    }

}
