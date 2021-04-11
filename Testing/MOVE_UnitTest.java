package Testing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import newbank.server.NewBank;
import newbank.server.CustomerID;

// Amhar Ford Unit Tests for MOVE
public class MOVE_UnitTest {

    private final NewBank bank = new NewBank();

    @Test
    @DisplayName("Fails if MOVE is lowercase")
    public void TEST1() throws FileNotFoundException{
        assertEquals("UNRECOGNIZED COMMAND.", bank.processRequest(new CustomerID("christina"), "move 50 checking savings"));
    }

    @Test
    @DisplayName("amount is negative")
    public void TEST2() throws FileNotFoundException{
        assertEquals("Error: - Invalid amount specified. Transfers must be at least £0.01\n" +
                "Invalid input. To move money between accounts, please use the following input format: \"MOVE <Amount> <From> <To>\"",
                bank.processRequest(new CustomerID("christina"), "MOVE -50 checking savings"));
    }

    @Test
    @DisplayName("amount is zero")
    public void TEST3() throws FileNotFoundException{
        assertEquals("Error: - Invalid amount specified. Transfers must be at least £0.01\n" +
                "Invalid input. To move money between accounts, please use the following input format: \"MOVE <Amount> <From> <To>\"",
                bank.processRequest(new CustomerID("christina"), "MOVE 0 checking savings"));
    }

    @Test
    @DisplayName("enter a non-existing account type")
    public void TEST4() throws FileNotFoundException{
        assertEquals("Error: - One or more accounts not recognised. Please check account details and try again.\n" +
                "Invalid input. To move money between accounts, please use the following input format: \"MOVE <Amount> <From> <To>\"",
                bank.processRequest(new CustomerID("christina"), "MOVE 50 checking investment"));
    }

    @Test
    @DisplayName("amount exceeds 1 million")
    public void TEST5() throws FileNotFoundException{
        assertEquals("Error: - One or more accounts not recognised. Please check account details and try again.\n" +
                        "Invalid input. To move money between accounts, please use the following input format: \"MOVE <Amount> <From> <To>\"",
                bank.processRequest(new CustomerID("christina"), "MOVE 1000001 checking savings"));
    }
}

