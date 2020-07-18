package Plato.server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Client {
    public static void main(String args[]) throws IOException, ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);

        Socket socket = new Socket("localhost", 4000);

        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.flush();
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

        System.out.println("ss");

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

                    String gamersExitHandler = null;
                    while (true) {
                        System.out.println("Your in GameProvider Loop");
//                        gamersExitHandler = scanner.nextLine();

//                        oos.writeUTF(gamersExitHandler);
//                        oos.flush();
                        if ( gamersExitHandler!=null && gamersExitHandler.equals("quit"))
                            break;
                        else {
                            xoGame(oos, ois);
                            break;
                        }
                    }
                    break;
                }
                case "join_room": {
                    System.out.println("Enter id:");
                    int id = scanner.nextInt();
                    scanner.nextLine();
                    oos.writeInt(id);
                    oos.flush();
                    String gamersExitHandler = null;
                    while (true) {
                        System.out.println("Your in GameProvider Loop");
//                        gamersExitHandler = scanner.nextLine();

//                        oos.writeUTF(gamersExitHandler);
//                        oos.flush();
                        if ( gamersExitHandler!=null && gamersExitHandler.equals("quit"))
                            break;
                        else {
                            xoGame(oos , ois);
                            break;
                        }
                    }
                    break;
                }
                case "get_rooms": {
                    ConcurrentHashMap<Integer, RoomInfo> rooms = (ConcurrentHashMap<Integer, RoomInfo>) ois.readObject();
                    System.out.println(rooms);
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
        boolean showType = false ;
        while (true) {
            String typeAndTurn = ois.readUTF();
            type = typeAndTurn.charAt(0);
            if(  ! showType ) {
                System.out.println("YourType : " + type);
                showType = true;
            }
            turn = typeAndTurn.charAt(1);
            System.out.println("Turn : " + turn);
            if (type == turn) {
                System.out.println("EnterCoordinates");
                int row = scanner.nextInt();
                int col = scanner.nextInt();
                scanner.nextLine();
                oos.writeUTF(row + "" + col);
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

    public static void guessWordGame(ObjectOutputStream oos, ObjectInputStream ois) throws IOException, ClassNotFoundException {
        Scanner scanner = new Scanner(System.in)  ;

        for (int i = 0; i < 2 ; i++) {

            String type = ois.readUTF() ;
            if (type.equals("guess")){
                int chances = ois.readInt();

                while (chances > 0){
                    System.out.println("Enter Character :");
                    char guessedChar = scanner.nextLine().charAt(0) ;
                    oos.writeChar(guessedChar);
                    oos.flush();

                    System.out.println(ois.readUTF());

                    chances-- ;

                }

                String result = ois.readUTF() ;
                System.out.println(result);
            }

            if(type.equals("word")){

                System.out.println("Enter Word :");
                String chosenWord= scanner.nextLine() ;

                oos.writeUTF(chosenWord);
                oos.flush();

                int chances = chosenWord.length() ;

                while (chances > 0){
                    System.out.println(ois.readUTF());
                    chances--;
                }
                String result = ois.readUTF() ;
                System.out.println(result);
            }
        }

        String finalResult = ois.readUTF() ;
        System.out.println(finalResult);

    }

}



