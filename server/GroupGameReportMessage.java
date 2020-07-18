package Plato.server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class GroupGameReportMessage extends Message implements Serializable {

    private HashMap<String,Integer> usernamesAndScores;
    public GroupGameReportMessage(Date date , HashMap<String,Integer> usernamesAndScores) {
        super(date);
        this.usernamesAndScores = usernamesAndScores ;

    }

    public HashMap<String, Integer> getUsernamesAndScores() {
        return usernamesAndScores;
    }
}
