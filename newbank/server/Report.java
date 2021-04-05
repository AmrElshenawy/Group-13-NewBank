package newbank.server;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.io.IOException;
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.List;


public class Report {

    //fields
    private ArrayList<Account> accounts;
    private NewBank bank;

    //constructor
    public Report() throws FileNotFoundException, IOException {
        bank = new NewBank();
    }

    //methods
    //get all customer from the db
    public String allCustomers(){
        String outputString = "Customer accounts:"+"\n";
        for(Customer record : bank.getName2CustomersMapping().values()){
            outputString += record.getFullName()+" "+record.getAccounts().toString()+"\n";
        }
        return outputString;
    }

    //get total number of accounts
    public String getNoAccounts(){
        Integer count = 0;
        int i = 0;
        List types = Arrays.asList(Account.AccountType.values());
        for(Customer record : bank.getName2CustomersMapping().values()){
            for(Account acc: record.getAccounts()){
                try{
                    if(types.contains(Account.AccountType.valueOf(acc.getAccountType().toString().toUpperCase()))){
                        count++;
                    }
                }catch (Exception e){
                }
            }
            i++;
        }
        return "Number of accounts: "+count.toString();
    }

    //get total amount of deposits
    public String getTotalDeposits(){
        double sum = 0;
        for(Customer record : bank.getName2CustomersMapping().values()){
            for(Account acc: record.getAccounts()){
                sum += acc.getOpeningBalance();
            }
        }
        return "Total deposits: "+sum;
    }
    //get total amount of accounts providing loans
    public String accountsOfferingLoans(){
        int total = 0;
        double sum = 0;
        for(MicroLoan record : bank.getAccount2MicroloansOffered().values()){
            total++;
            sum += record.getAmount();
        }
        return "Number of accounts providing micro loans: " +total +" worth a value of "+sum;
    }

    //get total amount of accounts receiving loans
    public String accountsReceivingLoans(){
        int total = 0;
        double sum = 0;
        for(MicroLoan record : bank.getAccount2MicroloansReceived().values()){
            total++;
            sum += record.getAmount();
        }
        return "Number of accounts receiving micro loans: " +total +" worth a value of "+sum;
    }

    //get total amount of customers providing loans
    public String customersOfferingLoans(){
        int total = 0;
        double sum = 0;
        for(MicroLoan record : bank.getCustomer2MicroloansOffered().values()){
            total++;
            sum += record.getAmount();
        }
        return "Number of customers providing micro loans: " +total +" worth a value of "+sum;
    }

    //get total amount of customers receiving loans
    public String customersReceivingLoans(){
        int total = 0;
        double sum = 0;
        for(MicroLoan record : bank.getCustomer2MicroloansReceived().values()){
            total++;
            sum += record.getAmount();
        }
        return "Number of customers receiving micro loans: " +total +" worth a value of "+sum;
    }

    //total amount of repayments by week and by month

    //generate report
    public void generateReport(){
        String fileOutput = "";
        fileOutput += allCustomers()+"\n";
        fileOutput += getNoAccounts()+"\n";
        fileOutput += getTotalDeposits()+"\n";
        fileOutput += accountsOfferingLoans()+"\n";
        fileOutput += accountsReceivingLoans()+"\n";
        fileOutput += customersOfferingLoans()+"\n";
        fileOutput += customersReceivingLoans()+"\n";
        System.out.println(fileOutput);
        writeToFile(fileOutput);
    }

    //create output file
    public void writeToFile(String output){
        try{
            // if the file doesn't exist yet, create it here
            File myFile = new File("auditReport.txt");
            if(myFile.createNewFile()){
                //now write output to the file
                System.out.println("File created: "+myFile.getName());
                FileWriter myWriter = new FileWriter("auditReport.txt");
                myWriter.write(output);
                myWriter.close();
                System.out.println("Successfully wrote output to the file.");

            // if the file already exists, overwrite it with new data
            } else {
                System.out.println("File already exists. Overwriting with new output");
                try {
                    FileWriter myWriter = new FileWriter("auditReport.txt");
                    myWriter.write(output);
                    myWriter.close();
                    System.out.println("Successfully wrote output to the file.");
                } catch (IOException e) {
                    System.out.println("An error occurred.");
                    e.printStackTrace();
                }
            }
        } catch (IOException e){
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
