package server.concurrency;

import client.ClientSide;
import enums.ErrorMessages;
import enums.Functions;
import enums.ServerStatus;
import server.client.Client;
import server.command.Command;
import server.request.Request;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * Servers worker thread.
 * Each thread services pending request from buffer.
 *
 * Created by on 3/29/2017
 * @author Natnael Seifu
 */
public class Worker extends Thread {

    private Queue serviceBuffer;
    private Set<Client> activeList;
    private Set<Client> registered;

    public Worker (String name, Queue buffer, Set<Client> activeList, Set<Client> registered) {
        super(name);
        this.serviceBuffer = buffer;
        this.activeList = activeList;
        this.registered = registered;
    }

    @Override
    public void run() {
        System.out.println(getName() + " Started");
        Request request;
        PrintWriter out = null;

        while(!Thread.interrupted()) {
            try {
                /* Remove Request from buffer if any */
                if ((request = (Request) serviceBuffer.poll()) != null) {
                    System.out.println(getName() + " Servicing ...");
                    Command cmd = request.getCmd();
                    String clientUserName = request.getRequester();
                    Socket clientSocket = request.getConnection();

                    out = new PrintWriter(clientSocket.getOutputStream(), true);

                    switch (cmd.getFunction()) {

                        case Functions.REGISTER:
                            register(clientSocket, cmd.getPayload());
                            break;

                        case Functions.LOGIN:
                            login(clientSocket, cmd.getPayload());
                            break;

                        case Functions.MSG:
                            message(clientUserName, cmd);
                            break;

                        case Functions.CLIST:
                            clist(clientSocket);
                            break;

                        case Functions.DISCONNECT:
                            disconnect(clientUserName, clientSocket);
                            break;

                        default:
                            System.out.println(request);
                            out.println(ErrorMessages.INVALIDFORMAT);
                            break;

                    }

                    System.out.println(getName() + " Done");
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            /* Close writer because it is unique to each client */
//            if (out != null) out.close();

            // Don't stress the CPU
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
                break;
            }
        }
        System.out.println(getName() + " Stopped");
    }

    /**
     *
     *
     * @param socket -
     * @param payload -
     * @throws IOException -
     */
    private synchronized void register(Socket socket, String payload) throws IOException {
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        String[] input = payload.split(",");
        if (input.length == 2) {
            Client client = new Client(input[0], input[1]);
            /*  */
            if (!registered.contains(client)) {
                /* Register new Client */
                registered.add(client);
                System.out.println(registered);
                /* send ACK to client */
                out.println(ErrorMessages.SUCCESS);
            } else
                out.println(ErrorMessages.DUPCLIENTID);
        } else
            out.println(ErrorMessages.INVALIDFORMAT);
    }

    /**
     *
     *
     * @param socket -
     * @param payload -
     */
    private synchronized void login(Socket socket, String payload) throws IOException {
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        String[] input = payload.split(",");
        Client client = null;

//        System.out.println("In Login" + Arrays.toString(input));

        if (input.length == 2) {
            String userName = input[0];
            String password = input[1];

            /* Find client in registered list */
            /* return error if client doesn't exist */
            for (Client c: registered) {
                if (c.getUserName().equals(userName)) {
                    client = c;
                    break;
                }
            }

            if (client != null) {
                if (client.getPassword().equals(password)) {
                    if(!activeList.contains(client)) {
                        /* add client to active list */
                        client.setConnection(socket);
                        activeList.add(client);
                    }
                    /* Sent response */
                    out.println(ErrorMessages.SUCCESS);
                } else {
                    out.println(ErrorMessages.ACCESSDENIED);
                    client.getConnection().close();
                }
            } else {
                out.println(ErrorMessages.ACCESSDENIED);
                client.getConnection().close();
            }
        } else
            out.println(ErrorMessages.INVALIDFORMAT);
    }

    /**
     *
     *
     * @param sender -
     * @param message -
     * @throws IOException -
     */
    private synchronized void message(String sender, Command message) throws IOException {
        // broad cast message to all online clients
        PrintWriter broadCast = null;
        for (Client client: activeList) {
            if (!client.getUserName().equals(sender)) {
                broadCast = new PrintWriter(client.getConnection().getOutputStream(),
                        true);
                broadCast.println(sender + " > " + message.getPayload());
            }
        }
    }

    /**
     *
     *
     * @param client -
     */
    private synchronized void clist(Socket client) throws IOException {
        // broad cast message to all online clients
        PrintWriter sendList = new PrintWriter(client.getOutputStream(),
                true);

        sendList.println(activeList);
        sendList.println(ErrorMessages.SUCCESS);
    }

    /**
     *
     *
     * @param clientID -
     * @param client -
     * @throws IOException
     */
    private synchronized void disconnect(String clientID, Socket client) throws IOException {
        // disconnect a client aka remove from active list\
        activeList.remove(new Client(clientID, ""));
        //TODO remove later
        System.out.println(activeList);
        client.close();
    }
}
