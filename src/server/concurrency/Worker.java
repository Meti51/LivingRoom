package server.concurrency;

import client.ClientSide;
import enums.Functions;
import enums.ServerStatus;
import server.client.Client;
import server.command.Command;
import server.request.Request;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Queue;

/**
 * Servers worker thread.
 * Each thread services pending request from buffer.
 *
 * Created by on 3/29/2017
 * @author Natnael Seifu
 */
public class Worker extends Thread {

    private Queue serviceBuffer;
    private List<Client> activeList;
    private List<Client> registered;
    private Object lock;

    public Worker (String name, Queue buffer, List<Client> activeList, List<Client> registered) {
        super(name);
        this.serviceBuffer = buffer;
        this.activeList = activeList;
        this.registered = registered;
    }

    @Override
    public void run() {
        System.out.println(getName() + " Started");
        Request request;

        while(true) {
            try {
                if ((request = (Request) serviceBuffer.poll()) != null) {
                    System.out.println(getName() + " Servicing ...");
                    Command cmd = request.getCmd();
                    Socket client = request.getConnection();

                    PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                    String[] input;

                    switch (cmd.getFunction()) {

                        case Functions.REGISTER:
                            input = cmd.getPayload().split(",");
                            register(out, input[0], input[1]);
                            break;

                        case Functions.LOGIN:
                            input = cmd.getPayload().split(",");
                            login(input[0], input[1]);
                            break;

                        case Functions.MSG:
                            message(cmd.getPayload());
                            break;

                        case Functions.CLIST:
                            clist(client);
                            break;

                        case Functions.DISCONNECT:
                            out.println("Closing connection. Bye");
                            client.close();
                            break;

                        default:
                            break;

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Don't stress the CPU
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // ServerSide is preparing to terminate
            if (this.isInterrupted()) {
                break;
            }
        }
        System.out.println(getName() + " Stopped");
    }

    /**
     *
     *
     * @param out -
     * @param clientID -
     * @param password -
     * @return -
     */
    private synchronized ServerStatus register(PrintWriter out, String clientID, String password) {

        return ServerStatus.SUCCESS;
    }

    /**
     *
     *
     * @param clientID -
     * @param password -
     * @return -
     */
    private synchronized ServerStatus login(String clientID, String password) {
        return ServerStatus.SUCCESS;
    }

    /**
     *
     *
     * @param message -
     * @return -
     */
    private synchronized ServerStatus message(String message) {
        // broad cast message to all online clients
        return ServerStatus.SUCCESS;
    }

    /**
     *
     *
     * @param client -
     * @return -
     */
    private synchronized ServerStatus clist(Socket client) {
        // broad cast message to all online clients
        return ServerStatus.SUCCESS;
    }

    /**
     *
     * @param clientID -
     * @return -
     */
    private synchronized ServerStatus disconnect(String clientID) {
        // disconnect a client aka remove from active list
        return ServerStatus.SUCCESS;
    }
}
