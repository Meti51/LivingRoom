Multi Threaded Server and Chatroom

How to start Server
--------------------

1. Locate src directory and execute this command on command line: javac server/StartServer.java
   This will compile the source code to executable

2. Then run server by using command: java server/StartServer ./filepath
   i.e. file path of the registered clients list.
        [This file has to match the format of Programming Assignment specifications].

How to start Client
--------------------

1. Locate src directory and execute this command on command line: javac client/StartClient.java
   This will compile the source code to executable

2. Then run server by using command: java client/StartClient


Code File Structure and detail
-------------------------------

src
    client [Client side app: a way for clients to communicate with server]
        concurrency [Client side threads]
            Reader.java [This thread reads incoming message from server and display it to client]
            Writer.java [This thread waits for user input and sends input to server]

        ClientSide.java [Client side app top code. starts connection, starts the threads.]
        StartClient.java [java main method to start ClientSide. put in for convenience]

    enums
        ErrorMessages.java [holds possible error codes for server and client side apps]
        Functions.java [Functionality that the server supports.]
        ServerStatus.java [For server user only. A way to keep track of state of server after each request.]

    server
        client
            Client.java [Represents a client in server. Holds info about client]

        command
            Command.java [incoming transmission is parsed and encapsulated here.]

        concurrency
            Dispatcher.java [This thread accepts client connection request and place it is request buffer. holds Persistent connection]
            Worker.java [This thread takes request off of request buffer and service it. Non persist to client]

        files
            registered.txt [This file holds previously registered clients for when server is taken down.]

        request
            Request.java [encapsulate request. holds parsed command, username of requester and connection socket of client.]
                         [Request is created by dispatcher and placed on request buffer]

        Server.java [Top level server app. This class start server connections, loads registered clients, starts the threads and wait for incoming connections]
        StartServer.java [java main method to start top level server class for convenience. not necessary for starting server]

!!! for more information please look at the classes in question.


