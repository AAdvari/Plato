package Plato ;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class UserHandler implements Runnable {
    private final DataInputStream dis ;
    private final DataOutputStream dos ;
    private final ObjectInputStream ois ;
    private final ObjectOutputStream oos ;
    private ConcurrentHashMap<Integer,Room> rooms ;
    private Map<String , User> users =  UsersList.getUsersList();

    public UserHandler(Socket socket , ConcurrentHashMap<Integer, Room> rooms) throws IOException {
        this.rooms = rooms ;
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
                        int count = 0 ;
                        for(String user : usernames){
                            if(count == 3)
                                break;
                            if( user.contains(username)) {
                                compatible.add(users.get(user));
                                count++ ;
                            }
                        }
                        oos.writeObject(compatible);
                    }
                    case "make_room":{

                        User roomOwner ;
                        roomOwner = (User)ois.readObject() ;
                        Room room = new Room(roomOwner , "Temp1" , 2) ; // roomOwner Will be added automatically to room !
                        rooms.put(Room.number , room) ;
                    }
                    case "join_room":{
                        int roomId = dis.readInt() ;
                        Room joiningRoom = rooms.get(roomId) ;
                        User addingUser = (User) ois.readObject();
                        joiningRoom.addUser(addingUser);
                        if(joiningRoom.getUsersCount() == joiningRoom.getCapacity())
                            joiningRoom.start();
                    }
                    case "get_rooms":{

                    }
                    case "watch":{

                    }
                    case "profile_info":{
                        User requestedUser = (User)ois.readObject() ;

                    }

                }

            } catch (IOException | ClassNotFoundException e) {

                // What if user disconnects ? (maybe in the game )
                e.printStackTrace();
            }

        }

    }
}
