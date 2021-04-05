package newbank.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class UserRegistration {

    private BufferedReader in;
    private PrintWriter out;
    private Customer customer;

    public UserRegistration(Socket s, NewBank bank){
        try {
            in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            out = new PrintWriter(s.getOutputStream(), true);

            // ask for username
            String userName;
            do {
                out.println("Please choose a username:");
                userName = in.readLine();
                if (bank.checkCustomer(userName)) {
                    out.println("Username is already taken!");
                }
            } while (bank.checkCustomer(userName) && userName != null);
            // ask for user's name
            out.println("Enter your name, you will use this to login:");
            String fullName = in.readLine();
            // ask for user's address
            out.println("Enter the first line of your address. E.g \"123 Penny Lane\"");
            String addressLine1 = in.readLine();
            out.println("Enter your town/city:");
            String addressLine2 = in.readLine();
            out.println("Enter your postcode: ");
            String addressLine3 = in.readLine();
            // ask for user's DoB
            out.println("Enter your DoB using the format DD/MM/YYYY:");
            String dob = in.readLine();
            // ask for user's tax ID
            out.println("Enter your tax ID:");
            String taxID = in.readLine();
            String password1;
            String password2;
            do {
                out.println("Enter a password:");
                password1 = in.readLine();
                out.println("Re-enter password:");
                password2 = in.readLine();
                if (!passwordOK(password1) || password1 == null || password2 == null) {
                    out.println("Password must be longer than 8 characters, contain at least " +
                            "one capital letter and one special character.");
                } else if (!password1.equals(password2)) {
                    out.println("Password entries do not match. Please try again.");
                }
            } while (!passwordOK(password1) || !password1.equals(password2) || password1 == null || password2 == null);

            customer = new Customer(fullName.toLowerCase());
            customer.setAddress(addressLine1, addressLine2, addressLine3);
            customer.setBirthdate(dob);
            customer.setTaxId(taxID);
            customer.setPassword(password1);
            customer.setCustomerID((customer.getFullName() + customer.getPassword()).hashCode());
            out.println(accountSelectionMessage());
            String selection = in.readLine();
            switch (selection) {
                case "1":
                    accountSetup(Account.AccountType.CHECKING);
                    break;
                case "2":
                    accountSetup(Account.AccountType.SAVINGS);
                    break;
                case "3":
                    accountSetup(Account.AccountType.MONEYMARKET);
                    break;
                default : out.println("Invalid selection.");
            }
            bank.addCustomer(userName, customer); // add customer to NewBank HashMap.
            out.println(congratulationsMessage());
            in.readLine();
        }
        catch (IOException e){
            System.out.println(e);
        }
    }

    public void accountSetup(Account.AccountType accountType){
        try {
            out.println("Enter the opening balance for the account:");
            String openingBalance = in.readLine();
            Double openingBalanceDouble = Double.parseDouble(openingBalance);

            customer.setAccount(new Account(openingBalanceDouble, accountType));
        }
        catch (IOException e){
            System.out.println(e);
        }
    }

    public Customer getCustomer(){
        return customer;
    }

    // Helper method for setPassword, checks password meets security requirements.
    // 1.Password must be at least 8 characters
    // 2. Password must contain a capital letter
    // 3. Password must contain a special character
    // 4. Password must not be on "bad passwords list"
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
        for (String badPassword : badPasswords){
            if (badPassword.equals(password)){
                return true;
            }
        }
        return false;
    }

    // Option menu message printer.
    public String accountSelectionMessage(){
        String accountSelectionMessage =
                "\n" +
                "==================================================\n" +
                "||          *** ACCOUNT REGISTRATION ***        ||\n" +
                "==================================================\n" +
                "|| Please select one of the following options:  ||\n" +
                "||      1. CHECKING                             ||\n" +
                "||      2. SAVINGS                              ||\n" +
                "||      3. MONEYMARKET                          ||\n" +
                "||  (enter the number corresponding to your     ||\n" +
                "||   choice and press enter)                    ||\n" +
                "==================================================\n" +
                "\nSelection:";
        return accountSelectionMessage;
    }

    // Option menu message printer.
    public String congratulationsMessage(){
        String congratulationsMessage =
                "\n" +
                "==================================================\n" +
                "||          *** CONGRATULATIONS!!! ***          ||\n" +
                "==================================================\n" +
                "||                                              ||\n" +
                "||      You're account has been created         ||\n" +
                "||      and is ready to use!                    ||\n" +
                "||                                              ||\n" +
                "||      You will now be redirected to the       ||\n" +
                "||      homepage where you will be able to      ||\n" +
                "||      log in to your account.                 ||\n" +
                "||                                              ||\n" +
                "||           PRESS ENTER TO CONTINUE            ||\n" +
                "==================================================\n";
        return congratulationsMessage;
    }
}
