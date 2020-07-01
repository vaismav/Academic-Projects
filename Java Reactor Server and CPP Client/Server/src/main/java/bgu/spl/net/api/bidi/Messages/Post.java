package bgu.spl.net.api.bidi.Messages;

import bgu.spl.net.srv.Connections;
import bgu.spl.net.api.bidi.Message;
import bgu.spl.net.api.DataBase;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

public class Post extends Message {

    private String content;

    public Post(String s) {
        super();
        content=s;
    }


    @Override
    public Message process(int connectionId, Connections connections, DataBase db) {
        if(!db.isLogedIn(connectionId))
            return new Error(5);
        String userNameOfThisConnection = db.getNameOfConnectionId(connectionId);
        LinkedList<String> nonFollowingUsersList = new LinkedList<>();
        for(String name: getTaggedUsersInContent()) {
            if (db.isRegistered(name) && !db.isFirstFollowingSecond(name,userNameOfThisConnection))
                nonFollowingUsersList.add(name);
        }

        sendToListOfUsers(nonFollowingUsersList,connectionId,connections,db);
        sendToListOfUsers(db.getFollowers(connectionId),connectionId,connections,db);
        db.addPost(connectionId,this);

        return new Ack(5);
    }

    private void sendToListOfUsers(LinkedList<String> users, int connectionId, Connections connections, DataBase db) {
        for(String username:users){
            int targetConnection=db.isLogedIn(username);
            if(targetConnection!=-1)
                connections.send(targetConnection,new Notification(1,db.getNameOfConnectionId(connectionId),content));
            else
                db.addToMessageQueue(username,new Notification(1,db.getNameOfConnectionId(connectionId),content));
        }
    }


    private String[] getTaggedUsersInContent() {
        Set<String> output=new LinkedHashSet<>();
        int nextTag = content.indexOf('@',0);
        int endOfTag = 0;

        while(nextTag >= 0 && nextTag!=content.length()){
            endOfTag = content.indexOf(' ',nextTag);
            if(endOfTag >= 0){
                output.add(content.substring(nextTag+1,endOfTag));
            }else{
                if(!content.substring(nextTag+1).equals(" "))
                    output.add(content.substring(nextTag+1));
            }
            if(nextTag+1 >= content.length())
                nextTag=-1;
            else
                nextTag = content.indexOf('@',nextTag+1);


        }
        return output.toArray(new String[output.size()]);
    }


}
