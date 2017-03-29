package server.concurrency;

import server.enums.ServerStatus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Servers worker thread.
 * Each thread accept client request and
 * service with the appropriate response.
 *
 * Created by on 3/29/2017
 * @author Natnael Seifu
 */
public class Worker extends Thread {

    private ServerSocket server = null;
    private BufferedReader in = null;
    private PrintWriter out = null;

    public Worker (String name, ServerSocket server) {
        super(name);
        this.server = server;
    }

    @Override
    public void run() {
        System.out.println(getName() + " Started");
        while(true) {

            Socket client = null;

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
                    if (client == null) {
                        break;
                    }

                    String line = in.readLine();
                    System.out.println(line);

                    if (line.contains("disconnect")) {
                        out.println("Bye");
                        client.close();
                        break;
                    }
                    out.println("hello there");
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

//            try {
//                Thread.sleep(6000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

            if (this.isInterrupted()) {
                break;
            }
        }
        System.out.println(getName() + " Stopped");
    }

    public String parse() {
//        try {
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return null;
    }

    public synchronized ServerStatus registerClient(String clientID, String password) {
        return ServerStatus.SUCCESS;
    }

    public synchronized ServerStatus loginClient(String clientID, String password) {
        return ServerStatus.SUCCESS;
    }

    public synchronized ServerStatus disconnectClient(String clientID) {
        // disconnect a client aka remove from active list
        return ServerStatus.SUCCESS;
    }

    public synchronized ServerStatus message(String message) {
        // broad cast message to all online clients
        return ServerStatus.SUCCESS;
    }
}
