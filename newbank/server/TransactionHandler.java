package newbank.server;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TransactionHandler {
    private File dB = new File("newbank/TransactionsTable.txt");
    private String id;
    private String customerName;
    private ArrayList<String> plainDBContent = new ArrayList<>(); // Used for updating database
    private ArrayList<String> allTransactions;  // Holds all accounts information about a customer
    private List<ArrayList<String>> returnInfo = new ArrayList<ArrayList<String>>();    // Holds all database information

    // Method used to fully scan the database and return information in an arraylist of arraylists line by line.
    // This method is primarily used to fill in and refresh the Hashmap with information from the database.
    public List<ArrayList<String>> scanFullDB() throws FileNotFoundException {
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
                    allTransactions = new ArrayList<String>();  // Holds all transactions information
                    for(String field : commas){
                        // Array semicolons refreshes on each key:value pair. So it is always size 2.
                        String[] semicolons = field.split(":"); // Take each split on comma earlier, and split again on semicolons
                        switch(semicolons[0]){      // Switch on each key value, if the key matches the case, add the value
                            case "transactionId":
                                allTransactions.add(semicolons[1]);
                                break;
                            case "transactionDateTime":
                                allTransactions.add(semicolons[1]);
                                break;
                            case "senderAccountId":
                                allTransactions.add(semicolons[1]);
                                break;
                            case "senderName":
                                allTransactions.add(semicolons[1]);
                                break;
                            case "receiverAccountId":
                                allTransactions.add(semicolons[1]);
                                break;
                            case "receiverName":
                                allTransactions.add(semicolons[1]);
                                break;
                            case "amount":
                                allTransactions.add(semicolons[1]);
                                break;
                            case "message":
                                allTransactions.add(semicolons[1]);
                                break;
                            default:
                                break;
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

    // Method used to save transactions and update the database
    public void saveSession(HashMap<Account, Transaction> transactions) throws IOException{
        BufferedWriter writer = new BufferedWriter(new FileWriter(dB, false));
        String output = "";
        for(Transaction transaction: transactions.values()){
            output += "transactionId" + transaction.getTransactionId() + ",";
            output += "transactionDateTime" + new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(transaction.getTransactionDateTime()) + ",";
            output += "senderAccountId" + String.valueOf( transaction.getSenderId()) + ",";
            output += "senderName" + transaction.getSenderName() + ",";
            output += "receiverAccountId" + String.valueOf(transaction.getReceiverId()) + ",";
            output += "receiverName" + transaction.getReceiverName() + ",";
            output += "amount" + String.valueOf(transaction.getAmount()) + ",";
            output += System.lineSeparator();
        }
        writer.write(output);
        writer.close();
    }
}
