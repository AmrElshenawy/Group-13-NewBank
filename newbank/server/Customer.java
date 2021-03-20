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
	private CustomerID customerId;
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

	public String setPassword(String password){
		if (passwordOK(password)){
			this.password = password;
			return "SUCCESS";
		} else {
			return "FAIL";
		}
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

	public int getCustomerId() {
		return customerId.getUniqueID();
	}

	public String getPassword(){
		return this.password;
	}

	// Helper method for setPassword, checks password meets security requirements.
	// Password must be at least 8 characters
	// Password must contain a capital letter
	// Password must contain a special character
	// Password must not be on "bad passwords list"
	public boolean passwordOK(String password){
		String charList = "!#$£%^&*()-_=+;://|/?`~";
		boolean capitalLetter = false;
		boolean specialChar = false;
		for (int i = 0; i < password.length(); i++){
			if (password.charAt(i) >= 65 && password.charAt(i) <= 90){
				capitalLetter = true;
			} else if (charList.indexOf(password.charAt(i)) != 1){
				specialChar = true;
			}
		}
		if (!capitalLetter || !specialChar || password.length() < 8 || badPassword(password)){
			return false;
		} else {
			return true;
		}
	}

	// Helper method to check password against a list of known common passwords.
	public boolean badPassword(String password){
		String[] badPasswords = {"123456", "123456789", "picture1", "password", "12345678",
				"111111", "123123", "12345", "1234567890", "senha", "1234567",
				"qwerty", "abc123", "Million2", "0", "1234", "iloveyou",
				"aaron431", "password1", "qqww1122"};
		for (int i = 0; i < badPasswords.length; i++){
			if (badPasswords[i].equals(password)){
				return true;
			}
		}
		return false;
	}
}
