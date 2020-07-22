package Plato.server;

import java.io.Serializable;

public class RoomInfo implements Serializable {
    private String name ;
    private String type ;
    private int id ;
    private int capacity;
    private boolean isStarted ;

    public RoomInfo(String name, String type, int id, int capacity , boolean isStarted) {
        this.name = name;
        this.type = type;
        this.id = id;
        this.capacity = capacity;
        this.isStarted = isStarted;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }


    //test

    @Override
    public String toString() {
        return "RoomInfo{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", id=" + id +
                ", capacity=" + capacity +
                '}';
    }
}
