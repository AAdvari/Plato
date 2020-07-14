package Plato.server;

import java.util.Date;

public class Message {
    private Date date;
    private String content ;


    public Message(Date date, String content) {
        this.date = date;
        this.content = content;
    }

    // test

    @Override
    public String toString() {
        return "Message{" +
                "date=" + date +
                ", content='" + content + '\'' +
                '}';
    }
}