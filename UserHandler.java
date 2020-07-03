package Plato ;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class UserHandler implements Runnable {
    private final DataInputStream dis ;
    private final DataOutputStream dos ;
    public UserHandler(Socket socket) throws IOException {
        dis = new DataInputStream(socket.getInputStream()) ;
        dos = new DataOutputStream(socket.getOutputStream()) ;
    }
    @Override
    public void run() {
        while (true){
            try {
                String command = dis.readUTF() ;
                switch (command){
                    case "login" :{

                    }
                    case "register" : {

                    }
                    case "search_for_friend" : {

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
