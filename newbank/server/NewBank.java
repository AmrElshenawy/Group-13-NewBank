package newbank.server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.lang.Integer.parseInt;
import static java.lang.Double.parseDouble;

public class NewBank {
	
	private static final NewBank bank = new NewBank();
	private HashMap<String,Customer> customers;
	private HashMap<Integer,Customer> accountIdCustomer; //maps account number to customer object
	private DatabaseHandler fullReport = new DatabaseHandler();
	private HashMap<Account,Transaction> transactions;
	private ArrayList<Transaction> transactionsList;
	private TransactionHandler handleTransactions = new TransactionHandler();
	private HashMap<Customer, MicroLoan> customerMicroloansOffered;
	private HashMap<Customer, MicroLoan> customerMicroloansReceived;
	private HashMap<Account, MicroLoan> accountMicroloansOffered;
	private HashMap<Account, MicroLoan> accountMicroloansReceived;
	private boolean didDatabaseChange = false;
	private ArrayList<LoanRequest> loanRequestArrayList;
	protected Double maxLoanAmount = 1000.00;
	protected Double maxMicroLoanRate = 7.25;
	protected int maxLoanDuration = 104; //number of weeks
	protected int minLoanDuration = 1; // 1 week duration

	public NewBank() {
		customers = new HashMap<>();
		transactions = new HashMap<>();
		accountIdCustomer = new HashMap<>();
		customerMicroloansOffered = new HashMap<>();
		customerMicroloansReceived = new HashMap<>();
		accountMicroloansOffered = new HashMap<>();
		accountMicroloansReceived = new HashMap<>();
		transactionsList = new ArrayList<>();
		fillHashMap_fromDB();
		readTransactionsFromDB();
		Interest interest = new Interest(customers,transactions);
		populateLoanRequestList();

	}

	public static NewBank getBank() {
		return bank;
	}

	public HashMap<String,Customer> getName2CustomersMapping(){
		return customers;
	}

