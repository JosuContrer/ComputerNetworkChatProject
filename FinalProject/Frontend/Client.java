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
    private boolean firstLogged = true;
    // ->Client IO streams
    private DataInputStream inputC = null;
    private DataOutputStream outputC = null;
    // ->Client input messages
    Scanner scn = new Scanner(System.in);

    public Client(String userName, Integer serverSocket) throws Exception {
        if(userName.length() >= 1) {
            try {
                this.ip = InetAddress.getLocalHost(); // get the local host IP Address
                this.socketC = new Socket(this.ip, serverSocket); // Create Client socket given ip and server socket
                this.inputC = new DataInputStream(this.socketC.getInputStream());
                this.outputC = new DataOutputStream(this.socketC.getOutputStream());
                this.userName = userName;
                //this.outputC.writeUTF(userName);
                System.out.println("Welcome " + this.userName);
            } catch (IOException e) {
                e.printStackTrace();
                throw new IOException("Client cannot connect at the moment");
            }

            start();
        } else{
            throw new Exception("not valid username");
        }
    }

    /**
     * Helper Method where the send and receive separate
     * threads are created.
     */
    private void start(){
        // Set thread for sending message
        Runnable sendMR = new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        if (!firstLogged) {
                            System.out.println("Select recipient: ");
                            String recipient = scn.nextLine();
                            System.out.print("type message: ");
                            String msg = scn.nextLine();
                            outputC.writeUTF(recipient + "@" + msg);
                        } else {
                            firstLogged = false;
                            outputC.writeUTF(userName);
                        }
                    } catch (IOException e) {
                        System.out.println("Client output error disconnected");
                        e.printStackTrace();
                        return;
                    }
                }
            }
        };

        // Set thread for receiving message
        Runnable receiveMR = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        String[] packet = inputC.readUTF().split("@", 3);
                        if(packet[0].equals("M")){ // Message received
                            System.out.println(packet[1] + ": " + packet[2]);
                        }else if(packet[0].equals("A")){ // Action received
                            System.out.println(packet[1]);
                        }
                    } catch (IOException e) {
                        System.out.println("Client input error disconnected");
                        e.printStackTrace();
                        return;
                    }
                }
            }
        };

        Thread receiveMT = new Thread(receiveMR, userName + " RX Thread");
        Thread sendMT = new Thread(sendMR, userName + "Tx Thread");

        sendMT.start();
        receiveMT.start();
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
