package newbank.server;

public class Account {
	
	private String accountName;
	private double openingBalance;
	enum AccountType {CHILDREN, SENIOR, CHECKING, SAVINGS, MONEYMARKET, OVERDRAFT}
	private AccountType accountType;
	enum InstructionType {WITHDRAW, DEPOSIT}
	private InstructionType instructionType;

	public Account(String accountName, double openingBalance) {
		this.accountName = accountName;
		this.openingBalance = openingBalance;
	}

	public Account(String accountName, double openingBalance, AccountType type) {
		this.accountName = accountName;
		this.openingBalance = openingBalance;
		this.accountType = type;
	}

	public void modifyBalance(double amount, InstructionType type){
		if(type.equals(InstructionType.DEPOSIT)){
			openingBalance += amount;
		} else if(type.equals(InstructionType.WITHDRAW)){
			// do overdraft check
			if(openingBalance - amount >= 0){
				openingBalance -= amount;
			}

		}
	}

	public String toString() {
		return (accountType + ", " + accountName + ": " + openingBalance);
	}

}
