package client;

/**
 * Created by Natnael on 3/29/2017.
 *
 */
public class StartClient {

    public static void main(String[] args) {
        ClientSide client = new ClientSide("127.0.0.1", 4444);
        client.init();
    }

}
