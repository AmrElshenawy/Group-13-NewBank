package newbank.server;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;

public class NewBank {
	
	private static final NewBank bank = new NewBank();
	private HashMap<String,Customer> customers;
	private DatabaseHandler fullReport = new DatabaseHandler();
	
	private NewBank() {
		customers = new HashMap<>();
		fillHashMap_fromDB();
	}
	
	private void fillHashMap_fromDB() {
		List<String[]> scanOutput;
		try {
			scanOutput = fullReport.scanFullDB();
			for(String[] line : scanOutput){
				for(int i = 0; i < line.length; i++){
					Customer x = new Customer(line[1]);
					x.setAccount((new Account(line[4], Double.parseDouble(line[5]), Account.AccountType.valueOf(line[3].toUpperCase()))));
					x.setPassword(line[2]);
					customers.put(line[1], x);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static NewBank getBank() {
		return bank;
	}
	
	public synchronized CustomerID checkLogInDetails(String userName, String password) throws FileNotFoundException {
		DatabaseHandler customerDB = new DatabaseHandler();
		customerDB.findInDB(userName.toLowerCase());
		if(customerDB.getName().equalsIgnoreCase(userName)) {
			if (customerDB.getPassword().equals(password)){
				return new CustomerID(userName.toLowerCase(), password);
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
			default : return "FAIL";
			}
		}
		return "FAIL";
	}
	
	private String showMyAccounts(CustomerID customer) {
		return (customers.get(customer.getKey())).accountsToString();
	}

}
