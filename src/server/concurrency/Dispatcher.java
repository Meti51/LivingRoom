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
 * Created by Natnael on 3/30/2017.
 */
public class Dispatcher extends Thread {

    private Queue<Request> queue;
    private ServerSocket server;

    public Dispatcher(String name, ServerSocket server, Queue clientQueue) {
        super(name);
        this.queue = clientQueue;
        this.server = server;
    }

    @Override
    public void run() {
        Socket client = null;
        BufferedReader in = null;

        System.out.println(getName() + " Started");

        while(true) {
            try {
                client = server.accept();
                System.out.println(getName() + " connected to " + client.toString());
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));

                /* Add request to buffer. will be serviced with worker threads */
                queue.offer(new Request(new Command(in.readLine()), client));

                System.out.println(getName() + " Added to buffer");
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

            // Don't stress the CPU
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            /* way to stop threads */
            if (Thread.interrupted()) {
                System.out.println("ServerSide Preparing to terminate");
                break;
            }
        }
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
            return true;
        }
        if (command.isEmpty()) {
            return true;
        }

//        char[] val = command.toCharArray();
//
//        if (val[0] != '<' || val[val.length-1] != '>') {
//            return true;
//        }

        return false;
    }
}
