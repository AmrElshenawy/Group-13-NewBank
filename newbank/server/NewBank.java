package newbank.server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
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
		List<ArrayList<String>> scanOutput;
		try {
			scanOutput = fullReport.scanFullDB();
			for(ArrayList<String> line : scanOutput){
				for(int i = 0; i < line.size(); i++){
					Customer x = new Customer(line.get(1));
					x.setAccount(new Account(line.get(4), Double.parseDouble(line.get(5)), Account.AccountType.valueOf(line.get(3).toUpperCase())));
					x.setPassword(line.get(2));
					x.setCustomerID((x.getFullName() + x.getPassword()).hashCode());
					customers.put(line.get(1), x);
					if(line.size() > 6){
						x.setAccount(new Account(line.get(7), Double.parseDouble(line.get(8)), Account.AccountType.valueOf(line.get(6).toUpperCase())));
					}
					if(line.size() > 9){
						x.setAccount(new Account(line.get(10), Double.parseDouble(line.get(11)), Account.AccountType.valueOf(line.get(9).toUpperCase())));
					}
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
				return new CustomerID(userName.toLowerCase());
			}
		}
		return null;
	}

	// commands from the NewBank customer are processed in this method
	public synchronized String processRequest(CustomerID customer, String request) throws FileNotFoundException {
		if(customers.containsKey(customer.getKey())) { //this isn't a safe check - how to change?
			String[] requestSplit = request.split(" ");
			switch(requestSplit[0]) {
			case "SHOWMYACCOUNTS" : return showMyAccounts(customer);
			case "NEWACCOUNT":
				ArrayList<Account> accountsList = customers.get(customer.getKey()).getAccounts();
				if(accountsList.size() >= 3){
					return "MAXIMUM NUMBER OF ACCOUNTS REACHED!";
				}
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
					return "Error: Invalid input. To move money between accounts, please use the following" +
							" input format: \"MOVE <Amount> <From> <To>\"";
				} else {
					Account accountFrom = returnAccount(requestSplit[2], customers.get(customer.getKey()));
					Account accountTo = returnAccount(requestSplit[3], customers.get(customer.getKey()));
					Double amount = Double.parseDouble(requestSplit[1]);
					if (accountFrom == null || accountTo == null) {
						return "Error: One or more accounts not recognised. Please check account details and try again.";
					} else if (amount <= 0){
						return "Error: Invalid amount specified. Transfers must be at least £0.01";
					} else if (!sufficientFunds(accountFrom, amount)){
						return "Error: insufficient funds available to carry out transfer";
					} else {
						accountFrom.modifyBalance(amount, Account.InstructionType.WITHDRAW);
						accountTo.modifyBalance(amount, Account.InstructionType.DEPOSIT);
						return "SUCCESS! Move transfer complete.";
					}
				}
				case "PAY":
					//PAY <Amount> <From Users Account> <To Payee's UserName>
					if (requestSplit.length != 4){
						return "FAIL";
					} else {
						return payPerson(customer, requestSplit );
					}
				case "CONFIRM":
					DatabaseHandler save = new DatabaseHandler();
					try {
						save.saveSession(customers);
					} catch (IOException e) {
						e.printStackTrace();
					}
					return "TRANSACTION CONFIRMED!";

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
	public Account returnAccount(String accountTypeString, Customer customer){
		Account.AccountType accountType = Account.AccountType.valueOf(accountTypeString);
		for (Account account : customer.getAccounts()){
			if (account.getAccountType().equals(accountType)){
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
	/* Method: payPerson
	Expected input: String from command line: PAY <Amount> <From Users Account> <To Payee's UserName>
	assumptions:
		- Person paying is using main account to pay from
		- Payee's account is the first account
	*/
	private String payPerson (CustomerID customer, String[] requestSplit ) throws FileNotFoundException {
		Double payment = Double.parseDouble(requestSplit[1]);
		CustomerID payeeID = findPayeeID(requestSplit[3]);
		String payeeAccountName = findPayeeAccountName(requestSplit[3]);
		Account userAccount = returnAccount(requestSplit[2], customers.get(customer.getKey()));
		Account payeeAccount = returnAccount(payeeAccountName, customers.get(payeeID.getKey()));
		return transferFunds(userAccount, payeeAccount, payment);
	}

	public void addCustomer(String hashKey, Customer customer){
		customers.put(hashKey, customer);
	}

	public boolean checkCustomer(String username) {
		if (customers.containsKey(username)){
			return true;
		} else {
			return false;
		}
	}

	// Helper method for to return a Payee CustomerID .
	private CustomerID findPayeeID(String userName) throws FileNotFoundException{
		DatabaseHandler customerDB = new DatabaseHandler();
		customerDB.findInDB(userName.toLowerCase());
		if(customerDB.getName().equalsIgnoreCase(userName)){
			return new CustomerID(userName.toLowerCase());
		}
		return null;
	}
	// Helper method for to return the Payee's first account object.
	private String findPayeeAccountName (String userName) throws FileNotFoundException{
		DatabaseHandler customerDB = new DatabaseHandler();
		customerDB.findInDB(userName.toLowerCase());
		if(customerDB.getName().equalsIgnoreCase(userName)){
			return customerDB.getAccountName(1);
		}
		return null;
	}
	// Helper method to transfer funds
	private String transferFunds(Account user, Account payee, double payment){
		if (user == null || payee == null || !sufficientFunds(user, payment)){
			return "FAIL";
		} else {
			user.modifyBalance(payment, Account.InstructionType.WITHDRAW);
			payee.modifyBalance(payment, Account.InstructionType.DEPOSIT);
			return "SUCCESS";
		}
	}
}

