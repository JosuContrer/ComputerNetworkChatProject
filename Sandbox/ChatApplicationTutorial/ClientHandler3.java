package Sandbox.ChatApplicationTutorial;

import Sandbox.SocketProgrammingTutorial.Server;
import com.sun.org.apache.xpath.internal.operations.String;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;
import java.lang.*;

/**
 * The difference between this client handler and the previous one
 * is that this one takes in 4 parameters in the constructor: socket,
 * IO streams, and the a name variable.
 *  1) Name variable - this variable holds the name of the client
 *      that is connected to the server.
 *  Two actions happen wit this:
 *  1) When the handler receives a string it breaks it into the message
 *  and the recipient part. In this case is uses the StringTokenizer class
 *  but since it is a legacy class another class like the split() method can
 *  be used. The format of the message string is message # recipient
 *  2) Once we get the recipient it then searches for the name of the
 *  recipient in the connected client list (this list is stored as a vector
 *  in the server). Then if it finds the recipients name in the client list,
 *  it forwards the message on the output stream with the name of the sender
 *  prefixed to the message.
 */
public class ClientHandler3 extends Thread{

    // Identification Variables
    private Socket socket = null;
    private DataInputStream in = null;
    private DataOutputStream out = null;

    // Date and time Variables
    DateFormat dateF = new SimpleDateFormat("yyyy/MM/dd");
    DateFormat timeF = new SimpleDateFormat("hh:mm:ss");

    // Name of Client
    java.lang.String clientName = null;
    boolean isLoggedIn = false;

    // Constructor
    public ClientHandler3(Socket s, java.lang.String name, DataInputStream i, DataOutputStream o){
        this.socket = s;
        this.in = i;
        this.out = o;
        this.clientName = name;
        this.isLoggedIn = true;
    }


    // Run method
    @Override
    public void run(){
        java.lang.String received;
        String toReturn;

        while(true){
            try {
                // Recieve answer
                received = in.readUTF();

                System.out.println("Received: " + received);

                // Client exit request
                if(received.equals("Exit")){
                    this.isLoggedIn = false;
                    System.out.println("Closing connection with client on request: " + this.socket);
                    this.socket.close();
                    System.out.println("Connection closed...");
                    break;
                }

                // Parse string into recipient and message
                StringTokenizer st = new StringTokenizer(received, "#");
                java.lang.String msg = st.nextToken();
                java.lang.String recipient = st.nextToken();

                System.out.println("RECIPIENT: " + recipient);

                // Handle message to and from
                for(ClientHandler3 connectedClients: Server3.clientList){
                    if(connectedClients.clientName.equals(recipient) && connectedClients.isLoggedIn==true){
                        connectedClients.out.writeUTF(this.clientName + ": " + msg);
                        break;
                    } else {
                        this.out.writeUTF("User not connected");
                        break;
                    }
                }

            } catch (IOException e) {
                try {
                    this.socket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                e.printStackTrace();
                break;
            }
        }

        // When the client exits
        try{
            this.out.close();;
            this.in.close();
        }catch (IOException i){
            i.printStackTrace();
        }
    }
}
