package newbank.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Customer {
	
	private ArrayList<Account> accounts;
	private String fullName;
	private HashMap<String, String> address;
	private String dob;
	private String taxId;
	private int customerId;
	private String password;
	
	public Customer(String name) {
		accounts = new ArrayList<>();
		address = new HashMap<>();
		fullName = name;
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

}
