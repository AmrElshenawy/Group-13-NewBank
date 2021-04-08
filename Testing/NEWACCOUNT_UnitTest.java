package Testing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import newbank.server.NewBank;
import newbank.server.CustomerID;

// Amr Elshenawy Unit Tests for NEWACCOUNT
public class NEWACCOUNT_UnitTest {

    private final NewBank bank = new NewBank();

    @Test
    @DisplayName("Fails if an account type already exists -> For bhagy: (NEWACCOUNT CHECKING)")
    public void TEST1() throws FileNotFoundException{
        assertEquals("ERROR. ACCOUNT TYPE ALREADY EXISTS!", bank.processRequest(new CustomerID("bhagy"), "NEWACCOUNT CHECKING"));
        //assertEquals("FAIL", bank.processRequest(new CustomerID("amrelshenawy"), "NEWACCOUNT 500 moneymarket"));
    }

    @Test
    @DisplayName("Passes if creating a new account that doesn't exist -> For amrelshenawy: (NEWACCOUNT savings), (NEWACCOUNT moneymarket)")
    public void TEST2() throws FileNotFoundException{
        assertEquals("SUCCESS", bank.processRequest(new CustomerID("amrelshenawy"), "NEWACCOUNT savings"));
        assertEquals("SUCCESS", bank.processRequest(new CustomerID("amrelshenawy"), "NEWACCOUNT moneymarket"));
    }

    @Test
    @DisplayName("Fails if number of accounts exceed 3 -> For bhagy: (NEWACCOUNT MONEYMARKET), (NEWACCOUNT savings)")
    public void TEST3() throws FileNotFoundException{
        assertEquals("SUCCESS", bank.processRequest(new CustomerID("bhagy"), "NEWACCOUNT MONEYMARKET"));
        assertEquals("MAXIMUM NUMBER OF ACCOUNTS REACHED!", bank.processRequest(new CustomerID("bhagy"), "NEWACCOUNT savings"));
    }

    @Test
    @DisplayName("Fails if input arguments are incorrect -> For bhagy: (NEWACCOUNT 500 moneymarket)")
    public void TEST4() throws FileNotFoundException{
        assertEquals("INCORRECT COMMAND ENTERED!", bank.processRequest(new CustomerID("bhagy"), "NEWACCOUNT 500 moneymarket"));
    }

    @Test
    @DisplayName("Passes with account type case-insensitive -> For amrelshenawy: (NEWACCOUNT SAVINGS), (NEWACCOUNT moneymarket)")
    public void TEST5() throws FileNotFoundException{
        assertEquals("SUCCESS", bank.processRequest(new CustomerID("amrelshenawy"), "NEWACCOUNT SAVINGS"));
        assertEquals("SUCCESS", bank.processRequest(new CustomerID("amrelshenawy"), "NEWACCOUNT moneymarket"));
    }

    @Test
    @DisplayName("Fails if no account type is included -> For bhagy: (NEWACCOUNT 500)")
    public void TEST6() throws FileNotFoundException{
        assertEquals("PLEASE PROVIDE CHECKING, SAVINGS OR MONEYMARKET AS TYPE.", bank.processRequest(new CustomerID("bhagy"), "NEWACCOUNT 500"));
    }

    @Test
    @DisplayName("Fails if incorrect account type entered -> For john: (NEWACCOUNT overdraft)")
    public void TEST7() throws FileNotFoundException{
        assertEquals("PLEASE PROVIDE CHECKING, SAVINGS OR MONEYMARKET AS TYPE.", bank.processRequest(new CustomerID("john"), "NEWACCOUNT overdraft"));
    }

    @Test
    @DisplayName("Fails if NEWACCOUNT is lowercase -> For john: (newaccount moneymarket)")
    public void TEST8() throws FileNotFoundException{
        assertEquals("UNRECOGNIZED COMMAND.", bank.processRequest(new CustomerID("john"), "newaccount moneymarket"));
    }
}
