package Plato;

import java.util.HashMap;
import java.util.Map;

public class UsersList {
    private static HashMap<String , User> singleMap = null ;
    private UsersList(){}

    public static Map<String , User> getUsersList(){
        if(singleMap==null)
            singleMap = new HashMap<String , User>() ;
        return singleMap ;
    }
}
