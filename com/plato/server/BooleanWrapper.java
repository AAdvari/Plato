package com.plato.server;

public class BooleanWrapper {
    public volatile boolean bool ;

    public BooleanWrapper(boolean bool) {
        this.bool = bool;
    }

    public synchronized void setBoolean(boolean bool) {
        this.bool = bool;
    }
}
