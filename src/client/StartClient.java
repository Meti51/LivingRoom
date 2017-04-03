package client;

/**
 * Helper class to start Client apps
 *
 * Created on 3/29/2017.
 * @author Natnael Seifu (seifu003)
 */
public class StartClient {

    public static void main(String[] args) {
        /* if the client is not local to server change the ip address accordingly */
        /* if port is changed here, change servers port as well. */
        ClientSide client = new ClientSide("127.0.0.1", 4444);
        client.init();
    }
}
