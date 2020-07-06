package FinalProject.Frontend;

import FinalProject.Backend.ClientHandler;
import FinalProject.Backend.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * A Client can receive and send messages at the same time.
 * This is done with the use of individual threads: one for
 * receiving and another for sending messages.
 */
public class Client {

    // Class variables
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
        if(userName.length() >= 1) {
            try {
                this.ip = InetAddress.getLocalHost(); // get the local host IP Address
                this.socketC = new Socket(this.ip, serverSocket); // Create Client socket given ip and server socket
                this.inputC = new DataInputStream(this.socketC.getInputStream());
                this.outputC = new DataOutputStream(this.socketC.getOutputStream());
                this.userName = userName;
                //this.outputC.writeUTF(userName);
                this.loggedIn = true;
                System.out.println("Welcome " + this.userName);
            } catch (IOException e) {
                e.printStackTrace();
                throw new IOException("Client cannot connect at the moment");
            }
        } else{
            throw new Exception("not valid username");
        }
    }

    /**
     * Closes client sockets and streams
     */
    public void close(){
        try {
            socketC.close();
            inputC.close();
            outputC.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        System.out.println("Enter username: ");
        Scanner scn = new Scanner(System.in);
        String userName = scn.nextLine();
        try {
            Client c = new Client(userName, 5056);
        }catch (IOException e){
            e.printStackTrace();
        }catch (Exception u){ // error with username
            u.printStackTrace();
        }
    }
}
