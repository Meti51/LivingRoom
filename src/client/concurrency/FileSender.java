package client.concurrency;

import static enums.Limits.MAXFILESIZE;
import static enums.Limits.READBUFSIZE;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Listens to other client request for file.
 *
 * Created on 4/11/2017.
 * @author Natnael Seifu [seifu003]
 */
public class FileSender extends Thread {

    private ServerSocket listener;
    private int port;
//    private String baseDirectory;

    public FileSender (String name, int port) {
        super(name);
        this.port = port;
    }

    @Override
    public void run() {
        System.out.println(getName() + " File Sender started.");
        BufferedReader in = null;
        String filePath = null;

        try {
            listener = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println(getName() + " " + e.getMessage());
            System.exit(0);
        }

        while (!Thread.interrupted()) {
            Socket peer;
            try {
                peer = listener.accept();
            } catch (IOException e) {
                System.out.println(getName() + " " + e.getMessage());
                return;
            }

            try {
                in = new BufferedReader(new InputStreamReader(peer.getInputStream()));
            } catch (IOException e) {
                System.out.println(getName() + " " + e.getMessage());
            }

            if (in != null) {
                try {
                    /* wait for path to be sent */
                    filePath = in.readLine();
                } catch (IOException e) {
                    System.out.println();
                }
            }

            File file;
            FileInputStream fileInput = null;
            OutputStream out = null;
            byte[] buffer = new byte[READBUFSIZE];

            if (filePath != null) {
                file = new File(filePath);
                /* Check file size */
                if (file.length() < MAXFILESIZE) {
                    try {
                        fileInput = new FileInputStream(file);
                    } catch (FileNotFoundException e) {
                        System.out.println("Sender: " + getName() + " " + e.getMessage());
                    }

                    if (fileInput != null) {
                        try {
                            /* stream to peer client [file requester] */
                            out = peer.getOutputStream();
                        } catch (IOException e) {
                            System.out.println(getName() + " " + e.getMessage());
                        }

                        if (out != null) {
                            int byteCount;
                            try {
                                /* read 8192 bytes at a time */
                                while ((byteCount = fileInput.read(buffer)) > 0) {
                                    /* send bytes to peer */
                                    out.write(buffer, 0, byteCount);
                                }
                            } catch (IOException e) {
                                System.out.println(getName() + " " + e.getMessage());
                            } finally {
                                try {
                                    fileInput.close();
                                } catch (IOException e) {
                                    System.out.println(e.getMessage());
                                }
                            }
                        }
                    }
                } else {
                    System.out.println("File is too big");
                }
            }
        }
    }

    /**
     *
     */
    public void closeSocket() {
        try {
            this.listener.close();
        } catch (IOException e) {
            System.out.println(getName() + " listener " + e.getMessage());
        }
    }
}
