package server;

/**
 * Helper class to start Server
 *
 * Created by Natnael on 3/29/2017.
 * start multithreaded server.
 */
public class StartServer {

    public static void main(String[] args) {
        System.out.println("Server starting ...");
        Server server = new Server(args[0], 4444, 1);
        server.init();

//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        System.out.println("Server Terminated");
    }

}
