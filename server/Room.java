package Plato.server;


import java.io.*;
import java.util.*;
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
        new Thread(new StreamHandler(watchingUser)).start();

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

        switch (name) {
            case "xo": {
                xoGameProvider();
                break;
            }
            case "guessWord": {
                guessWordGameProvider();
                break;
            }
            case "dotsGame": {
                dotsGameProvider();
                break;
            }
        }
        gameStarted = false;

    }



    /* Providers */

    public void dotsGameProvider() {

        int playersCount = capacity;


        /* Initializing Boxes , Edges , Dots ,(Server Data) ... */
        LinkedHashSet<Edge> edges = new LinkedHashSet<>();
        Box[][] boxes = new Box[6][6];
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                boxes[j][i] = new Box();
                boxes[j][i].setDotsAndEdges(i, j);
                edges.addAll(boxes[j][i].edges);
            }
        }


        int turn = 0;
        boolean turnMustChange = true;
        HashMap<String, Integer> usersScores = new HashMap<>();

        try {
            while (!allEdgesChosen(edges)) {

                for (int i = 0; i < gamers.size(); i++) {
                    ObjectOutputStream oos = gamers.get(i).getUserHandler().getOos();
                    oos.writeUTF("continue");
                    oos.flush();
                }

                for (int i = 0; i < gamers.size(); i++) {
                    ObjectOutputStream oos = gamers.get(i).getUserHandler().getOos();
                    if (i == turn) {
                        oos.writeUTF("turn");

                    } else
                        oos.writeUTF("wait");

                    oos.flush();
                }


                //  user's Data ( user is specified according to turn )
                String username = gamers.get(turn).getUser().getUsername();
                ObjectInputStream ois = gamers.get(turn).getUserHandler().getOis();
                int userInitialScore = boxesCount(boxes, username);

                // Edge Components
                int x1, x2, y1, y2;

                // Getting & Parsing Edge Components From User
                String userEdgeBeginAndEnd = ois.readUTF();
                x1 = Integer.parseInt(String.valueOf(userEdgeBeginAndEnd.charAt(0)));
                y1 = Integer.parseInt(String.valueOf(userEdgeBeginAndEnd.charAt(1)));
                x2 = Integer.parseInt(String.valueOf(userEdgeBeginAndEnd.charAt(2)));
                y2 = Integer.parseInt(String.valueOf(userEdgeBeginAndEnd.charAt(3)));


                Edge edge = new Edge(new Dot(x1, y1), new Dot(x2, y2));
                for (Edge edge1 :
                        edges) {
                    if (edge.equals(edge1))
                        edge1.setChosen(true);
                }
                setOwnerShipToRectangles(boxes, username, edges);

                // This Statement is For Testing Server ( Should be Removed ! )
                print(boxes);

                // Checking and Storing Scores

                for (int i = 0; i < gamers.size(); i++) {
                    String userName = gamers.get(i).getUser().getUsername();
                    usersScores.put(userName, boxesCount(boxes, userName));
                }

                for (int i = 0; i < gamers.size(); i++) {
                    ObjectOutputStream outStream = gamers.get(i).getUserHandler().getOos();

                    // Sending Boxes State
                    outStream.reset();
                    outStream.writeObject(boxes);
                    outStream.flush();

                    // Sending Scores
                    outStream.writeObject(usersScores);
                    outStream.flush();

                    //Sending Data To Watchers
                    sendDotsGameDataToWatchers(boxes , usersScores);

                    int userFinalScore = boxesCount(boxes, username);
                    if (userFinalScore > userInitialScore)
                        turnMustChange = false;
                    else
                        turnMustChange = true;
                }
                if (turnMustChange) {
                    turn++;
                    if (turn > gamers.size() - 1)
                        turn = 0;
                }

            }

            // endGame message (players)....
            for (int i = 0; i < gamers.size(); i++) {
                ObjectOutputStream oos = gamers.get(i).getUserHandler().getOos();
                oos.writeUTF("finish");
                oos.flush();
            }


            // Endgame message (watchers)
            sendEndGameMessageToWatchers();


            // GroupGameReport Should Be Sent to each pair of gamers (new Chat ) ....
            GroupGameReportMessage ggrm = new GroupGameReportMessage(new Date(), usersScores);

            for (int i = 0; i < gamers.size(); i++) {

                // Adding Score :
                String user = gamers.get(i).getUser().getUsername();
                gamers.get(i).getUser().addScoreToGame(usersScores.get(user) * 10, "dotsGame");

                for (int j = i + 1; j < gamers.size(); j++) {
                    User user1 = gamers.get(i).getUser();
                    User user2 = gamers.get(i).getUser();
                    if (user1.getConversation(user2) == null) {
                        Conversation conversation = new Conversation();
                        user1.addConversation(user2, conversation);
                        user2.addConversation(user1, conversation);
                        conversation.sendMessage(ggrm);
                    } else {
                        user1.getConversation(user2).sendMessage(ggrm);
                    }
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
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
        try {
            char turn = 'O';
            User winner = null;
            User looser = null;

            boolean RolesSentToWatchers = false;

            while (true) {
                System.out.println("Game Launched ! ");
                player1Oos.writeUTF("O" + turn);
                player1Oos.flush();
                player2Oos.writeUTF("X" + turn);
                player2Oos.flush();


                if (!RolesSentToWatchers) {
                    String player1Username = player1Data.getUser().getUsername();
                    String player2Username = player2Data.getUser().getUsername();
                    for (UserAndHandler userAndHandler : watchers) {
                        ObjectOutputStream oos = userAndHandler.getUserHandler().getOos();
                        oos.writeUTF(player1Username + " " + "O");
                        oos.flush();
                        oos.writeUTF(player2Username + " " + "X");
                    }
                    RolesSentToWatchers = true;
                }


                if (turn == 'O') {
                    String move = player1Ois.readUTF();
                    // ADD move To Table
                    int row = Integer.parseInt(String.valueOf(move.charAt(0)));
                    int col = Integer.parseInt(String.valueOf(move.charAt(1)));


                    table[row][col] = 'O';
                    printTable(table);

                    char result = isGameFinished(table); //C == Continue  / D == Draw // O,X == Win
                    if (result == 'O') {
                        player1Oos.writeUTF("winnerO");
                        player1Oos.flush();
                        player2Oos.writeUTF("winnerO");
                        player2Oos.flush();

                        winner = player1Data.getUser();
                        looser = player2Data.getUser();

                        break;
                    } else if (result == 'C') {

                        player1Oos.writeUTF("continue");
                        player1Oos.flush();
                        player2Oos.writeUTF("continue");
                        player2Oos.flush();
                    } else {
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

                    sendXOTableToWatchers(table , player1Data.getUser().getUsername() , player2Data.getUser().getUsername());

                    char result = isGameFinished(table);
                    if (isGameFinished(table) == 'X') {
                        player1Oos.writeUTF("winnerX");
                        player1Oos.flush();
                        player2Oos.writeUTF("winnerX");
                        player2Oos.flush();

                        winner = player2Data.getUser();
                        looser = player1Data.getUser();


                        break;
                    } else if (result == 'C') {
                        player1Oos.writeUTF("continue");
                        player1Oos.flush();
                        player2Oos.writeUTF("continue");
                        player2Oos.flush();
                    } else {
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


            sendEndGameMessageToWatchers();


            if (type.equals("ranked") && isGameFinished(table) != 'D')
                winner.addWinScoreToGame("xo");
            if (type.equals("ranked") && isGameFinished(table) == 'D') {
                winner.addDrawScoreToGame("xo");
                looser.addDrawScoreToGame("xo");
            }

            if (winner.getConversation(looser) == null) {
                Conversation conversation = new Conversation();
                winner.addConversation(looser, conversation);
                looser.addConversation(winner, conversation);
            }
            winner.getConversation(looser).sendMessage(new GameReportMessage
                    (new Date(), "xo", winner.getUsername(), looser.getUsername(),
                            isGameFinished(table) == 'D'));

//            rooms.remove(id);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private void guessWordGameProvider() {

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

        String guessingUsername = player2Data.getUser().getUsername() ;
        String waitingUsername = player1Data.getUser().getUsername() ;

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
                guessOos.writeInt(chances);
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

                    // SendingData To Watchers ..
                    sendGuessWordGameDataToWatchers(String.valueOf(word) , guessingUsername , waitingUsername);

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

                guessingUsername = player1Data.getUser().getUsername() ;
                waitingUsername = player2Data.getUser().getUsername() ;

                chosenWord = null;
                word = null;

            }
            sendEndGameMessageToWatchers();
            User winner, looser;
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
                if (type.equals("ranked")) {
                    player1Data.getUser().addDrawScoreToGame("guessWord");
                    player2Data.getUser().addDrawScoreToGame("guessWord");
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    /* Utility Methods For Providers */


    /*
    guessWordGame utility method
     */
    private String replace(String string, char ch) {
        int index = string.indexOf(ch);
        return string.substring(0, index) + "*" + string.substring(index + 1, string.length());

    }

    /*
    just For Testing Server (guessWord)
     */
    private static void printWord(char[] word) {
        for (int i = 0; i < word.length; i++) {
            System.out.print(word[i]);
        }
        System.out.println("");
    }

    public void setOwnerShipToRectangles(Box[][] boxes, String username, LinkedHashSet<Edge> edges) {
        for (Box[] boxArray :
                boxes) {
            for (Box box :
                    boxArray) {
                boolean boxFilled = true;
                for (Edge edge : box.edges) {
                    if (!edge.isChosen)
                        boxFilled = false;
                }
                if (boxFilled && box.owner == null) {
                    box.setOwner(username);
                }
            }
        }
    }

    public boolean allEdgesChosen(LinkedHashSet<Edge> edges) {
        for (Edge edge :
                edges) {
            if (!edge.isChosen)
                return false;
        }
        return true;
    }

    public void print(Box[][] boxes) {
        for (Box[] boxArray :
                boxes) {
            for (Box box :
                    boxArray) {

                System.out.print(box + " ");

            }
            System.out.println();

        }

    }


    public int boxesCount(Box[][] boxes, String user) {

        int count = 0;
        for (Box[] boxArray :
                boxes) {
            for (Box box :
                    boxArray) {
                if (box.owner != null && box.owner.equals(user))
                    count++;
            }
        }

        return count;
    }

    /*
    just for Testing Server (xo)
     */
    private void printTable(char[][] table) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (table[i][j] == 'O' || table[i][j] == 'X')
                    System.out.print(table[i][j]);
                else
                    System.out.print(" ");
            }
            System.out.println("");
        }
    }

    /*
    OutPut is The Winner Character(xo) !
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

        boolean draw = false;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (!(table[i][j] == 'O' || table[i][j] == 'X'))
                    return 'C';
            }

        }
        return 'D';

    }




    /* Stream Providers */
    private void sendXOTableToWatchers(char[][] table , String OUsername ,String XUsername) {

        for (UserAndHandler userAndHandler : watchers) {
            try {
                ObjectOutputStream oos = userAndHandler.getUserHandler().getOos() ;
                oos.writeUTF("run");
                oos.flush();
                oos.writeObject(table);
                oos.flush();
                oos.writeUTF(OUsername);
                oos.flush();
                oos.writeUTF(XUsername);
                oos.flush();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendDotsGameDataToWatchers(Box[][] boxes, HashMap<String, Integer> usersScores) {

        for (UserAndHandler watcher :
                watchers) {
            ObjectOutputStream oos = watcher.getUserHandler().getOos();
            try {
                oos.writeUTF("run");
                oos.flush();
                oos.writeObject(boxes);
                oos.flush();
                oos.writeObject(usersScores);
                oos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void sendGuessWordGameDataToWatchers(String word, String guessingUser, String waitingUser) {

        try {
            for (UserAndHandler watcher : watchers) {
                ObjectOutputStream oos = watcher.getUserHandler().getOos();
                oos.writeUTF("run");
                oos.flush();
                oos.writeUTF(word);
                oos.flush();
                oos.writeUTF(guessingUser);
                oos.flush();
                oos.writeUTF(waitingUser);
                oos.flush();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void sendEndGameMessageToWatchers(){
        for (UserAndHandler watcher :
                watchers) {
            ObjectOutputStream oos = watcher.getUserHandler().getOos();
            try {
                oos.writeUTF("end");
                oos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }





    /* These Classes Handle Client's Reactions ( can be extended ) */

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
                    if (command != null && command.startsWith("change_capacity") && name.equals("dotsGame")) {
                        int newCapacity = Integer.parseInt(String.valueOf(command.charAt(15)));
                        capacity = newCapacity;
                    }

                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }


        }

    }

    class StreamHandler implements Runnable {
        private final UserAndHandler userAndHandler;
        private final ObjectOutputStream oos;
        private final ObjectInputStream ois;
        private final User userData;

        public StreamHandler(UserAndHandler userAndHandler) {
            this.userAndHandler = userAndHandler;
            userData = userAndHandler.getUser();
            oos = userAndHandler.getUserHandler().getOos();
            ois = userAndHandler.getUserHandler().getOis();
        }


        @Override
        public void run() {
            try {
                while (gameStarted) {
                    String command = null;

                    Thread.currentThread().sleep(400);
                    // Checking InputStream ... !
                    if (ois.available() > 0) {
                        command = ois.readUTF();
                    }


                    if (command != null && command.equals("quit")) {
                        synchronized (watchers) {
                            watchers.remove(userAndHandler);
                        }
                        synchronized (userAndHandler.getUserHandler()) {
                            userAndHandler.getUserHandler().notify();
                        }
                    }

                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }


        }
    }



}

// These Classes Are implemented to ease DotGame Representation in Client
class Dot implements Serializable {
    public int x;
    public int y;

    public Dot(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dot dot = (Dot) o;
        return x == dot.x &&
                y == dot.y;
    }

}

class Edge implements Serializable {
    public Dot begin;
    public Dot end;
    public boolean isChosen = false;

    public Edge(Dot begin, Dot end) {
        this.begin = begin;
        this.end = end;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return (begin.equals(edge.begin) && end.equals(edge.end)) ||
                (begin.equals(edge.end) && end.equals(edge.begin));
    }

    public void setChosen(boolean state) {
        isChosen = state;
    }

}

// each Box has an Owner :)
class Box implements Serializable {

    //order is important
    public ArrayList<Dot> dots;
    public ArrayList<Edge> edges;

    public String owner = null;


    public Box() {
        dots = new ArrayList<>();
        edges = new ArrayList<>();
    }

    public void setDotsAndEdges(int boxX, int boxY) {
        dots.add(new Dot(boxX, boxY));
        dots.add(new Dot(boxX + 1, boxY));
        dots.add(new Dot(boxX, boxY + 1));
        dots.add(new Dot(boxX + 1, boxY + 1));

        edges.add(new Edge(dots.get(0), dots.get(1))); // e12 --> index == 0
        edges.add(new Edge(dots.get(0), dots.get(2))); // e13 --> index == 1
        edges.add(new Edge(dots.get(1), dots.get(3))); // e24 --> index == 2
        edges.add(new Edge(dots.get(2), dots.get(3))); // e34 --> index == 3
    }

//        12
///     1- - -2
///  13 | box | 24
//      3- - -4
//         34

    public void setOwner(String username) {
        owner = username;
    }

    @Override
    public String toString() {
        String out = "";
        if (edges.get(1).isChosen) {
            out += "|";
        } else
            out += " ";

        if (edges.get(0).isChosen) {
            out += "-";
        } else
            out += " ";
        if (edges.get(3).isChosen) {
            out += "_";
        } else
            out += " ";
        if (edges.get(2).isChosen) {
            out += "|";
        } else
            out += " ";

        return out;

    }
}



