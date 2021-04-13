package newbank.client;

import newbank.server.DatabaseHandler;
import newbank.server.NewBank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Date;
import java.util.Timer;
import java.util.TimerTask;

public class ExampleClient extends Thread{
	
	private Socket server;
	private PrintWriter bankServerOut;	
	private BufferedReader userInput;
	private Thread bankServerResponseThread;
	
	public ExampleClient(String ip, int port) throws UnknownHostException, IOException {
		server = new Socket(ip,port);
		userInput = new BufferedReader(new InputStreamReader(System.in)); 
		bankServerOut = new PrintWriter(server.getOutputStream(), true); 
		
		bankServerResponseThread = new Thread() {
			private BufferedReader bankServerIn = new BufferedReader(new InputStreamReader(server.getInputStream())); 
			public void run() {
				try {
					while(true) {
						String response = bankServerIn.readLine();
						if (response != null){
							System.out.println(response);
							if(response.equals("SUCCESS. You have been logged out.")){
								System.exit(0);
							}
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}
		};
		bankServerResponseThread.start();
	}
	
	
	public void run() {
		while(true) {
			try {
				TimerTask task = newTask();
				timedExit("start", task);
				while(true) {
					String command = userInput.readLine();
					timedExit("stop", task);
					bankServerOut.println(command);
					task = newTask();
					timedExit("start", task);

				}				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void timedExit(String instruction, TimerTask task){
		Timer timer = new Timer();
		if(instruction.equals("start")){
			//System.out.println("started!");
			timer.schedule(task, new Date(System.currentTimeMillis()+30*1000));
		} else if(instruction.equals("stop")){
			//System.out.println("stopped!");
			task.cancel();
		}
	}

	private TimerTask newTask(){
		return new TimerTask() {
			@Override
			public void run() {
				//save changes to database before closing
//				DatabaseHandler save = new DatabaseHandler();
//				NewBank bank = new NewBank();
//				try {
//					save.saveSession(bank.getName2CustomersMapping());
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
				System.out.println("You have been logged out due to inactivity");
				System.exit(0);
			}
		};
	}
	
	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
		
		new ExampleClient("localhost",14002).start();
	}
}
