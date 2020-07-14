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
    private User currentUser = null ; // Will Be Assigned after Login .... !
    private ConcurrentHashMap<Integer,Room> rooms ;

    private Map<String , User> users =  UsersList.getUsersList();

    public UserHandler(Socket socket , ConcurrentHashMap<Integer, Room> rooms) throws IOException {
        this.rooms = rooms ;
        dis = new DataInputStream(socket.getInputStream()) ;
        dos = new DataOutputStream(socket.getOutputStream()) ;
        ois = new ObjectInputStream(socket.getInputStream()) ;
        oos = new ObjectOutputStream(socket.getOutputStream()) ;
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

                        if(users.containsKey(username) && users.get(username).getPassword().equals(password)) {
                            User foundUser = users.get(username) ;
                            oos.writeObject(foundUser);
                            this.currentUser = foundUser ;
                        }
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
                        Room room = new Room(new UserAndHandler(currentUser , this) , "Temp1" , 2) ; // roomOwner Will be added automatically to room !
                        rooms.put(Room.number , room) ;
                        room.start();
                        this.wait();
                    }
                    case "join_room":{
                        int roomId = dis.readInt() ;
                        Room joiningRoom = rooms.get(roomId) ;
                        joiningRoom.addUser(new UserAndHandler(currentUser , this));
                        this.wait();
                        // changing GameRunning boolean is done in addUser method (Room Class)
                    }
                    case "get_rooms":{
                        oos.writeObject(rooms);
                    }
                    case "watch":{
                        int roomId = dis.readInt() ;
                        Room watchingRoom = rooms.get(roomId) ;
                        this.wait();
                    }
                    case "send_message":{

                    }
                }
            } catch (IOException  e) {

                // What if user disconnects ? (maybe in the game )
                System.out.println("!");
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }

    public DataInputStream getDis() {
        return dis;
    }

    public DataOutputStream getDos() {
        return dos;
    }

    public ObjectInputStream getOis() {
        return ois;
    }

    public ObjectOutputStream getOos() {
        return oos;
    }
}
