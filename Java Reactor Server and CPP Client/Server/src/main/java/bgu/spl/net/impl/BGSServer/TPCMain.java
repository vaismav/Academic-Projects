package bgu.spl.net.impl.BGSServer;


import bgu.spl.net.api.DataBase;
import bgu.spl.net.api.EncoderDecoderImpl;
import bgu.spl.net.api.bidi.BidiMessagingProtocolImpl;
import bgu.spl.net.srv.Server;

public class TPCMain {
    public static void main(String[] args) {
        DataBase db = new DataBase(); //one shared object
        try {
            int port = Integer.parseInt(args[0]);

        Server.threadPerClient(
                port, //port
                () -> new BidiMessagingProtocolImpl(db), //protocol factory
                EncoderDecoderImpl::new //message encoder decoder factory
        ).serve();
        } catch (Exception e) {
            e.printStackTrace();
    }
    }

}
