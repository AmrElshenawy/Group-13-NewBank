package Testing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import newbank.server.NewBank;
import newbank.server.CustomerID;

// Amhar Ford Unit Tests for WITHDRAW
public class WITHDRAW_UnitTest{

    private final NewBank bank = new NewBank();

    @Test
    @DisplayName("Fails if WITHDRAW is lowercase")
    public void TEST1() throws FileNotFoundException{
        assertEquals("UNRECOGNIZED COMMAND.", bank.processRequest(new CustomerID("amhar"), "withdraw 50 checking"));
    }

    @Test
    @DisplayName("Account type does not exist")
    public void TEST2() throws FileNotFoundException{
        assertEquals("Error: One or more accounts not recognised. Please check account details and try again.", bank.processRequest(new CustomerID("amhar"), "WITHDRAW 50 moneymarket"));
    }

    @Test
    @DisplayName("Amount is negative")
    public void TEST3() throws FileNotFoundException{
        assertEquals("Error: Invalid amount specified. Transfers must be at least £0.01", bank.processRequest(new CustomerID("amhar"), "WITHDRAW -50 checking"));
    }

    @Test
    @DisplayName("Amount is zero")
    public void TEST4() throws FileNotFoundException{
        assertEquals("Error: Invalid amount specified. Transfers must be at least £0.01", bank.processRequest(new CustomerID("amhar"), "WITHDRAW 0 checking"));
    }

    @Test
    @DisplayName("Amount above maximum allowed")
    public void TEST5() throws FileNotFoundException{
        assertEquals("Error: Invalid amount specified. Transfers cannot exceed £1 million. Please contact Customer Service to authorize payment", bank.processRequest(new CustomerID("amhar"), "WITHDRAW 1000001 checking"));
    }
}

