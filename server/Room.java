package Plato;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;

public class Room extends Thread {


    public static int number = 0 ;
    private final int id ;
    private final int capacity  ;
    private final String gameName  ;
    private boolean gameRunning = false ;
    private ArrayList<UserAndHandler> users = new ArrayList<>() ;
    private ArrayList<UserAndHandler> watchers = new ArrayList<>();


    public Room(UserAndHandler user , String gameName , int capacity) {
        users.add(user) ;
        id = number ;
        this.capacity = capacity ;
        this.gameName = gameName;
        number++ ;
    }

    public synchronized void addUser(UserAndHandler user){
        users.add(user) ;
        if (getUsersCount() == getCapacity())
            gameRunning = true;
    }

    public int getUsersCount(){
        return users.size() ;
    }

    public int getCapacity(){
        return capacity ;
    }

    public synchronized void addWatcher(UserAndHandler watchingUser){
        watchers.add(watchingUser) ;
    }

    public boolean isGameRunning() {
        return gameRunning;
    }

    @Override
    public void run() {
        while ( ! isGameRunning() ){

        }
        UserAndHandler player1 = users.get(0);
        UserAndHandler player2 = users.get(1);

        DataOutputStream dosPlayer1 = player1.getUserHandler().getDos() ;
        DataInputStream disPlayer1 = player2.getUserHandler().getDis() ;

        DataOutputStream dosPlayer2 = player1.getUserHandler().getDos() ;
        DataInputStream disPlayer2 = player2.getUserHandler().getDis() ;




    }

    /*
    * Client should have a Special Thread , only for receiving messages from server ....
    *  while(true) {
    *    if ( messageFromServer is your Turn )
    *          sendSomeThing
    *    if ( messageFromServer is wait )
    *           Do Nothing !
    *    if ( messageFromServer is Loose/Win )
    *           Show To Users !
    * }
    *          */
}
