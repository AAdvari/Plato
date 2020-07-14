package Plato.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    public static final String DATABASE_DIRECTORY = "c:\\Users\\AmirHossein\\Desktop\\db.plato" ;

    public static volatile ConcurrentHashMap<Integer , Room> rooms = new ConcurrentHashMap<>() ;
    public static void main(String args[]) throws IOException {

        ServerSocket server = new ServerSocket(4000) ;


        /* |  Commented-Code is Tested   |  */
        /* V                             V  */

//        ExecutorService executorService = Executors.newFixedThreadPool(2) ;
//
//        Socket socket = null ;
//        while (true){
//            socket = server.accept() ;
//            executorService.execute(new UserHandler(socket , rooms));
//
//        }
//        Thread updater = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Scanner scanner = new Scanner(System.in);
//                System.out.println("Enter update to Update Database : ");
//                String command = scanner.nextLine() ;
//                if (command.equals("update")){
//                    File file = new File("c\\Users\\AmirHossein\\Desktop\\db.plato");
//                    if ( ! file.exists() )
//                        file.delete() ;
//                    try {
//                        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
//                        oos.writeObject(UsersList.getUsersList());
//                        oos.flush();
//                        oos.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }) ;
//        updater.start();



        Socket socket = null ;
        while (true){
            socket = server.accept() ;
            new Thread(new UserHandler(socket,rooms )).start();
        }
    }
}
