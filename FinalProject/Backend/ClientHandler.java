package FinalProject.Backend;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

/**
 * This is a helper class that handles clients with threads
 * TODO: add another thread that sends a heartbeat to each client
 * to make sure they are still connected. If not heartbeat is received
 * then that means the Client disconnected and then we get them off the
 * connected list.
 */
public class ClientHandler implements Runnable{

    // Class variables
    // ->Client identification elements
    private String userName = null;
    private Socket socket = null;
    private boolean isLoggedIn = false;
    // ->Client IO streams
    private DataOutputStream outputC = null;
    private DataInputStream inputC = null;

    public ClientHandler(String userName, Socket s, DataInputStream input, DataOutputStream output){
        this.userName = userName;
        this.socket = s;
        this.inputC = input;
        this.outputC = output;
        this.isLoggedIn = true;
    }

    @Override
    public void run(){
        String received = null;
        while(true){
            try {
                // Client received inputStream
                received = inputC.readUTF();
                System.out.println(received);

                // Logout request
                if(received.equals("Exit")){
                    this.isLoggedIn = false;
                    System.out.println("Connection closed on request from client username: " + this.userName);
                    break;
                }

                // Parse header and message
                //  header@message
                //  header contains the username of the person we are trying to communicate with
                String[] message = received.split("@", 2);

                boolean isFound = false;
                // Check if the recipient is connected TODO: when changed to hashmaps change this. Also, add a disconnected list (maybe)
                for(ClientHandler c: Server.clientConnectedList){
                    if(c.isLoggedIn && c.userName.equals(message[0])){
                        c.outputC.writeUTF("M@" + this.userName + "@" + message[1]);
                        isFound = true;
                        break;
                    }
                }

                if(!isFound){ this.outputC.writeUTF("A@This user was not found"); }


            } catch (IOException e){
                try {
                    this.socket.close();
                    this.outputC.close();;
                    this.inputC.close();
                }catch (IOException i){
                    System.out.println("Error closing socket");
                    i.printStackTrace();
                    break;
                }
                System.out.println("Client Handler exception");
                e.printStackTrace();
                break;
            }
        }

        // When the client exits
        try{
            this.socket.close();
            this.outputC.close();;
            this.inputC.close();
        }catch (IOException i){
            i.printStackTrace();
        }
    }

    public String getUserName(){
        return userName;
    }

}
