package FinalProject.Backend;

import FinalProject.CommunicationConstants;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

/**
 * This Server can handle multiple client request with the help of the
 * ClientHandler helper class.
 *
 */
public class Server {

    // Class variables
    private ServerSocket serverSocket = null; //Socket Server
    public static LinkedList<ClientHandler> clientConnectedList = new LinkedList<>();
    public static connectedUserManager connectedUserManager = new connectedUserManager();

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
        System.out.println("Server Starting on port\n  Local Port: " + this.serverSocket.getLocalPort() + "\n  Address: " + this.serverSocket.getInetAddress());

        while(true){
            Socket clientRequestSocket = null;
            Thread t = null;
            try {
                // Server ready to for new Client to connect
                //System.out.println("Server ready for new Client");
                clientRequestSocket = serverSocket.accept();

                // Client request has to be processed and handled
                // -> Create IO streams for communication of the client
                DataInputStream input = new DataInputStream(clientRequestSocket.getInputStream());
                DataOutputStream output = new DataOutputStream(clientRequestSocket.getOutputStream());

                Boolean clientConnectionS = true;
                String clientUsername = null;
                // -> Create a ClientHandler Object
                try {
                    clientUsername = input.readUTF();
                }catch (IOException e){
                    System.out.println("Client Connection Failed");
                    clientConnectionS = false;
                }

                if(clientConnectionS) {
                    // -> Check if user already took that name
                    Boolean isDuplicate = !connectedUserManager.addUser(clientUsername);
                    Integer dup = CommunicationConstants.NOT_DUPLICATE;
                    if (isDuplicate) {
                        dup = CommunicationConstants.IS_DUPLICATE;
                    }
                    output.write(dup);

                    if (isDuplicate) {
                        System.out.println(" Try Again Login-> Client with duplicate username: " + clientUsername);
                    } else {
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
            } catch (IOException e){
                System.out.println("SERVER EXCEPTION");
                e.printStackTrace();
                System.out.println("SERVER CLOSING...");
                break;
            }
        }
    }

    public static void deleteClient(String userName){
        for(ClientHandler ch: clientConnectedList){
            if(userName.equals(ch.getUserName())){
                clientConnectedList.remove(clientConnectedList.indexOf(ch));
            }
        }
    }

    public void close() {
        try {
            this.serverSocket.close();
        }catch (IOException e){

        }
    }

    public static void main(String[] args){
//        Server s = new Server(5056);
        new Server(59091);
    }
}
