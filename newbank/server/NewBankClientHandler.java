package newbank.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Date;
import java.util.Timer;
import java.util.TimerTask;

public class NewBankClientHandler extends Thread{
	
	private NewBank bank;
	private BufferedReader in;
	private PrintWriter out;
	private Socket s;
	
	public NewBankClientHandler(Socket s) throws IOException {
		bank = NewBank.getBank();
		in = new BufferedReader(new InputStreamReader(s.getInputStream()));
		out = new PrintWriter(s.getOutputStream(), true);
		this.s = s;
	}
	
	public void run() {
		// keep getting requests from the client and processing them
		try {
			out.println(welcomeMessage());
			String[] okEntries = {"1", "2"};
			String selection;
			do {
				selection = in.readLine();
				if (!selection.equals(okEntries[0]) && !selection.equals(okEntries[1])){
					out.println("Invalid selection! Please try again.");
				}
			} while (!selection.equals(okEntries[0]) && !selection.equals(okEntries[1]));
			switch (selection){
				case "1": // Register user
					UserRegistration newUser = new UserRegistration(s, bank);
					run();
				case "2": // Log in existing user
					// ask for user name
					out.println("Enter Registered Name");
					String userName = in.readLine();
					// ask for password
					out.println("Enter Password");
					String password = in.readLine();
					out.println("Checking Details...");
					// authenticate user and get customer ID token from bank for use in subsequent requests
					CustomerID customer = bank.checkLogInDetails(userName, password);
					// if the user is authenticated then get requests from the user and process them
					if(customer != null) {
						out.println("Log In Successful. What do you want to do?");
						timedExit(1200); // app times out after 20 minutes
						if(customer.getKey().equalsIgnoreCase("staff")){
							out.println(bank.getAvailableCommands("staff"));
						}
						else{
							out.println(bank.getAvailableCommands("general"));
						}
						while(true) {
							String request = in.readLine();
							//System.out.println("Request from " + customer.getKey());
							String response = bank.processRequest(customer, request);
							out.println(response);
							timedExit(300); // app times out 2 minutes after the last command was entered
						}
					}
					else {
						out.println("Log In Failed");
						run();
					}
				default:
					out.println("Invalid selection. Try Again.");
					run();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				in.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
				Thread.currentThread().interrupt();
			}
		}
	}

	public String welcomeMessage(){
		String welcomeMessage =
				"\n" +
				"==================================================\n" +
				"||           *** WELCOME TO NEWBANK ***         ||\n" +
				"==================================================\n" +
				"|| Please select one of the following options:  ||\n" +
				"||      1. SIGN UP                              ||\n" +
				"||      2. LOG IN                               ||\n" +
				"||  (enter the number corresponding to your     ||\n" +
				"||   choice and press enter)                    ||\n" +
				"==================================================\n" +
				"\nSelection:";
		return welcomeMessage;
	}

	private void timedExit(int seconds){
		Timer timer = new Timer();
		TimerTask exitApp = new TimerTask() {
			@Override
			public void run() {
				System.out.println("You have been logged out due to inactivity");
				System.exit(0);
			}
		};
		timer.schedule(exitApp, new Date(System.currentTimeMillis()+seconds*1000));
	}
}
