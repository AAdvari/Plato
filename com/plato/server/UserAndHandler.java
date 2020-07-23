package com.plato.server;

import java.io.Serializable;

public class UserAndHandler implements Serializable {
    private User user;
    private UserHandler userHandler ;

    public UserAndHandler(User user, UserHandler userHandler) {
        this.user = user;
        this.userHandler = userHandler;
    }
    public User getUser() {
        return user;
    }

    public UserHandler getUserHandler() {
        return userHandler;
    }
}
