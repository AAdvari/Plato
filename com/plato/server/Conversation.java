package com.plato.server;

import java.io.Serializable;
import java.util.ArrayList;

public class Conversation implements Serializable {
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

    @Override
    public String toString() {
        return "Conversation{" +
                "messages=" + messages +
                '}';
    }
}
