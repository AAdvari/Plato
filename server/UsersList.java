package Plato.server;

import java.io.*;
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
        users.put("amir" , new User("amir" , "1234")) ;
        users.put("reza" , new User("reza" , "1234")) ;
        users.put("ahmad" , new User("ahmad" , "1234")) ;
        users.put("majid" , new User("majid" , "1234")) ;
        users.put("mehrdad" , new User("mehrdad" , "1234")) ;

    }

    public ConcurrentHashMap<String, User> getUsers() {
        return users;
    }
}
