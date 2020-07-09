package FinalProject.Backend;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * This Client Handler class is a helper class that implements the
 * runnable interface for the multithreading of the chat application.
 * In this class the client is set up and it handles the requests made
 * by the client to the Server.
 */
public class ClientHandler implements Runnable{

    // ->Client identification elements
    private String userName = null;
    private Socket socket = null;
    private boolean isLoggedIn = false;

    // ->Client IO streams
    private DataOutputStream outputC = null;
    private DataInputStream inputC = null;
    private Boolean isDuplicate = false;

    public ClientHandler(){}

    public ClientHandler(String userName, Socket s, DataInputStream input, DataOutputStream output, Boolean isDuplicate){
        this.userName = userName;
        this.socket = s;
        this.inputC = input;
        this.outputC = output;
        this.isLoggedIn = true;
        this.isDuplicate = isDuplicate;
    }

    @Override
    public void run(){
        String received = null;
        while(true){
            try
            {
                // Client received inputStream
                received = inputC.readUTF();

                // Parse the packet for ID and message
                String[] packet = parsePacket(received);
                Integer controlID = Integer.parseInt(packet[0]);

                // -> HANDLE PACKET FROM SENT FROM CLIENT
                switch (controlID)
                {
                    case CommunicationConstants.LOG_OUT: // Logout request
                        this.isLoggedIn = false;
                        disconnectClient();
                        break;

                    case CommunicationConstants.WHISPER_MESSAGE: // Send Message to user request
                        System.out.println(" Send Message: " + this.userName + "->" + packet[1]);
                        boolean isFound = false;

                        // Check if the recipient is connected
                        for (ClientHandler c : Server.clientConnectedList) {
                            if (c.isLoggedIn && c.userName.equals(packet[1])) {
                                c.outputC.writeUTF(CommunicationConstants.WHISPER_MESSAGE + "@" + this.userName + "@" + packet[2]);
                                isFound = true;
                                break;
                            }
                        }

                        if (!isFound) {
                            System.out.println("  User not found: " + packet[1]);
                            this.outputC.writeUTF(CommunicationConstants.USER_NOT_FOUND + "@" + packet[1]);
                        }
                        break;

                    case CommunicationConstants.CONNECTED_USERS_REQUEST: // Connected users request
                        this.outputC.writeUTF(CommunicationConstants.CONNECTED_USERS_REQUEST + "@" + Server.connectedUserManager.toString());
                        break;
                }
            }
            catch (IOException e)
            {
                try
                {
                    disconnectClient();
                }
                catch (IOException i)
                {
                    System.out.println("Error closing socket");
                    i.printStackTrace();
                    break;
                }
                System.out.println("Client has disconnected: " + this.userName);
                break;
            }
        }
        // When the client exits
        try
        {
            disconnectClient();
        }
        catch (IOException i)
        {
            i.printStackTrace();
        }
    }

    /**
     * Parse packet sent by client
     *     -> header@username@message
     *     -> header contains the action, username of the person we are trying to communicate, and the message
     * The username and message depend on the action in the header
     * @param received
     * @return
     */
    private String[] parsePacket(String received){
        return received.split("@", 3);
    }

    private void disconnectClient() throws IOException {
        Server.connectedUserManager.deleteUser(userName);
        this.socket.close();
        this.outputC.close();;
        this.inputC.close();
        Server.deleteClient(userName);
    }

    public String getUserName() {
        return userName;
    }
}
