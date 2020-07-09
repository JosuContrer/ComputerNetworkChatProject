package FinalProject.Frontend;

import FinalProject.CommunicationConstants;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Client abstract class that initializes the socket
 * connection request with the Server and initializes
 * the I/O streams for the client to the server.
 */
public class Client {

    // ->Client identification elements
    private String userName = null;
    private Socket socketC = null;
    private InetAddress ip = null;
    public boolean loggedIn = false;

    // ->Client IO streams
    public DataInputStream inputC = null;
    public DataOutputStream outputC = null;

    public Client(){
        this.loggedIn = false;
    }

    public Client(String userName, Integer serverSocket) throws Exception {
        if(userName.length() >= 1) // Check for a blank username
        {
            try {
                this.ip = InetAddress.getLocalHost();               // Get the local host IP Address
                this.socketC = new Socket(this.ip, serverSocket);   // Create Client socket given ip and server socket
                this.inputC = new DataInputStream(this.socketC.getInputStream());
                this.outputC = new DataOutputStream(this.socketC.getOutputStream());
                this.userName = userName;
                this.loggedIn = true;
                this.outputC.writeUTF(userName); // Send to server for username validation
            } catch (IOException e) { // Error connecting with server
                throw new IOException(CommunicationConstants.SERVER_FAILED);
            }
        }
        else
        {
            throw new Exception(CommunicationConstants.INVALID_USERNAME);
        }
    }

    /**
     * Closes client sockets and streams.
     */
    public void close(){
        try
        {
            loggedIn = false;
            socketC.close();
            inputC.close();
            outputC.close();
        }
        catch (IOException e)
        {
            System.out.println("Client Forced Close");
        }
    }

    /**
     * To change the username of the client if needed.
     * @param userName
     */
    public void setUserName(String userName){
        this.userName = userName;
    }

}
