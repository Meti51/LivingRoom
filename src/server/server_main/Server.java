package server.server_main;

import server.client.Client;
import server.concurrency.Dispatcher;
import server.concurrency.Worker;
import server.controller.ServerController;
import server.server_file.ServerFile;
import server.request.Request;

import java.io.*;
import java.net.ServerSocket;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Main server app
 *
 * Created on 3/29/2017.
 * @author Natnael Seifu [seifu003]
 */
public class Server {

    /*
    Should be changed by clientThreadCounter() only
    to avoid synchronization issues.
    */
    private static int clientThreadCounter = 0;

    /* Active clients */
    /* Hold requests here */
    private Queue<Request> serviceBuffer = new ConcurrentLinkedQueue<>();
    /* list of server_file ids and details */
    /* server_file ids as keys */
    private HashMap<String, ServerFile> fileList;
    /* Logged in  Clients */
    private Set<Client> activeList;
    /* Registered clients */
    private Set<Client> registered;

    private ServerSocket server = null;
    private Thread controller = null;
    private Thread[] dispatchers = null;
    private Thread[] workers = null;
    private String filePath;
    private int howManyThreads;

    public Server(String filePath, int port, int howManyThreads) {
        try {
            this.howManyThreads = howManyThreads;
            this.filePath = filePath;

            server = new ServerSocket(port);
            activeList = new HashSet<>();
            registered = new HashSet<>();
            fileList = new HashMap<>();
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
        joinThreads();
    }

    /**
     * Terminate Server
     * waits until all threads has finished.
     */
    public void preterminationCleanup() {
        try {
            /* Terminate threads */
            for (Thread worker : workers) {
                /* interrupt and wait */
                worker.interrupt();
                worker.join();
            }

            try {
                server.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

            for (Thread dispatcher: dispatchers) {
                /* interrupt and wait */
                dispatcher.interrupt();
                dispatcher.join();
            }

            /* stop controller */
            controller.interrupt();

        } catch (InterruptedException ex) {
            //
        } finally {
            /* update registered list */
            writeRegistered(filePath);
        }
    }

    /**
     * Load registered clients from server_file.
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
                /* white space not allowed in password and username */
                registered.add(new Client(client[0].trim(), client[1].trim()));
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
     * Writes registered client list to server_file
     * on server termination.
     *
     * @param filePath -
     */
    private void writeRegistered(String filePath) {
        FileWriter fileWriter = null;
        BufferedWriter writer = null;

        try {
            fileWriter = new FileWriter(filePath);
            writer = new BufferedWriter(fileWriter);
            String line = "";
            Client client;

            for (Client regclient : registered) {
                client = regclient;
                line = line.concat(client.getUserName() + "," + client.getPassword());
                /* write each client on a single line */
                writer.write(line);
                writer.newLine();
                /* reset line buffer */
                line = "";
            }

            writer.close();
            fileWriter.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            try {
                if (fileWriter != null) {
                    fileWriter.close();
                    writer.close();
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

        /* Controller thread */
        controller =
            new ServerController("Controller", this);
        controller.start();

        workers = new Thread[howmany];
        for (int i = 0; i < howmany; i++) {
            workers[i] = new Worker("Worker Thread #" + i, serviceBuffer,
                activeList, registered, fileList);
            workers[i].start();
        }

        dispatchers = new Thread[howmany];
        for (int i = 0; i < howmany; i++) {
            dispatchers[i] = new Dispatcher("Dispatcher Thread #" + i, server,
                serviceBuffer);
            dispatchers[i].start();
        }
    }

    /**
     * Block main process from terminating before threads
     */
    private void joinThreads() {
        // Join worker threads
        for (Thread worker: workers) {
            try {
                worker.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Join dispatcher threads
        for (Thread dispatcher: dispatchers) {
            try {
                dispatcher.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * controls thread clientThreadCounter.
     * enter positiver number to increment
     * negative number to decrement.
     *
     * @param offset -
     * @return -1 on fail or 0 on success
     */
    public synchronized static int clientThreadCounter(int offset) {
        int check = Math.abs(offset);
        if (check > 1) return -1;
        clientThreadCounter += offset;
        return 0;
    }

    /**
     * for synchronized read.
     *
     * @return -
     */
    public synchronized static int getClientThreadCounter() {
        return clientThreadCounter;
    }
}
