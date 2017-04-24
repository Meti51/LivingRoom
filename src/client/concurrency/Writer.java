package client.concurrency;

import static client.client_port.ClientPort.PEERPORT;

import enums.Functions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

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
        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
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
            String message = null;

            try {
                if (keyboard.ready()) {
                    message = keyboard.readLine();
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

            if (message != null) {
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
                    Thread.sleep(200);
                } catch (InterruptedException e) {
//                System.out.println(e.getMessage());
                    break;
                }
            }
        }

        System.out.println(getName() + "'s Writer thread has terminated");
    }

    /**
     * formats raw string to server usable
     * command string.
     * <p>
     * Note: this is considered as raw input by server.
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
                if (sp.length == 4 && fileExists(sp[1].trim())) {
                    rVal = "<" + Functions.FPUT + "," +
                                sp[1].trim() + "," +
                                sp[2].trim() + "," +
                                sp[3].trim() + "," + ">";
                } else if (sp.length == 2  && fileExists(sp[1].trim())) {
                    /*
                     relieve the user of reponsibility to find out
                     the ip address. user will only send file name.
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

    private boolean fileExists(String fileName) {
        File file = new File(fileName);

        try {
            new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            return false;
        }

        return true;
    }
}
