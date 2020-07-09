package FinalProject.Tests;

import FinalProject.CommunicationConstants;

import org.junit.Test;
import static org.junit.Assert.*;

import FinalProject.Backend.*;
import FinalProject.Frontend.*;

public class FunctionalTestingInactiveServer {

    // CONNECTED_USER_MANGER
    @Test
    public void connectedUserListTest(){
        connectedUserManager cu = new connectedUserManager();
        cu.addUser("Q");
        cu.addUser("W");
        cu.addUser("E");
        cu.addUser("R");
        cu.addUser("T");
        cu.addUser("Y");

        String finalString = "Q,W,E,T,Y,U,";

        // Check if user was added
        assertTrue(cu.addUser("U"));

        // Try to add duplicate user
        assertFalse(cu.addUser("U"));

        // Delete user and then try to add them back
        assertTrue(cu.deleteUser("R"));

        // Try to delete a user that is not in the list
        assertFalse(cu.deleteUser("R"));

        // Check to String method output
        assertEquals(finalString, cu.toString());
    }

    // CLIENT TESTS -> these tests a run with an inactive server
    @Test
    public void clientBlankUsernameTest(){
        // When creating a new client check for blank username
        Exception e = assertThrows(Exception.class, () -> {new Client("", 59091);});
        assertTrue(e.getMessage().contains(CommunicationConstants.INVALID_USERNAME));
    }

    @Test
    public void clientConnectingToDisconnectedServerTest(){
        // When creating a new client check for blank username
        Exception e = assertThrows(Exception.class, () -> {new Client("Josue", 59091);});
        assertTrue(e.getMessage().contains(CommunicationConstants.SERVER_FAILED));
    }

}
