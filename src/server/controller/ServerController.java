package server.controller;

import static server.controller.ControlCmds.EXIT;

import java.util.Scanner;
import server.Server;

/**
 *
 *
 * Created on 4/10/2017.
 * @author Natnael Seifu [seifu003]
 */
public class ServerController extends Thread {

  private Server server;

  public ServerController (String name, Server server) {
    super(name);
    this.server = server;
  }

  @Override
  public void run() {
    System.out.println(getName() + " Started");
    Scanner listen = new Scanner(System.in);

    while (!Thread.interrupted()) {
      String command = listen.nextLine();

      switch (command.toUpperCase()) {

        case ControlCmds.ECHO:
          System.out.println("echoo");
          break;

        case ControlCmds.EXIT:
        /*
        Server will exit it self after
        this method is invoked.
        */
          server.preterminationCleanup();
          break;

        default:
          System.out.println("unsupported command");
          break;

      }
    }
  }
}
