package com.plato.server;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class User implements Serializable {
    private static final long serialVersionUID = -123L;
    private String username;
    private String password;
    private volatile byte[] profilePic = null;
    private volatile ArrayList<User> friends;
    private volatile String bioText;
    private volatile ConcurrentHashMap<String, Integer> gamesList; // Mapping games to their scores !
    private volatile ConcurrentHashMap<User, Conversation> conversations;
    private volatile ArrayList<String> friendRequests;  // String are  usernames (senders)...

    public User(String username, String password) {
        this.password = password;
        this.username = username;
        this.friends = new ArrayList<>();


        this.gamesList = new ConcurrentHashMap<>();
        gamesList.put("xo", 0);
        gamesList.put("guessWord", 0);
        gamesList.put("dotsGame", 0);


        this.conversations = new ConcurrentHashMap<>();
        this.friendRequests = new ArrayList<>();
    }

    public synchronized void setProfilePic(byte[] profilePic) {
        this.profilePic = profilePic;
    }

    public String getBioText() {
        return bioText;
    }

    public synchronized void setBioText(String bioText) {
        this.bioText = bioText;
    }

    public byte[] getProfilePic() {
        return profilePic;
    }

    public synchronized void addFriendRequest(String username) {
        friendRequests.add(username);

    }

    public synchronized void removeFriendRequest(String username) {
        friendRequests.remove(username);
    }

    public synchronized void addFriend(User user) {
        friends.add(user);
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public synchronized void addWinScoreToGame(String game) {

        gamesList.put(game, gamesList.get(game) + 100);
    }

    public synchronized void addDrawScoreToGame(String game) {

        gamesList.put(game, gamesList.get(game) + 20);
    }

    public synchronized void addScoreToGame(int bonus, String game) {
        gamesList.put(game, gamesList.get(game) + bonus);
    }


    public int getGameScore(String game) {
        return gamesList.get(game);
    }

    public Conversation getConversation(User destUser) {
        if (conversations.contains(destUser)) {
            return conversations.get(destUser);
        } else
            return null;
    }

    public synchronized void addConversation(User destUser, Conversation conversation) {
        conversations.put(destUser, conversation);
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /// Just for Test
    public ConcurrentHashMap<User, Conversation>
    getConversations() {
        return conversations;
    }


//    @Override
//   logi public String toString() {
//        return "User{" +
//                "username='" + username + '\'' +
//                ", password='" + password + '\'' +
//                ", friends=" + friends +
//                ", bioText='" + bioText + '\'' +
//                ", gamesList=" + gamesList +
//                ", conversations=" + conversations +
//                ", friendRequests=" + friendRequests +
//                '}';
//    }
}
