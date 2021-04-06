package newbank.server;

import java.time.LocalDateTime;


/*Purpose: This class is designed to create a loan offer with the necessary information for users to make mirco loans
 and place the offer into the market place
 */

public class LoanOffer {
    protected int offerId;
    protected String offerName;
    protected Double interestRate;
    protected int installments;
    protected int duration;
    protected Double amount;
    protected LocalDateTime offerExpirationDate;
    protected Double maxLoanAmount = 1000.00;
    protected Double totalInterest;

    public LoanOffer(int requestingId, String requestingName, Double loanAmount, Double interestRate, int installments, int duration, LocalDateTime expirationDate) {
        this.offerId = requestingId;
        this.offerName = requestingName;
        this.interestRate = interestRate;
        this.installments = installments;
        this.duration = duration;
        this.offerExpirationDate = expirationDate;
        this.amount = loanAmount;
    }
    //Setters

    public void setOfferId(int offerId) {
        this.offerId = offerId;
    }

    public void setOfferName(String offerName) {
        this.offerName = offerName;
    }

    public void setInterestRate(Double interestRate) {
        this.interestRate = interestRate;
    }

    public void setInstallments(int installments) {
        this.installments = installments;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setOfferExpirationDate(LocalDateTime offerExpirationDate) {
        this.offerExpirationDate = offerExpirationDate;
    }

    public void setMaxLoanAmount(Double maxLoanAmount) {
        this.maxLoanAmount = maxLoanAmount;
    }
    //Getters
    public int getOfferId() {
        return offerId;
    }

    public String getOfferName() {
        return offerName;
    }

    public Double getInterestRate() {
        return interestRate;
    }

    public int getInstallments() {
        return installments;
    }

    public int getDuration() {
        return duration;
    }

    public LocalDateTime getOfferExpirationDate() {
        return offerExpirationDate;
    }

    public Double getMaxLoanAmount() {
        return maxLoanAmount;
    }
    //Methods
    public void displayOffer(){
        System.out.println("Your ID: " + offerId);
        System.out.println("Your UserName: " + offerName);
        System.out.println("interested rate Offered: " + interestRate+"%");
        System.out.println("number of installments: " + installments);
        System.out.println("loan duration: " + duration);
        System.out.println("total interest: " + totalInterest);
        System.out.println("Offer Expiration Date: " + offerExpirationDate);
    }

    public Double monthlyRepayment(){
        Double p = amount;
        Double r = (interestRate/100)/installments;
        int n = installments;
        return p*(r*(1+r)*n)/((1+r)*n-1); // source: https://www.kasasa.com/blog/how-to-calculate-loan-payments-in-3-easy-steps;
    }

}
