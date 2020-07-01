package bgu.spl.net.api.bidi.Messages;

import bgu.spl.net.srv.Connections;
import bgu.spl.net.api.bidi.Message;
import bgu.spl.net.api.DataBase;

public class Notification extends Message {
    private Object[] data=new Object[4];




    public Notification(int i, String nameOfConnectionId, String content) {
        super();
        data[0]=(short)9;
        data[1]=(short)i;
        data[2]=nameOfConnectionId;
        data[3]=content;
    }


    @Override
    public Message process(int connectionId, Connections connections, DataBase db) {
        return null;
    }

    @Override
    public Object[] getMessageObjects(){return data;}
}
