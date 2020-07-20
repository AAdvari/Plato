package com.plato.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    public static final String DATABASE_DIRECTORY = "c:\\Users\\AmirHossein\\Desktop\\db.plato" ;

    public static volatile ConcurrentHashMap<Integer , Room> rooms = new ConcurrentHashMap<>() ;
    public static void main(String args[]) throws IOException {

        ServerSocket server = new ServerSocket(3838) ;

        Thread updater = new Thread(new Runnable() {
            @Override
            public void run() {
                Scanner scanner = new Scanner(System.in);
                System.out.println("Enter update to Update Database : ");
                String command = scanner.nextLine() ;
                if (command.equals("update")){
                    File file = new File("c\\Users\\AmirHossein\\Desktop\\db.plato");
                    if ( ! file.exists() )
                        file.delete() ;
                    try {
                        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
                        oos.writeObject(UsersList.getUsersList());
                        oos.flush();
                        oos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }) ;
        updater.start();


        /* This Part Can Be Done In Rooms .... ! */
        Thread emptyAndFullRoomRemover = new Thread( new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.currentThread().sleep(2000);
                    Enumeration<Integer> roomsKeySet = rooms.keys();
                    Iterator iterator = roomsKeySet.asIterator();
                    while (iterator.hasNext()) {
                        Integer i = (Integer) iterator.next();
                        if (rooms.get(i).getUsersCount() == 0)
                            rooms.remove(i);
                        if (rooms.get(i).getUsersCount() == rooms.get(i).getCapacity() && rooms.get(i).isGameStarted()){
                            Thread.currentThread().sleep(2500);
                            rooms.remove(i) ;
                        }
                    }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        emptyAndFullRoomRemover.start();

        ExecutorService executorService = Executors.newFixedThreadPool(5) ;
        Socket socket = null ;
        while (true){
            System.out.println("waiting for connection");
            socket = server.accept() ;
            System.out.println("user connected");
            executorService.execute(new UserHandler(socket , rooms));

        }

//        Socket socket = null ;
//        while (true){
//            socket = server.accept() ;
//            new Thread(new UserHandler(socket,rooms)).start();
//        }
    }
}
