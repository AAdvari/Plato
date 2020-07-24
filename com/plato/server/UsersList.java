package com.plato.server;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UsersList {
    private volatile ConcurrentHashMap<String, User> users = null;

    public UsersList() {

        // Load From File

//            File file = new File(Server.DATABASE_DIRECTORY) ;
//            try {
//                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file)) ;
//                singleMap = (ConcurrentHashMap<String , User>) ois.readObject() ;
//                ois.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (ClassNotFoundException e) {
//                e.printStackTrace();
//            }


        //Just For Test
        users = new ConcurrentHashMap<String, User>();


        users.put("amir", new User("amir", "1234"));
        users.put("reza", new User("reza", "1234"));
        users.put("ahmad", new User("ahmad", "1234"));
        users.put("majid", new User("majid", "1234"));
        users.put("mehrdad", new User("mehrdad", "1234"));


        Conversation c = new Conversation();

        User amir = users.get("amir");
        User reza = users.get("reza");

        amir.addConversation(reza, c);
        reza.addConversation(amir, c);

        TextMessage[] t = new TextMessage[20];
        t[0] = new TextMessage(new Date(), amir, "Hi, Can You Hear Me?");
        t[1] = new TextMessage(new Date(), reza, "OMG YES IT'S Working");
        t[2] = new TextMessage(new Date(), amir, "Dude Android Studio Sucks");
        t[3] = new TextMessage(new Date(), amir, "I hate it");
        t[4] = new TextMessage(new Date(), reza, "I Know man");
        t[5] = new TextMessage(new Date(), amir, "So... Chat works rn?");
        t[6] = new TextMessage(new Date(), reza, "I think so");
        t[7] = new TextMessage(new Date(), reza, "Let's test it");
        t[8] = new TextMessage(new Date(), amir, "We are doing it right now");
        t[9] = new TextMessage(new Date(), amir, "idiot");
        t[10] = new TextMessage(new Date(), reza, "STFU Im the one who fixed it");
        t[11] = new TextMessage(new Date(), amir, "If you didn't fucked up \n you didn't need to fix it moron");
        t[12] = new TextMessage(new Date(), amir, "Consider yourself dead from now \n disgusting animal");
        t[13] = new TextMessage(new Date(), amir, "idiot");
        t[14] = new TextMessage(new Date(), reza, "STFU Im the one who fixed it");
        t[15] = new TextMessage(new Date(), amir, "If you didn't fucked up \n you didn't need to fix it moron");
        t[16] = new TextMessage(new Date(), amir, "Consider yourself dead from now \n disgusting animal");

        for (int i = 0; i < 17 ; i++)
            c.sendMessage(t[i]);

        System.out.println("conversation is set");


    }

    public ConcurrentHashMap<String, User> getUsers() {
        return users;
    }
}
