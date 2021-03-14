package newbank.server;

public class CustomerID {
	private String key;
	private String password;
	
	public CustomerID(String key, String password) {
		this.key = key;
		this.password = password;
	}
	
	public String getKey() {
		return key;
	}

	public int getUniqueID() {
		return (key+password).hashCode(); // returns a hascode of the username + password combined as a unique id
	}
}
