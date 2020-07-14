package Plato.server;

import java.io.*;
import java.util.ArrayList;

public class Room extends Thread {


    public static int number = 0;
    private final int id;
    private final int capacity;
    private final String name;
    private final String type; //"casual" or "ranked"

    private boolean gamersReadyForCount = false;
    private boolean gameStarted = false;


    private volatile UserAndHandler user1;
    private volatile UserAndHandler user2;
    private volatile ArrayList<UserAndHandler> watchers = new ArrayList<>();


    public Room(UserAndHandler user, String type, String name, int capacity) {
        addUser(user);
        ;
        id = number;
        this.capacity = capacity;
        this.name = name;
        this.type = type;

        number++;
    }

    public synchronized void addUser(UserAndHandler user) {
        if (user1 == null)
            user1 = user;
        else if (user2 == null)
            user2 = user;
        else
            return;
        if( getUsersCount() == capacity)
            gamersReadyForCount = true ;

        new Thread(new GameProvider(user)).start();
    }

    public int getUsersCount() {
        if (user1 == null && user2 != null)
            return 1;
        else if (user2 != null && user1 == null)
            return 2;
        else
            return 0;
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
                if( getUsersCount() == capacity)
                    break;
            }
            gameStarted = true ;

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    class GameProvider implements Runnable {

        private UserAndHandler userAndHandler;
        private ObjectOutputStream oos;
        private ObjectInputStream ois;
        private User userData;

        public GameProvider(UserAndHandler userAndHandler) {
            this.userAndHandler = userAndHandler;
            this.oos = userAndHandler.getUserHandler().getOos();
            this.ois = userAndHandler.getUserHandler().getOis();
            this.userData = userAndHandler.getUser();
        }

        @Override
        public void run() {
            try {
                while (true) {
                    String command = null;
                    command = ois.readUTF();
                    switch (command) {

                        case "quit": {
                            if (userData.equals(user1))
                                user1 = null;
                            else
                                user2 = null;
                            userAndHandler.getUserHandler().notify();
                            break;
                        }
                        /// Other Cases is Based On The Game !
                    }


                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

    }
    class StreamProvider{
        // SomeThing Similar to GameProvider !

    }
}

