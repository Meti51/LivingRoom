package server.request;

import server.command.Command;
import java.net.Socket;

/**
 * Created by Natnael on 3/29/2017.
 *
 */
public class Request {
    private Socket conn;
    private Command command;

    public Request(Command command, Socket conn) {
        this.conn = conn;
        this.command = command;
    }

    public Socket getConnection() {
        return conn;
    }

    public Command getCmd() {
        return command;
    }
}
