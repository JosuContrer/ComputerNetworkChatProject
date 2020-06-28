package Sandbox.MultiClientsTutorial;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class allows us to use threads in a separate way since
 * each time this class is instantiated will be when a request
 * comes.
 *  1) Since this class extends the "Thread" class it assumes all
 *      properties of "Threads."
 *  2) The constructor of this class takes three parameters, which
 *      can uniquely identify an incoming request. This means a socket,
 *      DataInputStream (to read from), and DataOutputStream (to write to).
 *      This basically allows the server to extract the port number and
 *      stream objects when we receive a request form a client. Once these
 *      are extracted then a new thread object is creates and then the start()
 *      method is invoked.
 *  3) Because we extend "Threads" we have to override the run() method.
 *      Therefore in the run() method of this class three operation are
 *      preformed: 1. In this example it requests the user to specify
 *      wether time or date are needed; 2. Reads the answer input stream
 *      object and writtes the output on the output stream obejct.
 */
public class ClientHandler extends Thread{

    // Identification Variables
    private Socket socket = null;
    private DataInputStream in = null;
    private DataOutputStream out = null;

    // Date and time Variables
    DateFormat dateF = new SimpleDateFormat("yyyy/MM/dd");
    DateFormat timeF = new SimpleDateFormat("hh:mm:ss");

    // Constructor
    public ClientHandler(Socket s, DataInputStream i, DataOutputStream o){
        this.socket = s;
        this.in = i;
        this.out = o;
    }

    // Run method
    @Override
    public void run(){
        String received;
        String toReturn;

        while(true){
            try {
                // Ask user
                out.writeUTF("Choose Date or Time. Exit will terminate connection.");

                // Recieve answer
                received = in.readUTF();

                // Client exit request
                if(received.equals("Exit")){
                    System.out.println("Closing connection with client on request: " + this.socket);
                    this.socket.close();
                    System.out.println("Connection closed...");
                    break;
                }

                // Handle the type of stream that client wants
                Date date = new Date();
                switch (received){
                    case "Date":
                        toReturn = dateF.format(date);
                        out.writeUTF(toReturn);
                        break;
                    case "Time":
                        toReturn = timeF.format(date);
                        out.writeUTF(toReturn);
                        break;
                    default:
                        out.writeUTF("INVALID INPUT");
                        break;
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
