package com.plato.server;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UsersList {
    private volatile  ConcurrentHashMap<String , User> users = null ;
    public UsersList(){

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


        User amir = new User("amir" , "1234");
        User reza = new User("reza" , "1234");

        users.put("amir" , amir) ;
        users.put("reza" , reza) ;
        users.put("ahmad" , new User("ahmad" , "1234")) ;
        users.put("majid" , new User("majid" , "1234")) ;
        users.put("mehrdad" , new User("mehrdad" , "1234")) ;



        Conversation c = new Conversation();

        amir.addConversation(reza,c);
        reza.addConversation(amir,c);

         TextMessage t1 = new TextMessage(new Date(),amir,"HIIIIII");
         TextMessage t2 = new TextMessage(new Date(),reza,"helpooo");
         c.sendMessage(t1);
         c.sendMessage(t2);

        System.out.println("conversation is set");


    }

    public ConcurrentHashMap<String, User> getUsers() {
        return users;
    }
}
