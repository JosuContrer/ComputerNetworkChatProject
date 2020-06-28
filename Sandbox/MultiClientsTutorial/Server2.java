package Sandbox.MultiClientsTutorial;

import java.io.*;
import java.net.*;

/**
 * This class allows a server to be created. This server allows multiple
 * clients to connect using threads.
 *  1) When establishing connection the server socket object is initialized
 *      and inside a while loop a socket object accepts incoming connections,
 *  2) We obtain the streams by extracting the input stream and outputstream
 *      object from the current requests' socket object
 *  3) After the stream is obtained and the port number, a new clientHandler
 *      object is created with these parameters.
 *  4) We then invoke the start() method on the newly created thread object.
 */
public class Server2 {
    // Class variables
    private ServerSocket server = null;

    public Server2(int port) {
        try {
            // Instantiate Sever Socket
            server = new ServerSocket(port);
            System.out.println("Server 2 Started");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Start server and wait for connections
        while (true) {
            Socket socket = null;
            try {
                // Wait for a client to connect
                System.out.println("Waiting for a Client to connect ...");
                socket = server.accept(); // the accept method blocks  until client connects to server
                System.out.println("A new Client is connected: " + socket);

                // Take IO streams from client
                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                // Create thread for new client
                System.out.println("Assigning new thread to client");
                Thread clientThread = new ClientHandler(socket, in, out);

                // Start the thread
                clientThread.start();

            } catch (IOException i) {
                System.out.println("Closing connection with Clients");
                try {
                    socket.close();
                }catch (IOException c){
                    c.printStackTrace();
                }
                i.printStackTrace();
            }
        }
    }

    public static void main(String args[]){
        Server2 s = new Server2(5056);
    }

}
