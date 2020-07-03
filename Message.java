package Plato;

import java.util.Date;

public class Message {
    private Date date;
    private User sender ;
    private String content ;

    public Message(Date date, User sender, String content) {
        this.date = date;
        this.sender = sender;
        this.content = content;
    }
}