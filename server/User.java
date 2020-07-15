package Plato.server ;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class User implements Serializable {
    private String username;
    private String password ;
    private volatile byte[] profilePic = null ;
    private volatile Room participatingRoom;
    private volatile ArrayList<User> friends;
    private volatile String bioText ;
    private volatile HashMap<String , Integer> gamesList ; // Mapping games to their scores !
    private volatile HashMap<User , Conversation> conversations ;

    public User(String username , String password) {
        this.password = password ;
        this.username = username;
        this.participatingRoom = null;
        this.friends = new ArrayList<>();
        this.conversations = new HashMap<>();
    }
    public synchronized void setProfilePic(byte[] profilePic){
        this.profilePic = profilePic ;
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

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public synchronized void addScoreToGame(String game){
        gamesList.put(game , gamesList.get(game) + 100 ) ;
    }

    public int getGameScore(String game){
        return gamesList.get(game) ;
    }
    public Conversation getConversation(User destUser){
        return conversations.get(destUser) ;
    }
    public synchronized void addConversation(User destUser , Conversation conversation){
        conversations.put(destUser , conversation) ;
    }



    /// Just for Test
    public HashMap<User, Conversation> getConversations() {
        return conversations;
    }
}
