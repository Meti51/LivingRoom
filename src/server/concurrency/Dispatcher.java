package server.concurrency;

import static server.server_const.Limits.CLIENTLIMIT;

import server.server_main.Server;
import server.command.Command;
import server.request.Request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Queue;

/**
 * Accept connection from client and place request
 * in service buffer. operations need to be atomic.
 *
 * Created on 3/30/2017.
 * @author Natnael Seifu [seifu003]
 */
public class Dispatcher extends Thread {

    private Queue<Request> queue;
    private ServerSocket server;

    public Dispatcher(String name, ServerSocket server, Queue<Request> clientQueue) {
        super(name);
        this.queue = clientQueue;
        this.server = server;

    }

    @Override
    public void run() {
        Socket client = null;
        BufferedReader in = null;

        System.out.println(getName() + " Started");

        while(!Thread.interrupted()) {
            /* Accept connection */
            try {
                client = server.accept();
            } catch (IOException e) {
                System.out.println("Dispatchet Accept: " + e.getMessage());
            }

            /* Stop dispatcher if server is closed */
            if (server.isClosed()) break;

            System.out.println(getName() + " connected to " + client);

            if (client != null) {
                try {
                    in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                } catch (IOException e) {
                    System.out.println("Dispatcher reader: " + e.getMessage());
                }
            }

            /*
            Create new thread to handle
            persistent connection.
             */
            final Socket finalClient = client;
            final BufferedReader finalIn = in;
            if (Server.getClientThreadCounter() < CLIENTLIMIT) {
                if (finalIn != null) {
                    new Thread() {
                        public void run() {
                            /* Keep connection persistent for client */
                            System.out.println("Client Thread started");
                            while (true) {
                                try {
                                    String req = null;
                                    if (finalIn.ready()) {
                                        req = finalIn.readLine();
                                    }

                                    if (validate(req)) {
                                    /*
                                    Add request to buffer and will be
                                    serviced by worker threads.
                                    */
                                        queue.offer(new Request(new Command(req), finalClient));
                                    }
                                } catch (IOException e) {
                                /* client ended connection */
                                    System.out.println("Client: " + e.getMessage());
                                    break;
                                }

                                if (server.isClosed()) {
                                    if (finalClient != null) {
                                        try {
                                            finalClient.close();
                                        } catch (IOException e) {
                                            System.out.println(e.getMessage());
                                        }
                                    }
                                    break;
                                }

                                // Don't stress the CPU
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    break;
                                }
                            }
                            System.out.println("Persistent Client thread terminated");
                            Server.clientThreadCounter(-1);
                        }
                    }.start();
                    /* synchronized thread clientThreadCounter increment */
                    Server.clientThreadCounter(1);
                } else {
                    System.out.println("Persistent thread creation limit reached");
                    queue.offer(new Request(new Command("disconnect"), client));
                }
            }
        }

        System.out.println(getName() + " Stopped");
    }

    /**
     * validate incoming command
     * returns false if validation failed.
     *
     * @param command - incoming transmission
     * @return -
     */
    private boolean validate(String command) {
        return command != null && !command.isEmpty();
    }
}
