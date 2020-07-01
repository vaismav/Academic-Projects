package bgu.spl.net.api.bidi.Messages;

import bgu.spl.net.srv.Connections;
import bgu.spl.net.api.bidi.Message;
import bgu.spl.net.api.DataBase;

import java.util.LinkedList;

public class Follow extends Message {

    private String[] userList;
    private int numOfUsers;
    private boolean follow;

    public Follow(boolean follow, int numOfUsers, String[] userList) {
        super();
        this.follow=follow;
        this.numOfUsers=numOfUsers;
        this.userList=userList;
    }


    @Override
    public Message process(int connectionId, Connections connections, DataBase db) {
        if(!db.isLogedIn(connectionId))
            return new Error(4);
        LinkedList<String> validUsers=new LinkedList<>();
        LinkedList<String> successfulUpdated;
        if (follow) {
            for (String user:userList){
                if(!db.isFirstFollowingSecond(connectionId,user))
                    validUsers.add(user);
            }
            successfulUpdated=db.follow(connectionId,validUsers);
        }else{
            for (String user:userList){
                if(db.isFirstFollowingSecond(connectionId,user))
                    validUsers.add(user);
            }
            successfulUpdated=db.unFollow(connectionId,validUsers);
        }
        if(successfulUpdated.isEmpty())
            return new Error(4);

        return new Ack(4,successfulUpdated);

    }


}
