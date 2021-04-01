# University of Bath - Software Engineering 2
## Banking Application Project - Group 13
<ins>Group members:</ins>
* Amr Elshenawy
* Amhar Ford
* Cliff Dufresne
* John Glaister
* Rebecca Docherty

### **Overview**
This is a Java server-client architecture project run over the CLI that aims to replicate and develop modern banking features and options in a fast-paced Agile environment.

The project runs over a period of 6 weeks with team members changing roles between Scrum Master, Product Owner and Team Developer. There is no dedicated tester as every developer acts as their own tester for the features they implement.

### **Details**
Upon initiating the application, the user has an option to either login into an existing account or sign up for a new account.
1. Logging in:
   * To login in, a user would have to enter their registered name (case-insensitive) and their password (case-sensitive).
2. Signing up:
   * To sign up, a user would have to enter information for account creation. This includes their name, address, account type and password. There are set minimum criterias that your password will have to meet to be accepted. An error message will pop up indicating that the password is not sufficient.
  
There are several account types that a customer may choose from, those are Checking (this is the default account type), Savings and Moneymarket. If no account type is set during registeration or adding a new account, the default is applied. In addition, a customer may only own up to a maximum of three banking accounts. Customers have the option to move money across their accounts and pay other registered customers. In addition, they are also to take out or provide Microloans.

Bank staff members have the ability to delete customers from the bank's database, delete a specific account for a customer and generate reports and transactions history. 

### **Technicalities**
Depending on the IDE used, the application could be started from the run/debug options within the IDE. Please ensure that the server is initiated before the client. If the application is being run from the CLI (command line interface), enter the following:
1. `javac newbank/server/*.java`
2. `java newbank.server.NewBankServer`
3. `javac newbank/client/*.java`
4. `java newbank.client.ExampleClient`

There are a few main commands available in order to interact with the bank, those are:
* `NEWACCOUNT <openingbalance> <account type>`
  * NEWACCOUNT 2500 moneymarket - Will create a new Moneymarket account with 2500 balance.
* `SHOWMYACCOUNTS`
  * Will display all accounts and their information for the customer.
* `MOVE <amount> <from> <to>`
  * MOVE 5000 checking savings - Will move 5000 from Checkings account to Savings account.
* `PAY <amount> <from account type> <to customer name>`
  * PAY 5500 checking john - Will pay 5500 deducted from Checkings paid to John's default account which is always Checkings.
* `DELETE <customer ID>`
  * DELETE 489418943 - Will delete all records pertinent to the customer with ID# 489418943. Accessible by staff members only.
* `DELETE <customer ID> <account type>`
  * DELETE 489418943 moneymarket - Will delete Moneymarket account for customer ID# 489418943. Accessible by staff members only.
* `AUDITREPORT`
  * Generates a report of all banking activity. Accessible by staff members only.
* `HELP`
  * Displays all of the available commands.
* `HELP <command name>`
  * Displays information about the specified command.
* `CONFIRM`
  * Will save and confirm all session actions into the database.

### **Design**
To facilitate the server-client architecture, two main objects shall exist. A server object from NewBankServer.java and a client object from ExampleClient.java. A bank object is then created from NewBank.java where all of the main actions of banking would occur. Within the bank object, an intermediate Hashmap data structure is used to store Customer objects. Each Customer object would contain an Account object. These Account objects are to be stored in a List in the Customer object and would refer to each account type.

In order to save and store actions and transactions occuring over a session's period, a database in the form CSV text file was created to store the relevant information for following sessions. Since information stored in the Hashmap is volatile after a session's termination, a DatabaseHandler was created to read and write to/from the Hashmap. When a user enters the command `CONFIRM`, the DatabaseHandler is triggered to read all of the updated information in the Hashmap and save it in the text file. Furthermore, as soon as the application is started, the DatabaseHandler is called to populate the Hashmap with all the information store in the database. The Hashmap acts as an intermediate link between a live session and the permanent database. The following diagram demonstrates the link between them.

![Relationship between a live session, Hashmap and the Database](Images/ProcessRequest_Hashmap_Database%20Interaction.png)




