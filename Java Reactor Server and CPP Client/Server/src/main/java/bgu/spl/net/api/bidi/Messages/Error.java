package bgu.spl.net.api.bidi.Messages;

import bgu.spl.net.srv.Connections;
import bgu.spl.net.api.bidi.Message;
import bgu.spl.net.api.DataBase;

public class Error extends Message {

    private Object[] data=new Object[2];

    public Error(int op) {
        super();
        data[0]=(short)11;
        data[1]=(short)op;

    }


    @Override
    public Message process(int connectionId, Connections connections, DataBase db) {
        return null;
    }


    @Override
    public Object[] getMessageObjects() {
        return data;
    }
}
