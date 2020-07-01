package bgu.spl.net.api.bidi;

import bgu.spl.net.srv.Connections;
import bgu.spl.net.api.DataBase;

public class BidiMessagingProtocolImpl implements BidiMessagingProtocol<Message> {
    private DataBase db;
    private int connectionId;
    private Connections connections;
    private boolean shouldTerminate=false;

    public BidiMessagingProtocolImpl(DataBase base){
        db=base;
    }


    @Override
    public void start(int connectionId, Connections connections) {
        this.connectionId=connectionId;
        this.connections=connections;
    }


    @Override
    public void process(Message message) {
        if(message.shouldLogout()) {
            Message ackOfLogout = message.process(connectionId, connections, db);
            connections.send(connectionId,ackOfLogout);
            if(ackOfLogout.shouldLogout()) {
                connections.disconnect(connectionId);
                db.logout(connectionId);
                shouldTerminate=true;
            }
        }
        else
            connections.send(connectionId,message.process(connectionId, connections, db));
        sendWaitingMessages();

    }

    private void sendWaitingMessages() {
        if(db.isLogedIn(connectionId)){
            Message nextMessage=db.getNextWaitingMessage(connectionId);
            while(nextMessage!=null){
                connections.send(connectionId,nextMessage);
                nextMessage=db.getNextWaitingMessage(connectionId);
            }
        }
    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }
}
