package Plato.server;

import com.sun.jdi.connect.Connector;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Client {
    public static void main(String args[]) throws IOException, ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);

        Socket socket = new Socket("localhost", 4000);

        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.flush();
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

        User current = null;

        String command = null;
        while (true) {
            System.out.println("Enter Command :");
            command = scanner.nextLine();
            oos.writeUTF(command);
            oos.flush();
            switch (command) {
                case "login": {
                    String username, password;
                    System.out.println("Username  :");
                    username = scanner.nextLine();
                    System.out.println("passWord :");
                    password = scanner.nextLine();

                    oos.writeUTF(username);
                    oos.flush();
                    oos.writeUTF(password);
                    oos.flush();

                    Object obj = ois.readObject();
                    if (obj != null) {
                        System.out.println(obj);
                        current = (User) obj;
                    } else
                        System.out.println("Wrong ! ");
                    break;

                }
                case "register": {
                    String username, password;
                    System.out.println("Username  :");
                    username = scanner.nextLine();
                    System.out.println("passWord :");
                    password = scanner.nextLine();

                    oos.writeUTF(username);
                    oos.flush();
                    oos.writeUTF(password);
                    oos.flush();

                    String result = ois.readUTF();
                    if (result.equals("successful"))
                        System.out.println("Done !");
                    else if (result.equals("failed"))
                        System.out.println("Username Already Taken ... ! ");


                    break;

                }
                case "search_for_friend": {
                    System.out.println("Enter Username : ! ");
                    String username = scanner.nextLine();
                    oos.writeUTF(username);
                    oos.flush();

                    Object obj = ois.readObject();
                    if (obj != null)
                        System.out.println(obj);

                    break;
                }

                case "send_message": {
                    System.out.println("Enter Username : ");
                    String username = scanner.nextLine();
                    oos.writeUTF(username);
                    oos.flush();
                    System.out.println("message : ");
                    String message = scanner.nextLine();
                    oos.writeUTF(message);
                    oos.flush();
                    break;
                }
                case "make_room": {

                    String type;
                    String name;
                    int capacity;
                    System.out.println("type :");
                    type = scanner.nextLine();
                    System.out.println("name :");
                    name = scanner.nextLine();
                    System.out.println("Capacity");
                    capacity = scanner.nextInt();
                    scanner.nextLine();


                    oos.writeUTF(name);
                    oos.flush();
                    oos.writeUTF(type);
                    oos.flush();
                    oos.writeInt(capacity);
                    oos.flush();

                    oos.reset();

                    dotsGame(oos, ois);
                    break;
                }
                case "join_room": {
                    System.out.println("Enter id:");
                    int id = scanner.nextInt();
                    scanner.nextLine();
                    oos.writeInt(id);
                    oos.flush();

                    oos.reset();

                    dotsGame(oos, ois);
                    break;
                }
                case "get_rooms": {
                    ConcurrentHashMap<Integer, RoomInfo> rooms = (ConcurrentHashMap<Integer, RoomInfo>) ois.readObject();
                    System.out.println(rooms);
                    break;
                }
                case "watch": {
                    System.out.println("Enter Room id :");
                    int id = scanner.nextInt();
                    scanner.nextLine();
                    oos.writeInt(id);
                    oos.flush();
                    DotsGameStream(oos, ois);
                    break;
                }
                case "get_info":{
                    break;
                }
            }
//                singleMap.put("amir" , new User("amir" , "1234")) ;
//                singleMap.put("reza" , new User("reza" , "1234")) ;
//                singleMap.put("ahmad" , new User("ahmad" , "1234")) ;
//                singleMap.put("majid" , new User("majid" , "1234")) ;
//                singleMap.put("mehrdad" , new User("mehrdad" , "1234")) ;

        }
    }

    public static void xoGame(ObjectOutputStream oos, ObjectInputStream ois) throws IOException {
        Scanner scanner = new Scanner(System.in);
        char type;
        char turn;
        boolean showType = false;
        while (true) {
            String typeAndTurn = ois.readUTF();
            type = typeAndTurn.charAt(0);
            if (!showType) {
                System.out.println("YourType : " + type);
                showType = true;
            }
            turn = typeAndTurn.charAt(1);
            System.out.println("Turn : " + turn);
            if (type == turn) {
                System.out.println("EnterCoordinates");

                String move = scanner.nextLine();
//                int row = scanner.nextInt();
//                int col = scanner.nextInt();
//                scanner.nextLine();

//                oos.writeUTF(row + "" + col);
                oos.writeUTF(move);
                oos.flush();


                String result = ois.readUTF();
                if (result.startsWith("winner") || result.startsWith("draw")) {
                    System.out.println(result);
                    break;
                } else System.out.println(result);


            }
            if (type != turn) {
                String result = ois.readUTF();
                if (result.startsWith("winner") || result.startsWith("draw")) {
                    System.out.println(result);
                    break;
                } else System.out.println(result);
            }
        }
    }

    public static void guessWordGame(ObjectOutputStream oos, ObjectInputStream ois) throws IOException {
        Scanner scanner = new Scanner(System.in);

        for (int i = 0; i < 2; i++) {

            String type = ois.readUTF();
            if (type.equals("guess")) {
                int chances = ois.readInt();

                while (chances > 0) {
                    System.out.println("Enter Character :");
                    char guessedChar = scanner.nextLine().charAt(0);
                    System.out.println("guessedChar" + guessedChar);
                    oos.writeChar(guessedChar);
                    oos.flush();

                    System.out.println(ois.readUTF());

                    chances--;

                }

                String result = ois.readUTF();
                System.out.println(result);
            }

            if (type.equals("word")) {

                System.out.println("Enter Word :");
                String chosenWord = scanner.nextLine();

                System.out.println(chosenWord);
                oos.writeUTF(chosenWord);
                oos.flush();

                int chances = chosenWord.length();

                while (chances > 0) {
                    System.out.println(ois.readUTF());
                    chances--;
                }
                String result = ois.readUTF();
                System.out.println(result);
            }
        }

        String finalResult = ois.readUTF();
        System.out.println(finalResult);

    }

    public static void dotsGame(ObjectOutputStream oos, ObjectInputStream ois) {


        Scanner scanner = new Scanner(System.in);
        Box[][] boxes;
        String whatToDo;


        BooleanWrapper gameState = new BooleanWrapper(false);

        try {
            while (!gameState.bool) {
                whatToDo = null ;
                if (ois.available() > 0) {
                    whatToDo = ois.readUTF();
                }
                if (whatToDo != null && whatToDo.equals("continue")) {

                    String moveOrWait = ois.readUTF();
                    if (moveOrWait.equals("turn")) {

                        int x1, y1, x2, y2;
                        System.out.println("Enter Coordinates : ");
                        x1 = scanner.nextInt();
                        y1 = scanner.nextInt();
                        x2 = scanner.nextInt();
                        y2 = scanner.nextInt();

                        scanner.nextLine();
                        String sendingCoordinates = String.valueOf(x1) + y1 + x2 + y2;
                        oos.writeUTF(sendingCoordinates);
                        oos.flush();

                    }
                    if (moveOrWait.equals("wait")) {
                        System.out.println("Wait For Others to Move ! ");
                    }

                    boxes = (Box[][]) ois.readObject();

                    HashMap<String, Integer> usersScores = (HashMap<String, Integer>) ois.readObject();


                    print(boxes);
                    System.out.println(usersScores);


                }
                if (whatToDo != null && whatToDo.equals("finish")) {
                    break;
                }

            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }


    }

    public static void print(Box[][] boxes) {
        for (Box[] boxArray :
                boxes) {
            for (Box box :
                    boxArray) {

                System.out.print(box + " ");

            }
            System.out.println();

        }

    }

    public static void xoGameStream(ObjectOutputStream oos, ObjectInputStream ois) throws IOException, ClassNotFoundException {

        Scanner scanner = new Scanner(System.in) ;


        while (true) {
            String state = ois.readUTF();
            if (state.equals("run")) {

                char[][] table = (char[][]) ois.readObject();
                printTable(table);
                String OUsername = ois.readUTF();
                String XUsername = ois.readUTF();
                System.out.println("O :" + OUsername);
                System.out.println("X :" + XUsername);


            }
            if (state.equals("end"))
                break;
        }

    }

    public static void DotsGameStream(ObjectOutputStream oos, ObjectInputStream ois) throws IOException, ClassNotFoundException {

        BooleanWrapper stopStream = new BooleanWrapper(false) ;
        Scanner scanner = new Scanner(System.in)  ;

        (new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    if(scanner.hasNext()){
                        String command = scanner.nextLine();
                        try {
                            oos.writeUTF(command);
                            oos.flush();

                            if(command.equals("quit")){
                                stopStream.setBoolean(true);
                                break;
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        })).start();

        String state = null ;
        Box[][] boxes  ;
        HashMap<String, Integer> userScores  ;
        while ( ! stopStream.bool) {

            if(ois.available() > 0 ) {
                 state = ois.readUTF();
            }
            if ( state!=null && state.equals("run")) {

                boxes = (Box[][]) ois.readObject();
                userScores = (HashMap<String, Integer>) ois.readObject();

                print(boxes);
                System.out.println(userScores);

                state = null;

            }
            if ( state!=null && state.equals("end"))
                break;
        }

    }

    public static void GuessWordGameStream(ObjectOutputStream oos, ObjectInputStream ois) throws IOException, ClassNotFoundException {

        while (true) {

            String state = ois.readUTF();
            if (state.equals("run")) {

                String word = ois.readUTF();
                System.out.println(word);


                System.out.println("Guessing User" + ois.readUTF());
                System.out.println("Waiting User" + ois.readUTF());


            }
            if (state.equals("end"))
                break;
        }


    }


    /*
 just for Testing Server (xo)
  */
    public static void printTable(char[][] table) {
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


}



