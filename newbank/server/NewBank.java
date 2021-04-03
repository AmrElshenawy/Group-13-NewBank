package newbank.server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.parseInt;
import static java.lang.Double.parseDouble;

public class NewBank {
	
	private static final NewBank bank = new NewBank();
	private HashMap<String,Customer> customers;
	private HashMap<Integer,Customer> accounts; //maps account number to customer object
	private DatabaseHandler fullReport = new DatabaseHandler();
	private HashMap<Account,Transaction> transactions;
	private ArrayList<Transaction> transactionsList;
	private TransactionHandler handleTransactions = new TransactionHandler();
	private HashMap<Customer, MicroLoan> customerMicroloansOffered;
	private HashMap<Customer, MicroLoan> customerMicroloansReceived;
	private HashMap<Account, MicroLoan> accountMicroloansOffered;
	private HashMap<Account, MicroLoan> accountMicroloansReceived;
	
	public NewBank() {
		customers = new HashMap<>();
		transactions = new HashMap<>();
		accounts = new HashMap<>();
		customerMicroloansOffered = new HashMap<>();
		customerMicroloansReceived = new HashMap<>();
		accountMicroloansOffered = new HashMap<>();
		accountMicroloansReceived = new HashMap<>();
		fillHashMap_fromDB();
		readTransactionsFromDB();
		transactionsList = new ArrayList<>();

	}

	public static NewBank getBank() {
		return bank;
	}

	public HashMap<String,Customer> getName2CustomersMapping(){
		return customers;
	}

	public HashMap<Account,Transaction> getAccount2TransactionsMapping(){
		return transactions;
	}

	public HashMap<Customer, MicroLoan> getCustomer2MicroloansOffered(){ return customerMicroloansOffered; }

	public HashMap<Customer, MicroLoan> getCustomer2MicroloansReceived(){ return customerMicroloansReceived; }

	public HashMap<Account, MicroLoan> getAccount2MicroloansOffered(){ return accountMicroloansOffered; }

	public HashMap<Account, MicroLoan> getAccount2MicroloansReceived(){ return accountMicroloansReceived; }

