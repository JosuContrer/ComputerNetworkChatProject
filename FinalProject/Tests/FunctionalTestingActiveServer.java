package FinalProject.Tests;

import FinalProject.CommunicationConstants;
import FinalProject.Backend.*;
import FinalProject.Frontend.*;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.IOException;

/**
 * This test class tests the expected functional communication
 * for request on the I/O streams of a running server. To run
 * these test a server has to be initialized beforehand and
 * then this class can be run.
 */
public class FunctionalTestingActiveServer {

    Client c1;
    Client c2;

    @Before
    public void singelClientSetup() throws Exception{
        String userName = "Q";
        c1 = new Client(userName, CommunicationConstants.CONNECTION_SOCKET);

        c2 = new Client(userName, CommunicationConstants.CONNECTION_SOCKET);
    }

    @Test
    public void notDuplicateClientTest() throws IOException {
        assertEquals(c1.inputC.read(), CommunicationConstants.NOT_DUPLICATE);
    }

    @Test
    public void duplicateClientTest() throws IOException{
        assertEquals(c2.inputC.read(), CommunicationConstants.IS_DUPLICATE);
    }

}
