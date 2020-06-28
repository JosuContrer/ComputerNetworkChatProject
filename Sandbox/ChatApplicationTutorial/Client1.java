package Sandbox.ChatApplicationTutorial;

import Sandbox.MultiClientsTutorial.Client2;

import java.io.IOException;
import java.io.*;
import java.net.*;
import java.util.Scanner;

/**
 * The client is different the previous clients. A message to client
 * should be deilvered and received seperatly. This means that this
 * communication of the send asnd recive should be implemented as
 * separate activites rather than sequential. This separate implemantation
 * can be done using threads! So to do this we will have to create two
 * threads in the client side:
 *  1) SendMessage: This thread will be uused to send the message to other
 *      clients. This thread takes the input message to send and the recipient
 *      to deliver to. It uses the message#recipinet message format and writes
 *      this message on the output stream of this client. The handler then breaks
 *      the message and recipient part and delivers it to the particular recipient
 *
 *  2) Receive Message: A similar approach is taken when receiving a message. The
 *      difference is when a client tires to write on the client input stream. For
 *      this we use the readUTF() method.
 */
public class Client1 {

    private Integer serverSocketNumber = null;
    private Socket clientSocket = null;
    private InetAddress ip = null;

    private DataInputStream in = null;
    private DataOutputStream out = null;

    private Scanner scn = new Scanner(System.in);

    public Client1(Integer serverS) throws IOException {
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
        // SendMessage Activity
        Thread sendMessage = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    // Read the message to deliver
                    String msg = scn.nextLine();
                    try{
                        out.writeUTF(msg);
                    }catch (IOException i){
                        i.printStackTrace();
                    }
                }
            }
        });

        // ReceiveMessage Activity
        Thread receiveMessage = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try{
                        String inputMessage = in.readUTF();
                        System.out.println(inputMessage);
                    }catch (IOException i){
                        i.printStackTrace();
                    }
                }
            }
        });

        sendMessage.start();
        receiveMessage.start();
    }

    public static void main(String[] args) throws IOException {
        Client1 c = new Client1(5056);
    }
}
