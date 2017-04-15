LivingRoom
----------

Living room is Server and client combo personal chatroom. Users can start their own server that
will enable their "Guests" to join them in conversation in their virtual "Living Room".

How to start Server
--------------------

1. Locate src directory and execute this command on command line: javac server/StartServer.java
   This will compile the source code to executable

2. Then run server using command: java server/StartServer ./filepath
   i.e. file path of the registered clients list.
        [This file has to match the format of Programming Assignment specifications].

How to start Client
--------------------

1. Locate src directory and execute this command on command line: javac client/StartClient.java
   This will compile the source code to executable

2. Then run client app using command: java client/StartClient


Code File Structure and detail
-------------------------------

src
    client [Client side app: a way for clients to communicate with server]
        concurrency [Client side threads]
            FileReceiver.java: is a one time thread created and started when client requests for
                               file from peer client [triggered by FGET]. This thread will make
                               a connection to peer, using ip address and port number returned
                               from server, and saves incoming file [started from Read thread].
            FileSender.java: This thread is created when client successfully logs in with the server
                             and is alive until client disconnects. This thread listens to incoming
                             file requests from peers, open the requested file and send it to the
                             peer client.
            Reader.java: This thread reads incoming transmission from server and takes appropriate
                         action [starts FileReceiver thread if its a file request].
            Writer.java: This thread waits for user input and sends formatted input to server.

        ClientSide.java: Client side app top level code. starts connection, starts the threads
                         [starts Writer, Reader, FileSender, and FileReceiver threads].

        StartClient.java: java main method to start ClientSide.

    enums
        ErrorMessages.java: holds possible error codes for server and client side apps.
        Functions.java: Functionality supported by server.
        Limits.java: Resource control settings.
        ServerStatus.java: For server user only. A way to keep track of state of server
                           after each request.

    server
        client
            Client.java: Represents a client in server. Holds info about client.

        command
            Command.java: incoming transmission is parsed and encapsulated here.

        concurrency
            Dispatcher.java: This thread accepts client connection request and place it is
                             request buffer. holds Persistent connection.
            Worker.java: This thread takes request off of request buffer and service it.
                         Non persist to client.

        controller
            ControlCmds.java: server control commands.
            ServerController.java: Listens for user input from server side and if commands are
                                   supported, implements them else throws unsupported command error
                                   message.

        files
            registered.txt: This file holds previously registered clients for when server
                            is taken down.

        request
            Request.java: encapsulate request. holds parsed command, username of requester
                          and connection socket of client. [Request is created by dispatcher
                          and placed on request buffer]

        Server.java: Top level server app. This class start server connections, loads registered
                     clients, starts the threads and wait for incoming connections.

        StartServer.java: java main method to start top level server.

Supported Server Function
-------------------------

    REGISTER
      ● New users register with the server
      ● This is a one time operation
      ● Format <REGISTER, client_ID, password>
      ● Client ID and password are just strings
      ● Potential error codes : Success, Duplicate Id, Invalid Format

    LOGIN
      ● Once user has registered with the server, user can then login using the password
      ● Format : <LOGIN, client_ID, password>
      ● Potential error codes: Success, Access denied, Invalid Format
      ● If Access is denied, then the server should close the connection

    MSG
      ● Format <MSG, message>
      ● Once the server receives the message, it broadcasts on all other active clients
      ● Potential error codes : Success, Invalid Format

    DISCONNECT
      ● Sends a disconnect message
      ● Server removes user from the active list
      ● Format <DISCONNECT>
      ● No response from the server

    CLIST
      ● Format <CLIST>
      ● The server will respond with the list of all active clients, separated by clients
      ● Potential Error Code : Success

    FLIST
      ● Format : <FLIST>
      ● The server will respond with the list of filenames and file_IDs
      ● Potential error Codes :Success

    FPUT
      ● Adds a filename to the server, Assign a file_ID
      ● Format <FPUT, filename, IP_Address, port>
      ● Multiple FPUT commands can be issued one by one
      ● Potential error messages : Success, Invalid Ip/port, Invalid format

    FGET
      ● Format <FGET, file_ID>
      ● The server will return the corresponding client details Then the current client
        will connect to the other client And retrieve the file using the filename

!!! for more information please look at the java classes in question.


