package Plato ;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class UserHandler implements Runnable {
    private final DataInputStream dis ;
    private final DataOutputStream dos ;
    private final ObjectInputStream ois ;
    private final ObjectOutputStream oos ;
    private Map<String , User> users =  UsersList.getUsersList();
    public UserHandler(Socket socket) throws IOException {
        dis = new DataInputStream(socket.getInputStream()) ;
        dos = new DataOutputStream(socket.getOutputStream()) ;
        ois = new ObjectInputStream(socket.getInputStream()) ;
        oos =  new ObjectOutputStream(socket.getOutputStream()) ;
    }
    @Override
    public void run() {
        while (true){
            try {
                String command = dis.readUTF() ;
                switch (command){
                    case "login" :{
                        // These lines may change in order to simultaneous operations in client
                        String username,password ;
                        username = dis.readUTF() ;
                        password = dis.readUTF() ;

                        if(users.containsKey(username) && users.get(username).getPassword().equals(password))
                            oos.writeObject(users.get(username));
                        else
                            oos.writeObject(null);


                    }
                    case "register" : {
                        String username,password ;
                        username = dis.readUTF() ;
                        password = dis.readUTF() ;
                        users.put(username , new User(username , password)) ;
                    }
                    case "search_for_friend" : {
                        String username ;
                        username = dis.readUTF() ;
                        ArrayList<User> compatible  = new ArrayList<>();
                        Set<String> usernames = users.keySet();

                    }
                    case "make_room":{

                    }
                    case "join_room":{
                        // CountDown
                        // Controlling Game
                    }
                }

            } catch (IOException e) {

                // What if user disconnects ? (maybe in the game )
                e.printStackTrace();
            }

        }

    }
}
