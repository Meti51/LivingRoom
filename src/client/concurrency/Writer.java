package client.concurrency;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * read user input, format and send to server
 * as a request.
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
            outStream = new PrintWriter(socket.getOutputStream(),
                    true);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }

        /* block for user input every loop */
        while (!Thread.interrupted()) {
            String message = keyboard.nextLine();

            outStream.println(constTransmission(message));

            /*
             * Don't stress the CPU
             * writer sleep more than reader.
             *
             * This is needed for client termination coordination.
             * Other wise this thread will not get the interrupt signal
             * since it blocks and wait for I/O
             */
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
//                System.out.println(e.getMessage());
                break;
            }
        }

        System.out.println(getName() + "'s Writer thread has terminated");
    }

    /**
     * formats raw string to server usable
     * command string.
     * <p>
     * Note: this is considered as raw by server.
     *
     * @param raw user input
     * @return formatted command string
     */
    private String constTransmission (String raw) {
        String rVal = raw.trim().toUpperCase();

        switch (rVal) {

            case "CLIST":
                rVal = "<CLIST>";
                break;

            case "DISCONNECT":
                rVal = "<DISCONNECT>";
                break;

            default:
                rVal = "<MSG," + "[" + getName() + "] " + raw + ">";
                break;

        }

        return rVal;
    }
}
