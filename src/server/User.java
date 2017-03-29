package server;

import java.net.Socket;

/**
 * Created by Natnael on 3/29/2017.
 */
public class User {
    Socket conn;
    String name;

    public User(String name, Socket conn) {
        this.name = name;
        this.conn = conn;
    }

    public String getName() {
        return name;
    }
}
