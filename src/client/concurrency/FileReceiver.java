package client.concurrency;

import static enums.Limits.READBUFSIZE;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Accept connect to peer client,
 * request file and save it.
 *
 * Created on 4/11/2017.
 * @author Natnael Seifu [seifu003]
 */
public class FileReceiver extends Thread {

  private String fileName;
  private String ip_addr;
  private String port;

  public FileReceiver (String name, String fileName,
      String ip_addr, String port) {

    super(name);
    this.fileName = fileName;
    this.ip_addr = ip_addr;
    this.port = port;
  }

  @Override
  public void run() {
    System.out.println(getName() + " File Receiver started");
    Socket peer;
    PrintWriter out;
    InputStream in;

    File file;
    FileOutputStream writeToFile;
    InputStream peerInput;
    byte[] buffer = new byte[READBUFSIZE];

    file = new File("test_file_2.txt");

    /* if file exists, overwrite else create new file */
    try {
      writeToFile = new FileOutputStream(file);
    } catch (FileNotFoundException e) {
      System.out.println("Receiver: " + getName() + " " + e.getMessage());
      return;
    }

    try {
      peer = new Socket(ip_addr, Integer.valueOf(port));
    } catch (IOException e) {
      System.out.println("Receiver: " + getName() + " " + e.getMessage());
      return;
    }

    try {
      out = new PrintWriter(peer.getOutputStream(), true);
      out.println(fileName);
    } catch (IOException e) {
      System.out.println("Receiver: " + getName() + " " + e.getMessage());
      return;
    }

    try {
      in = peer.getInputStream();
    } catch (IOException e) {
      System.out.println("Receiver: " + getName() + " " + e.getMessage());
      return;
    }

    try {
      peerInput = peer.getInputStream();
    } catch (IOException e) {
      System.out.println("Receiver: " + getName() + " " + e.getMessage());
      return;
    }

    if (peerInput != null) {
      int readCount;
      try {
        /* read from socket */
        while ((readCount = peerInput.read(buffer)) > 0) {
          /* write to file */
          writeToFile.write(buffer, 0, readCount);
        }
      } catch (IOException e) {
        System.out.println(getName() + " " + e.getMessage());
      } finally {
        try {
          writeToFile.close();
          out.close();
          in.close();
          peer.close();
        } catch (IOException e) {
          System.out.println(e.getMessage());
        }
      }
    }
  }
}
