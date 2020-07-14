package Plato.server;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    public static volatile ConcurrentHashMap<Integer , Room> rooms = new ConcurrentHashMap<>() ;
    public static void main(String args[]) throws IOException {

        ServerSocket server = new ServerSocket(4000) ;
//        ExecutorService executorService = Executors.newFixedThreadPool(2) ;
//
//        Socket socket = null ;
//        while (true){
//            socket = server.accept() ;
//            executorService.execute(new UserHandler(socket , rooms));
//
//        }
        Socket socket = null ;
        while (true){
            socket = server.accept() ;
            new Thread(new UserHandler(socket,rooms )).start();
            System.out.println("hhn");
        }

    }
}
