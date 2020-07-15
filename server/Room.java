package Plato.server;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;


public class Room extends Thread {


    public static int number = 0;
    private final int id;
    private final String name;
    private final String type; //"casual" or "ranked"

    private volatile int capacity;
    private volatile boolean gamersReadyForCount = false;
    private volatile boolean gameStarted = false;


    private volatile ArrayList<UserAndHandler> gamers = new ArrayList<>();
    private volatile ConcurrentHashMap<Integer, Room> rooms;
    private volatile ArrayList<UserAndHandler> watchers = new ArrayList<>();


    public Room(UserAndHandler user, String type, String name, ConcurrentHashMap<Integer, Room> rooms, int capacity) {
        addUser(user);

        id = number;
        this.capacity = capacity;
        this.name = name;
        this.type = type;
        this.rooms = rooms;

        number++;
    }

    public synchronized void addUser(UserAndHandler user) {
        gamers.add(user);
        if (getUsersCount() == capacity)
            gamersReadyForCount = true;

        new Thread(new GamersExitHandler(user)).start();
    }

    public int getUsersCount() {
        return gamers.size();
    }

    public int getCapacity() {
        return capacity;
    }


    public synchronized void addWatcher(UserAndHandler watchingUser) {
        watchers.add(watchingUser);
    }


    @Override
    public void run() {

        try {
            while (true) {
                while (!gamersReadyForCount) {
                    Thread.currentThread().sleep(2000);
                }
                Thread.currentThread().sleep(1000 * 10);
                if (getUsersCount() == capacity)
                    break;
            }
            gameStarted = true;

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        xoGameProvider();

    }


    private void xoGameProvider() {
        // O and X
        char[][] table = new char[3][3];

        UserAndHandler player1Data = gamers.get(0);
        UserAndHandler player2Data = gamers.get(1);

        ObjectOutputStream player1Oos = player1Data.getUserHandler().getOos();
        ObjectOutputStream player2Oos = player2Data.getUserHandler().getOos();

        ObjectInputStream player1Ois = player1Data.getUserHandler().getOis();
        ObjectInputStream player2Ois = player2Data.getUserHandler().getOis();

        /// Player1 == O  Player2 == X

        // Indicating O and X
        // Move is st Like 01X
        try {
            char turn = 'O';
            User winner = null;
            User looser = null;

            while (true) {
                player1Oos.writeUTF("O" + turn);
                player2Oos.writeUTF("X" + turn);


                if (turn == 'O') {
                    String move = player1Ois.readUTF();
                    // ADD move To Table
                    int row = Integer.parseInt(String.valueOf(move.charAt(0)));
                    int col = Integer.parseInt(String.valueOf(move.charAt(1)));

                    table[row][col] = 'O';

                    if (isGameFinished(table) == 'O') {
                        player1Oos.writeUTF("winnerO");
                        player2Oos.writeUTF("winnerO");

                        winner = player1Data.getUser();
                        looser = player2Data.getUser();

                        break;
                    } else {
                        player1Oos.writeUTF("continue");
                        player2Oos.writeUTF("continue");
                    }
                    turn = 'X';
                }


                if (turn == 'X') {
                    String move = player2Ois.readUTF();
                    int row = Integer.parseInt(String.valueOf(move.charAt(0)));
                    int col = Integer.parseInt(String.valueOf(move.charAt(1)));

                    table[row][col] = 'X';

                    if (isGameFinished(table) == 'X') {
                        player1Oos.writeUTF("winnerX");
                        player2Oos.writeUTF("winnerX");

                        winner = player2Data.getUser();
                        looser = player1Data.getUser();


                        break;
                    } else {
                        player1Oos.writeUTF("continue");
                        player2Oos.writeUTF("continue");
                    }
                    turn = 'O';

                }
            }
            if(type.equals("ranked"))
                winner.addScoreToGame("xo");

            if( winner.getConversation(looser) == null){
                Conversation conversation = new Conversation() ;
                winner.addConversation(looser , conversation);
                looser.addConversation(winner , conversation);
            }
            winner.getConversation(looser).sendMessage(new GameReportMessage
                    (new Date() , "xo" , winner.getUsername() , looser.getUsername()));


        } catch (IOException e) {
            e.printStackTrace();
        }

        /*
        Client Should Do ST like This ... :
        while(true){
        getTurn();
        if ( Turn is O ) {
               writeAnswer() == > Update Table
               getResponse()
        }

        else{
                getResponse()
             }

          }

         */


    }

    /*
    OutPut is The Winner Character !
     */
    private char isGameFinished(char[][] table) {
        int X = 0;
        int O = 0;
        //Cols
        for (int j = 0; j < 3; j++) {
            for (int i = 0; i < 3; i++) {
                if (table[i][j] == 'X') {
                    X++;
                }
                if (table[i][j] == 'O') {
                    O++;
                }
            }
            if(X==3)
                return 'X';
            if(O==3)
                return 'O' ;
            X = 0 ; O = 0 ;
        }
        //Rows
        for (int j = 0; j < 3; j++) {
            for (int i = 0; i < 3; i++) {
                if (table[j][i] == 'X') {
                    X++;
                }
                if (table[j][i] == 'O') {
                    O++;
                }
            }
            if(X==3)
                return 'X';
            if(O==3)
                return 'O' ;
            X = 0 ; O = 0 ;
        }

        for (int i = 0 ; i < 3 ; i++){
            if(table[i][i] == 'X')
                X++;
            if(table[i][i] == 'O')
                O++;
        }
        if(X==3) return 'X' ;
        if(O==3) return 'O' ;

        return 'C' ;

    }


    class GamersExitHandler implements Runnable {

        private UserAndHandler userAndHandler;
        private ObjectOutputStream oos;
        private ObjectInputStream ois;
        private User userData;

        public GamersExitHandler(UserAndHandler userAndHandler) {
            this.userAndHandler = userAndHandler;
            this.oos = userAndHandler.getUserHandler().getOos();
            this.ois = userAndHandler.getUserHandler().getOis();
            this.userData = userAndHandler.getUser();
        }

        @Override
        public void run() {
            try {
                while (!gameStarted) {
                    String command = null;

                    // Checking InputStream ... !
                    ois.mark(1);
                    int bytesRead = ois.read(new byte[1]);
                    ois.reset();
                    if (bytesRead != -1) {
                        command = ois.readUTF();
                    }

                    if (command.equals("quit")) {
                        gamers.remove(userAndHandler) ;

                        if (getUsersCount() == 0) {
                            rooms.remove(id);
                        }
                        userAndHandler.getUserHandler().notify();
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

    }

}

