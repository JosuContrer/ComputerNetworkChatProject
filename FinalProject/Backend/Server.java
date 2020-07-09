package FinalProject.Backend;

import FinalProject.CommunicationConstants;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

/**
 * This Server can handle multiple client connection requests. This
 * Server creates a separate thread for each client that connects
 * to handle message between the connected clients without disruption
 * of the incoming client connection requests.
 */
public class Server {

    // Class variables
    private ServerSocket serverSocket = null; //Socket Server
    public static LinkedList<ClientHandler> clientConnectedList = new LinkedList<>();
    public static ConnectedUserManager connectedUserManager = new ConnectedUserManager();

    public Server(Integer serverSocketNumber)
    {
        try
        {
            this.serverSocket = new ServerSocket(serverSocketNumber);
            System.out.println("Chat Server Started");
        }
        catch (IOException e)
        {
            System.out.println("Server couldn't be initialized");
            e.printStackTrace();
        }

        // Setup and start infinite loop
        start();
    }

    /**
     * This helper method contains the infinite loop for the server to listen for multiple client requests
     */
    private void start()
    {
        System.out.println("Server Starting on port\n  Local Port: " + this.serverSocket.getLocalPort() + "\n  Address: " + this.serverSocket.getInetAddress());

        while(true){
            Socket clientRequestSocket = null;
            Thread t = null;
            try
            {
                // Server ready to for new Client to connect
                clientRequestSocket = serverSocket.accept();

                // -> Create IO streams for communication of the client
                DataInputStream input = new DataInputStream(clientRequestSocket.getInputStream());
                DataOutputStream output = new DataOutputStream(clientRequestSocket.getOutputStream());

                Boolean clientConnectionS = true;
                String clientUsername = null;

                // Client username sent when logging in
                try
                {
                    clientUsername = input.readUTF();
                }
                catch (IOException e)
                {
                    System.out.println("Client Connection Failed");
                    clientConnectionS = false;
                }

                if(clientConnectionS)
                {
                    // -> Check if user already took that name
                    Boolean isDuplicate = !connectedUserManager.addUser(clientUsername);
                    Integer dup = CommunicationConstants.NOT_DUPLICATE;
                    if (isDuplicate)
                    {
                        dup = CommunicationConstants.IS_DUPLICATE;
                    }
                    output.write(dup);

                    if (isDuplicate)
                    {
                        System.out.println("  Try Again Login-> Client with duplicate username: " + clientUsername);
                    }
                    else
                    {
                        System.out.println("  Client connecting with username:  " + clientUsername);

                        // -> Create Client Handler
                        ClientHandler client = new ClientHandler(clientUsername, clientRequestSocket, input, output, isDuplicate);

                        // -> Add client to connected list
                        clientConnectedList.add(client);

                        // -> Instantiate thread for client
                        t = new Thread(client, clientUsername + " Sever Thread");
                        t.start();
                    }
                }
            }
            catch (IOException e)
            {
                System.out.println("SERVER EXCEPTION");
                e.printStackTrace();
                System.out.println("SERVER CLOSING...");
                break;
            }
        }
    }

    /**
     * Helper function to delete a clientHandler when a client disconnects
     * @param userName
     */
    public static void deleteClient(String userName)
    {
        for(ClientHandler ch: clientConnectedList)
        {
            if(userName.equals(ch.getUserName()))
            {
                clientConnectedList.remove(clientConnectedList.indexOf(ch));
            }
        }
    }

    /**
     * Close Server Socket Connection
     */
    public void close()
    {
        try {
            this.serverSocket.close();
        }catch (IOException e){

        }
    }

    public static void main(String[] args){
        new Server(CommunicationConstants.CONNECTION_SOCKET);
    }
}
