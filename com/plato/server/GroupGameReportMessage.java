package com.plato.server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class GroupGameReportMessage extends Message implements Serializable {
    private static final long serialVersionUID = -4244442345L;
    private HashMap<String,Integer> usernamesAndScores;
    public GroupGameReportMessage(Date date , HashMap<String,Integer> usernamesAndScores) {
        super(date);
        this.usernamesAndScores = usernamesAndScores ;

    }

    public HashMap<String, Integer> getUsernamesAndScores() {
        return usernamesAndScores;
    }


    //test

    @Override
    public String toString() {
        return "GroupGameReportMessage{" +
                "usernamesAndScores=" + usernamesAndScores +
                '}';
    }
}
