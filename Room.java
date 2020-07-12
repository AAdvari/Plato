package Plato;

import java.util.ArrayList;

public class Room implements Runnable{

    public static int number = 0 ;
    private int id ;
    public Room(User user) {
        users.add(user) ;
        id = number ;
        number++ ;
    }

    ArrayList<User> users = new ArrayList<>() ;

    @Override
    public void run() {

    }
}
