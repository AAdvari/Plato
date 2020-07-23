package com.plato.server;

import java.io.Serializable;

public class UserScoreInfo implements Serializable {
    private static final long serialVersionUID = -4262345L;
    private String username ;
    private Integer score ;


    public UserScoreInfo(String username, Integer score) {
        this.username = username;
        this.score = score;
    }

    public String getUsername() {
        return username;
    }

    public Integer getScore() {
        return score;
    }
}
