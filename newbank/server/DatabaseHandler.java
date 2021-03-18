package newbank.server;

import java.io.*;

public class DatabaseHandler  {

    private File dB = new File("newbank\\Database.txt");
    private String id;
    private String customerName;
    private String password;
    private String accountType;
    private String accountName;
    private String accountBalance;
    
    public void readDB(String name) throws FileNotFoundException{
        String info = "";
        try{
            BufferedReader reader = new BufferedReader(new FileReader(dB));
            info = reader.readLine();
            while(info != null){
                String[] commas = info.split(",");
                if(commas[1].contains(name)){
                    for(String field : commas){
                        String[] semicolons = field.split(":");
                        switch(semicolons[0]){
                            case "id":
                                id = semicolons[1];
                                break;
                            case "name":
                                customerName = semicolons[1];
                                break;
                            case "password":
                                password = semicolons[1];
                                break;
                            case "accounttype":
                                accountType = semicolons[1];
                                break;
                            case "accountname":
                                accountName = semicolons[1];
                                break;
                            case "accountbalance":
                                accountBalance = semicolons[1];
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

    public String getID(){
        return this.id;
    }

    public String getName(){
        return this.customerName;
    }

    public String getPassword(){
        return this.password;
    }

    public String getAccountType(){
        return this.accountType;
    }

    public String getAccountName(){
        return this.accountName;
    }

    public String getAccountBalance(){
        return this.accountBalance;
    }

    public void writeDB(){

    }
}
