package Plato.server;

import java.util.Date;

public class Message {
    private Date date;


    public Message(Date date) {
        this.date = date;

    }

    // test


    @Override
    public String toString() {
        return "Message{" +
                "date=" + date +
                '}';
    }
}