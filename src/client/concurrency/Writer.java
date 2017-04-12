package client.concurrency;

import static client.constants.ClientConsts.PEERPORT;

import enums.Functions;

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

    private Socket socket = null;

    public Writer (String name, Socket socket) {
        super(name);
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
                Thread.sleep(500);
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
        String[] sp = raw.split(",");
        String rVal = sp[0].trim().toUpperCase();

        switch (rVal) {

            case Functions.CLIST:
                rVal = "<CLIST>";
                break;

            case Functions.FLIST:
                rVal = "<" + Functions.FLIST + ">";
                break;

            case Functions.FPUT:
                /*
                user only enters command and file name.
                socket info is determined by client app.
                <FPUT,filename,ip_addr,port>
                */
                if (sp.length == 4) {
                    rVal = "<" + Functions.FPUT + "," +
                                sp[1].trim() + "," +
                                sp[2].trim() + "," +
                                sp[3].trim() + "," + ">";
                }

                /* minimum length of 2 */
                if (sp.length == 2) {
                    /*
                     relieve the user of reponsibility
                     to find out what its ip address and port
                     number is. user will only send file name.
                     since server is aware of users public ip address
                     and port number, it can fill the info when request
                     is received.
                     */
                    rVal = "<" + Functions.FPUT + "," +
                            sp[1].trim() + "," +
                                    "%," +
                            PEERPORT + "," + ">";
                }
                break;

            case Functions.FGET:
                if (sp.length == 2) {
                    rVal = "<" + Functions.FGET + "," + sp[1].trim() + ">";
                } else {
                    /* server throw invalidformat */
                    rVal = "<" + Functions.FGET + ">";
                }
                break;

            case Functions.DISCONNECT:
                rVal = "<DISCONNECT>";
                break;

            default:
                rVal = "<MSG," + "[" + getName() + "] " + raw + ">";
                break;

        }

        return rVal;
    }
}
