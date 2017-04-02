package client.concurrency;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * Created by Natnael on 4/1/2017.
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

        while (!Thread.interrupted()) {
            String message = keyboard.nextLine();

            outStream.println(getName() + "," + message);

            // Don't stress the CPU
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
//                System.out.println(e.getMessage());
                break;
            }
        }

        System.out.println(getName() + "'s Writer thread has terminated");
    }
}
