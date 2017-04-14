package client.client_main;

import static client.client_port.ClientPort.PEERPORT;

import client.concurrency.FileSender;
import client.concurrency.Reader;
import client.concurrency.Writer;
import client.client_port.ClientPort;
import enums.ErrorMessages;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Client side app
 *
 * Created on 3/29/2017.
 * @author Natnael Seifu [seifu003]
 */
public class ClientSide {

    private PrintWriter outStream = null;
    private BufferedReader inStream = null;
    private Socket client = null;

    public ClientSide(String ip, int port) {
        try {
            // connect to server
            client = new Socket(ip, port);
            client.setKeepAlive(true);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }

        // write outStream the connection
        try {
            outStream = new PrintWriter(client.getOutputStream(),
                    true);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        // read from connection
        try {
            inStream = new BufferedReader(new InputStreamReader(
                    client.getInputStream()));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     *
     */
    void init() {

        String userName = menu();

        if (userName == null) {
            return;
        }
        System.out.println("Client App Started");

        /* Names Threads after the client user name */
        Thread reader = new Reader(userName, client);
        Thread writer = new Writer(userName, client);
        ClientPort.setPort();
        Thread fileSender = new FileSender(userName, PEERPORT);

        reader.start();
        writer.start();
        fileSender.start();

        try {
            reader.join();
            /*
            if reader is interrupted stop writer and
            sender thread as well.
            */
            writer.interrupt();
            writer.join();

            fileSender.interrupt();
            ((FileSender) fileSender).closeSocket();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * show menu to user.
     * registering will loop and show menu again on success.
     * terminate application otherwise.
     *
     * login will always terminate the menu loop.
     *
     * refer to loginSequence() and registerSequence()
     * for more detail.
     *
     * @return -
     */
    private String menu () {
        Scanner scan = new Scanner(System.in);
        String rVal = null;
        boolean status = true;

        while (status) {
            System.out.println("/***** Chat Room *****/");
            System.out.println("Register -> Press 1");
            System.out.println("Login -> Press 2");
            System.out.print("Choice: ");

            String selection = scan.nextLine();

            switch (selection) {
                case "1":
                    status = registerSequence();
                    break;
                case "2":
                    rVal = loginSequence();
                    status = false;
                    break;
                default:
                    System.out.println("INVALID SELECTION: " + selection);
                    break;
            }
        }

        return rVal;
    }

    /**
     * client login sequence
     * returns user name on success
     * or null on failure.
     *
     * @return -
     */
    private String loginSequence() {

        String userName;
        String password;
        String verify;

        Scanner scan = new Scanner(System.in);

        /* ask for user name and password */
        System.out.println("/***** Login *****/");
        Console console = System.console();
        userName = console.readLine("User Name: ");
        char[] p = console.readPassword("Password: ");

        password = String.valueOf(p);

        /* send to server for verification */
        System.out.println("Login," + userName + "," + password);
        outStream.println("Login," + userName + "," + password);

        try {
            verify = inStream.readLine();
        } catch (IOException e) {
            System.out.println("Server refused Login");
            return null;
        }

        if (!verify.equals(ErrorMessages.SUCCESS)) {
            System.out.println(verify);
            return null;
        }

        System.out.println(verify);

        return userName;
    }

    /**
     * Register a client.
     * <p>
     * returns true on success or false on failure.
     *
     * @return -
     */
    private boolean registerSequence() {

        String userName;
        String password;
        String verify = "";

        Scanner scan = new Scanner(System.in);

        /* ask for user name and password */
        System.out.println("/***** Register *****/");
        System.out.print("User Name: ");
        userName = scan.nextLine();
        System.out.print("Password: ");
        password = scan.nextLine();

        /* send to server for verification */
        outStream.println("Register," + userName + "," + password);

        try {
            verify = inStream.readLine();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        if (verify.equals(ErrorMessages.DUPCLIENTID)) {
            System.out.println(verify);
            return false;
        }

        if (verify.equals(ErrorMessages.INVALIDFORMAT)) {
            System.out.println(verify);
            return false;
        }

        return true;
    }
}
