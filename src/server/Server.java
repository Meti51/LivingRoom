package server;

import server.concurrency.Worker;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Natnael on 3/29/2017.
 *
 */
public class Server {

    /* Active clients */
    /* This list is only accessible by synchronized functions */
    private List<User> activeClients = new LinkedList<>();

    private ServerSocket server = null;
    private Thread[] workers = null;  // thread pool
    private int howManyThreads;

    public Server (int port, int howManyThreads) {
        try {
            this.howManyThreads = howManyThreads;
            server = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("Could not listen on port " + port);
            e.printStackTrace();
        }
    }

    /**
     * Initialize server
     */
    public void init() {
        createThreadpool(howManyThreads);
    }

    /**
     * Terminate Server
     * waits until all threads has finished.
     */
    public void stop() {
        for (int i = 0; i < howManyThreads; i++) {
            workers[i].interrupt();
        }
    }

    /**
     * Create thread pool start and join them.
     * if successfull shouldn't return until
     * server is terminated.
     *
     * @param howmany - how many threads to create
     */
    private void createThreadpool(int howmany) {
        workers = new Thread[howmany];
        for (int i = 0; i < howmany; i++) {
            workers[i] = new Worker("Thread #" + i, server);
            workers[i].start();
        }

        // Join worker threads
        for (int i = 0; i < howmany; i++) {
            try {
                workers[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
