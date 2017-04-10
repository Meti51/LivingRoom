package client.concurrency;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.Socket;

/**
 * Blocks until message sent from server.
 * read message from server and display for
 * client.
 *
 * Created on 4/1/2017.
 * @author Natnael Seifu [seifu003]
 */
public class Reader extends Thread {

    private String name = null;
    private Socket socket = null;

    public Reader(String name, Socket inStream) {
        super(name);
        this.name = name;
        this.socket = inStream;
    }

    @Override
    public void run() {
//        System.out.println(name + " Started");
        BufferedReader inStream;

        try {
            inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }

        /* Listen loop */
        while (!Thread.interrupted()) {
            try {
                String msg = inStream.readLine();
                if (msg != null) {
                    System.out.println(msg);
                } else {
                    /* Underlying socket closed from server side*/
                    break;
                }
            } catch (IOException e) {
                System.out.println(getName() + " " + e.getMessage());
                break;
            }

            // Don't stress the CPU
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
//                System.out.println(e.getMessage());
                break;
            }
        }

        System.out.println(getName() + "'s Reader thread has terminated");
    }
}
