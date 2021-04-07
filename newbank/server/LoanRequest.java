package newbank.server;

/*Purpose: This class is designed to create a loan request with the necessary information for users to make mirco loans
 and place the request into the market place
 */

public class LoanRequest {
    protected int requestId;
    protected String requestName;
    protected Double interestRate;
    protected int installments;
    protected Double amount;
    protected Double maxLoanAmount = 1000.00;
    protected Double totalInterest;
    protected Double installmentPayment;
    protected int duration;
    protected Double compoundPeriods = 365.00 ; //= number of compounding periods per year

    public LoanRequest(int requestingId, String requestingName, Double loanAmount, Double interestRate, int installments, int duration) {
        this.requestId = requestingId;
        this.requestName = requestingName;
        this.amount = loanAmount;
        this.interestRate = interestRate;
        this.installments = installments;
        this.duration = duration;
        this.installmentPayment = repaymentPlan();
        this.totalInterest = totalInterestCalculation();
    }
    //Setters

    public void setrequestId(int requestId) {
        this.requestId = requestId;
    }

    public void setrequestName(String requestName) {
        this.requestName = requestName;
    }

    public void setInterestRate(Double interestRate) {
        this.interestRate = interestRate;
    }

    public void setInstallments(int installments) {
        this.installments = installments;
    }
    public void setMaxLoanAmount(Double maxLoanAmount) {
        this.maxLoanAmount = maxLoanAmount;
    }
    //Getters
    public int getrequestId() {
        return requestId;
    }

    public String getrequestName() {
        return requestName;
    }

    public Double getInterestRate() {
        return interestRate;
    }

    public int getInstallments() {
        return installments;
    }
    public Double getMaxLoanAmount() {
        return maxLoanAmount;
    }
    //Methods
    public String displayRequest(){
        return "|UserID: " + requestId + " | UserName: " + requestName + " |Amount: " + String.format("%.2f",amount)+ " | interested rate requested: " + String.format("%.2f",interestRate) + "% | number of installments: " + installments + " | installment payment: "+ String.format("%.2f",installmentPayment) + " | Loan Duration: " + duration + " | total interest: " + String.format("%.2f",totalInterest) +" | "+ System.lineSeparator();
    }
    /* Returns the amount each installment payment will be.
    A=P*(r(1+r)^n/(1+r)^n−1)
    P is the principal amount borrowed
    A is the periodic amortization payment
    r is the periodic interest rate divided by 100 (nominal annual interest rate also divided by 12 in case of monthly installments), and
    n is the total number of payments (for a 30-year loan with monthly payments n = 30 × 12 = 360)*/
    public Double repaymentPlan(){
        Double p = amount;
        Double n = (double)installments;
        Double r = (interestRate/(100.00*n));
        //System.out.println((1-Math.pow((1+(r/n)),-(n*t))));
        return p*(r*Math.pow((1+r),n))/(Math.pow((1+r),n)-1);// source: https://math.stackexchange.com/questions/664029/whats-the-math-formula-that-is-used-to-calculate-the-monthly-payment-in-this-mo#:~:text=A%20is%20the%20periodic%20amortization,%3D%2030%20%C3%97%2012%20%3D%20360)
    }
    //Calculate the total amount of interested earned on a loan
    public Double totalInterestCalculation (){
        return (repaymentPlan()*installments) - amount;
    }

}
