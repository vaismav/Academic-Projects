package bgu.spl.net.api.bidi.Messages;

import bgu.spl.net.srv.Connections;
import bgu.spl.net.api.bidi.Message;
import bgu.spl.net.api.DataBase;

public class Pm extends Message {
    private  String targetUsername;
    private  String content;

    public Pm(String first, String second) {
        super();
        targetUsername=first;
        content=second;
    }



    @Override
    public Message process(int connectionId, Connections connections, DataBase db) {
        if(!db.isRegistered(targetUsername) || !db.isLogedIn(connectionId))
            return new Error(6);

        int targetConnection=db.isLogedIn(targetUsername);
        if(targetConnection!=-1)
            connections.send(targetConnection,new Notification(0,db.getNameOfConnectionId(connectionId),content));
        else
            db.addToMessageQueue(targetUsername,new Notification(0,db.getNameOfConnectionId(connectionId),content));
        db.addPM(connectionId,targetUsername,this);
        return new Ack(6);
    }
}
