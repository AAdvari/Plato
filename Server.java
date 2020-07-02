import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    public static void main(String args[]) throws IOException {
        File userDataFile = new File("")

        ServerSocket server = new ServerSocket(4000) ;
        ExecutorService executorService = Executors.newFixedThreadPool(2) ;

        Socket socket = null ;
        while (true){
            socket = server.accept() ;
            executorService.execute(new UserHandler(socket));

        }

    }
}
