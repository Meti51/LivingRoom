package client;

import client.concurrency.Reader;
import client.concurrency.Writer;
import client.work.ClientWork;
import enums.ErrorMessages;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Natnael on 3/29/2017.
 *
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

        String userName = null;
        String password = null;
        Scanner scan = new Scanner(System.in);

        /* Initial Login sequence */
        /* TCP connection will established temporarily for validation */
        try {
            System.out.println("/***** Login *****/");
            System.out.print("User Name: ");
            userName = scan.nextLine();
            System.out.print("Password: ");
            password = scan.nextLine();

            outStream.println(",Login," + userName + "," + password);

            String verify = inStream.readLine();

            if (!verify.equals(ErrorMessages.SUCCESS)) {
                System.out.println(verify);
                System.out.println("User name not found");
                System.out.println("would you like to register? [y/n]");
                String response = scan.nextLine();

                if (response.equalsIgnoreCase("y")) {
                    outStream.println(",Register," + userName + "," + password);
                    verify = inStream.readLine();

                    if (verify.equals(ErrorMessages.SUCCESS)) {
                        System.out.println(ErrorMessages.SUCCESS);
                        /* log newly registered user automatically after registration */
                        outStream.println(",Login," + userName + "," + password);
                        verify = inStream.readLine();
                        if (!verify.equals(ErrorMessages.SUCCESS)) {
                            System.out.println("Login failed");
                            return;
                        }
                    } else {
                        System.out.println("Okay. Bye");
                        client.close();
                        return;
                    }
                } else {
                    System.out.println("Okay. Bye");
                    client.close();
                    return;
                }
            } else System.out.println(verify);
        } catch (IOException e) {
            //
        }

        /* Names after the client user name */
        this.reader = new Reader(userName, client);
        this.writer = new Writer(userName, client);

        this.reader.start();
        this.writer.start();

        try {
            this.reader.join();
            writer.interrupt();

            this.writer.join();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
}
