package bgu.spl.net.api.bidi.Messages;

import bgu.spl.net.srv.Connections;
import bgu.spl.net.api.bidi.Message;
import bgu.spl.net.api.DataBase;

public class Login extends Message {
    private String username;
    private String password;


    public Login(String first, String second) {
        username=first;
        password=second;
    }


    @Override
    public Message process(int connectionId, Connections connections, DataBase db) {
        if(db.login(username,password,connectionId))
            return new Ack(2);
        return new Error(2);
    }

}
