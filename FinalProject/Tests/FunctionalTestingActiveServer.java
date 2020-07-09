package FinalProject.Tests;

import FinalProject.CommunicationConstants;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import FinalProject.Backend.*;
import FinalProject.Frontend.*;

import java.io.IOException;

public class FunctionalTestingActiveServer {

    Client c1;

    @Before
    public void singelClientSetup() throws Exception{
        String userName = "Q";
        c1 = new Client(userName, 59091);
        c1.outputC.writeUTF(userName);
    }

    @Test
    public void notDuplicateClientTest() throws IOException {
        assertEquals(c1.inputC.read(), CommunicationConstants.NOT_DUPLICATE);
    }

}
