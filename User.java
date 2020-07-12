package Plato ;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
    private String username;
    private String password ;
    private long scores;
    private Room participatingRoom;
    private ArrayList<User> friends;
    private ArrayList<Conversation> conversations ;

    public User(String username , String password) {
        this.password = password ;
        this.username = username;
        this.scores = 0 ;
        this.participatingRoom = null;
        this.friends = new ArrayList<>();
        this.conversations = new ArrayList<>();
    }

    public String getPassword() {
        return password;
    }
}
