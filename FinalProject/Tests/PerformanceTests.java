package FinalProject.Tests;

import FinalProject.Frontend.Client;
import FinalProject.Frontend.CommunicationConstants;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;


/**
 * Performance tests for server
 */
public class PerformanceTests {

    public static LinkedList<String> userNames = new LinkedList<>();

    /**
     * Loads unique userNames form file provided.
     * @throws FileNotFoundException
     */
    public void loadUserNames() throws FileNotFoundException
    {
        Scanner sc = new Scanner(new File("file path"));

        while (sc.hasNext())
        {
            String s = sc.next();
            userNames.add(s);
        }
        sc.close();
    }

    /**
     * Worst Case is based on the data structure that runs the client handler
     * @param xIntervals
     * @throws FileNotFoundException
     */
    public void serverRunTime(int xIntervals) throws FileNotFoundException {

        // Garbage collector before test
        System.gc();

        // Load the file with unique userNames
        loadUserNames();

        // For communication with the opposite sides of the client list (worst case)
        String firstUserName = userNames.get(0);
        String lastUserName = "";

        // Keep track of failed connections
        double connectionFailed = 0;

        // Final run times for each run
        LinkedList<Double> runTimes = new LinkedList<>();

        for(int i = 0; i < xIntervals; i++)
        {
            // Communicate a message from first to last take time
            double start = System.currentTimeMillis();

            // Worst case for data structure
            for(String user: userNames){
                if(user != userNames.get(i)){
                    break;
                }
            }

            double elapsedTime = ((System.currentTimeMillis() - start) / 1000.0);
            runTimes.push(elapsedTime);
        }

        // Print out Results
        System.out.println(runTimes);
        System.out.println("Failed Connects: " + connectionFailed);
    }

    /**
     * Connect clients consecutively to server.
     * @param numberOfClients
     * @throws Exception
     */
    public void serverFails(int numberOfClients) throws Exception {

        // Garbage collector before test
        System.gc();

        loadUserNames();
        String lastUserName;
        // Keep track of failed connections
        double connectionFailed = 0;
        double connectionSucc = 0;
        double stringLength = 0;

        for(int o = 0; o < numberOfClients; o++)
        { // Add clients to Server
            if(o%10 == 0){
                System.out.println("Total Clients Connected " + o);
                System.out.println("Connection Successful: " + connectionSucc);
                System.out.println("Failed Connects: " + connectionFailed);
            }
            lastUserName = userNames.get(o);
            try {
                stringLength += lastUserName.length();
               new Client(lastUserName, CommunicationConstants.CONNECTION_SOCKET);
               connectionSucc++;
            }catch (Exception e){
                connectionFailed++;
            }
        }

        System.out.println("String Length: " + stringLength);
    }

    public static void main(String[] args){

        PerformanceTests p = new PerformanceTests();
        try {
            // Setup tests as described in paper
            p.serverFails(5);
            //p.serverFails(100000);
        }catch (Exception e){
            e.printStackTrace();
        }
        while(true){}

    }
    
}
