package com.plato.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    public static volatile UsersList users = new UsersList() ;
    public static final String DATABASE_DIRECTORY = "/db.plato" ;

    public static volatile ConcurrentHashMap<Integer , Room> rooms = new ConcurrentHashMap<>() ;
    public static void main(String args[]) throws IOException {


        ServerSocket server = new ServerSocket(4000) ;

        Thread updater = new Thread(new Runnable() {
            @Override
            public void run() {
                Scanner scanner = new Scanner(System.in);
                System.out.println("Enter update to Update Database : ");
                String command = scanner.nextLine() ;
                if (command.equals("update")){
                    File file = new File("/db.plato");
                    if ( ! file.exists() )
                        file.delete() ;
                    try {
                        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
                        synchronized (users.getUsers()) {
                            oos.writeObject(users.getUsers());
                            oos.flush();
                            oos.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }) ;
        updater.start();


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
                            Thread.currentThread().sleep(10000);
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

        ExecutorService executorService = Executors.newFixedThreadPool(4000) ;
        Socket socket = null ;
        while (true){
            socket = server.accept() ;
            executorService.execute(new UserHandler(socket , rooms , users.getUsers()));

        }

//        Socket socket = null ;
//        while (true){
//            socket = server.accept() ;
//            new Thread(new UserHandler(socket,rooms , users.getUsers())).start();
//        }
    }
}
