package client.concurrency;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * Created on 4/11/2017.
 * @author Natnael Seifu [seifu003]
 */
public class FileSender extends Thread{

    private ServerSocket listener;
    private Socket peer;
    private int port;

    public FileSender (String name, int port) {
        super(name);
        this.port = port;
    }

    @Override
    public void run() {
        try {
            listener = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println(getName() + " " + e.getMessage());
            System.exit(0);
        }

        while (!Thread.interrupted()) {

            try {
                peer = listener.accept();
            } catch (IOException e) {
                System.out.println(getName() + " " + e.getMessage());
            }

            sendFile(peer, "");
        }
    }

    private void sendFile(Socket peer, String filePath) {
        System.out.println("sending");
    }
}
