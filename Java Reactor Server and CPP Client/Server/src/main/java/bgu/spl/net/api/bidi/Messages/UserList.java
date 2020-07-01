package bgu.spl.net.api.bidi.Messages;

import bgu.spl.net.srv.Connections;
import bgu.spl.net.api.bidi.Message;
import bgu.spl.net.api.DataBase;

public class UserList extends Message {

    public UserList() {
        super();

    }


    @Override
    public Message process(int connectionId, Connections connections, DataBase db) {
        if(!db.isLogedIn(connectionId))
            return new Error(7);
        return new Ack(7,db.getListOfRegisteredUsers());
    }
}
