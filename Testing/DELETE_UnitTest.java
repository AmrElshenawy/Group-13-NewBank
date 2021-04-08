package Testing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import newbank.server.NewBank;
import newbank.server.CustomerID;

// Amr Elshenawy Unit Tests for DELETE
public class DELETE_UnitTest {
    
    private NewBank bank = new NewBank();

    @Test
    @DisplayName("Fails if DELETE is lowercase -> (delete 522655285)")
    public void TEST1() throws FileNotFoundException{
        //bank = new NewBank();
        assertEquals("UNRECOGNIZED COMMAND.", bank.processRequest(new CustomerID("staff"), "delete 522655285"));
    }

    @Test
    @DisplayName("Fails if user is not a staff member -> For Bhagy: (DELETE 522655285)")
    public void TEST2() throws FileNotFoundException{
        assertEquals("NOT A STAFF MEMBER. ACTION DENIED!", bank.processRequest(new CustomerID("bhagy"), "DELETE 522655285"));
        assertEquals("NOT A STAFF MEMBER. ACTION DENIED!", bank.processRequest(new CustomerID("john"), "DELETE 522655285"));
        assertEquals("NOT A STAFF MEMBER. ACTION DENIED!", bank.processRequest(new CustomerID("christina"), "DELETE 522655285"));
        assertEquals("NOT A STAFF MEMBER. ACTION DENIED!", bank.processRequest(new CustomerID("amrelshenawy"), "DELETE 522655285"));
    }

    @Test
    @DisplayName("Fails if staff member attempts to delete staff account -> For Staff: (DELETE -1444844)")
    public void TEST3() throws FileNotFoundException{
        assertEquals("CANNOT DELETE STAFF ACCOUNT. ACTION DENIED!", bank.processRequest(new CustomerID("staff"), "DELETE -1444844"));
    }

    @Test
    @DisplayName("Fails if input arguments are incorrect -> For Staff: (DELETE 522655285 checking dummyargument)")
    public void TEST4() throws FileNotFoundException{
        assertEquals("UNRECOGNIZED COMMAND.", bank.processRequest(new CustomerID("staff"), "DELETE 522655285 checking dummyargument"));
    }

    @Test
    @DisplayName("Fails if customer ID doesn't exist -> For Staff: (DELETE 123456)")
    public void TEST5() throws FileNotFoundException{
        assertEquals("ERROR. Cannot find Customer ID.", bank.processRequest(new CustomerID("staff"), "DELETE 123456"));
    }

    @Test
    @DisplayName("Fails if account type doesn't exist -> For Staff: (DELETE 522655285 MONEYMARKET)")
    public void TEST6() throws FileNotFoundException{
        assertEquals("ERROR. Cannot find Account Type.", bank.processRequest(new CustomerID("staff"), "DELETE 522655285 MONEYMARKET"));
    }

    @Test
    @DisplayName("Fails if customer ID doesn't exist for account type deletion -> For Staff: (DELETE 132456 MONEYMARKET)")
    public void TEST7() throws FileNotFoundException{
        assertEquals("ERROR. Please check the parameters entered. Cannot find Customer ID for this Account Type.", bank.processRequest(new CustomerID("staff"), "DELETE 123456 MONEYMARKET"));
    }

    @Test
    @DisplayName("Passes with deleting account type case-insensitive -> For Staff: (DELETE 1647908596 SAVINGS), (DELETE 1647908596 savings)")
    public void TEST8() throws FileNotFoundException{
        assertEquals("Account type SAVINGS for customer ID# 1647908596 DELETED!", bank.processRequest(new CustomerID("staff"), "DELETE 1647908596 SAVINGS"));
        bank = new NewBank();
        assertEquals("Account type SAVINGS for customer ID# 1647908596 DELETED!", bank.processRequest(new CustomerID("staff"), "DELETE 1647908596 savings"));
    }

    @Test
    @DisplayName("Passes with deleting an existing customer ID -> For Staff: (DELETE 1647908596)")
    public void TEST9() throws FileNotFoundException{
        assertEquals("Customer ID# 1647908596 DELETED!", bank.processRequest(new CustomerID("staff"), "DELETE 1647908596"));
    }

    @Test
    @DisplayName("Fails if Customer ID# and Account Type are swapped -> For Staff: (DELETE savings 1647908596)")
    public void TEST10() throws FileNotFoundException{
        assertEquals("ERROR. Please check the parameters entered. Cannot find Customer ID for this Account Type.", bank.processRequest(new CustomerID("staff"), "DELETE savings 1647908596"));
    }
}
