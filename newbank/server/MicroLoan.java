package newbank.server;

import java.time.LocalDate;

public class MicroLoan extends Transaction{

    protected Double interestRate;
    protected int installments;
    protected LocalDate repaymentDate;
    protected Double maxLoanAmount = 1000.00;

    public MicroLoan(LocalDate dateTime, int senderId, String senderName, int receiverId, String receiverName, Double amount, String message, TransactionType type, Double interestRate, int installments, LocalDate repaymentDate) {
        super(dateTime, senderId, senderName, receiverId, receiverName, amount, message, type);
        this.interestRate = interestRate;
        this.installments = installments;
        this.repaymentDate = repaymentDate;
    }

    // getters
    public Double getInterest() {
        return interestRate;
    }

    public int getInstallments() {
        return installments;
    }

    public LocalDate getRepaymentDate() {
        return repaymentDate;
    }

    public Double getMaxLoanAmount() {
        return maxLoanAmount;
    }

    // setters
    public void setInterest(Double interest) {
        this.interestRate = interest;
    }

    public void setInstallments(int installments) {
        this.installments = installments;
    }

    public void setRepaymentDate(LocalDate repaymentDate) {
        this.repaymentDate = repaymentDate;
    }

    // calculate interest and payments
    public Double monthlyRepayment(){
        Double p = amount;
        Double r = (interestRate/100)/installments;
        int n = installments;
        return p*(r*(1+r)*n)/((1+r)*n-1); // source: https://www.kasasa.com/blog/how-to-calculate-loan-payments-in-3-easy-steps;
    }
}
