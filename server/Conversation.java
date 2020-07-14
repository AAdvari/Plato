package Plato.server;

import java.util.ArrayList;

public class Conversation {
    // Recall That The Order Of Messages maintains in ArrayList !
    private ArrayList<Message> messages ;

    public Conversation() {
        messages = new ArrayList<>() ;
    }
    // private ArrayList<Game ? > gamesPlayed ;

    public synchronized void sendMessage(Message content){
        messages.add(content) ;
    }


    /// just for test


    public ArrayList<Message> getMessages() {
        return messages;
    }
}
