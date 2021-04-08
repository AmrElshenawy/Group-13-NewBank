package newbank.server;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Interest {

    private static double checkingInterest;
    private static double savingsInterest;
    private static double moneyMarketInterest;
    private static double overdraftInterest;
    private DateFormat dateFormat = new SimpleDateFormat("kk:mm:ss 'on' dd/MM/yyyy");

    public Interest(HashMap<String,Customer> customers, HashMap<Account,Transaction> transactions) {
        this.checkingInterest = 0.50;
        this.savingsInterest = 2.00;
        this.moneyMarketInterest = 5.00;
        this.overdraftInterest = 39.90;

        startDailyCalculation(customers);
        startMonthlyPayment(customers, transactions);
    }

    // Scheduler method. Schedules interest on each account in the Hashmap to be calculated daily.
    public void startDailyCalculation(HashMap<String,Customer> customers){

        TimerTask dailyInterest = new TimerTask(){
            public void run(){
                updateInterest(customers);
            }
        };


        System.out.println("Interest Calculator started. Next update at " + dateFormat.format(todayEleven()) + ".");
        Timer daily = new Timer("Daily Interest Calculator");
        long timePeriod = TimeUnit.DAYS.toMillis(1);
        //long timePeriod = TimeUnit.SECONDS.toMillis(2);

        daily.scheduleAtFixedRate(dailyInterest, todayEleven(), timePeriod);
        //daily.scheduleAtFixedRate(dailyInterest, 0, timePeriod);

    }

    // Scheduler method. Schedules interest on each account in the Hashmap to be paid approximately monthly (on a 30 day cycle).
    public void startMonthlyPayment(HashMap<String,Customer> customers, HashMap<Account,Transaction> transactions){

        TimerTask interestPayment = new TimerTask(){
            public void run(){
                payInterest(customers, transactions);
            }
        };

        System.out.println("Interest payment started. Next payment at " + dateFormat.format(todayElevenThirty()) + ".");
        Timer monthly = new Timer("Monthly Interest Payment");
        long timePeriod = TimeUnit.DAYS.toMillis(30);
        //long timePeriod = TimeUnit.SECONDS.toMillis(4);

        monthly.scheduleAtFixedRate(interestPayment, todayElevenThirty(), timePeriod);
        //monthly.scheduleAtFixedRate(interestPayment, 0, timePeriod);

    }

    // Method to calculate daily interest for each bank account and store a running total for the month in the customer.interest field.
    public void updateInterest(HashMap<String,Customer> customers) {

        for (Customer customer : customers.values()) {
            for (Account account : customer.getAccounts()) {
                double dailyInterest;
                switch (account.getAccountType()) {
                    case CHECKING:
                        if (account.getOpeningBalance() >= 0) {
                            dailyInterest = account.getOpeningBalance() * ((checkingInterest/100) / 365);
                            account.setInterest(account.getInterest() + dailyInterest);
                        } else {
                            dailyInterest = account.getOpeningBalance() * ((overdraftInterest/100) / 365);
                            account.setInterest(account.getInterest() + dailyInterest);
                        }
                        break;
                    case SAVINGS:
                        dailyInterest = account.getOpeningBalance() * ((savingsInterest/100) / 365);
                        account.setInterest(account.getInterest() + dailyInterest);
                        break;
                    case MONEYMARKET:
                        dailyInterest = account.getOpeningBalance() * ((moneyMarketInterest/100) / 365);
                        account.setInterest(account.getInterest() + dailyInterest);
                        //System.out.println("Balance: " + account.getOpeningBalance() + "Interest: " + account.getInterest() + ", Daily interest: " + dailyInterest);
                        break;
                }
            }
        }
        System.out.println("Interest updated on all accounts at " + dateFormat.format(Calendar.getInstance().getTime()) + ".");
    }

    // Method to move the accrued interest for each account (account.interest) into the account.balance field, and zero account.interest.
    public void payInterest(HashMap<String,Customer> customers, HashMap transactions) {
        Customer newBank = customers.get("newbank");
        Account newBankAccount = newBank.getAccounts().get(0);
        int newBankAccountID = newBankAccount.getAccountId();

        for (String customerUserName : customers.keySet()) {
            Customer customer = customers.get(customerUserName);
            for (Account account : customer.getAccounts()) {
                if (account.getInterest() < 0) {
                    modifyBalance((account.getInterest()*-1),account,newBankAccount);
                    transactions.put(account.getAccountId(),
                            transactionHelper(account.getInterest(), customerUserName, account.getAccountId(), "NewBank", newBankAccountID));
                } else {
                    modifyBalance(account.getInterest(),newBankAccount,account);
                    transactions.put(newBankAccountID,
                            transactionHelper(account.getInterest(), "NewBank", newBankAccountID, customerUserName, account.getAccountId()));
                }
                account.setInterest(0);
            }
        }
        System.out.println("Interest paid on all accounts at " + dateFormat.format(Calendar.getInstance().getTime()) + ".");
    }

    // Helper method to pay interest from the NewBank account into the customer account.
    public void modifyBalance(double amount, Account senderAccount, Account receiverAccount){
        senderAccount.modifyBalance(amount, Account.InstructionType.WITHDRAW);
        receiverAccount.modifyBalance(amount, Account.InstructionType.DEPOSIT);
    }

    // Helper method set up transaction objects to reflect interest payments made.
    public Transaction transactionHelper(double amount, String senderName, int senderAccountID, String receiverName, int receiverAccountID){
        Date dateTime = Calendar.getInstance().getTime();
        String formattedDate = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(dateTime);
        String message = "Monthly interest payment of " + amount + " on " + formattedDate;
        return new Transaction(LocalDateTime.now(), senderAccountID, senderName, receiverAccountID, receiverName, amount, message, Transaction.TransactionType.PAYMENT);
    }

    // Helper method for time format for daily interest calculation - this is done at 11pm every day.
    public Date todayEleven(){

        int hour = 23;
        int minute = 0;
        int second = 0;

        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY,hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);

        return calendar.getTime();
    }

    // Helper method for time format for monthly interest payment - this is done at 11:30pm starting on the 28th day of the current month.
    public Date todayElevenThirty(){

        int day = 28;
        int hour = 23;
        int minute = 30;
        int second = 0;

        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.DAY_OF_MONTH,day);
        calendar.set(Calendar.HOUR_OF_DAY,hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);

        return calendar.getTime();
    }
}
