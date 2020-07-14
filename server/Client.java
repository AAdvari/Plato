package Plato.server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.Set;

public class Client {
    public static void main(String args[]) throws IOException, ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);

        Socket socket = new Socket("localhost", 4000);

        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.flush();
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

        System.out.println("ss");

        User current = null ;

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
                    password = scanner.nextLine() ;

                    oos.writeUTF(username);
                    oos.flush();
                    oos.writeUTF(password);
                    oos.flush();

                    Object obj = ois.readObject();
                    if (obj != null) {
                        System.out.println(obj);
                        current= (User)obj ;
                    }
                    else
                        System.out.println("Wrong ! ");
                    break;

                }
                case "register": {
                    String username, password;
                    System.out.println("Username  :");
                    username = scanner.nextLine();
                    System.out.println("passWord :");
                    password = scanner.nextLine() ;

                    oos.writeUTF(username);
                    oos.flush();
                    oos.writeUTF(password);
                    oos.flush();

                    String result = ois.readUTF() ;
                    if(result.equals("successful"))
                        System.out.println("Done !");
                    else if(result.equals("failed"))
                        System.out.println("Username Already Taken ... ! ");


                    break;

                }
                case "search_for_friend" :{
                    System.out.println("Enter Username : ! ");
                    String username = scanner.nextLine() ;
                    oos.writeUTF(username);
                    oos.flush() ;

                    Object obj = ois.readObject() ;
                    if(obj != null)
                        System.out.println(obj);

                    break;
                }
                case "send_message" :{
                    System.out.println("Enter Username : ");
                    String username = scanner.nextLine() ;
                    oos.writeUTF(username);
                    oos.flush();
                    System.out.println("message : ");
                    String message = scanner.nextLine() ;
                    oos.writeUTF(message);
                    oos.flush() ;
                    break;
                }
//                singleMap.put("amir" , new User("amir" , "1234")) ;
//                singleMap.put("reza" , new User("reza" , "1234")) ;
//                singleMap.put("ahmad" , new User("ahmad" , "1234")) ;
//                singleMap.put("majid" , new User("majid" , "1234")) ;
//                singleMap.put("mehrdad" , new User("mehrdad" , "1234")) ;

            }
        }
    }

}
