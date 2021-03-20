package newbank.server;

import java.util.HashMap;
import java.util.Locale;

public class NewBank {
	
	private static final NewBank bank = new NewBank();
	private HashMap<String,Customer> customers;
	
	private NewBank() {
		customers = new HashMap<>();
		addTestData();
	}
	
	private void addTestData() {
		Customer bhagy = new Customer();
		bhagy.setAccount(new Account("Main", 1000.0, Account.AccountType.CHECKING));
		bhagy.setPassword("Password!");
		customers.put("Bhagy", bhagy);

		Customer christina = new Customer();
		christina.setAccount(new Account("Savings", 1500.0, Account.AccountType.SAVINGS));
		christina.setPassword("123456");
		customers.put("Christina", christina);

		Customer john = new Customer();
		john.setAccount(new Account("Checking", 250.0, Account.AccountType.CHECKING));
		john.setPassword("picture1");
		customers.put("John", john);
	}
	
	public static NewBank getBank() {
		return bank;
	}
	
	public synchronized CustomerID checkLogInDetails(String userName, String password) {
		if(customers.containsKey(userName)) {
			Customer customer = customers.get(userName);
			if (customer.getPassword().equals(password)){
				return new CustomerID(userName, password);
			}
		}
		return null;
	}

	// commands from the NewBank customer are processed in this method
	public synchronized String processRequest(CustomerID customer, String request) {
		if(customers.containsKey(customer.getKey())) { //this isn't a safe check - how to change?
			String[] requestSplit = request.split(" ");
			switch(requestSplit[0]) {
			case "SHOWMYACCOUNTS" : return showMyAccounts(customer);
			case "NEWACCOUNT":
				double openingBalance = 0;
				String accountName = requestSplit[1];
				openingBalance = Double.parseDouble(requestSplit[2]);
				Account.AccountType type;
				if(requestSplit.length == 4){
					String accountType = requestSplit[3];

					if(accountType.equalsIgnoreCase(Account.AccountType.CHILDREN.toString())){
						type = Account.AccountType.CHILDREN;
					}
					else if(accountType.equalsIgnoreCase(Account.AccountType.SENIOR.toString())){
						type = Account.AccountType.SENIOR;
					}  
					else if(accountType.equalsIgnoreCase(Account.AccountType.CHECKING.toString())){
						type = Account.AccountType.CHECKING;
					} 
					else if(accountType.equalsIgnoreCase(Account.AccountType.SAVINGS.toString())){
						type = Account.AccountType.SAVINGS;
					} 
					else if(accountType.equalsIgnoreCase(Account.AccountType.MONEYMARKET.toString())){
						type = Account.AccountType.MONEYMARKET;
					} 
					else if(accountType.equalsIgnoreCase(Account.AccountType.OVERDRAFT.toString())){
						type = Account.AccountType.OVERDRAFT;
					} 
					else{
						type = Account.AccountType.CHECKING; // default case
					} 
					customers.get(customer.getKey()).setAccount(new Account(accountName,openingBalance,type));
				} 
				else{
					type = Account.AccountType.CHECKING; //default account type if no type is specified
					customers.get(customer.getKey()).setAccount(new Account(accountName, openingBalance,type));
				} 
				return "SUCCESS";
			case "MOVE":
				// MOVE <Amount> <From> <To>
				if (requestSplit.length != 4){
					return "FAIL";
				} else {
					Account accountFrom = returnAccount(requestSplit[2], customers.get(customer.getKey()));
					Account accountTo = returnAccount(requestSplit[3], customers.get(customer.getKey()));
					Double amount = Double.parseDouble(requestSplit[1]);
					if (accountFrom == null || accountTo == null || !sufficientFunds(accountFrom, amount)) {
						return "FAIL";
					} else {
						accountFrom.modifyBalance(amount, Account.InstructionType.WITHDRAW);
						accountTo.modifyBalance(amount, Account.InstructionType.DEPOSIT);
						return "SUCCESS";
					}
				}
			default : return "FAIL";
			}
		}
		return "FAIL";
	}
	
	private String showMyAccounts(CustomerID customer) {
		return (customers.get(customer.getKey())).accountsToString();
	}

	// Helper method for MOVE to check whether the account requested is registered to the user
	// and return the corresponding account object.
	public Account returnAccount(String accountNameString, Customer customer){
		for (Account account : customer.getAccounts()){
			if (account.getAccountName().equals(accountNameString)){
				return account;
			}
		}
		return null;
	}

	// Helper method for MOVE to check that the account that the user woould like to transfer
	// funds from has sufficient funds to cover the transfer.
	public boolean sufficientFunds(Account account, Double amount){
		if (account.getOpeningBalance() >= amount){
			return true;
		} else {
			return false;
		}
	}

}
