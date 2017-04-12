package client.concurrency;

import static enums.Functions.FGET;

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

    private Socket socket = null;

    public Reader(String name, Socket inStream) {
        super(name);
        this.socket = inStream;
    }

    @Override
    public void run() {
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
                    transHandler(msg);
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
                Thread.sleep(100);
            } catch (InterruptedException e) {
//                System.out.println(e.getMessage());
                break;
            }
        }

        System.out.println(getName() + "'s Reader thread has terminated");
    }

    /**
     * if the client has requested for file, this
     * procedure will create the FileReceiver thread
     * and transfer control.
     *
     * @param transmission incoming message from server
     * @see FileReceiver
     */
    private void transHandler (String transmission) {

        String[] theFist = transmission.split(",");

        switch (theFist[0]) {

            case FGET:
                new FileReceiver(getName(), theFist[1],
                    theFist[2], theFist[3]).start();
                break;

            default:
                System.out.println(transmission);
                break;

        }
    }
}
