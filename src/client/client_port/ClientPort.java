package client.client_port;

import java.io.IOException;
import java.net.Socket;

/**
 *
 *
 * Created on 4/11/2017.
 * @author Natnael Seifu [seifu003]
 */
public class ClientPort {

  private static final int MINPORT = 11111;
  private static final int MAXPORT = 99999;

  public static int PEERPORT = -1;

  /**
   *
   */
  public static void setPort() {
    for (int i = MINPORT; i <= MAXPORT; i++) {
      if (available(i)) {
        PEERPORT = i;
        break;
      }
    }
  }

  /**
   *
   * @param port -
   * @return -
   */
  private static boolean available(int port) {
    System.out.println("--------------Testing port " + port);
    Socket s = null;
    try {
      s = new Socket("localhost", port);

      // If the code makes it this far without an exception it means
      // something is using the port and has responded.
      System.out.println("--------------Port " + port + " is not available");
      return false;
    } catch (IOException e) {
      System.out.println("--------------Port " + port + " is available");
      return true;
    } finally {
      if(s != null){
        try {
          s.close();
        } catch (IOException e) {
          System.out.println(e.getMessage());
        }
      }
    }
  }
}
