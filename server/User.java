package Plato ;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class User implements Serializable {
    private String username;
    private String password ;
    private byte[] profilePic = null ;
    private long scores;
    private Room participatingRoom;
    private ArrayList<User> friends;
    private String bioText ;
    private HashMap<String , Integer> gamesList ;
    private ArrayList<Conversation> conversations ;

    public User(String username , String password) {
        this.password = password ;
        this.username = username;
        this.scores = 0 ;
        this.participatingRoom = null;
        this.friends = new ArrayList<>();
        this.conversations = new ArrayList<>();
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
}
