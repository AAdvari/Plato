package com.plato.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UsersList {
    private volatile static ConcurrentHashMap<String , User> singleMap = null ;
    private UsersList(){

    }

    public static Map<String , User> getUsersList(){
        // some default users made due to testing purposes ...
        if(singleMap==null) {
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
            singleMap = new ConcurrentHashMap<String, User>();
            singleMap.put("amir" , new User("amir" , "1234")) ;
            singleMap.put("reza" , new User("reza" , "1234")) ;
            singleMap.put("ahmad" , new User("ahmad" , "1234")) ;
            singleMap.put("majid" , new User("majid" , "1234")) ;
            singleMap.put("mehrdad" , new User("mehrdad" , "1234")) ;
        }
        return singleMap ;
    }

}
