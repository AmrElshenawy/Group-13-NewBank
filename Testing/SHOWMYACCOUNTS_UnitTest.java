package Testing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import newbank.server.NewBank;
import newbank.server.CustomerID;

public class SHOWMYACCOUNTS_UnitTest{
    
    private final NewBank bank = new NewBank();

    @Test
    @DisplayName("Fails if SHOWMYACCOUNTS is lowercase")
    public void TEST1() throws FileNotFoundException{
        assertEquals("UNRECOGNIZED COMMAND.", bank.processRequest(new CustomerID("bhagy"), "showmyaccounts"));
    }

    @Test
    @DisplayName("Correct accounts return for Bhagy")
    public void TEST2() throws FileNotFoundException{
        assertEquals("CHECKING, 512469: 2220.0\nSAVINGS, 10673045: 1500.0", bank.processRequest(new CustomerID("bhagy"), "SHOWMYACCOUNTS"));
    }

    @Test
    @DisplayName("Correct accounts return for John")
    public void TEST3() throws FileNotFoundException{
        assertEquals("CHECKING, 5058458: 25595.0\nSAVINGS, 14685857: 50.0", bank.processRequest(new CustomerID("john"), "SHOWMYACCOUNTS"));
    }

    @Test
    @DisplayName("Correct accounts return for Christina")
    public void TEST4() throws FileNotFoundException{
        assertEquals("CHECKING, 7110635: 38460.0\nSAVINGS, 11864414: 965.0\nMONEYMARKET, 37966632: 2960.0", bank.processRequest(new CustomerID("christina"), "SHOWMYACCOUNTS"));
    }

    @Test
    @DisplayName("Correct accounts return for AmrElshenawy")
    public void TEST5() throws FileNotFoundException{
        assertEquals("CHECKING, 5890347: 100600.0", bank.processRequest(new CustomerID("amrelshenawy"), "SHOWMYACCOUNTS"));
    }
}
