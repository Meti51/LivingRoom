package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by Natnael on 3/29/2017.
 *
 */
public class Client {

    private PrintWriter outStream = null;
    private BufferedReader inStream = null;

    public Client(String ip, int port) {
        try {
            // connect to server
            Socket client = new Socket(ip, port);

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
        Scanner keyboard = new Scanner(System.in);

        while (true) {
            System.out.print("Me > \t");
            String message = keyboard.nextLine();

            String out = constructMessage(message);

            if (this.outStream != null) {
                outStream.println(out);
                outStream.flush();            }

            if (this.inStream != null) {
                try {
                    String msg = inStream.readLine();
                    System.out.println(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String constructMessage(String msg) {
        String out = String.format("<" + msg + ">");

        return out;
    }



}
