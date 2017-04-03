package client.concurrency;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * Created on 4/1/2017.
 * @author Natnael Seifu [seifu003]
 */
public class Writer extends Thread {

    private String name = null;
    private Socket socket = null;

    public Writer (String name, Socket socket) {
        super(name);
        this.name = name;
        this.socket = socket;
    }

    @Override
    public void run() {
//        System.out.println(name + " Started");
        Scanner keyboard = new Scanner(System.in);
        PrintWriter outStream;

        try {
            outStream = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        /* block for user input every loop */
        while (!Thread.interrupted()) {
            String message = keyboard.nextLine();

            outStream.println(getName() + "," + message);

            /*
             * Don't stress the CPU
             * writer sleep more than reader.
             *
             * This is needed for client termination coordination.
             * Other wise this thread will not get the interrupt signal
             * since it blocks and wait for I/O
             */
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
//                System.out.println(e.getMessage());
                break;
            }
        }
        System.out.println(getName() + "'s Writer thread has terminated");
    }
}
