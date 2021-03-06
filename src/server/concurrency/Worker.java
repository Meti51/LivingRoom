package server.concurrency;


import static enums.Functions.*;

import enums.ErrorMessages;

import java.util.*;

import server.client.Client;
import server.command.Command;
import server.request.Request;
import server.server_file.ServerFile;

import java.io.*;
import java.net.Socket;

/**
 * Servers worker thread.
 * Each thread services pending request from buffer.
 *
 * Created by on 3/29/2017
 * @author Natnael Seifu
 */
public class Worker extends Thread {

    private Queue serviceBuffer;
    private Map<String, Client> activeList;
    private Set<Client> registered;
    private Map<String, ServerFile> fileList;

    /**
     *
     * @param name thread name
     * @param buffer work buffer
     * @param activeList list of logged in clients
     * @param registered list of registered clients
     * @param fileList list of files
     */
    public Worker (String name, Queue buffer, Map<String, Client> activeList,
        Set<Client> registered, Map<String, ServerFile> fileList) {
        super(name);
        this.serviceBuffer = buffer;
        this.activeList = activeList;
        this.registered = registered;
        this.fileList = fileList;
    }

    @Override
    public void run() {
        System.out.println(getName() + " Started");
        Request request = null;
        PrintWriter out;

        while(!Thread.interrupted()) {
            try {
                /* Remove Request from buffer if any. Operation is atomic */
                if ((request = (Request) serviceBuffer.poll()) != null) {

                    Command cmd = request.getCmd();
                    Socket clientSocket = request.getConnection();

                    out = new PrintWriter(clientSocket.getOutputStream(), true);

                    switch (cmd.getFunction()) {

                        case REGISTER:
                            register(clientSocket, cmd.getPayload());
                            break;

                        case LOGIN:
                            login(clientSocket, cmd.getPayload());
                            break;

                        case MSG:
                            message(cmd);
                            break;

                        case CLIST:
                            clist(clientSocket);
                            break;

                        case FLIST:
                            flist(clientSocket);
                            break;

                        case FPUT:
                            fput(clientSocket, cmd.getPayload());
                            break;

                        case FGET:
                            fget(clientSocket, cmd.getPayload());
                            break;

                        case DISCONNECT:
                            disconnect(clientSocket);
                            break;

                        default:
                            System.out.println(request);
                            out.println(ErrorMessages.INVALIDFORMAT);
                            break;

                    }
                }
            } catch (IOException e) {
                System.out.println("Service was not successful: " + request);
                System.out.println(getName() + " " + e.getMessage());
            } catch (Exception top) {
                System.out.println(top.getMessage());
            }

            // Don't stress the CPU
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                System.out.println(getName() + " " + e.getMessage());
                break;
            }
        }
        System.out.println(getName() + " Stopped");
    }

    /**
     * Register Sequence
     *
     * @param socket - client connection
     * @param payload - username, password
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
     * Login sequence
     *
     * @param socket - client connection
     * @param payload - username, password
     */
    private synchronized void login(Socket socket, String payload) throws IOException {
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        String[] input = payload.split(",");
        Client client = null;

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
                    Client c = activeList.get(client.getUserName());
                    if (c == null) {
                        /* add client to active list */
                        client.setConnection(socket);
                        activeList.put(client.getUserName(), client);
                        /* Sent response */
                        out.println(ErrorMessages.SUCCESS);
                    } else {
                        if (c.getConnection().isClosed()) {
                            /* add client to active list */
                            client.setConnection(socket);
                            activeList.put(client.getUserName(), client);
                            /* Sent response */
                            out.println(ErrorMessages.SUCCESS);
                            return;
                        }
                        /* already logged in */
                        out.println(ErrorMessages.ACCESSDENIED);
                        socket.close();
                    }
                } else {
                    out.println(ErrorMessages.ACCESSDENIED);
                    socket.close();
                }
            } else {
                out.println(ErrorMessages.ACCESSDENIED);
                socket.close();
            }
        } else {
            out.println(ErrorMessages.INVALIDFORMAT);
            socket.close();
        }
    }

    /**
     *
     *
     * @param message - message to broadcase
     * @throws IOException -
     */
    private synchronized void message(Command message) throws IOException {
        // broad cast message to all online clients
        PrintWriter broadCast;

        for (Map.Entry<String, Client> e: activeList.entrySet()) {
            broadCast = new PrintWriter(e.getValue().getConnection().getOutputStream(),
                true);
            broadCast.println(message.getPayload());
        }
    }

    /**
     * send list of active [logged] in clients
     *
     * @param client - Client connection
     */
    private synchronized void clist(Socket client) throws IOException {
        // broad cast message to all online clients
        PrintWriter sendList = new PrintWriter(client.getOutputStream(),
            true);

        sendList.println(activeList.keySet());
        sendList.println(ErrorMessages.SUCCESS);
    }

    /**
     * Server will respond with list server_file names and ids.
     *
     * @param client -
     */
    private synchronized void flist(Socket client) throws IOException {
        PrintWriter sendList = new PrintWriter(client.getOutputStream(),
            true);

        sendList.println("--------------------------- Files ---------------------------------");
        for (Map.Entry<String, ServerFile> entry: fileList.entrySet()) {
            sendList.println(entry.getValue());
        }
        sendList.println("---------------------------- End ----------------------------------");
        sendList.println(ErrorMessages.SUCCESS);
    }

    /**
     * Add server_file name to server along with client info.
     *
     * @param client requester
     * @param payload contains [filename, ip_addr and port]
     */
    private synchronized void fput(Socket client, String payload) throws IOException {
        PrintWriter sendList = new PrintWriter(client.getOutputStream(),
            true);
        String[] split = payload.split(",");

        if (split.length == 3) {

            /* if ip is unknown */
            if (split[1].trim().equals("%")) {
                split[1] = String.valueOf(client.getInetAddress());
                split[1] = split[1].replace("/", "");
            }

            ServerFile file = new ServerFile(split[0].trim(), split[1].trim(), split[2].trim());
            fileList.put(file.getId(), file);

            sendList.println(ErrorMessages.SUCCESS);
        } else {
            sendList.println(ErrorMessages.INVALIDFORMAT);
        }
    }

    /**
     * The server will return client detail that has server_file.
     *
     * @param payload contains file_ID
     */
    private synchronized void fget(Socket client, String payload) throws IOException {
        PrintWriter sendList = new PrintWriter(client.getOutputStream(),
            true);

        ServerFile file = fileList.get(payload);

        if (file != null) {
            sendList.println(FGET + "," +
                file.getFilename() + "," +
                file.getClient_ip_addr() + "," +
                file.getClient_port());
        } else {
            sendList.println(ErrorMessages.INVALIDFILEID);
        }
    }

    /**
     * close client connection and remove from active list
     *
     * @param client - client connection
     * @throws IOException -
     */
    private synchronized void disconnect(Socket client) throws IOException {
        // disconnect a client aka remove from active list\
        client.close();

        for (Map.Entry<String, Client> entry: activeList.entrySet()) {
            if (entry.getValue().getConnection().isClosed()) {
                activeList.remove(entry.getKey());
            }
        }
    }
}
