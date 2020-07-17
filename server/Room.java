package Plato.server;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;


public class Room extends Thread {


    public volatile static int number = 0;
    private final int id;
    private final String name;
    private final String type; //"casual" or "ranked"

    private volatile int capacity;
    private volatile boolean gamersReadyForCount = false;
    private volatile boolean gameStarted = false;


    private volatile ArrayList<UserAndHandler> gamers;
    private volatile ConcurrentHashMap<Integer, Room> rooms;
    private volatile ArrayList<UserAndHandler> watchers;


    public Room(String type, String name, ConcurrentHashMap<Integer, Room> rooms, int capacity) {

        this.capacity = capacity;
        this.name = name;
        this.type = type;
        this.rooms = rooms;
        gamers = new ArrayList<>();
        watchers = new ArrayList<>();

        id = number;
        number++;

    }

    public int getRoomId() {
        return id;
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

    public String getRoomName() {
        return name;
    }

    public String getRoomType() {
        return type;
    }

    private boolean areGamersReadyToCount() {
        return gamersReadyForCount;
    }


    public void addWatcher(UserAndHandler watchingUser) {
        watchers.add(watchingUser);
    }


    @Override
    public void run() {

        try {
            while (true) {
                while (!areGamersReadyToCount()) {
                    Thread.currentThread().sleep(2000);
                    System.out.println(getUsersCount());
                }
                Thread.currentThread().sleep(1000 * 10);
                if (getUsersCount() == capacity)
                    break;
            }
            gameStarted = true;

        } catch (InterruptedException e) {
            System.out.println("User Checker Thread Stopped !");
            e.printStackTrace();
        }

        switch (name){
            case "xo":{
                xoGameProvider();
                break;
            }
            case "guessWord":{
                guessWordGameProvider();
                break;
            }
        }

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
                System.out.println("Game Launched ! ");
                player1Oos.writeUTF("O" + turn);
                player1Oos.flush();
                player2Oos.writeUTF("X" + turn);
                player2Oos.flush();



                if (turn == 'O') {
                    String move = player1Ois.readUTF();
                    // ADD move To Table
                    int row = Integer.parseInt(String.valueOf(move.charAt(0)));
                    int col = Integer.parseInt(String.valueOf(move.charAt(1)));


                    table[row][col] = 'O';
                    printTable(table);

                    char result  = isGameFinished(table); //C == Continue  / D == Draw // O,X == Win
                    if ( result == 'O') {
                        player1Oos.writeUTF("winnerO");
                        player1Oos.flush();
                        player2Oos.writeUTF("winnerO");
                        player2Oos.flush();

                        winner = player1Data.getUser();
                        looser = player2Data.getUser();

                        break;
                    } else if (result == 'C'){

                        player1Oos.writeUTF("continue");
                        player1Oos.flush();
                        player2Oos.writeUTF("continue");
                        player2Oos.flush();
                    }
                    else {
                        player1Oos.writeUTF("draw");
                        player1Oos.flush();
                        player2Oos.writeUTF("draw");
                        player2Oos.flush();
                        winner = player2Data.getUser();
                        looser = player1Data.getUser();
                        break;
                    }

                }


                if (turn == 'X') {
                    String move = player2Ois.readUTF();
                    int row = Integer.parseInt(String.valueOf(move.charAt(0)));
                    int col = Integer.parseInt(String.valueOf(move.charAt(1)));

                    table[row][col] = 'X';
                    printTable(table);
                    char result = isGameFinished(table) ;
                    if (isGameFinished(table) == 'X') {
                        player1Oos.writeUTF("winnerX");
                        player1Oos.flush();
                        player2Oos.writeUTF("winnerX");
                        player2Oos.flush();

                        winner = player2Data.getUser();
                        looser = player1Data.getUser();


                        break;
                    } else if(result == 'C') {
                        player1Oos.writeUTF("continue");
                        player1Oos.flush();
                        player2Oos.writeUTF("continue");
                        player2Oos.flush();
                    }
                    else {
                        player1Oos.writeUTF("draw");
                        player1Oos.flush();
                        player2Oos.writeUTF("draw");
                        player2Oos.flush();
                        winner = player2Data.getUser();
                        looser = player1Data.getUser();
                        break;
                    }


                }

                if (turn == 'X')
                    turn = 'O';
                else
                    turn = 'X';
            }


            if (type.equals("ranked") && isGameFinished(table) != 'D')
                winner.addWinScoreToGame("xo");
            if (type.equals("ranked") && isGameFinished(table) == 'D'){
                winner.addDrawScoreToGame("xo");
                looser.addDrawScoreToGame("xo");
            }

            if (winner.getConversation(looser) == null) {
                Conversation conversation = new Conversation();
                winner.addConversation(looser, conversation);
                looser.addConversation(winner, conversation);
            }
            winner.getConversation(looser).sendMessage(new GameReportMessage
                    (new Date(), "xo", winner.getUsername(), looser.getUsername() ,
                            isGameFinished(table)=='D'));

            rooms.remove(id) ;
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

    private void guessWordGameProvider()  {

        UserAndHandler player1Data = gamers.get(0);
        UserAndHandler player2Data = gamers.get(1);
        ObjectOutputStream player1Oos = player1Data.getUserHandler().getOos();
        ObjectOutputStream player2Oos = player2Data.getUserHandler().getOos();
        ObjectInputStream player1Ois = player1Data.getUserHandler().getOis();
        ObjectInputStream player2Ois = player2Data.getUserHandler().getOis();

        // Choose => Player1
        // Guess => Player2
        boolean player1Guess = false;
        boolean player2Guess = false;

        ObjectOutputStream chooseOos = player1Oos;
        ObjectInputStream chooseOis = player1Ois;
        ObjectOutputStream guessOos = player2Oos;
        ObjectInputStream guessOis = player2Ois;
        // game has 2 Rounds ....
        String chosenWord;
        int chances;
        char[] word;
        try {
            for (int i = 0; i < 2; i++) {

                chooseOos.writeUTF("word");
                chooseOos.flush();
                chosenWord = chooseOis.readUTF();
                word = new char[chosenWord.length()];
                for (int j = 0; j < chosenWord.length(); j++)
                    word[j] = '-';

                chances = word.length;

                guessOos.writeUTF("guess");
                guessOos.flush();
                guessOos.writeInt(chances );
                guessOos.flush();

                while (chances > 0) {
                    char guessedChar = guessOis.readChar();
                    if (chosenWord.contains(String.valueOf(guessedChar))) {
                        int index = chosenWord.indexOf(guessedChar);
                        word[index] = guessedChar;
                        chosenWord = replace(chosenWord, guessedChar);
                        System.out.println(chosenWord);
                        printWord(word);
                    }

                    guessOos.writeUTF(String.valueOf(word));
                    guessOos.flush();

                    chooseOos.writeUTF(String.valueOf(word));
                    chooseOos.flush();

                    chances--;
                }
                boolean win = true;
                for (int f = 0; f < word.length; f++) {
                    if (word[f] == '-')
                        win = false;
                }
                String result;
                if (win) result = "guess:win";
                else result = "guess:loose";

                guessOos.writeUTF(result);
                guessOos.flush();

                chooseOos.writeUTF(result);
                chooseOos.flush();


                if (guessOos.equals(player1Oos))
                    player1Guess = win;
                else
                    player2Guess = win;

                // Change Roles ...
                guessOos = player1Oos;
                guessOis = player1Ois;

                chooseOis = player2Ois;
                chooseOos = player2Oos;

                chosenWord = null;
                word = null;


            }
            User winner,looser ;
            if (player1Guess ^ player2Guess) {// ^ == XOR operator
                if (player1Guess) {
                    player1Oos.writeUTF("winner");
                    player1Oos.flush();
                    player2Oos.writeUTF("looser");
                    player2Oos.flush();

                    winner = player1Data.getUser();
                    looser = player2Data.getUser();

                    if (type.equals("ranked")) {
                        winner.addWinScoreToGame("guessWord");

                    }
                } else {
                    player2Oos.writeUTF("winner");
                    player2Oos.flush();
                    player1Oos.writeUTF("looser");
                    player1Oos.flush();

                    winner = player2Data.getUser();
                    looser = player1Data.getUser();
                    if (type.equals("ranked")) {
                        winner.addWinScoreToGame("guessWord");
                    }
                }
            } else {
                player2Oos.writeUTF("draw");
                player2Oos.flush();
                player1Oos.writeUTF("draw");
                player1Oos.flush();
                if(type.equals("ranked")){
                    player1Data.getUser().addDrawScoreToGame("guessWord");
                    player2Data.getUser().addDrawScoreToGame("guessWord");
                }

            }

            rooms.remove(getRoomId()) ;

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private static String replace(String string, char ch) {
        int index = string.indexOf(ch);
        return string.substring(0, index) + "*" + string.substring(index + 1, string.length());

    }

    private static void printWord(char[] word) {
        for (int i = 0; i < word.length; i++) {
            System.out.print(word[i]);
        }
        System.out.println("");
    }

    private void printTable(char[][] table) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if(table[i][j] == 'O' || table[i][j] == 'X')
                System.out.print(table[i][j]);
                else
                    System.out.print(" ");
            }
            System.out.println("");
        }
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
            if (X == 3)
                return 'X';
            if (O == 3)
                return 'O';
            X = 0;
            O = 0;
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
            if (X == 3)
                return 'X';
            if (O == 3)
                return 'O';
            X = 0;
            O = 0;
        }

