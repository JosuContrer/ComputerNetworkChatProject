package FinalProject.Backend;

import FinalProject.Frontend.Client;
import Sandbox.ChatApplicationTutorial.ClientHandler3;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Vector;

/**
 * This Server can handle multiple client request with the help of the
 * ClientHandler helper class.
 *
 * TODO: if a client disconects or looses connection keep running server
 */
public class Server {

    // Class variables
    private ServerSocket serverSocket = null; //Socket Server
    public static ArrayList<ClientHandler> clientConnectedList = new ArrayList<>();
    public static UserController userController = new UserController();
//    static Vector<ClientHandler> clientConnectedList = new Vector<>(); // list of connected clients

    public Server(Integer serverSocketNumber){
        try {
            this.serverSocket = new ServerSocket(serverSocketNumber);
            System.out.println("Chat Server Started");
        }
        catch (IOException e) {
            System.out.println("Server couldn't be initialized");
            e.printStackTrace();
        }

        // Setup and Start infinite loop
        start();
    }

    /**
     * This helper method contains the infinite loop that allows to listen for multiple requests
     */
    private void start(){
        System.out.println("Server Starting on port " + this.serverSocket.toString());

        while(true){
            Socket clientRequestSocket = null;
            Thread t = null;
            try {
                // Server ready to for new Client to connect
                System.out.println("Server ready for new Client");
                clientRequestSocket = serverSocket.accept();

                // Client request has to be processed and handled
                // -> Create IO streams for communication of the client
                DataInputStream input = new DataInputStream(clientRequestSocket.getInputStream());
                DataOutputStream output = new DataOutputStream(clientRequestSocket.getOutputStream());

                // -> Create a ClientHandler Object
                String clientUsername = input.readUTF();
                System.out.println("Client username " + clientUsername + " is connecting");
                ClientHandler client = new ClientHandler(clientUsername, clientRequestSocket, input, output);

                // -> Add client to connected list
                clientConnectedList.add(client);
                userController.addUser(clientUsername);
                //clientConnectedList.add(client);

                // -> Instantiate thread for client
                t = new Thread(client, clientUsername + " Sever Thread");
                t.start();

            } catch (IOException e){
                System.out.println("Server exception");
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args){
        Server s = new Server(5056);
    }
}
