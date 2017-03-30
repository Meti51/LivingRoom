package server;

import client.ClientSide;
import server.client.Client;
import server.concurrency.Dispatcher;
import server.concurrency.Worker;
import server.request.Request;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Natnael on 3/29/2017.
 *
 */
public class ServerSide {

    /* Active clients */
    /* Hold requests here */
    private Queue<Request> serviceBuffer = new ConcurrentLinkedQueue<>();
    /* Logged in  Clients */
    private List<Client> activeList;
    /* Registered clients */
    private List<Client> registered;

    private ServerSocket server = null;
    private Thread[] dispatchers = null;
    private Thread[] workers = null;
    private String filePath;
    private int howManyThreads;

    public ServerSide(String filePath, int port, int howManyThreads) {
        try {
            this.howManyThreads = howManyThreads;
            this.filePath = filePath;

            server = new ServerSocket(port);
            activeList = new LinkedList<>();
            registered = new LinkedList<>();
        } catch (IOException e) {
            System.out.println("Could not listen on port " + port);
            e.printStackTrace();
        }
    }

    /**
     * Initialize server
     */
    public void init() {
        loadRegistered(filePath);
        createThreadpool(howManyThreads);
    }

    /**
     * Terminate ServerSide
     * waits until all threads has finished.
     */
    public void stop() {
        for (int i = 0; i < howManyThreads; i++) {
            workers[i].interrupt();
        }
    }

    /**
     * Load registered clients from file.
     *
     * @param filePath -
     */
    private void loadRegistered(String filePath) {
        FileReader fileReader = null;
        BufferedReader reader = null;

        try {
            fileReader = new FileReader(filePath);
            reader = new BufferedReader(fileReader);
            String line;
            String[] client;
            while ((line = reader.readLine()) != null) {
                client = line.split(",");
                registered.add(new Client(client[0], client[1]));
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            try {
                if (fileReader != null) {
                    fileReader.close();
                    reader.close();
                }
            } catch (IOException ex) {
                //
            }
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
            workers[i] = new Worker("Worker Thread #" + i, serviceBuffer, activeList, registered);
            workers[i].start();
        }

        dispatchers = new Thread[howmany];
        for (int i = 0; i < howmany; i++) {
            dispatchers[i] = new Dispatcher("Dispatcher Thread #" + i, server, serviceBuffer);
            dispatchers[i].start();
        }

        // Join worker threads
        for (int i = 0; i < howmany; i++) {
            try {
                workers[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Join dispatcher threads
        for (int i = 0; i < howmany; i++) {
            try {
                dispatchers[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
