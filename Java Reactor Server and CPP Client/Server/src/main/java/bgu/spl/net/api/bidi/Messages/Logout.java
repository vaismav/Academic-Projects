package bgu.spl.net.api.bidi.Messages;

import bgu.spl.net.srv.Connections;
import bgu.spl.net.api.bidi.Message;
import bgu.spl.net.api.DataBase;

public class Logout extends Message {

    public Logout() {
        super();
    }


    @Override
    public Message process(int connectionId, Connections connections, DataBase db) {
        if(db.isLogedIn(connectionId))
            return new Ack(3,true);
        return new Error(3);
    }

    @Override
    public boolean shouldLogout() {
        return true;
    }
}
