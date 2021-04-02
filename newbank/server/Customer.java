package newbank.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.time.LocalDate;
import java.time.Period;

public class Customer {
	
	private ArrayList<Account> accounts;
	private String fullName;
	private HashMap<String, String> address;
	private String dob;
	private String taxId;
	private int customerId;
	private String password;
	private LocalDate joinDate;
	private int numberLoans;

	
	public Customer(String name) {
		accounts = new ArrayList<>();
		address = new HashMap<>();
		fullName = name;
		joinDate = LocalDate.now();
		numberLoans = 0;
	}
	
	public String accountsToString() {
		String s = "";
		for(int i = 0; i < accounts.size(); i++){
			if(i != accounts.size() - 1){
				s += accounts.get(i).toString() + "\n";
			}
			else{
				s += accounts.get(i).toString();
			}
		}
		return s;
	}

	public String addressToString() {
		String s = "";
		for(String value: address.values()){
			s += value;
		}
		return s;
	}

	// setters
	public void setAccount(Account account) {
		accounts.add(account);
	}

	public void deleteAccount(Account account){
		accounts.remove(account);
	}

	public void setFullName(String name) {
		fullName = name;
	}

	public void setPassword(String password){
		this.password = password;
	}

	public void setAddress(String addressLine1, String addressLine2, String addressLine3) {
		address.put("HouseNumber+Street", addressLine1);
		address.put("City", addressLine2);
		address.put("Postal Code", addressLine3);
	}

	public void setTaxId(String taxIdentifier){
		taxId = taxIdentifier;
	}

	public void setBirthdate(String birthdate){
		dob = birthdate;
	}

	public int incrementNumberLoans() {
		numberLoans++;
		return numberLoans;
	}

	// getters
	public ArrayList<Account> getAccounts() {
		return accounts;
	}

	public String getFullName() {
		return fullName;
	}

	public HashMap<String, String> getAddress() {
		return address;
	}

	public String getBirthdate() {
		return dob;
	}

	public String getTaxId() {
		return taxId;
	}

	public void setCustomerID(int code){
		this.customerId = code;
	}
	
	public int getCustomerId() {
		return customerId;
	}

	public String getPassword(){
		return this.password;
	}

	public LocalDate getJoinDate(){
		return joinDate;
	}

	public int getNumberLoans() {
		return numberLoans;
	}

	/* methods check that client does not have more than 3 loans already
	and has been with the bank for at least 90 days */
	public boolean canTakeLoan(){
		Double sumLoans = 0.0;
		LocalDate acceptDate = joinDate.plus(Period.ofDays(90));
		for(Account account: this.getAccounts()){
			for(Transaction transaction: account.getTransactions()){
				if(transaction.getTransactionType().equals(Transaction.TransactionType.MICROLOAN)){
					sumLoans =+ transaction.getAmount();
				}
			}
		}

		return this.getNumberLoans() <= 3 && LocalDate.now().isAfter(acceptDate) && sumLoans < 1000 ? true : false;
	}

	//checks if a customer meets the criteria for offering a loan
	public boolean canOfferLoan(){
		Double sumDeposits = 0.0;
		int count = 0;
		Double totalBalance = 0.0;
		for(Account account: this.getAccounts()){
			totalBalance =+ account.getOpeningBalance();
			for(Transaction transaction: account.getTransactions()){
				if(transaction.getTransactionType().equals(Transaction.TransactionType.DEPOSIT)){
					count++;
					sumDeposits =+ transaction.getAmount();
				}
			}
		}
		return sumDeposits >= 1000.0 && count >= 3 && totalBalance > 0 ?  true : false;
	}



}
