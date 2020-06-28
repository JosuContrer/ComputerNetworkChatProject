package Sandbox.MultiClientsTutorial;

import java.io.*;
import java.net.*;
import java.util.Scanner;

/**
 * The client is simple:
 *  1) It establishes a socket connection
 *  2) It communicates with the server
 */
public class Client2 {

    private Integer serverSocketNumber = null;
    private Socket clientSocket = null;
    private InetAddress ip = null;

    private DataInputStream in = null;
    private DataOutputStream out = null;

    private Scanner scn = new Scanner(System.in);

    public Client2(Integer serverS) throws IOException {
        // Setup the client
        this.serverSocketNumber = serverS;
        this.ip = InetAddress.getByName("localhost"); // get local IP
        this.clientSocket = new Socket(ip, serverS); // establish connection with server port
        this.in = new DataInputStream(this.clientSocket.getInputStream());
        this.out = new DataOutputStream(this.clientSocket.getOutputStream());

        start();
    }

    // This method starts the clients communication with the client handler
    private void start() throws IOException {
        while (true){
            System.out.println(in.readUTF());
            String toSend = scn.nextLine();
            out.writeUTF(toSend);

            // Handle exit request of client
            if(toSend.equals("Exit")){
                clientSocket.close();
                System.out.println("Client disconnecting form Server...");
                break;
            }

            // print from Server
            String recived = in.readUTF();
            System.out.println(recived);
        }

        scn.close();
        in.close();
        out.close();
        System.out.println("Client out (peace)");
    }

    public static void main(String[] args) throws IOException {
        Client2 c = new Client2(5056);
    }

}
