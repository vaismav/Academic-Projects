package bgu.spl.net.api.bidi.Messages;

import bgu.spl.net.srv.Connections;
import bgu.spl.net.api.bidi.Message;
import bgu.spl.net.api.DataBase;

public class Stat extends Message {
    private String targetUsername;


    public Stat(String s) {
        super();
        targetUsername=s;
    }


    @Override
    public Message process(int connectionId, Connections connections, DataBase db) {
        if(!db.isLogedIn(connectionId) || !db.isRegistered(targetUsername))
            return new Error(8);
        int[] userStat=db.getStatOf(targetUsername);
        return new Ack(8,userStat[0],userStat[1],userStat[2]);
    }
}
