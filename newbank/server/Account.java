package newbank.server;

import java.util.Random;

public class Account {

    private String accountName;
    private double openingBalance;

    enum AccountType {CHILDREN, SENIOR, CHECKING, SAVINGS, MONEYMARKET, OVERDRAFT}

    private AccountType accountType;

    enum InstructionType {WITHDRAW, DEPOSIT}

    private InstructionType instructionType;

    public Account(String accountName, double openingBalance, AccountType type) {
        this.accountName = accountName;
        this.openingBalance = openingBalance;
        this.accountType = type;
    }

    public void modifyBalance(double amount, InstructionType type) {
        if (type.equals(InstructionType.DEPOSIT)) {
            openingBalance += amount;
        } else if (type.equals(InstructionType.WITHDRAW)) {
            // do overdraft check
            if (openingBalance - amount >= 0) {
                openingBalance -= amount;
            }

        }
    }

    public String setAccountNumber(AccountType type) {

        Random rand = new Random();

        //7 random digits form the base of the account number
        String baseAccountNumber = String.format("%07d", rand.nextInt(10000000));

        //the first digit of the account number relates to account type: 0 for children;
        //1 for seniors; 2 for checking; 3 for saving; 4 for money market; 5 for overdraft.
        String prefix = "";
        switch (type) {
            case CHILDREN:
                prefix = "0";
                break;
            case SENIOR:
                prefix = "1";
                break;
            case CHECKING:
                prefix = "2";
                break;
            case SAVINGS:
                prefix = "3";
                break;
            case MONEYMARKET:
                prefix = "4";
                break;
            case OVERDRAFT:
                prefix = "5";
                break;
        }
        String accountNumber = prefix + baseAccountNumber;


        return accountNumber;
    }


    // getters

    public String getAccountName() {
        return accountName;
    }

    public double getOpeningBalance() {
        return openingBalance;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    private InstructionType getInstructionType() {
        return instructionType;
    }

    public String toString() {
        return (accountType + ", " + accountName + ": " + openingBalance);
    }

}
