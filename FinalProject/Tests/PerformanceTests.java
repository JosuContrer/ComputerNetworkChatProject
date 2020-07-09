package FinalProject.Tests;

import FinalProject.Frontend.Client;
import FinalProject.Frontend.CommunicationConstants;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 *              Graph
 *  yClientGrowth
 *  ^
 *  |
 *  |
 *  |
 *  |
 *  |
 *  ---------------> xIntervals
 */
public class PerformanceTests {

    ArrayList<String> userNames = new ArrayList<>();

    /**
     * Loads unique userNames form file provided.
     * @throws FileNotFoundException
     */
    public void loadUserNames() throws FileNotFoundException
    {
        Scanner sc = new Scanner(new File("C:\\Users\\JosuD\\Documents\\Compsuter Science Grad Courses\\E1 20\\CS 513 - Computer Networks\\project\\src\\FinalProject\\Tests\\words.english.txt"));

        while (sc.hasNext())
        {
            String s = sc.next();
            userNames.add(s);
        }
        sc.close();
    }

    public void serverRunTime(int xIntervals, int yClientGrowth) throws Exception {

        // Garbage collector before test
        System.gc();

        // Load the file with unique userNames
        loadUserNames();

        // For communication with the opposite sides of the client list (worst case)
        String firstUserName = userNames.get(0);
        String lastUserName = "";

        Client firsClient = new Client("transfer", CommunicationConstants.CONNECTION_SOCKET);
        Client lastClient = null;

        // Keep track of failed connections
        double connectionFailed = 0;

        // Final run times for each run
        LinkedList<Double> runTimes = new LinkedList<>();

        for(int i = 0; i < xIntervals; i++)
        {
            for(int o = 0; o < yClientGrowth; o++)
            { // Add clients to Server
                lastUserName = userNames.get(i) + Integer.toString(o);
                try {
                    lastClient = new Client(lastUserName, CommunicationConstants.CONNECTION_SOCKET);
                }catch (Exception e){
                    connectionFailed++;
                }
            }

            // Communicate a message from first to last take time
            double start = System.currentTimeMillis();

            firsClient.outputC.writeUTF(CommunicationConstants.WHISPER_MESSAGE + "@" + lastUserName + "@You get my message");
            assertEquals("You get my message", lastClient.inputC.readUTF());

            double elapsedTime = ((System.currentTimeMillis() - start) / 1000.0);
            runTimes.push(elapsedTime);
        }

        // Print out Results
        System.out.println(runTimes);
        System.out.println("Failed Connects: " + connectionFailed);
    }

    public static void main(String[] args){

    }
    
}
