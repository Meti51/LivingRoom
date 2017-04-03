package client;

import client.concurrency.Reader;
import client.concurrency.Writer;
import enums.ErrorMessages;

import java.io.BufferedReader;
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

    private Thread reader;
    private Thread writer;

    public ClientSide(String ip, int port) {
        try {
            // connect to server
            client = new Socket(ip, port);
            client.setKeepAlive(true);

            // write outStream the connection
            outStream = new PrintWriter(client.getOutputStream(),
                    true);

            // read from connection
            inStream = new BufferedReader(new InputStreamReader(
                    client.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void init() {

        String userName = "";
        String password = "";
        Scanner scan = new Scanner(System.in);

        /*
         * Initial Login sequence
         * TCP connection will established temporarily for validation
         * This sequence is good to have for broad cast feature
         * without it, there now way of knowing where the message
         * is coming from or who is sending.
         *
         * since we need to know who sent the message, we need to know
         * who is logged on at this time.
         *
         * duplicate logins are ignored. duplicate registers throw DUPLICATE_ID
         * error.
         *
         * on initial start up, user is asked to enter user name and password
         * for login. if the user is registered, login will be successful.
         * if the user is not registered login will throw an error and user is
         * asked if he/she want to be registered with the user name and password.
         * if yes, user will be registered and automatically logged in.
         * if no, client app is terminate.
         *
         * just FYI, it works without login sequence
         */
//        try {
//            /* ask for user name and password */
//            System.out.println("/***** Login *****/");
//            System.out.print("User Name: ");
//            userName = scan.nextLine();
//            System.out.print("Password: ");
//            password = scan.nextLine();
//
//            /* send to server for verification */
//            outStream.println(",Login," + userName + "," + password);
//
//            String verify = inStream.readLine();
//
//            if (!verify.equals(ErrorMessages.SUCCESS)) {
//                System.out.println(verify);
//                System.out.println("User name not found");
//                System.out.println("would you like to register? [y/n]");
//                String response = scan.nextLine();
//
//                if (response.startsWith("y")) {
//                    outStream.println(",Register," + userName + "," + password);
//                    verify = inStream.readLine();
//
//                    if (verify.equals(ErrorMessages.SUCCESS)) {
//                        System.out.println(ErrorMessages.SUCCESS);
//                        /* log newly registered user automatically after registration */
//                        outStream.println(",Login," + userName + "," + password);
//                        verify = inStream.readLine();
//                        if (!verify.equals(ErrorMessages.SUCCESS)) {
//                            System.out.println("Login failed");
//                            return;
//                        }
//                    } else {
//                        System.out.println("Okay. Bye");
//                        client.close();
//                        return;
//                    }
//                } else {
//                    System.out.println("Okay. Bye");
//                    client.close();
//                    return;
//                }
//            } else System.out.println(verify);
//        } catch (IOException e) {
//            //
//        }

        System.out.println("Client App Started");

        /* Names Threads after the client user name */
        this.reader = new Reader(userName, client);
        this.writer = new Writer(userName, client);

        this.reader.start();
        this.writer.start();

        try {
            this.reader.join();
            /* if reader is interrupted stop writer thread as well */
            writer.interrupt();

            this.writer.join();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
}
