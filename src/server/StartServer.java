package server;

/**
 * Created by Natnael on 3/29/2017.
 * start multithreaded server.
 */
public class StartServer {

    public static void main(String[] args) {
        System.out.println("ServerSide starting ...");
        ServerSide server = new ServerSide(args[0], 4444, 10);
        server.init();

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        server.stop();
        System.out.println("ServerSide Terminated");
    }

}
