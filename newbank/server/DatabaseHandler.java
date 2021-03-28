package newbank.server;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DatabaseHandler  {

    private File dB = new File("newbank/Database.txt");
    private String id;
    private String customerName;
    private String password;
    private ArrayList<String> plainDBContent = new ArrayList<>(); // Used for updating database
    private ArrayList<String> allAccounts;  // Holds all accounts information about a customer
    private List<ArrayList<String>> returnInfo = new ArrayList<ArrayList<String>>();    // Holds all database information
    
    // Method used to fully scan the database and return information in an arraylist of arraylists line by line.
    // This method is primairly used to fill in and refresh the Hashmap with information from the database.
    public List<ArrayList<String>> scanFullDB() throws FileNotFoundException{
        String info = "";   // A full line in the database
        plainDBContent.clear();
        try{
            BufferedReader reader = new BufferedReader(new FileReader(dB));
            info = reader.readLine();
            while(info != null){
                plainDBContent.add(info + System.lineSeparator()); // Used for updating database
                String[] commas = info.split(",");  // Take a line, split it on commas
                ArrayList<String> values = new ArrayList<String>(); // Used to store the key:value value of each key in the database.
                for(String field : commas){
                    // Array semicolons refreshes on each key:value pair. So it is always size 2.
                    String[] semicolons = field.split(":"); // Take each each split on comma earlier, and split again on semicolons.
                    values.add(semicolons[1]);  // Take the value and add it
                }
                returnInfo.add(values); // After Arraylist values is filled with values from a complete line, add it to returnInfo
                info = reader.readLine();
            }
            reader.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return returnInfo;
    }
    
    // Method used to search for a specific customer name
    public void findInDB(String name) throws FileNotFoundException{
        String info = "";   // A full line in the database
        try{
            BufferedReader reader = new BufferedReader(new FileReader(dB));
            info = reader.readLine();
            while(info != null){
                String[] commas = info.split(",");  // Take a line, split it on commas
                if(commas[1].contains(name)){   // If the line belongs to the customer specified
                    allAccounts = new ArrayList<String>();  // Holds all accounts information
                    for(String field : commas){
                        // Array semicolons refreshes on each key:value pair. So it is always size 2.
                        String[] semicolons = field.split(":"); // Take each split on comma earlier, and split again on semicolons
                        switch(semicolons[0]){      // Switch on each key value, if the key matches the case, add the value
                            case "id":
                                id = semicolons[1];
                                break;
                            case "name":
                                customerName = semicolons[1];
                                break;
                            case "password":
                                password = semicolons[1];
                                break;
                            case "accounttype1":
                                allAccounts.add(semicolons[1]);
                                break;
                            case "accountname1":
                                allAccounts.add(semicolons[1]);
                                break;
                            case "accountbalance1":
                                allAccounts.add(semicolons[1]);;
                                break;
                            case "accounttype2":
                                allAccounts.add(semicolons[1]);
                                break;
                            case "accountname2":
                                allAccounts.add(semicolons[1]);
                                break;
                            case "accountbalance2":
                                allAccounts.add(semicolons[1]);
                                break;
                            case "accounttype3":
                                allAccounts.add(semicolons[1]);
                                break;
                            case "accountname3":
                                allAccounts.add(semicolons[1]);
                                break;
                            case "accountbalance3":
                                allAccounts.add(semicolons[1]);
                                break;
                            default:
                                break;
                        }
                    }
                }
                info = reader.readLine();
            }
            reader.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    // Get specified customer ID
    public String getID(){
        return this.id;
    }

    // Get specified customer name
    public String getName(){
        return this.customerName;
    }

    // Get specified customer password
    public String getPassword(){
        return this.password;
    }

    // Method used to get account type, for a specific account number
    // If account number doesn't exist, returns null
    public String getAccountType(int i){
        try{
            switch(i){
                case 1:
                    return allAccounts.get(0);
                case 2:
                    return allAccounts.get(3); 
                case 3:
                    return allAccounts.get(6);
            }
            return null;
        }
        catch(IndexOutOfBoundsException x){
            return null;
        }
    }

    // Method used to get account name, for a specific account number
    // If account number doesn't exist, returns null
    public String getAccountName(int i){
        try{
            switch(i){
                case 1:
                    return allAccounts.get(1);
                case 2:
                    return allAccounts.get(4); 
                case 3:
                    return allAccounts.get(7);
            }
            return null;
        }
        catch(IndexOutOfBoundsException x){
            return null;
        } 
    }

    // Method used to get account balance, for a specific account number
    // If account number doesn't exist, returns null
    public String getAccountBalance(int i){
        try{
            switch(i){
                case 1:
                    return allAccounts.get(2);
                case 2:
                    return allAccounts.get(5);
                case 3:
                    return allAccounts.get(8);
            }
            return null;
        }
        catch(IndexOutOfBoundsException x){
            return null;
        }
    }

    // Method used to save session transactions and update the database
    public void saveSession(HashMap<String, Customer> customers) throws IOException{
        BufferedWriter writer = new BufferedWriter(new FileWriter(dB, false));
        String output = "";
        for(Customer customer : customers.values()){
            output += "id:" + customer.getCustomerId() + ",";
            output += "name:" + customer.getFullName() + ",";
            output += "password:" + customer.getPassword() + ",";
            Integer counter = 1;
            if(customer.getAccounts().size() >= 1){
                for(Account account : customer.getAccounts()){
                    output += "accounttype" + counter.toString() + ":" + account.getAccountType().toString().toLowerCase() + ",";
                    output += "accountnumber" + counter.toString() + ":" + account.getAccountId() + ",";
                    output += "accountbalance" + counter.toString() + ":" + account.getOpeningBalance() + ",";
                    counter++;
                }
                output += System.lineSeparator();
            }
        }
        writer.write(output);
        writer.close();
    }
}
