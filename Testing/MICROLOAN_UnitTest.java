package Testing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import newbank.server.NewBank;
import newbank.server.CustomerID;

// Amhar Ford Unit Tests for MICROLOAN
public class MICROLOAN_UnitTest {

    private final NewBank bank = new NewBank();

    @Test
    @DisplayName("Fails if MICROLOAN is lowercase")
    public void TEST1() throws FileNotFoundException{
        assertEquals("UNRECOGNIZED COMMAND.", bank.processRequest(new CustomerID("christina"), "microloan 50 checking john"));
    }

    @Test
    @DisplayName("amount is negative")
    public void TEST2() throws FileNotFoundException{
        assertEquals("Error: Invalid amount specified. Loans must be at least £0.01 and at most £1000", bank.processRequest(new CustomerID("christina"), "MICROLOAN -50 checking john"));
    }

    @Test
    @DisplayName("amount is zero")
    public void TEST3() throws FileNotFoundException{
        assertEquals("Error: Invalid amount specified. Loans must be at least £0.01 and at most £1000", bank.processRequest(new CustomerID("christina"), "MICROLOAN 0 checking john"));
    }

    @Test
    @DisplayName("enter a non-existing user")
    public void TEST4() throws FileNotFoundException{
        assertEquals("Error: Payee does not exists in our records. Please check your payment instructions", bank.processRequest(new CustomerID("christina"), "MICROLOAN 50 checking sally"));
    }

    @Test
    @DisplayName("Amount exceeds maximum loan amount")
    public void TEST5() throws FileNotFoundException{
        assertEquals("Error: Invalid amount specified. Loans must be at least £0.01 and at most £1000",
                bank.processRequest(new CustomerID("christina"), "MICROLOAN 1001 checking john"));
    }
}

