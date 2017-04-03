package server.concurrency;

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
        BufferedReader in;

        System.out.println(getName() + " Started");

        while(!Thread.interrupted()) {
            try {
                client = server.accept();
                System.out.println(getName() + " connected to " + client.toString());
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));

                /* Keep connection for client */
                while (!client.isClosed()) {
                    String req = in.readLine();

                    if (validate(req)) {
                        /* Add request to buffer. will be serviced with worker threads */
                        queue.offer(parse(req, client));
                    }

                    // Don't stress the CPU
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
//                        System.out.println(e.getMessage());
                        break;
                    }
                }
            } catch (IOException e) {
                System.out.println(client + " " + e.getMessage());
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
        if (command == null) {
            return false;
        }
        if (command.isEmpty()) {
            return false;
        }

        String[] val = command.split(",");

        if (val.length == 1) {
            return false;
        }

        return true;
    }

    /**
     * Create Request from incoming transmission
     *
     * @param req - raw transmission
     * @param client - connection to client
     * @return - Request object
     */
    private Request parse(String req, Socket client) {
        Request request;

        String spiltRaw[] = req.split(",");
        String requester = spiltRaw[0];

        String rawCmd = "";
        for (int i = 1; i < spiltRaw.length; i++) {
            rawCmd = rawCmd.concat(spiltRaw[i].trim());
            if (i < spiltRaw.length - 1) {
                rawCmd = rawCmd.concat(",");
            }
        }
        Command cmd = new Command(rawCmd);
        request = new Request(requester, cmd, client);

        return request;
    }
}
