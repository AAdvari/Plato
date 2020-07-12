package Plato;

import java.util.ArrayList;

public class Room extends Thread{


    public static int number = 0 ;
    private final int id ;
    private final int capacity ;
    private final String gameName  ;
    private ArrayList<User> users = new ArrayList<>() ;

    public Room(User user , String gameName , int capacity) {
        users.add(user) ;
        id = number ;
        this.capacity = capacity ;
        this.gameName = gameName;
        number++ ;
    }

    public void addUser(User user){
        users.add(user) ;
    }

    public int getUsersCount(){
        return users.size() ;
    }

    public int getCapacity(){
        return capacity ;
    }

    @Override
    public void run() {

    }
}
