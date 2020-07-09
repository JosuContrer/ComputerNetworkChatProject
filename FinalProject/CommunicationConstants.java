package FinalProject;

/**
 * Client-Server Constants for Actions, Messaging, and System.
 */
public class CommunicationConstants {

    // CONNECTION SOCKET
    public static final int CONNECTION_SOCKET = 59091;

    // Communication Constants
    public static final int USER_NOT_FOUND = 5;
    public static final int CONNECTED_USERS_REQUEST = 6;
    public static final int WHISPER_MESSAGE = 7;
    public static final int LOG_OUT = 8;
    public static final int LOG_IN = 9;
    public static final int IS_DUPLICATE = 10;
    public static final int NOT_DUPLICATE = 11;

    // Server Constants
    public static final String SERVER_FAILED = "SERVER FAILED";

    // Client Constants
    public static final String INVALID_USERNAME = "INVALID USERNAME";
    public static final int HEART_BEAT_INTERVAL = 4000;

}
