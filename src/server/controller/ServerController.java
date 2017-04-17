package server.controller;

import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

import server.client.Client;
import server.server_file.ServerFile;
import server.server_main.Server;

import static server.controller.ControlCmds.ECHO;
import static server.controller.ControlCmds.EXIT;
import static server.controller.ControlCmds.STATUS;

/**
 * Server Controller thread listens to command
 * from server.
 *
 * limited functionality.
 *
 * Created on 4/10/2017.
 * @author Natnael Seifu [seifu003]
 */
public class ServerController extends Thread {

  private Server server;
  private HashMap<String, ServerFile> fileList;
  private Set<Client> registered;
  private Set<Client> active;

  public ServerController (String name, Server server, Set<Client> registered,
                           Set<Client> active,  HashMap<String, ServerFile> fileList) {
    super(name);
    this.server = server;
    this.registered = registered;
    this.active = active;
    this.fileList = fileList;
  }

  @Override
  public void run() {
    System.out.println(getName() + " Started");
    Scanner listen = new Scanner(System.in);

    while (!Thread.interrupted()) {
      String command = listen.nextLine();

      switch (command.toUpperCase()) {

        case ECHO:
          System.out.println("echoo");
          break;

        case STATUS:
          System.out.println("--------------- Server Status -----------------");
          System.out.println("---- " + registered.size() + " registered client");
          System.out.println("---- " + active.size() + " Active clients");
          System.out.println("---- " + fileList.size() + " Files on server");
          break;

        case EXIT:
          /*
          Server will exit it self after
          this method is invoked.
          */
          if (active.size() == 0) server.preterminationCleanup();
          else System.out.println("active clients still exist");
          break;

        default:
          System.out.println("unsupported command");
          break;

      }
    }
  }
}