	public HashMap<Integer,Customer> getAccountId2CustomerMapping(){ return accountIdCustomer; }

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
						accountIdCustomer.put(account1.getAccountId(), x);
					}
					x.setPassword(line.get(2));
					x.setCustomerID((x.getFullName() + x.getPassword()).hashCode());
					customers.put(line.get(1), x);

					if(line.size() > 6){
						Account account2 = new Account(Integer.parseInt(line.get(7)), Double.parseDouble(line.get(8)), Account.AccountType.valueOf(line.get(6).toUpperCase()));
						x.setAccount(account2);
						accountIdCustomer.put(account2.getAccountId(), x);
					}
					if(line.size() > 9){
						Account account3 = new Account(Integer.parseInt(line.get(10)), Double.parseDouble(line.get(11)), Account.AccountType.valueOf(line.get(9).toUpperCase()));
						x.setAccount(account3);
						accountIdCustomer.put(account3.getAccountId(), x);

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
		Transaction transaction = null;
		Account account = null;
		LocalDateTime dateTime = null;
		try {
			scanOutput = handleTransactions.scanFullDB();
			for(ArrayList<String> line: scanOutput){
				for(int i = 0; i < line.size(); i++){
					account = new Account(parseInt(line.get(2)));
					dateTime = LocalDateTime.parse(line.get(1),DateTimeFormatter.ofPattern("MM/dd/yyyy HH.mm.ss"));
					int senderId = parseInt(line.get(2));
					String senderName = line.get(3);
					int receiverId = parseInt(line.get(4));
					String receiverName = line.get(5);
					Double amount = parseDouble(line.get(6));
					String message = line.get(7);
					Transaction.TransactionType tranType = Transaction.TransactionType.valueOf(line.get(8));
					transaction = new Transaction(dateTime, senderId, senderName, receiverId, receiverName, amount, message, tranType);
					transaction.setTransactionId(Integer.parseInt(line.get(0)));
				}
				transactions.put(account, transaction);
				transactionsList.add(transaction);
				switch (transaction.getTransactionType().toString()){
					case "DEPOSIT":
						customers.get(line.get(5).toLowerCase()).setTransactionReceived(transaction);
						break;
					case "WITHDRAWAL":
						customers.get(line.get(3).toLowerCase()).setTransactionSent(transaction);
						break;
					default:
						customers.get(line.get(3).toLowerCase()).setTransactionSent(transaction);
						customers.get(line.get(5).toLowerCase()).setTransactionReceived(transaction);
						break;
				}
				if(transaction.getTransactionType().equals(Transaction.TransactionType.MICROLOAN)){
					Double interest = 7.00; //this will need to handled better in the future
					int installments = 12; //this will need to handled better in the future
					LocalDateTime repaymentDate = dateTime.plus(Period.ofYears(1));
					MicroLoan microLoan = new MicroLoan(dateTime, parseInt(line.get(2)), line.get(3), parseInt(line.get(4)), line.get(5), parseDouble(line.get(6)), line.get(7), Transaction.TransactionType.MICROLOAN, interest, installments, repaymentDate);
					customerMicroloansOffered.put(customers.get(line.get(3).toLowerCase()), microLoan); //creates a lookup mapping for the sending customer to the loan offered
					customerMicroloansReceived.put(customers.get(line.get(5).toLowerCase()), microLoan); //creates a lookup mapping for the receiving customer to the loan taken
					accountMicroloansOffered.put(account, microLoan); //creates a lookup mapping for the sending account to the loan offered
					accountMicroloansReceived.put(returnAccount("checking", customers.get(line.get(5).toLowerCase())), microLoan); //creates a lookup mapping for the receiving account to the loan taken
				}

			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	private void  populateLoanRequestList(){
		LoanOfferHandler loanOffersList = new LoanOfferHandler();
		loanRequestArrayList = loanOffersList.getLoanOffers();
	}
	
	public synchronized CustomerID checkLogInDetails(String userName, String password) throws IOException {
		try{
			DatabaseHandler customerDB = new DatabaseHandler();
			customerDB.findInDB(userName.toLowerCase());
			if(customerDB.getName().equalsIgnoreCase(userName)) {
				if (customerDB.getPassword().equals(password)){
					return new CustomerID(userName.toLowerCase());
				}
			}
		}catch(Exception e){
			System.out.println("Invalid user credentials. Please select option 1. SIGN UP.");
		}

		return null;
	}

	// commands from the NewBank customer are processed in this method
	public synchronized String processRequest(CustomerID customer, String request) throws FileNotFoundException {
		if(customers.containsKey(customer.getKey())) {
			String[] requestSplit = request.split(" ");
			switch(requestSplit[0]) {
			case "SHOWMYACCOUNTS":
				return showMyAccounts(customer);
			case "NEWACCOUNT":
				// NEWACCOUNT <Type>
				ArrayList<Account> accountsList = customers.get(customer.getKey()).getAccounts();
				if(accountsList.size() >= 3){
					return "MAXIMUM NUMBER OF ACCOUNTS REACHED!";
				}
				for(Account account : accountsList){
					if(account.getAccountType().toString().equalsIgnoreCase(requestSplit[1])){
						return "ERROR. ACCOUNT TYPE ALREADY EXISTS!";
					}
				}
				double openingBalance = 0;
				Account.AccountType accType;
				if(requestSplit.length == 2){
					String accountType = requestSplit[1];

					if(accountType.equalsIgnoreCase(Account.AccountType.CHECKING.toString())){
						accType = Account.AccountType.CHECKING;
					} 
					else if(accountType.equalsIgnoreCase(Account.AccountType.SAVINGS.toString())){
						accType = Account.AccountType.SAVINGS;
					} 
					else if(accountType.equalsIgnoreCase(Account.AccountType.MONEYMARKET.toString())){
						accType = Account.AccountType.MONEYMARKET;
					} 
					else{
						return "PLEASE PROVIDE CHECKING, SAVINGS OR MONEYMARKET AS TYPE.";
					} 
					customers.get(customer.getKey()).setAccount(new Account(openingBalance,accType));
					didDatabaseChange = true;
				} 
				else{
					return "INCORRECT COMMAND ENTERED!";
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
						customers.get(customer.getKey()).setTransactionSent(transaction);
						customers.get(customer.getKey()).setTransactionReceived(transaction);
						try {
							handleTransactions.saveSession(transactionsList);
						} catch (IOException e) {
							e.printStackTrace();
						}
						didDatabaseChange = true;
						return "SUCCESS! Move transfer complete.";
					}
				}
			case "PAY":
				// PAY <Amount> <From User's Account> <To Payee's UserName>
				if (requestSplit.length != 4){
					return "FAIL";
				} else if(!requestSplit[2].equalsIgnoreCase("CHECKING")){ // if user's account type is not checking
					return "Error: You can only pay another NewBank user from your checking account";
				} else if (Integer.parseInt(requestSplit[1]) <= 0){
					return "Error: Invalid amount specified. Transfers must be at least £0.01";
				}else {
					didDatabaseChange = true;
					return payPerson(customer, requestSplit, Transaction.TransactionType.PAYMENT);
				}
			case "DEPOSIT":
				// DEPOSIT <Amount> <To User's Account>
				try{
					Account accountTo = returnAccount(requestSplit[2], customers.get(customer.getKey()));
					Double deposit = Double.parseDouble(requestSplit[1]);
					if (accountTo == null) { // you can't deposit to an account that does not exist
						return "Error: One or more accounts not recognised. Please check account details and try again.";
					} else if (deposit <= 0){
						return "Error: Invalid amount specified. Transfers must be at least £0.01";
					} else {
						accountTo.modifyBalance(deposit, Account.InstructionType.DEPOSIT);
						LocalDateTime dateTime = LocalDateTime.now();
						String formattedDate = DateTimeFormatter.ofPattern("MM/dd/yyyy HH.mm.ss").format(dateTime);
						int senderId = customers.get(customer.getKey()).getCustomerId(); // customer ID (number)
						String senderName = customer.getKey();
						int receiverId = accountTo.getAccountId();
						String receiverName = customer.getKey();
						String message = "deposit from " + senderName + " to " + receiverName + " of " + deposit + " on " + formattedDate;
						Transaction.TransactionType tranType = Transaction.TransactionType.DEPOSIT;
						Transaction transaction = new Transaction(dateTime, senderId, senderName, receiverId, receiverName, deposit, message, tranType);
						transactions.put(accountTo, transaction);
						transactionsList.add(transaction);
						customers.get(customer.getKey()).setTransactionReceived(transaction);
						handleTransactions.saveSession(transactionsList);
						didDatabaseChange = true;
						return "SUCCESS! You deposited " + deposit + " into your account: " + accountTo.getAccountType();
					}
				}catch(Exception e){
					return "FAIL. Error: "+e.getMessage()+". Please try again";
				}
			case "WITHDRAW":
				// WITHDRAW <Amount> <From User's Account>
				try{
					Account accountFrom = returnAccount(requestSplit[2], customers.get(customer.getKey()));
					Double withdrawal = Double.parseDouble(requestSplit[1]);
					if (accountFrom == null) { // you can't withdraw from an account that does not exist
						return "Error: One or more accounts not recognised. Please check account details and try again.";
					} else if (withdrawal <= 0){
						return "Error: Invalid amount specified. Transfers must be at least £0.01";
					} else if (!sufficientFunds(accountFrom, withdrawal)){
						return "Error: insufficient funds available to carry out transfer";
					} else {
						accountFrom.modifyBalance(withdrawal, Account.InstructionType.WITHDRAW);
						LocalDateTime dateTime = LocalDateTime.now();
						String formattedDate = DateTimeFormatter.ofPattern("MM/dd/yyyy HH.mm.ss").format(dateTime);
						int senderId = accountFrom.getAccountId();
						String senderName = customer.getKey();
						int receiverId = customers.get(customer.getKey()).getCustomerId(); // customer ID (number)
						String receiverName = customer.getKey();
						String message = "withdrawal by " + receiverName + " from " + receiverName + " of " + withdrawal + " on " + formattedDate;
						Transaction.TransactionType tranType = Transaction.TransactionType.WITHDRAWAL;
						Transaction transaction = new Transaction(dateTime, senderId, senderName, receiverId, receiverName, withdrawal, message, tranType);
						transactions.put(accountFrom, transaction);
						transactionsList.add(transaction);
						customers.get(customer.getKey()).setTransactionSent(transaction);
						handleTransactions.saveSession(transactionsList);
						didDatabaseChange = true;
						return "SUCCESS! You withdrew " + withdrawal + " from your account: " + accountFrom.getAccountType();
					}
				}catch(Exception e){
					return "FAIL. Error: "+e.getMessage()+". Please try again";
				}
			case "VIEWLOANREQUESTS":
					//displays the list of current requests
					return displayLoanRequests (loanRequestArrayList);
			case "CREATELOANREQUEST":
					// CREATELOANREQUEST <Amount> <interestRate> <Installments> <Duration in weeks>
					if (checkLoanInputString(requestSplit)) {
						return loanInputStringError(requestSplit);
					} else {
						return createLoanRequest(customer, requestSplit, loanRequestArrayList);
					}
			case "LOANCALCULATOR":
					// CREATELOANREQUEST <Amount> <interestRate> <Installments> <Duration in weeks>
					if (checkLoanInputString(requestSplit)) {
						return loanInputStringError(requestSplit);
					} else {
						return loanCalculator(customer, requestSplit, loanRequestArrayList);
					}
			case "MICROLOAN":
				// MICROLOAN <Amount> <From User's Account> <To Payee's UserName>
				if (requestSplit.length != 4){
					return "FAIL";
				} else if (Integer.parseInt(requestSplit[1]) <= 0){
					return "Error: Invalid amount specified. Transfers must be at least £0.01";
				} else {
					String payeeAccountType = requestSplit[2];
					Customer sender = customers.get(customer.getKey());
					Customer payee = customers.get(requestSplit[3].toLowerCase());
					//return payPerson(customer, requestSplit, Transaction.TransactionType.MICROLOAN); //uncomment for testing
					if(sender.canOfferLoan() && payee.canTakeLoan()){
						didDatabaseChange = true;
						return payPerson(customer, requestSplit, Transaction.TransactionType.MICROLOAN);
					} else if(!sender.canOfferLoan()) {
						return "YOU ARE NOT AUTHORIZED TO OFFER LOANS";
					} else if(!payee.canTakeLoan()){
						return "PAYEE NOT AUTHORIZED TO RECEIVE LOANS";
					} else return "FAIL";
				}
			case "CONFIRM":
				DatabaseHandler save = new DatabaseHandler();
				if(!didDatabaseChange){
					return "NO CHANGES TO CONFIRM.";
				}
				else if(didDatabaseChange){
					try {
						save.saveSession(customers);
	
					} catch (IOException e) {
						e.printStackTrace();
					}
					return "ACTION CONFIRMED!";
				}
			case "DELETE":
				// DELETE <Customer ID>
				// DELETE <Customer ID> <AccountType>
				if(customer.getKey().equalsIgnoreCase("staff")){
					if(requestSplit.length == 2){
						for(Map.Entry<String, Customer> record : customers.entrySet()){
							String user = record.getKey();
							Customer object = record.getValue();
							if(Integer.toString(object.getCustomerId()).equals(requestSplit[1])){
								customers.remove(user);
								didDatabaseChange = true;
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
										didDatabaseChange = true;
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
			case "SHOWTRANSACTIONS":
				if (requestSplit.length != 1){
					return "FAIL";
				} else {
					return customers.get(customer.getKey()).showTransactions();
				}
			case "LOGOUT":
				logOut();
			default : return "UNRECOGNIZED COMMAND.";
			}
		}
		return "UNRECOGNIZED COMMAND.";
	}

	private void logOut(){
		System.out.println("\nSUCCESS. You have been logged out.");
		System.exit(0);
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
			//try{
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
				sender.setTransactionSent(transaction);
				receiver.setTransactionReceived(transaction);
				if(type.equals(Transaction.TransactionType.MICROLOAN)){
					Double interest = 7.00; //this will need to handled better in the future
					int installments = 12; //this will need to handled better in the future
					LocalDateTime repaymentDate = dateTime.plus(Period.ofYears(1));
					MicroLoan microLoan = new MicroLoan(dateTime, senderId, senderName, receiverId, receiverName, payment, message, type, interest, installments, repaymentDate);
					customerMicroloansOffered.put(sender, microLoan); //creates a lookup mapping for the sending customer to the loan offered
					customerMicroloansReceived.put(receiver, microLoan); //creates a lookup mapping for the receiving customer to the loan taken
					accountMicroloansOffered.put(user, microLoan); //creates a lookup mapping for the sending account to the loan offered
					accountMicroloansReceived.put(payee, microLoan); //creates a lookup mapping for the receiving account to the loan taken
				}
			try {
				handleTransactions.saveSession(transactionsList);
			} catch (IOException e) {
				e.printStackTrace();
			}
			//}catch(Exception e){
			//}

			return "SUCCESS";
		}
	}

	protected String getAvailableCommands(String name){
		String commands = "";
		if(name.equals("staff")){
			commands += "\n Available commands: \n";
			commands += "* DELETE <customer ID> \n";
			commands += "* DELETE <customer ID> <account type> \n";
			commands += "* AUDITREPORT \n";
			commands += "* CONFIRM \n";
			commands += "* LOGOUT \n";
		}
		else if(name.equals("general")){
			commands += "\n Available commands: \n";
			commands += "* NEWACCOUNT <account type> \n";
			commands += "* SHOWMYACCOUNTS \n";
			commands += "* MOVE <amount> <from> <to> \n";
			commands += "* DEPOSIT <amount> <to account type> \n";
			commands += "* WITHDRAW <amount> <from account type> \n";
			commands += "* SHOWTRANSACTIONS \n";
			commands += "* PAY <amount> <from account type> <to customer name> \n";
			commands += "* MICROLOAN <amount> <from account type> <to customer name> \n";
			commands += "* VIEWLOANREQUESTS \n";
			commands += "* CREATELOANREQUEST <amount> <interestRate> <installments> <duration in weeks> \n";
			commands += "* LOANCALCULATOR <amount> <interestRate> <installments> <duration in weeks> \n";
			commands += "* HELP \n";
			commands += "* HELP <command name> \n";
			commands += "* CONFIRM \n";
			commands += "* LOGOUT \n";
		}
		return commands;
	}

	private String helpCommandDescription(String commandName){
		String description = "";
		switch(commandName.toUpperCase()){
			case "NEWACCOUNT":
				description += "NEWACCOUNT checking - Will create a new Checking account with 0 balance.";
				break;
			case "SHOWMYACCOUNTS":
				description += "Will display all accounts and their information for the customer.";
				break;
			case "MOVE":
				description += "MOVE 5000 checking savings - Will move 5000 from Checking account to Savings account.";
				break;
			case "DEPOSIT":
				description += "DEPOSIT 1000 moneymarket - Will deposit 1000 into Moneymarket account.";
				break;
			case "WITHDRAW":
				description += "WITHDRAW 500 savings - Will withdraw 500 from Savings account.";
			case "SHOWTRANSACTIONS":
				description += "SHOWTRANSACTIONS - Will display all incoming and outgoing transactions from this account.";
				break;
			case "PAY":
				description += "PAY 5500 checking john - Will pay 5500 deducted from Checking paid to John's default account which is always Checking.";
				break;
			case "MICROLOAN":
				description += "MICROLOAN 5500 checking john - Will send 1000 deducted from Checking to John's default account which is always Checkings.";
				break;
			case "VIEWLOANREQUESTS":
				description += "VIEWLOANREQUESTS - Displays the list of current requests";
				break;
			case "CREATELOANREQUEST":
				description += "CREATELOANREQUEST 2000 5 10 20 - Will create a loan request for 2000 dollars at 5% interest rate for 10 installments over 20 weeks.";
				break;
			case "LOANCALCULATOR":
				description += "LOANCALCULATOR <Amount> <interestRate> <Installments> <Duration in weeks> - calculates the loan details for entered parameters.";
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
			case "LOGOUT":
				description += "Will close your banking session and exit the application";
				break;
		}
		return description;
	}
	//Method to print out loan requests
	private String displayLoanRequests (ArrayList<LoanRequest> loanRequestsList){
		String requests="";
		String loanRequests;
		ListIterator<LoanRequest> loan = loanRequestsList.listIterator();
		while (loan.hasNext())
		{
			loanRequests = loan.next().displayRequest();
			requests = requests.concat(loanRequests);
			//requests = requests.concat(\n);
		}
		return requests;
	}
	//Method to create a customer microloan request
	private String createLoanRequest(CustomerID customer, String[] requestSplit, ArrayList<LoanRequest> loanRequestArrayList){
		LoanRequest request = new LoanRequest(findCustomerIDNumber(customer), customer.getKey(), Double.parseDouble(requestSplit[1]), Double.parseDouble(requestSplit[2]), Integer.parseInt(requestSplit[3]), Integer.parseInt(requestSplit[4]));
		loanRequestArrayList.add(request);
		return "success";
	}
	//Helper method to get a users id from the Customer ID Object from the hashmap
	private int findCustomerIDNumber(CustomerID id){
		Customer profile = customers.get(id.getKey());
		return profile.getCustomerId();
	}
	//Method to create a customer microloan request
	private String loanCalculator(CustomerID customer, String[] requestSplit, ArrayList<LoanRequest> loanRequestArrayList){
		LoanRequest request = new LoanRequest(findCustomerIDNumber(customer), customer.getKey(), Double.parseDouble(requestSplit[1]), Double.parseDouble(requestSplit[2]), Integer.parseInt(requestSplit[3]), Integer.parseInt(requestSplit[4]));
		return request.displayRequest();
	}
	/* Method to check MarketPlace String Entry Errors
    Loan Command <Amount> <interestRate> <Installments>
    Checks:
    * Number of arguments
    * If loan request exceeds max loan amount
    * If interest rate exceeds max MicroLoan interest rate or is less than 0.
    * If pay back installments exceed monthly installments. Weekly, biweekly or monthly are okay.
    */
	private boolean checkLoanInputString (String [] requestSplit){
		if (requestSplit.length != 5) {
			return true;
		}
		if (Double.parseDouble(requestSplit[1]) > maxLoanAmount){
			return true;
		}
		if (Double.parseDouble(requestSplit[2]) > maxMicroLoanRate || Double.parseDouble(requestSplit[2]) < 0){
			return true;
		}
		if (Integer.parseInt(requestSplit[4]) > maxLoanDuration || Integer.parseInt(requestSplit[4]) < minLoanDuration){
			return true;
		}
		if ((Integer.parseInt(requestSplit[4])*12)/(52*Integer.parseInt(requestSplit[4])) > 1){
			return true;
		}
		return false;
	}
	private String loanInputStringError (String [] requestSplit){
		if (requestSplit.length != 5) {
			return "FAIL - Improper Number of Arguments";
		}
		if (Double.parseDouble(requestSplit[1]) > maxLoanAmount){
			return "You have exceeded the allowable loan amount request of " + maxLoanAmount + " pounds";
		}
		if (Double.parseDouble(requestSplit[2]) > maxMicroLoanRate || Double.parseDouble(requestSplit[2]) < 0){
			return "Interest rate must between 0 - " + maxMicroLoanRate +" %";
		}
		if (Integer.parseInt(requestSplit[4]) > maxLoanDuration || Integer.parseInt(requestSplit[4]) < minLoanDuration){
			return "Loan Request needs to between " + minLoanDuration + "week and " + maxLoanDuration +"weeks.";
		}
		if ((Integer.parseInt(requestSplit[4])*12)/(52*Integer.parseInt(requestSplit[4])) > 1){
			return "Installment payments must be at least made monthly";
		}
		return "False";
	}
}