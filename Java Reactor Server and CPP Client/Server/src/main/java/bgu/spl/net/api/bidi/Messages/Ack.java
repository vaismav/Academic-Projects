package bgu.spl.net.api.bidi.Messages;

import bgu.spl.net.srv.Connections;
import bgu.spl.net.api.bidi.Message;
import bgu.spl.net.api.DataBase;

import java.util.LinkedList;

public class Ack extends Message {
    private boolean shouldLogout=false;
    private Object[] ackData=new Object[5];

    public Ack(int op) {
        super();
        ackData[0]=(short)10;
        ackData[1]=(short)op;
    }

    public Ack(int op, boolean shouldLogout) {
        super();
        ackData[0]=(short)10;
        ackData[1]=(short)op;
        this.shouldLogout=shouldLogout;
    }

    public Ack(int op, LinkedList<String> listOfStrings) {
        super();
        ackData[0]=(short)10;
        ackData[1]=(short)op;
        ackData[2]=(short)listOfStrings.size();
        ackData[3]=listOfStrings;
    }

    public Ack(int op, int numPost, int numFollowers, int numFollowing) {
        super();
        ackData[0]=(short)10;
        ackData[1]=(short)op;
        ackData[2]=(short)numPost;
        ackData[3]=(short)numFollowers;
        ackData[4]=(short)numFollowing;
    }

    @Override
    public Message process(int connectionId, Connections connections, DataBase db) {
        return null;
    }

    @Override
    public boolean shouldLogout() {
        return shouldLogout;
    }

    @Override
    public Object[] getMessageObjects() {
        return ackData;
    }
}
