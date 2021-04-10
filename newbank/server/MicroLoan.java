package newbank.server;

import java.time.LocalDateTime;

public class MicroLoan extends Transaction{

    protected Double interestRate;
    protected int installments;
    protected LocalDateTime repaymentDate;
    protected Double maxLoanAmount = 1000.00;

    public MicroLoan(LocalDateTime dateTime, int senderId, String senderName, int receiverId, String receiverName, Double amount, String message, TransactionType type, Double interestRate, int installments, LocalDateTime repaymentDate) {
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

    public LocalDateTime getRepaymentDate() {
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

    public void setRepaymentDate(LocalDateTime repaymentDate) {
        this.repaymentDate = repaymentDate;
    }

    // calculate interest and payments
    public Double monthlyRepayment(){
        Double p = amount;
        Double n = (double)installments;
        Double r = (interestRate/(100.00*n));
        return p*(r*Math.pow((1+r),n))/(Math.pow((1+r),n)-1);// source: https://math.stackexchange.com/questions/664029/whats-the-math-formula-that-is-used-to-calculate-the-monthly-payment-in-this-mo#:~:text=A%20is%20the%20periodic%20amortization,%3D%2030%20%C3%97%2012%20%3D%20360)
        //Double p = amount;
        //Double r = (interestRate/100)/installments;
        //int n = installments;
        //return p*(r*(1+r)*n)/((1+r)*n-1); // source: https://www.kasasa.com/blog/how-to-calculate-loan-payments-in-3
        // -easy-steps;
    }
}
