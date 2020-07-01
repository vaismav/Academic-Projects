package bgu.spl.net.api.bidi.Messages;


import bgu.spl.net.srv.Connections;
import bgu.spl.net.api.bidi.Message;
import bgu.spl.net.api.DataBase;


public class Register extends Message {


    private String username;
    private String password;


    public Register(String first, String second) {
        super();
        username=first;
        password=second;
    }


    @Override
    public Message process(int connectionId, Connections connections, DataBase db) {
        if (!db.isRegistered(username)) {
            if (db.register(username, password)) {
                return new Ack(1);
            }
        }
        return new Error(1);
    }

}
