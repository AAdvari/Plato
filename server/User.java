package Plato.server ;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class User implements Serializable {
    private String username;
    private String password ;
    private byte[] profilePic = null ;
    private Room participatingRoom;
    private ArrayList<User> friends;
    private String bioText ;
    private HashMap<String , Integer> gamesList ; // Mapping games to their scores !
    private HashMap<User , Conversation> conversations ;

    public User(String username , String password) {
        this.password = password ;
        this.username = username;
        this.participatingRoom = null;
        this.friends = new ArrayList<>();
        this.conversations = new HashMap<>();
    }
    public void setProfilePic(byte[] profilePic){
        this.profilePic = profilePic ;
    }

    public String getBioText() {
        return bioText;
    }

    public void setBioText(String bioText) {
        this.bioText = bioText;
    }

    public byte[] getProfilePic() {
        return profilePic;
    }

    public String getPassword() {
        return password;
    }

    public Conversation getConversation(User destUser){
        return conversations.get(destUser) ;
    }
    public synchronized void addConversation(User destUser){
        conversations.put(destUser , new Conversation()) ;
    }



    /// Just for Test
    public HashMap<User, Conversation> getConversations() {
        return conversations;
    }
}
