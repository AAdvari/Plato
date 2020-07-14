package Plato.server;

import com.sun.jdi.ObjectCollectedException;

import java.io.*;
import java.util.concurrent.ConcurrentHashMap;

public class Test {

    public static void main(String args[]) throws IOException, ClassNotFoundException {

            File file = new File(Server.DATABASE_DIRECTORY) ;
        ObjectOutputStream oos =new ObjectOutputStream(new FileOutputStream(file));

        ConcurrentHashMap<String , User> singleMap = new ConcurrentHashMap<>( );
        singleMap.put("amir" , new User("amir" , "1234")) ;
        singleMap.put("reza" , new User("reza" , "1234")) ;
        singleMap.put("ahmad" , new User("ahmad" , "1234")) ;
        singleMap.put("majid" , new User("majid" , "1234")) ;
        singleMap.put("mehrdad" , new User("mehrdad" , "1234")) ;
        oos.writeObject(singleMap);
        oos.flush();
        oos.close();
        System.out.println("Done ! ");

        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file)) ;

        ConcurrentHashMap<String , User> obj = (ConcurrentHashMap<String , User>) ois.readObject() ;
        System.out.println(obj);
        ois.close();

    }
}
