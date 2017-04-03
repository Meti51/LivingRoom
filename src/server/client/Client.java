package server.client;

import java.net.Socket;
import java.util.Objects;

/**
 * Client info
 *
 * Created on 3/30/2017.
 * @author Natnael Seifu
 */
public class Client {

    private String userName;
    private String password;
    private Socket connection;

    public Client(String userName, String password) {
        this.userName = userName;
        this.password = password;
        /* When loading registered clients
           won't have connections yet */
        this.connection = null;
    }

    public void setConnection(Socket connection) {
        this.connection = connection;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public Socket getConnection() {
        return connection;
    }

    public String toString() {
        return userName;
    }

    /**
     * Clients are equal if their user name
     * are equal.
     *
     * @param obj - Client
     * @return -
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Client) {
            Client client = (Client) obj;
            if (client.userName.equals(this.userName)) {
                return true;
            }
        }
        return false;
    }
}
