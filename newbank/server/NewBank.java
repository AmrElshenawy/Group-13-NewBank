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
		bhagy.setPassword("password");
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
				String accountType = requestSplit[3].toUpperCase();
				openingBalance = Double.parseDouble(requestSplit[2]);
				Account.AccountType type;
				if(requestSplit.length == 3){
					if(accountType.equals(Account.AccountType.SAVINGS)){
						type = Account.AccountType.SAVINGS;
					} else if(accountType.equals(Account.AccountType.CHECKING)){
						type = Account.AccountType.CHECKING;
					} else if(accountType.equals(Account.AccountType.OVERDRAFT)){
						type = Account.AccountType.OVERDRAFT;
					} else if(accountType.equals(Account.AccountType.MONEYMARKET)){
						type = Account.AccountType.MONEYMARKET;
					} else type = Account.AccountType.CHECKING; // default case
					customers.get(customer.getKey()).setAccount(new Account(accountName,openingBalance,type));
				} else customers.get(customer.getKey()).setAccount(new Account(accountName,openingBalance));
				return "SUCCESS";
			default : return "FAIL";
			}
		}
		return "FAIL";
	}
	
	private String showMyAccounts(CustomerID customer) {
		return (customers.get(customer.getKey())).accountsToString();
	}

}
