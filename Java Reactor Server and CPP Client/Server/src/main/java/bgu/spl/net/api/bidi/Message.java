package bgu.spl.net.api.bidi;

import bgu.spl.net.srv.Connections;
import bgu.spl.net.api.DataBase;

public abstract class Message {

    public abstract Message process(int connectionId, Connections connections, DataBase db);

    public  boolean shouldLogout(){
        return false;
    }

    public Object[] getMessageObjects(){return null;}

}
