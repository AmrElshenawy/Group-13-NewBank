package Testing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import newbank.server.NewBank;
import newbank.server.CustomerID;

// Amr Elshenawy Unit Tests for HELP
public class HELP_UnitTest {
    
    private final NewBank bank = new NewBank();

    @Test
    @DisplayName("Fails if HELP is lowercase -> For Bhagy: (help), (help MICROLOAN)")
    public void TESTa() throws FileNotFoundException{
        assertEquals("UNRECOGNIZED COMMAND.", bank.processRequest(new CustomerID("bhagy"), "help"));
        assertEquals("UNRECOGNIZED COMMAND.", bank.processRequest(new CustomerID("bhagy"), "help MICROLOAN"));
    }

    @Test
    @DisplayName("Passes when command entered for HELP is case-insensitive -> For Bhagy: (HELP DEPOSIT), (HELP deposit)")
    public void TESTb() throws FileNotFoundException{
        assertEquals("DEPOSIT 1000 moneymarket - Will deposit 1000 into Moneymarket account.", bank.processRequest(new CustomerID("bhagy"), "HELP DEPOSIT"));
        assertEquals("DEPOSIT 1000 moneymarket - Will deposit 1000 into Moneymarket account.", bank.processRequest(new CustomerID("bhagy"), "HELP deposit"));
    }

    @Test
    @DisplayName("Returns correct available commands for Staff members -> For Staff: (HELP)")
    public void TESTc() throws FileNotFoundException{
        assertEquals("\nAvailable commands: \n"+
        "* DELETE <customer ID> \n" +
        "* DELETE <customer ID> <account type> \n" +
        "* AUDITREPORT \n" +
        "* HELP \n" +
        "* HELP <command name> \n" +
        "* CONFIRM \n" +
        "* LOGOUT", bank.processRequest(new CustomerID("staff"), "HELP"));
    }

    @Test
    @DisplayName("Returns correct available commands for General users -> For a non-staff user: (HELP)")
    public void TESTd() throws FileNotFoundException{
        assertEquals("\nAvailable commands: \n" +
        "* NEWACCOUNT <account type> \n" +
        "* SHOWMYACCOUNTS \n" +
        "* MOVE <amount> <from> <to> \n" +
        "* DEPOSIT <amount> <to account type> \n" +
        "* WITHDRAW <amount> <from account type> \n" +
        "* SHOWTRANSACTIONS \n" +
        "* PAY <amount> <from account type> <to customer name> \n" +
        "* MICROLOAN <amount> <from account type> <to customer name> \n" +
        "* VIEWLOANREQUESTS \n" +
        "* CREATELOANREQUEST <amount> <interestRate> <installments> <duration in weeks> \n" +
        "* LOANCALCULATOR <amount> <interestRate> <installments> <duration in weeks> \n" +
        "* HELP \n" +
        "* HELP <command name> \n" +
        "* CONFIRM \n" +
        "* LOGOUT", bank.processRequest(new CustomerID("john"), "HELP"));
    }

    @Test
    @DisplayName("Fails if command entered for HELP is unrecognized -> For any user: (HELP dummyargument)")
    public void TESTe() throws FileNotFoundException{
        assertEquals("UNRECOGNIZED COMMAND.", bank.processRequest(new CustomerID("staff"), "HELP dummyargument"));
        assertEquals("UNRECOGNIZED COMMAND.", bank.processRequest(new CustomerID("amrelshenawy"), "HELP DUMMYARGUMENT"));
    }

    @Test
    @DisplayName("Returns correct description for NEWACCOUNT")
    public void TESTf() throws FileNotFoundException{
        assertEquals("NEWACCOUNT checking - Will create a new Checking account with 0 balance.", bank.processRequest(new CustomerID("bhagy"), "HELP NEWACCOUNT"));
    }

    @Test
    @DisplayName("Returns correct description for SHOWMYACCOUNTS")
    public void TESTg() throws FileNotFoundException{
        assertEquals("Will display all accounts and their information for the customer.", bank.processRequest(new CustomerID("bhagy"), "HELP SHOWMYACCOUNTS"));
    }

