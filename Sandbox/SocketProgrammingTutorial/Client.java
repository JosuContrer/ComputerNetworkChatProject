package Sandbox.SocketProgrammingTutorial;

import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Client
{
    // Class Variables for socket and IO (Input/Output) streams
    private Socket socket = null;
//    private Scanner input = null;
    private DataInputStream input = null;
    private DataOutputStream out = null;

    // Constructor that sets IP Address and Port
    // IP Address: is the Internet Protocol (IP) address
    //     that is a numerical label assigned to each device
    //     connected to a computer network. The IP address
    //     serves to main functions: host or network indentification
    //     and location addressing. In this example the IP is of the
    //     local host, where code will run on single stand-alone
    //     machine.
    // Port: is a communication endpoint, in more detail in software
    //      within an OS, a port is a logical construct that indentifies
    //      a specific process or a type of network service.
    public Client(String address, int port)
    {
        // establish a connection
        try{
            // Instantiate socket with address
            socket = new Socket(address, port);
            System.out.println("Connected");

            // Input stream form terminal
            input = new DataInputStream(System.in);
//            input = new Scanner(System.in);
            // Output stream to the socket
            out = new DataOutputStream(socket.getOutputStream());

        } catch (UnknownHostException u) { System.out.println(u); }
          catch (IOException i) { System.out.print(i); }

        // Start the Client
        startClient();
    }

    private void startClient(){
        // String to read message form input
        String line = "";

        // Keep reading until over
        while(!line.equals("Over"))
        {
            try{
//                line = input.nextLine();
                line = input.readLine();
                System.out.println("You entered: " + line);
                out.writeUTF(line);
            }
            catch (IOException i)
            {
                System.out.println(i);
            }
        }

        // When the connection is over close it
        try
        {
            input.close();
            out.close();
            socket.close();
        }
        catch(IOException i )
        {
            System.out.println(i);
        }
    }

    public static void main(String args[])
    {
        Client client = new Client("127.0.0.1", 5000);
    }

}