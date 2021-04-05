package newbank.server;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Interest {

    private static double checkingInterest;
    private static double savingsInterest;
    private static double moneyMarketInterest;
    private static double overdraftInterest;

    public Interest(HashMap<String,Customer> customers, HashMap<Account,Transaction> transactions) {
        this.checkingInterest = 0.50;
        this.savingsInterest = 2.00;
        this.moneyMarketInterest = 5.00;
        this.overdraftInterest = 39.90;

        startDailyCalculation(customers);
        startMonthlyPayment(customers, transactions);
    }

    public void startDailyCalculation(HashMap<String,Customer> customers){

        TimerTask dailyInterest = new TimerTask(){
            public void run(){
                updateInterest(customers);
            }
        };

        System.out.println("Interest Calculator started");
        Timer daily = new Timer("Daily Interest Calculator");

        daily.scheduleAtFixedRate(dailyInterest, todayEleven(), TimeUnit.DAYS.toMillis(1));

    }

    public void startMonthlyPayment(HashMap<String,Customer> customers, HashMap<Account,Transaction> transactions){

        TimerTask interestPayment = new TimerTask(){
            public void run(){
                payInterest(customers, transactions);
            }
        };

        System.out.println("Interest payment started");
        Timer monthly = new Timer("Monthly Interest Payment");

        monthly.scheduleAtFixedRate(interestPayment, todayElevenThirty(), TimeUnit.DAYS.toMillis(30));

    }

    public void updateInterest(HashMap<String,Customer> customers) {

        for (Customer customer : customers.values()) {
            for (Account account : customer.getAccounts()) {
                double dailyInterest;
                switch (account.getAccountType()) {
                    case CHECKING:
                        if (account.getOpeningBalance() >= 0) {
                            dailyInterest = account.getOpeningBalance() * (checkingInterest / 365);
                            account.setInterest(account.getInterest() + dailyInterest);
                        } else {
                            dailyInterest = account.getOpeningBalance() * (overdraftInterest / 365);
                            account.setInterest(account.getInterest() + dailyInterest);
                        }
                    case SAVINGS:
                        dailyInterest = account.getOpeningBalance() * (savingsInterest / 365);
                        account.setInterest(account.getInterest() + dailyInterest);
                    case MONEYMARKET:
                        dailyInterest = account.getOpeningBalance() * (moneyMarketInterest / 365);
                        account.setInterest(account.getInterest() + dailyInterest);
                }
            }
        }
        System.out.println("Interest updated on all accounts");
    }

    public void payInterest(HashMap<String,Customer> customers, HashMap transactions) {
        Customer newBank = customers.get("NewBank");
        Account newBankAccount = newBank.getAccounts().get(0);
        int newBankAccountID = newBankAccount.getAccountId();

        for (String customerUserName : customers.keySet()) {
            Customer customer = customers.get(customerUserName);
            for (Account account : customer.getAccounts()) {
                account.modifyBalance(account.getInterest(), Account.InstructionType.DEPOSIT);
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
        System.out.println("Interest paid on all accounts");
    }

    public void modifyBalance(double amount, Account senderAccount, Account receiverAccount){
        senderAccount.modifyBalance(amount, Account.InstructionType.WITHDRAW);
        receiverAccount.modifyBalance(amount, Account.InstructionType.DEPOSIT);
    }

    public Transaction transactionHelper(double amount, String senderName, int senderAccountID, String receiverName, int receiverAccountID){
        Date dateTime = new Date();
        String formattedDate = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(dateTime);
        String message = "Monthly interest payment of " + amount + " on " + formattedDate;
        return new Transaction(dateTime, senderAccountID, senderName, receiverAccountID, receiverName, amount, message);
    }

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
