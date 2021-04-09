package Testing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import newbank.server.NewBank;
import newbank.server.CustomerID;

// Amhar Ford Unit Tests for SHOWTRANSACTIONS
public class SHOWTRANSACTIONS_UnitTest {

    private final NewBank bank = new NewBank();

    @Test
    @DisplayName("Fails if SHOWTRANSACTIONS is lowercase")
    public void TEST1() throws FileNotFoundException{
        assertEquals("UNRECOGNIZED COMMAND.", bank.processRequest(new CustomerID("bhagy"), "showmtranactions"));
    }

    @Test
    @DisplayName("Correct transactions for Christina")
    public void TEST2() throws FileNotFoundException{
        assertEquals(
                "\nHere are the transaction you've sent: \n" +
                "04/09/2021 | amount: 150.0 | account: 7110635 (CHECKING) | receiver: amhar | type: PAYMENT | id: 1828943545\n\n" +
                "Here are the transaction you've received: \n",
                bank.processRequest(new CustomerID("christina"), "SHOWTRANSACTIONS"));
    }

    @Test
    @DisplayName("wrong arguments 1")
    public void TEST3() throws FileNotFoundException{
        assertEquals("FAIL", bank.processRequest(new CustomerID("christina"), "SHOWTRANSACTIONS 12"));
    }

    @Test
    @DisplayName("wrong arguments 2")
    public void TEST4() throws FileNotFoundException{
        assertEquals("UNRECOGNIZED COMMAND.", bank.processRequest(new CustomerID("christina"), "12 SHOWTRANSACTIONS"));
    }

    @Test
    @DisplayName("wrong arguments 3")
    public void TEST5() throws FileNotFoundException{
        assertEquals("UNRECOGNIZED COMMAND.", bank.processRequest(new CustomerID("christina"), "SHOWMYTRANSACTIONS"));
    }

    @Test
    @DisplayName("enter an empty string")
    public void TEST6() throws FileNotFoundException{
        assertEquals("UNRECOGNIZED COMMAND.", bank.processRequest(new CustomerID("christina"), ""));
    }
}

