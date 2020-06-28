package Sandbox.ChatApplicationTutorial;

import Sandbox.MultiClientsTutorial.ClientHandler;
import Sandbox.MultiClientsTutorial.Server2;

import java.io.*;
import java.util.*;
import java.net.*;

/**
 * The previous server allowed us to connect multiple clients.
 *  1) Multiple clients where accepted using an infinite loop.
 *  2) Threads where assigned to each request to handle communication.
 *  3) The client name is stored into a vector to keep track of the
 *      connected devices. the vector stores the thread object, the
 *      helper class uses this vector to find the name of recipient
 *      to which message is to be delivered
 *  4) The start() method is invoked on each thread.
 */
public class Server3 {
    // Class variables
    static Vector<ClientHandler3> clientList = new Vector<>(); // list of connected clients
    static Integer i = 0; // for client count
    private ServerSocket server = null;

    public Server3(int port) {
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

                // Take IO streams from client
                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                System.out.println("A new Client is connected: ");
                System.out.println(" Name - " + socket + "\n InS - " + in + "\n OutS - " + out + "\n Number of Clients connected - " + i);

                // Create thread for new client
                System.out.println("Setting up new client with name: " + i.toString());
                ClientHandler3 currentClient = new ClientHandler3(socket, i.toString(), in, out);
                Thread clientThread = new Thread(currentClient);

                // Add client to connected list
                clientList.add(currentClient);
                i++;
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
        Server3 s = new Server3(5056);
    }

}
