package bgu.spl.net.api;

import bgu.spl.net.api.bidi.Message;


import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;

public class DataBase {

    private LinkedList<String> registeredUsers=new LinkedList<>();
    private ConcurrentHashMap<String,User> userMapByName=new ConcurrentHashMap<>();
    private ConcurrentHashMap<Integer,User> loggedInUsersMap=new ConcurrentHashMap<>();
    private Object registerLock = new  Object();


    public boolean isRegistered(String name){
            if (userMapByName.get(name) != null)
                return true;
            return false;
    }

    public boolean register(String name,String password){
        if(userMapByName.containsKey(name))
            return false;
        synchronized (registerLock) {
            if(userMapByName.containsKey(name))
                return false;
            userMapByName.put(name, new User(name, password));
            registeredUsers.add(name);
            return true;
        }

    }

    public boolean login(String username, String password, int connectionId) {
        if(!isRegistered(username) || loggedInUsersMap.get(connectionId)!=null)
            return false;
        synchronized (userMapByName.get(username)){
            User user=userMapByName.get(username);
            if(!user.logedIn && user.password.equals(password) && loggedInUsersMap.get(connectionId)==null) {
                user.logedIn = true;
                user.connectionId=connectionId;
                loggedInUsersMap.put(connectionId,user);
                return true;
            }
        }
        return false;
    }

    public boolean isLogedIn(int connectionId) {
        User user=loggedInUsersMap.get(connectionId);
        if(user==null)
            return false;
        return true;

    }

    public void logout(int connectionId) {
        loggedInUsersMap.get(connectionId).logedIn=false;
        loggedInUsersMap.remove(connectionId);
    }

    public boolean isFirstFollowingSecond(int connectionId, String user) {
        return loggedInUsersMap.get(connectionId).following.contains(user);
    }

    public boolean isFirstFollowingSecond(String first, String second) {
        return userMapByName.get(first).following.contains(second);
    }

    /**
     * get the connectionId of the user who request the action
     * @param connectionId
     * @param userList
     * @return LinkList<String> of usernames of the successful followed users.
     */
    public LinkedList<String> follow(int connectionId, LinkedList<String> userList) {
        LinkedList<String> output=new LinkedList<>();
        for (String user:userList){
            if(isRegistered(user)){
                if(loggedInUsersMap.get(connectionId).following.add(user) &&
                        userMapByName.get(user).followers.add(loggedInUsersMap.get(connectionId).username))
                    output.add(user);
            }
        }
        return output;
    }

    /**
     * get the connectionId of the user who request the action
     * @param connectionId
     * @param userList
     * @return LinkList<String> of the successful unfollowed users.
     */
    public LinkedList<String> unFollow(int connectionId, LinkedList<String> userList) {
        LinkedList<String> output=new LinkedList<>();
        for (String user:userList){
            if(isRegistered(user)){
                if(loggedInUsersMap.get(connectionId).following.remove(user) &&
                userMapByName.get(user).followers.remove(loggedInUsersMap.get(connectionId).username))
                    output.add(user);
            }
        }
        return output;
    }

    public String getNameOfConnectionId(int connectionId) {
        return loggedInUsersMap.get(connectionId).username;
    }

    public LinkedList<String> getFollowers(int connectionId) {
        LinkedList<String> output = new LinkedList<>();
        for(String username: loggedInUsersMap.get(connectionId).followers)
            output.add(username);
        return output;
    }

    public void addToMessageQueue(String username, Message message) {
        userMapByName.get(username).messagesQueue.add(message);
    }

    public LinkedList<String> getListOfRegisteredUsers() {
        return registeredUsers;
    }

    public void addPost(int connectionId, Message post) {
        loggedInUsersMap.get(connectionId).posts.add(post);
    }

    /**
     * gives back the user stat
     * @param username
     * @return int[] of [NumOfPosts][NumOfFollowers][NumOfFollowing]
     */
    public int[] getStatOf(String username) {
        User user=userMapByName.get(username);
        int[] output={user.posts.size(),user.followers.size(),user.following.size()};
        return output;
    }

    public Message getNextWaitingMessage(int connectionId) {
        return loggedInUsersMap.get(connectionId).messagesQueue.poll();
    }

    public int isLogedIn(String username) {
        User user=userMapByName.get(username);
        if(user==null || user.logedIn==false)
            return -1;
        return user.connectionId;

    }

    public void addPM(int connectionId, String targetConnection, Message pm) {
        synchronized (loggedInUsersMap.get(connectionId).pm) {
            loggedInUsersMap.get(connectionId).pm.add(pm);
        }
        synchronized (userMapByName.get(targetConnection).pm) {
            userMapByName.get(targetConnection).pm.add(pm);
        }
    }


    private class User {
        protected String username;
        protected String password;
        protected boolean logedIn=false;
        protected int connectionId;
        protected ConcurrentSkipListSet<String> following=new ConcurrentSkipListSet<>();
        protected ConcurrentSkipListSet<String> followers=new ConcurrentSkipListSet<>();
        protected ConcurrentLinkedQueue<Message> messagesQueue = new ConcurrentLinkedQueue<>();
        protected LinkedList<Message> posts= new LinkedList<>();
        protected LinkedList<Message> pm= new LinkedList<>();

        public User(String name, String password) {
        username=name;
        this.password=password;
        }


    }
}
