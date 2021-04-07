package newbank.server;

import java.util.ArrayList;
/*This class needs to be fully developed to maintain a database of loan requests*/

public class LoanOfferHandler {

    private ArrayList<LoanRequest> loanRequestsList = new ArrayList<LoanRequest>();

    public LoanOfferHandler(){
       generateLoanRequestList();
    }


    public ArrayList<LoanRequest> getLoanOffers(){
        return loanRequestsList;
    }

    private void generateLoanRequestList(){
        loanRequestsList.add(new LoanRequest(1647908596, "christina", 500.00, 3.25, 4, 4 ));
        loanRequestsList.add(new LoanRequest(522655285, "amrelshenawy", 200.00, 7.25, 12, 52 ));
    }

}
