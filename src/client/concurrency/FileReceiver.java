package client.concurrency;

import static client.constants.ClientConsts.ROOT;
import static enums.Limits.READBUFSIZE;

import java.io.*;
import java.net.Socket;

/**
 * Connect to peer client,
 * request file and save it.
 * <p>
 * Note: thread will terminate
 * after file is saved.
 *
 * Created on 4/11/2017.
 * @author Natnael Seifu [seifu003]
 */
public class FileReceiver extends Thread {

  private String fileName;
  private String ip_addr;
  private String port;

  FileReceiver(String name, String fileName,
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

    File file;
    FileOutputStream writeToFile;
    InputStream peerInput;
    byte[] buffer = new byte[READBUFSIZE];

    /* Create file with file name from ROOT directory */
    String[] fileEnd = fileName.split("\\\\");
    String filePath = ROOT + fileEnd[fileEnd.length-1];
    file = new File(filePath);

    /* if file exists, overwrite else create new file */
    try {
      writeToFile = new FileOutputStream(file);
    } catch (FileNotFoundException e) {
      System.out.println("Receiver: " + getName() + " " + e.getMessage());
      return;
    }

    /* connect to peer */
    try {
      peer = new Socket(ip_addr, Integer.valueOf(port));
    } catch (IOException e) {
      System.out.println("Receiver: " + getName() + " " + e.getMessage());
      return;
    }

    /* send file name requested */
    try {
      out = new PrintWriter(peer.getOutputStream(), true);
      out.println(fileName);
    } catch (IOException e) {
      System.out.println("Receiver: " + getName() + " " + e.getMessage());
      return;
    }

    /* incoming file stream */
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
        System.out.println("Receiving File [" + filePath + "]...");
        while ((readCount = peerInput.read(buffer)) > 0) {
          /* write to file */
          writeToFile.write(buffer, 0, readCount);
        }
        System.out.println("File Saved");
      } catch (IOException e) {
        System.out.println(getName() + " " + e.getMessage());
      } finally {
        try {
          /* Release file resources */
          writeToFile.close();
          peer.close();
          out.close();
        } catch (IOException e) {
          System.out.println(e.getMessage());
        }
      }
    }
  }
}
