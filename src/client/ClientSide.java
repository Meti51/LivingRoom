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
public class ClientSide {

    private PrintWriter outStream = null;
    private BufferedReader inStream = null;
    private Socket client = null;

    public ClientSide(String ip, int port) {
        try {
            // connect to server
            client = new Socket(ip, port);

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

        while (!client.isClosed()) {
            System.out.print("Me > ");
            String message = keyboard.nextLine();

//            String out = constructMessage(message);

            if (this.outStream != null) {
                outStream.println(message.replace("Me >", ""));
            }

            try {
                String msg = inStream.readLine();
                System.out.println(msg);
            } catch (IOException e) {
//                System.out.println("Not ready");
            }

            if (message.contains("disconnect")) {
                System.out.println("Ending chatroom");
                break;
            }
        }
    }

    private String constructMessage(String msg) {
        String out = String.format("<MSG, " + msg + ">");

        return out;
    }
}