	private void fillHashMap_fromDB() {
		List<ArrayList<String>> scanOutput;
		try {
			scanOutput = fullReport.scanFullDB();
			for(ArrayList<String> line : scanOutput){
				for(int i = 0; i < line.size(); i++){
					Customer x = new Customer(line.get(1));
					if(line.size() > 3){
						Account account1 = new Account(Integer.parseInt(line.get(4)), Double.parseDouble(line.get(5)), Account.AccountType.valueOf(line.get(3).toUpperCase()));
						x.setAccount(account1);
						accounts.put(account1.getAccountId(), x);
					}
					x.setPassword(line.get(2));
					x.setCustomerID((x.getFullName() + x.getPassword()).hashCode());
					customers.put(line.get(1), x);

					if(line.size() > 6){
						Account account2 = new Account(Integer.parseInt(line.get(7)), Double.parseDouble(line.get(8)), Account.AccountType.valueOf(line.get(6).toUpperCase()));
						x.setAccount(account2);
						accounts.put(account2.getAccountId(), x);
					}
					if(line.size() > 9){
						Account account3 = new Account(Integer.parseInt(line.get(10)), Double.parseDouble(line.get(11)), Account.AccountType.valueOf(line.get(9).toUpperCase()));
						x.setAccount(account3);
						accounts.put(account3.getAccountId(), x);

					}
					customers.put(line.get(1), x);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void readTransactionsFromDB() {
		List<ArrayList<String>> scanOutput;
		try {
			scanOutput = handleTransactions.scanFullDB();
			for(ArrayList<String> line: scanOutput){
				for(int i = 0; i < line.size(); i++){
					Account account = new Account(parseInt(line.get(2)));
					LocalDateTime dateTime = LocalDateTime.parse(line.get(1),DateTimeFormatter.ofPattern("dd/MM/yyyy HH.mm.ss"));
					int senderId = parseInt(line.get(2));
					String senderName = line.get(3);
					int receiverId = parseInt(line.get(4));
					String receiverName = line.get(5);
					Double amount = parseDouble(line.get(6));
					String message = line.get(7);
					Transaction.TransactionType tranType = Transaction.TransactionType.valueOf(line.get(8));
					Transaction transaction = new Transaction(dateTime, senderId, senderName, receiverId, receiverName, amount, message, tranType);
					transactions.put(account, transaction);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
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
		if(customers.containsKey(customer.getKey())) {
			String[] requestSplit = request.split(" ");
			switch(requestSplit[0]) {
			case "SHOWMYACCOUNTS" :
				return showMyAccounts(customer);
			case "NEWACCOUNT":
				// NEWACCOUNT <Name> <OpeningBalance> <Type>
				ArrayList<Account> accountsList = customers.get(customer.getKey()).getAccounts();
				if(accountsList.size() >= 3){
					return "MAXIMUM NUMBER OF ACCOUNTS REACHED!";
				}
				double openingBalance = 0;
				openingBalance = Double.parseDouble(requestSplit[2]);
				Account.AccountType accType;
				if(requestSplit.length == 3){
					String accountType = requestSplit[3];

					if(accountType.equalsIgnoreCase(Account.AccountType.CHILDREN.toString())){
						accType = Account.AccountType.CHILDREN;
					}
					else if(accountType.equalsIgnoreCase(Account.AccountType.SENIOR.toString())){
						accType = Account.AccountType.SENIOR;
					}  
					else if(accountType.equalsIgnoreCase(Account.AccountType.CHECKING.toString())){
						accType = Account.AccountType.CHECKING;
					} 
					else if(accountType.equalsIgnoreCase(Account.AccountType.SAVINGS.toString())){
						accType = Account.AccountType.SAVINGS;
					} 
					else if(accountType.equalsIgnoreCase(Account.AccountType.MONEYMARKET.toString())){
						accType = Account.AccountType.MONEYMARKET;
					} 
					else if(accountType.equalsIgnoreCase(Account.AccountType.OVERDRAFT.toString())){
						accType = Account.AccountType.OVERDRAFT;
					} 
					else{
						accType = Account.AccountType.CHECKING; // default case
					} 
					customers.get(customer.getKey()).setAccount(new Account(openingBalance,accType));
				} 
				else{
					accType = Account.AccountType.CHECKING; //default account type if no type is specified
					customers.get(customer.getKey()).setAccount(new Account(openingBalance,accType));
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
						LocalDateTime dateTime = LocalDateTime.now();
						String formattedDate = DateTimeFormatter.ofPattern("MM/dd/yyyy HH.mm.ss").format(dateTime);
						int senderId = accountFrom.getAccountId();
						String senderName = customer.getKey();
						int receiverId = accountTo.getAccountId();
						String receiverName = customer.getKey();
						String message = "payment from "+senderName+" to "+receiverName+" of "+amount+" on "+ formattedDate;
						Transaction.TransactionType tranType = Transaction.TransactionType.TRANSFER;
						Transaction transaction = new Transaction(dateTime, senderId, senderName, receiverId, receiverName, amount, message, tranType);
						transactions.put(accountFrom, transaction);
						transactionsList.add(transaction);
						return "SUCCESS! Move transfer complete.";
					}
				}
			case "PAY":
				// PAY <Amount> <From User's Account> <To Payee's UserName>
				if (requestSplit.length != 4){
					return "FAIL";
				} else {
					return payPerson(customer, requestSplit, Transaction.TransactionType.PAYMENT);
				}
			case "MICROLOAN":
				// MICROLOAN <Amount> <From User's Account> <To Payee's UserName>
				if (requestSplit.length != 4){
					return "FAIL";
				} else {
					String payeeAccountID = requestSplit[2];
					Customer payee = accounts.get(Integer.parseInt(payeeAccountID)); //wrong
					Customer sender = accounts.get(Integer.parseInt(customer.getKey())); //wrong
					if(sender.canOfferLoan() && payee.canTakeLoan()){
						return payPerson(customer, requestSplit, Transaction.TransactionType.MICROLOAN);
					} else if(!sender.canOfferLoan()) {
						return "YOU ARE NOT AUTHORIZED TO OFFER LOANS";
					} else if(!payee.canTakeLoan()){
						return "YOU ARE NOT AUTHORIZED TO RECEIVE LOANS";
					} else return "FAIL";
				}

			case "CONFIRM":
				DatabaseHandler save = new DatabaseHandler();
				//TransactionHandler saveTransactions = new TransactionHandler();
				try {
					save.saveSession(customers);
					handleTransactions.saveSession(transactionsList);
					//saveTransactions.saveSession(transactions);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return "ACTION CONFIRMED!";
			case "DELETE":
				// DELETE
				// DELETE <Customer ID> <AccountType>
				if(customer.getKey().equalsIgnoreCase("staff")){
					if(requestSplit.length == 2){
						for(Map.Entry<String, Customer> record : customers.entrySet()){
							String user = record.getKey();
							Customer object = record.getValue();
							if(Integer.toString(object.getCustomerId()).equals(requestSplit[1])){
								customers.remove(user);
								return "Customer ID# " + object.getCustomerId() + " DELETED!";
							}
						}
					}
					else if(requestSplit.length == 3){
						for(Map.Entry<String, Customer> record :customers.entrySet()){
							Customer object = record.getValue();
							if(Integer.toString(object.getCustomerId()).equals(requestSplit[1])){
								ArrayList<Account> accList = object.getAccounts();
								for(Account acc : accList){
									if(acc.getAccountType().toString().equalsIgnoreCase(requestSplit[2])){
										object.deleteAccount(acc);
										return "Account type " + requestSplit[2].toUpperCase() + " for customer ID# " + object.getCustomerId() + " DELETED!";
									}
								}

							}
						}
					}
				}
				else{
					return "NOT A STAFF MEMBER. ACTION DENIED!";
				}
			case "AUDITREPORT":
				// AUDITREPORT
				if(customer.getKey().equalsIgnoreCase("staff")){
					try{
						Report report = new Report();
						report.generateReport();
					} catch (IOException e) {
						e.printStackTrace();
					}
					return "SUCCESS";
				}else{
					return "NOT A STAFF MEMBER. ACTION DENIED!";
				}

			case "HELP":
				if(requestSplit.length == 1){
					if(customer.getKey().equalsIgnoreCase("staff")){
						return getAvailableCommands("staff");
					}
					else{
						return getAvailableCommands("general");
					}
				}
				else if(requestSplit.length == 2){
					return helpCommandDescription(requestSplit[1]);
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
	public Account returnAccount(String accountTypeString, Customer customer){
		Account.AccountType accountType = Account.AccountType.valueOf(accountTypeString.toUpperCase());
		for (Account account : customer.getAccounts()){
			if (account.getAccountType().equals(accountType)){
				return account;
			}
		}
		return null;
	}

	// Helper method for MOVE to check that the account that the user would like to transfer
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
	private String payPerson (CustomerID customer, String[] requestSplit, Transaction.TransactionType type) throws FileNotFoundException {
		Double payment = Double.parseDouble(requestSplit[1]);
		CustomerID payeeName = findPayeeID(requestSplit[3]);
		String payeeAccountType = findPayeeAccountType(requestSplit[3]);
		Account userAccount = returnAccount(requestSplit[2], customers.get(customer.getKey()));
		Account payeeAccount = returnAccount(payeeAccountType, customers.get(payeeName.getKey()));
		return transferFunds(userAccount, payeeAccount, payment, type);
	}

	public void addCustomer(String hashKey, Customer customer){
		customers.put(hashKey, customer);
		DatabaseHandler save = new DatabaseHandler();
		try {
			save.saveSession(customers);
			handleTransactions.saveSession(transactionsList);
		} catch (IOException e) {
			e.printStackTrace();
		}
		fillHashMap_fromDB();
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
	private String findPayeeAccountType (String userName) throws FileNotFoundException{
		DatabaseHandler customerDB = new DatabaseHandler();
		customerDB.findInDB(userName.toLowerCase());
		if(customerDB.getName().equalsIgnoreCase(userName)){
			return customerDB.getAccountType(1);
		}
		return null;
	}
	// Helper method to transfer funds
	private String transferFunds(Account user, Account payee, double payment, Transaction.TransactionType type){
		if (user == null || payee == null || !sufficientFunds(user, payment)){
			return "FAIL";
		} else {
			user.modifyBalance(payment, Account.InstructionType.WITHDRAW);
			payee.modifyBalance(payment, Account.InstructionType.DEPOSIT);
			LocalDateTime dateTime = LocalDateTime.now();
			String formattedDate = DateTimeFormatter.ofPattern("MM/dd/yyyy HH.mm.ss").format(dateTime);
			int senderId = user.getAccountId();
			int receiverId = payee.getAccountId();
			String senderName = "";
			Customer sender = null;
			String receiverName = "";
			Customer receiver = null;
			for(Map.Entry<String, Customer> record : customers.entrySet()) { //record is of type Map, record.getValue() returns a Customer object
				for(Account account: record.getValue().getAccounts()){ //record.getValue().getAccounts() returns a List of Account objects
					if(account.getAccountId() == senderId){
						senderName = record.getKey();
						sender = record.getValue();
					}
					if(account.getAccountId() == receiverId){
						receiverName = record.getKey();
						receiver = record.getValue();
					}
					break;
				}
			}
			String message = "payment from "+senderName+" to "+receiverName+" of "+payment+" on "+formattedDate;
			Transaction transaction = new Transaction(dateTime, senderId, senderName, receiverId, receiverName, payment, message, type);
			transactions.put(user, transaction);
			transactionsList.add(transaction);
			if(type.equals(Transaction.TransactionType.MICROLOAN)){
				Double interest = 7.00; //this will need to handled better in the future
				int installments = 12; //this will need to handled better in the future
				LocalDateTime repaymentDate = dateTime.plus(Period.ofYears(1));
				MicroLoan microLoan = new MicroLoan(dateTime, senderId, senderName, receiverId, receiverName, payment, message, type, interest, installments, repaymentDate);
				customerMicroloansOffered.put(sender, microLoan); //creates a lookup mapping for the sending customer to the loan offered
				customerMicroloansReceived.put(receiver, microLoan); //creates a lookup mapping for the receiving customer to the loan taken
				accountMicroloansOffered.put(user, microLoan); //creates a lookup mapping for the sending account to the loan offered
				accountMicroloansReceived.put(payee, microLoan); //creates a lookup mapping for the receiving account to the loan taken
				receiver.incrementNumberLoans();
			}

			return "SUCCESS";
		}
	}

	private String getAvailableCommands(String name){
		String commands = "";
		if(name.equals("staff")){
			commands += "Available commands: \n";
			commands += "* DELETE <customer ID> \n";
			commands += "* DELETE <customer ID> <account type> \n";
			commands += "* CONFIRM";
		}
		else if(name.equals("general")){
			commands += "Available commands: \n";
			commands += "* NEWACCOUNT <openingbalance> <account type> \n";
			commands += "* SHOWMYACCOUNTS \n";
			commands += "* MOVE <amount> <from> <to> \n";
			commands += "* PAY <amount> <from account type> <to customer name> \n";
			commands += "* MICROLOAN <amount> <from account type> <to customer name> \n";
			commands += "* HELP \n";
			commands += "* HELP <command name> \n";
			commands += "* CONFIRM";
		}
		return commands;
	}

	private String helpCommandDescription(String commandName){
		String description = "";
		switch(commandName.toUpperCase()){
			case "NEWACCOUNT":
				description += "NEWACCOUNT 2500 moneymarket - Will create a new Moneymarket account with 2500 balance.";
				break;
			case "SHOWMYACCOUNTS":
				description += "Will display all accounts and their information for the customer.";
				break;
			case "MOVE":
				description += "MOVE 5000 checking savings - Will move 5000 from Checking account to Savings account.";
				break;
			case "PAY":
				description += "PAY 5500 checking john - Will pay 5500 deducted from Checking paid to John's default account which is always Checking.";
				break;
			case "MICROLOAN":
				description += "MICROLOAN 5500 checking john - Will send 1000 deducted from Checking to John's default account which is always Checkings.";
				break;
			case "DELETE":
				description += "DELETE 489418943 - Will delete all records pertinent to the customer with ID# 489418943. Accessible by staff members only. \n";
				description += "DELETE 489418943 moneymarket - Will delete Moneymarket account for customer ID# 489418943. Accessible by staff members only.";
				break;
			case "AUDITREPORT":
				description += "Generates a report of all banking activity. Accessible by staff members only.";
			case "CONFIRM":
				description += "Will save and confirm all session actions into the database.";
				break;
		}
		return description;
	}
}