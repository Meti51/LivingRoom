package client;

/**
 * Created by Natnael on 3/29/2017.
 *
 */
public class StartClient {

    public static void main(String[] args) {
        Client client = new Client("127.0.0.1", 4444);
        client.init();
    }

}
