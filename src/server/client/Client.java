package server.client;

/**
 * Client info
 *
 * Created by Natnael on 3/30/2017.
 */
public class Client {

    private String userName;
    private String password;

    public Client(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }
}
