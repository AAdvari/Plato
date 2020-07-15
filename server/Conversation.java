package Plato.server;

import java.util.ArrayList;

public class Conversation {
    // Recall That The Order Of Messages maintains in ArrayList !
    private volatile ArrayList<Message> messages ;

    public Conversation() {
        messages = new ArrayList<>() ;
    }


    public synchronized void sendMessage(Message content){
        messages.add(content) ;
    }


    /// just for test


    public ArrayList<Message> getMessages() {
        return messages;
    }
}
