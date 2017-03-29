package server;

import java.net.ServerSocket;

/**
 * Created by Natnael on 3/29/2017.
 * start multithreaded server.
 */
public class StartServer {

    public static void main(String[] args) {
        System.out.println("Server starting ...");
        Server server = new Server(4444, 10);
        server.init();

//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        server.stop();
        System.out.println("Server Terminated");
    }

}
