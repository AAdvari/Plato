package Plato;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UsersList {
    private static ConcurrentHashMap<String , User> singleMap = null ;
    private UsersList(){}

    public static Map<String , User> getUsersList(){
        if(singleMap==null)
            singleMap = new ConcurrentHashMap<String , User>() ;
        return singleMap ;
    }
}
