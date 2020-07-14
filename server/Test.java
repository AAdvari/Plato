package Plato.server;

import java.io.ObjectStreamException;
import java.lang.invoke.StringConcatFactory;
import java.lang.reflect.Array;
import java.util.*;

public class Test {

    public static void main(String args[]){
        Map<String , User> singleMap = new HashMap<>() ;

        singleMap.put("amir" , new User("amir" , "1234")) ;
        singleMap.put("reza" , new User("reza" , "1234")) ;
        singleMap.put("ahmad" , new User("ahmad" , "1234")) ;
        singleMap.put("majid" , new User("majid" , "1234")) ;
        singleMap.put("mehrdad" , new User("mehrdad" , "1234")) ;


        ArrayList<User> users = (ArrayList<User>) singleMap.values();
        for (User user : users){

        }


    }
}
