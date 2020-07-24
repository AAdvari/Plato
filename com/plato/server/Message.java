package com.plato.server;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {
    private static final long serialVersionUID = -1433121233L;
    private Date date;


    public Date getDate() {
        return date;
    }

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