package Testing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import newbank.server.NewBank;
import newbank.server.CustomerID;

// Amr Elshenawy Unit Tests for SHOWMYACCOUNTS
public class SHOWMYACCOUNTS_UnitTest{
    
    private final NewBank bank = new NewBank();

    @Test
    @DisplayName("Fails if SHOWMYACCOUNTS is lowercase")
    public void TESTa() throws FileNotFoundException{
        assertEquals("UNRECOGNIZED COMMAND.", bank.processRequest(new CustomerID("bhagy"), "showmyaccounts"));
    }

    @Test
    @DisplayName("Correct accounts return for Bhagy")
    public void TESTb() throws FileNotFoundException{
        assertEquals("CHECKING, 1512469: 2220.0\nSAVINGS, 20673045: 1500.0", bank.processRequest(new CustomerID("bhagy"), "SHOWMYACCOUNTS"));
    }

    @Test
    @DisplayName("Correct accounts return for John")
    public void TESTc() throws FileNotFoundException{
        assertEquals("CHECKING, 15058458: 25595.0\nSAVINGS, 24685857: 50.0", bank.processRequest(new CustomerID("john"), "SHOWMYACCOUNTS"));
    }

    @Test
    @DisplayName("Correct accounts return for Christina")
    public void TESTd() throws FileNotFoundException{
        assertEquals("CHECKING, 17110635: 38460.0\nSAVINGS, 21864414: 965.0", bank.processRequest(new CustomerID("christina"), "SHOWMYACCOUNTS"));
    }

    @Test
    @DisplayName("Correct accounts return for AmrElshenawy")
    public void TESTe() throws FileNotFoundException{
        assertEquals("CHECKING, 15890347: 100600.0", bank.processRequest(new CustomerID("amrelshenawy"), "SHOWMYACCOUNTS"));
    }
}