        for (int i = 0; i < 3; i++) {
            if (table[i][i] == 'X')
                X++;
            if (table[i][i] == 'O')
                O++;
        }
        if (X == 3) return 'X';
        if (O == 3) return 'O';

        X = O = 0;

        for (int i = 0; i < 3; i++) {
            if (table[i][2 - i] == 'X')
                X++;
            if (table[i][2 - i] == 'O')
                O++;
        }
        if (X == 3) return 'X';
        if (O == 3) return 'O';

        boolean draw = false ;
        for (int i = 0; i < 3 ; i++) {
            for (int j = 0; j <3 ; j++) {
                if( ! (table[i][j]=='O' || table[i][j]=='X') )
                    return 'C' ;
            }

        }
        return 'D' ;

    }


    class GamersExitHandler implements Runnable {

        private final UserAndHandler userAndHandler;
        private final ObjectOutputStream oos;
        private final ObjectInputStream ois;
        private final User userData;

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

                    Thread.currentThread().sleep(2000);
                    System.out.println("Here !");
                    // Checking InputStream ... !

                    if (ois.available() > 0) {
                        command = ois.readUTF();
                    }

                    if (command != null && command.equals("quit")) {
                        synchronized (gamers) {
                            gamers.remove(userAndHandler);
                        }

                        if (getUsersCount() == 0) {
                            rooms.remove(id);
                        }
                        userAndHandler.getUserHandler().notify();
                    }
                    System.out.println("Here !! ");
                    break;
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }


        }

    }

}

