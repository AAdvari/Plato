package Plato;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UsersList {
    private static ConcurrentHashMap<String , User> singleMap = null ;
    public static Map<Dialog , Conversation> chats  = null ;

    private UsersList(){

    }

    public static Map<String , User> getUsersList(){
        // some default users made due to testing purposes ...
        if(singleMap==null) {
            // Load From File
            singleMap = new ConcurrentHashMap<String, User>();
            singleMap.put("amir" , new User("amir" , "1234")) ;
            singleMap.put("reza" , new User("reza" , "1234")) ;
            singleMap.put("ahmad" , new User("ahmad" , "1234")) ;
            singleMap.put("majid" , new User("majid" , "1234")) ;
            singleMap.put("mehrdad" , new User("mehrdad" , "1234")) ;
        }
        return singleMap ;
    }
    public static Map<Dialog , Conversation> getChatsList(){
        if(chats == null){
            // Load From File !
        }
        return chats ;
    }
}
