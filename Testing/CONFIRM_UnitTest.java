package Testing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import newbank.server.NewBank;
import newbank.server.CustomerID;

// Amr Elshenawy Unit Tests for CONFIRM
public class CONFIRM_UnitTest {
    
    private final NewBank bank = new NewBank();

    @Test
    @DisplayName("Fails if CONFIRM is lowercase -> For any user: (confirm)")
    public void TESTa() throws FileNotFoundException{
        assertEquals("UNRECOGNIZED COMMAND.", bank.processRequest(new CustomerID("staff"), "confirm"));
    }

    @Test
    @DisplayName("Fails if no change done -> For any user: (CONFIRM)")
    public void TESTb() throws FileNotFoundException{
        assertEquals("NO CHANGES TO CONFIRM.", bank.processRequest(new CustomerID("bhagy"), "CONFIRM"));
    }

    @Test
    @DisplayName("Fails if only SHOWMYACCOUNT was triggered, since it doesn't modify the DB.")
    public void TESTc() throws FileNotFoundException{
        bank.processRequest(new CustomerID("bhagy"), "SHOWMYACCOUNTS");
        assertEquals("NO CHANGES TO CONFIRM.", bank.processRequest(new CustomerID("bhagy"), "CONFIRM"));
    }
    
    @Test
    @DisplayName("Passes if NEWACCOUNT was triggered.")
    public void TESTd() throws FileNotFoundException{
        bank.processRequest(new CustomerID("amrelshenawy"), "NEWACCOUNT MONEYMARKET");
        assertEquals("CHANGES CONFIRMED!", bank.processRequest(new CustomerID("amrelshenawy"), "CONFIRM"));
    }

    @Test
    @DisplayName("Passes if MOVE was triggered.")
    public void TESTe() throws FileNotFoundException{
        bank.processRequest(new CustomerID("bhagy"), "MOVE 100 CHECKING SAVINGS");
        assertEquals("CHANGES CONFIRMED!", bank.processRequest(new CustomerID("bhagy"), "CONFIRM"));
    }

    @Test
    @DisplayName("Passes if PAY was triggered.")
    public void TESTf() throws FileNotFoundException{
        bank.processRequest(new CustomerID("bhagy"), "PAY 100 CHECKING amrelshenawy");
        assertEquals("CHANGES CONFIRMED!", bank.processRequest(new CustomerID("bhagy"), "CONFIRM"));
    }

    @Test
    @DisplayName("Passes if DEPOSIT was triggered.")
    public void TESTg() throws FileNotFoundException{
        bank.processRequest(new CustomerID("amrelshenawy"), "DEPOSIT 1000 CHECKING");
        assertEquals("CHANGES CONFIRMED!", bank.processRequest(new CustomerID("amrelshenawy"), "CONFIRM"));
    }

    @Test
    @DisplayName("Passes if WITHDRAW was triggered.")
    public void TESTh() throws FileNotFoundException{
        bank.processRequest(new CustomerID("amrelshenawy"), "WITHDRAW 500 CHECKING");
        assertEquals("CHANGES CONFIRMED!", bank.processRequest(new CustomerID("amrelshenawy"), "CONFIRM"));
    }

    @Test
    @DisplayName("Fails if only VIEWLOANREQUESTS is triggered, since it doesn't modify the DB.")
    public void TESTi() throws FileNotFoundException{
        bank.processRequest(new CustomerID("amrelshenawy"), "VIEWLOANREQUESTS");
        assertEquals("NO CHANGES TO CONFIRM.", bank.processRequest(new CustomerID("amrelshenawy"), "CONFIRM"));
    }

    @Test
    @DisplayName("Fails if only CREATELOANREQUEST is triggered, since it doesn't modify the DB.")
    public void TESTj() throws FileNotFoundException{
        bank.processRequest(new CustomerID("amrelshenawy"), "CREATELOANREQUEST");
        assertEquals("NO CHANGES TO CONFIRM.", bank.processRequest(new CustomerID("amrelshenawy"), "CONFIRM"));
    }

    @Test
    @DisplayName("Fails if only LOANCALCULATOR is triggered, since it doesn't modify the DB.")
    public void TESTk() throws FileNotFoundException{
        bank.processRequest(new CustomerID("amrelshenawy"), "LOANCALCULATOR");
        assertEquals("NO CHANGES TO CONFIRM.", bank.processRequest(new CustomerID("amrelshenawy"), "CONFIRM"));
    }

    @Test
    @DisplayName("Fails if only MICROLOAN is triggered, since it doesn't modify the DB.")
    public void TESTl() throws FileNotFoundException{
        bank.processRequest(new CustomerID("john"), "MICROLOAN 500 checking christina");
        assertEquals("NO CHANGES TO CONFIRM.", bank.processRequest(new CustomerID("john"), "CONFIRM"));
    }

    @Test
    @DisplayName("Passes if DELETE for customer is triggered.")
    public void TESTm() throws FileNotFoundException{
        bank.processRequest(new CustomerID("staff"), "DELETE 1618832874");
        assertEquals("CHANGES CONFIRMED!", bank.processRequest(new CustomerID("staff"), "CONFIRM"));
    }

    @Test
    @DisplayName("Passes if DELETE for account is triggered.")
    public void TESTn() throws FileNotFoundException{
        bank.processRequest(new CustomerID("staff"), "DELETE 1647908596 savings");
        assertEquals("CHANGES CONFIRMED!", bank.processRequest(new CustomerID("staff"), "CONFIRM"));
    }

    @Test
    @DisplayName("Fails if only AUDITREPORT is triggered, since it doesn't modify the DB.")
    public void TESTo() throws FileNotFoundException{
        bank.processRequest(new CustomerID("staff"), "AUDITREPORT");
        assertEquals("NO CHANGES TO CONFIRM.", bank.processRequest(new CustomerID("staff"), "CONFIRM"));
    }

    @Test
    @DisplayName("Fails if only SHOWTRANSACTIONS is triggered, since it doesn't modify the DB.")
    public void TESTp() throws FileNotFoundException{
        bank.processRequest(new CustomerID("christina"), "SHOWTRANSACTIONS");
        assertEquals("NO CHANGES TO CONFIRM.", bank.processRequest(new CustomerID("christina"), "CONFIRM"));
    }

    @Test
    @DisplayName("Passes if LOGOUT is triggered.")
    public void TESTq() throws FileNotFoundException{
        bank.processRequest(new CustomerID("christina"), "LOGOUT");
        assertEquals("CHANGES CONFIRMED!", bank.processRequest(new CustomerID("christina"), "CONFIRM"));
    }
}