    @Test
    @DisplayName("Returns correct description for MOVE")
    public void TESTh() throws FileNotFoundException{
        assertEquals("MOVE 5000 checking savings - Will move 5000 from Checking account to Savings account.", bank.processRequest(new CustomerID("bhagy"), "HELP MOVE"));
    }

    @Test
    @DisplayName("Returns correct description for DEPOSIT")
    public void TESTi() throws FileNotFoundException{
        assertEquals("DEPOSIT 1000 moneymarket - Will deposit 1000 into Moneymarket account.", bank.processRequest(new CustomerID("bhagy"), "HELP DEPOSIT"));
    }

    @Test
    @DisplayName("Returns correct description for WITHDRAW")
    public void TESTj() throws FileNotFoundException{
        assertEquals("WITHDRAW 500 savings - Will withdraw 500 from Savings account.", bank.processRequest(new CustomerID("bhagy"), "HELP WITHDRAW"));
    }

    @Test
    @DisplayName("Returns correct description for SHOWTRANSACTIONS")
    public void TESTk() throws FileNotFoundException{
        assertEquals("SHOWTRANSACTIONS - Will display all incoming and outgoing transactions from this account.", bank.processRequest(new CustomerID("bhagy"), "HELP SHOWTRANSACTIONS"));
    }

    @Test
    @DisplayName("Returns correct description for PAY")
    public void TESTl() throws FileNotFoundException{
        assertEquals("PAY 5500 checking john - Will pay 5500 deducted from Checking paid to John's default account which is always Checking.", bank.processRequest(new CustomerID("bhagy"), "HELP PAY"));
    }

    @Test
    @DisplayName("Returns correct description for MICROLOAN")
    public void TESTm() throws FileNotFoundException{
        assertEquals("MICROLOAN 5500 checking john - Will send 1000 deducted from Checking to John's default account which is always Checkings.", bank.processRequest(new CustomerID("bhagy"), "HELP MICROLOAN"));
    }

    @Test
    @DisplayName("Returns correct description for VIEWLOANREQUESTS")
    public void TESTn() throws FileNotFoundException{
        assertEquals("VIEWLOANREQUESTS - Displays the list of current requests", bank.processRequest(new CustomerID("bhagy"), "HELP VIEWLOANREQUESTS"));
    }

    @Test
    @DisplayName("Returns correct description for CREATELOANREQUEST")
    public void TESTo() throws FileNotFoundException{
        assertEquals("CREATELOANREQUEST 2000 5 10 20 - Will create a loan request for 2000 dollars at 5% interest rate for 10 installments over 20 weeks.", bank.processRequest(new CustomerID("bhagy"), "HELP CREATELOANREQUEST"));
    }

    @Test
    @DisplayName("Returns correct description for LOANCALCULATOR")
    public void TESTp() throws FileNotFoundException{
        assertEquals("LOANCALCULATOR 2000 5 10 20 - Calculates the loan details for entered parameters.", bank.processRequest(new CustomerID("bhagy"), "HELP LOANCALCULATOR"));
    }

    @Test
    @DisplayName("Returns correct description for DELETE")
    public void TESTq() throws FileNotFoundException{
        assertEquals("DELETE 489418943 - Will delete all records pertinent to the customer with ID# 489418943. Accessible by staff members only. \n" +
        "DELETE 489418943 moneymarket - Will delete Moneymarket account for customer ID# 489418943. Accessible by staff members only.", bank.processRequest(new CustomerID("bhagy"), "HELP DELETE"));
    }

    @Test
    @DisplayName("Returns correct description for AUDITREPORT")
    public void TESTr() throws FileNotFoundException{
        assertEquals("Generates a report of all banking activity. Accessible by staff members only.", bank.processRequest(new CustomerID("bhagy"), "HELP AUDITREPORT"));
    }

    @Test
    @DisplayName("Returns correct description for CONFIRM")
    public void TESTs() throws FileNotFoundException{
        assertEquals("Will save and confirm all session actions into the database.", bank.processRequest(new CustomerID("bhagy"), "HELP CONFIRM"));
    }

    @Test
    @DisplayName("Returns correct description for LOGOUT")
    public void TESTt() throws FileNotFoundException{
        assertEquals("Will close your banking session and exit the application", bank.processRequest(new CustomerID("bhagy"), "HELP LOGOUT"));
    }
}