package Plato.server;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UserHandler implements Runnable {

    private final ObjectInputStream ois;
    private final ObjectOutputStream oos;
    private volatile User currentUser = null; // Will Be Assigned after Login .... !

    private volatile ConcurrentHashMap<Integer, Room> rooms;

    private volatile ConcurrentHashMap<String, User> users ;

    public UserHandler(Socket socket, ConcurrentHashMap<Integer, Room> rooms , ConcurrentHashMap<String , User> users) throws IOException {
        this.rooms = rooms;
        this.users = users ;
        oos = new ObjectOutputStream(socket.getOutputStream());
        oos.flush();
        ois = new ObjectInputStream(socket.getInputStream());
    }

    @Override
    public void run() {


        while (true) {
            try {

                String command = ois.readUTF();
                System.out.println("command :" + command);
                switch (command) {
                    case "login": {
                        // These lines may change in order to simultaneous operations in client(Change it :) )
                        String username, password;
                        username = ois.readUTF();
                        password = ois.readUTF();

                        if (users.containsKey(username) && users.get(username).getPassword().equals(password)) {
                            User foundUser = users.get(username);
                            oos.reset();
                            oos.writeObject(foundUser);
                            this.currentUser = foundUser;
                        } else
                            oos.writeObject(null);

                        oos.flush();

                        break;
                    }
                    case "register": {
                        String username, password;
                        username = ois.readUTF();
                        password = ois.readUTF();
                        if (!users.containsKey(username)) {
                            users.put(username, new User(username, password));
                            oos.writeUTF("successful");
                        } else {
                            oos.writeUTF("failed");
                        }
                        oos.flush();


                        /*  Just For Test */
                        System.out.println(users);
                        break;
                    }
                    case "search_for_friend": {
                        String username;
                        username = ois.readUTF();
                        ArrayList<User> compatible = new ArrayList<>();
                        Set<String> usernames = users.keySet();
                        int count = 0;
                        for (String user : usernames) {
                            if (count == 3)
                                break;
                            if (user.contains(username)) {
                                compatible.add(users.get(user));
                                count++;
                            }
                        }
                        oos.writeObject(compatible);
                        oos.flush();

                        break;
                    }
                    case "send_friend_request": {

                        String destUsername = ois.readUTF() ;
                        users.get(destUsername).addFriendRequest(currentUser.getUsername());

                        break;

                    }
                    case "send_friend_request_answer":{

                        String destUser = ois.readUTF() ;
                        String answer = ois.readUTF() ;
                        if(answer.equals("accept")){
                            users.get(destUser).addFriend(currentUser);
                            currentUser.addFriend(users.get(destUser));
                            currentUser.removeFriendRequest(destUser);
                        }
                        if(answer.equals("reject")){
                            currentUser.removeFriendRequest(destUser);
                        }
                        break;
                    }
                    case "make_room": {
                        String name,type;
                        int capacity ;
                        name = ois.readUTF() ;
                        type = ois.readUTF() ;
                        capacity = ois.readInt() ;


                        Room room = new Room(type, name, rooms, capacity);
                        room.addUser(new UserAndHandler(currentUser , this));
                        rooms.put(room.getRoomId(), room);
                        room.start();
                        synchronized (this){
                            this.wait();
                        }

                        break;
                    }
                    case "join_room":{
                        int roomId = ois.readInt();
                        Room joiningRoom = rooms.get(roomId);
                        System.out.println(joiningRoom);
                        joiningRoom.addUser(new UserAndHandler(currentUser, this));
                        System.out.println("User Added !");
                        synchronized (this){
                            this.wait();
                        }
                        break;
                        // changing GameRunning boolean is done in addUser method (Room Class)
                    }

                    case "get_rooms": {
                        ConcurrentHashMap<Integer , RoomInfo> roomsInfo = new ConcurrentHashMap<>( );
                        for (Room room : rooms.values()){
                            roomsInfo.put( room.getRoomId() , new RoomInfo(
                                    room.getRoomName() , room.getRoomType() ,room.getRoomId(), room.getCapacity() , room.isGameStarted()) ) ;
                        }
                        oos.writeObject(roomsInfo);
                        oos.flush();
                        break;
                    }
                    case "watch": {
                        int roomId = ois.readInt();
                        Room watchingRoom = rooms.get(roomId);
                        watchingRoom.addWatcher(new UserAndHandler(currentUser , this));
                        synchronized (this){
                            this.wait();
                        };
                        break;
                    }
                    case "send_message": {
                        String destUsername = ois.readUTF();
                        String messageContent = ois.readUTF();

                        User destUser = users.get(destUsername);
                        if (currentUser.getConversation(destUser) == null) {
                            Conversation conversation = new Conversation() ;
                            currentUser.addConversation(destUser , conversation);
                            destUser.addConversation(currentUser , conversation);
                        }
                        Conversation conversation = currentUser.getConversation(destUser);
                        conversation.sendMessage(new TextMessage(new Date(), currentUser, messageContent));

                        System.out.println(users.get(destUsername).getConversation(currentUser).getMessages());
                        System.out.println(currentUser.getConversation(destUser).getMessages());
                        System.out.println(conversation.getMessages());
                        break;
                    }
                    case "leader_board":{
                        String game = ois.readUTF() ;
                        ArrayList<UserScoreInfo> usersScores = new ArrayList<>() ;
                        for (User user : users.values()){
                            usersScores.add(new UserScoreInfo(user.getUsername() , user.getGameScore(game))) ;
                        }
                        usersScores.sort((o1, o2) -> o1.getScore().compareTo(o2.getScore()));
                        oos.writeObject(usersScores);
                        oos.flush() ;
                        break;

                    }
                    case "update_username":{
                        String newUsername = ois.readUTF() ;
                        if(users.containsKey(newUsername))
                            oos.writeUTF("failed");
                        else {
                            oos.writeUTF("successful");
                            currentUser.setUsername(newUsername);
                        }
                        oos.flush();
                        break;

                    }
                    case "update_profile_pic":{
                        byte[] profilePicBytes = (byte[])ois.readObject() ;
                        currentUser.setProfilePic(profilePicBytes);
                        break;

                    }
                    case "update_bio_text":{
                        String bioText = ois.readUTF() ;
                        currentUser.setBioText(bioText);
                        break;

                    }

                    // Test
                    case "get_info":{
                        System.out.println(currentUser);
                        break;
                    }

                }

            } catch (IOException e) {


                System.out.println("!");
                e.printStackTrace();
                break;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }

    }


    public ObjectInputStream getOis() {
        return ois;
    }

    public ObjectOutputStream getOos() {
        return oos;
    }
}
