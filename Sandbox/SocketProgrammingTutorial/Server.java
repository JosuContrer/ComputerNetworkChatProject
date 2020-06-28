package Sandbox.SocketProgrammingTutorial;

import java.net.*;
import java.io.*;

/**
 * To create a Server Application we need two sockets:
 * 1) ServerSocket: this socket waits for client requests.
 *     This request form the client happens when it makes
 *     a new Socket().
 * 2) Socket for communication: a normal socket that is
 *      used to communicate with the client.
 */
public class Server {

    // Class variables
    private ServerSocket server = null;
    private Socket socket = null;
    private DataInputStream in = null;

    public Server(int port)
    {
        // Start server and wait for connection
        try
        {
            // Instantiate Sever Socket
            server = new ServerSocket(port);
            System.out.println("Server Started");

            // Wait for a client to connect
            System.out.println("Waiting for a Client to connect ...");
            socket = server.accept(); // the accept method blocks  unitl client connects to server
            System.out.println("Client accepted");

            // Take input from client
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

            String line = "";

            // Read message form client until an over maessage is sent
            while(!line.equals("Over"))
            {
                try {
                    line = in.readUTF();
                    System.out.println("Server message recieved: " + line);
                } catch (IOException i){
                    System.out.println(i);
                }
            }

            // Client close connection
            System.out.println("Closing connection with Client");
            socket.close();
            in.close();
        }
        catch(IOException i)
        {
            System.out.println(i);
        }
    }

    public static void main(String args[]){
        Server server = new Server(5000);
    }
}
