package Plato.server;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {
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