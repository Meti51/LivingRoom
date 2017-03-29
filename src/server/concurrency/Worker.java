package server.concurrency;

import server.enums.Function;
import server.enums.ServerStatus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

/**
 * Servers worker thread.
 * Each thread accept client request and
 * service with the appropriate response.
 *
 * Created by on 3/29/2017
 * @author Natnael Seifu
 */
public class Worker extends Thread {

    private ServerSocket server;

    public Worker (String name, ServerSocket server) {
        super(name);
        this.server = server;
    }

    @Override
    public void run() {
        System.out.println(getName() + " Started");
        while(true) {

            Socket client;
            BufferedReader in;
            PrintWriter out;

            try {
                client = server.accept();
                System.out.println("Thread " + getName() + " connected to " + client.toString());
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                out = new PrintWriter(client.getOutputStream(), true);
            } catch (IOException e) {
                System.out.println(e.getMessage());
                System.out.println("Server may be down");
                System.out.println("Please try again");
                System.out.println("Shutting down");
                break;
            }

            // communication loop
            while (true) {
                try {
                    String line = in.readLine();
                    if (validate(line)) {
                        out.println("<Invalid command>");
                        out.flush();
                        client.close();
                        break;
                    }

                    String[] command = parse(line);
                    System.out.println(Arrays.toString(command));

                    switch(command[0]) {

                        case Function.REGISTER:
                            break;

                        case Function.LOGIN:
                            break;

                        case Function.MSG:
                            break;

                        case Function.CLIST:
                            break;

                        case Function.DISCONNECT:
                            break;

                        default:
                            break;

                    }
                } catch (IOException e) {
                    System.out.println("client ended connection");
                    break;
                }
            }

            // Don't stress the CPU
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Server is preparing to terminate
            if (this.isInterrupted()) {
                break;
            }
        }
        System.out.println(getName() + " Stopped");
    }

    /**
     * validate incoming command
     * returns true if validation failed.
     *
     * @param command -
     * @return -
     */
    private boolean validate(String command) {
        if (command == null) {
            return false;
        }
        if (command.isEmpty()) {
            return false;
        }

        char[] val = command.toCharArray();

        if (val[0] != '<' || val[val.length-1] != '>') {
            return false;
        }

        System.out.println("Validation failed");
        return true;
    }

    /**
     * validation has to happen before this method is called
     *
     * @param command - raw message
     * @return - expected [FUNC,PAYLOAD]
     */
    private String[] parse(String command) {

        String clean = command.replace("<", "");
        clean = clean.replace(">", "");
        String cmd[] = clean.split(",");

        /* Expected result [function, string] */
        String result[] = new String[2];
        result[0] = cmd[0].trim().toUpperCase();

        String payload = "";
        for (int i = 1; i < cmd.length; i++) {
            payload = payload.concat(cmd[i].trim());
            if (i < cmd.length-1) {
                payload = payload.concat(",");
            }
        }
        result[1] = payload;

        return result;
    }

    private synchronized ServerStatus registerClient(String clientID, String password) {
        return ServerStatus.SUCCESS;
    }

    private synchronized ServerStatus loginClient(String clientID, String password) {
        return ServerStatus.SUCCESS;
    }

    private synchronized ServerStatus disconnectClient(String clientID) {
        // disconnect a client aka remove from active list
        return ServerStatus.SUCCESS;
    }

    private synchronized ServerStatus message(String message) {
        // broad cast message to all online clients
        return ServerStatus.SUCCESS;
    }
}
