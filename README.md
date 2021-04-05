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

The project runs over a period of 6 weeks with team members changing roles between Scrum Master, Product Owner and Team Developer. There is no dedicated tester as every developer acts as their own tester for the features they implement. To faciliate an effective and practical Agile workflow, two meetings were held on a weekly basis to address different topics during the sprint. Meeting one would focus on the tickets & features to be worked on for the current/upcoming sprint and setting goals and priorities. In addition, we would discuss sprint review and sprint retrospective. Meeting two would be mid-sprint which focuses mainly on progress by the development team, any blockers or issues slowing down implementations and what could be done to resolve them.

We had a dedicated collaboration server which we would reguarlly on a daily basis to discuss different matters over text. This is also where our virtual over-text stand-ups would happen and dicuss different coding challenges and design questions that are being working on during the sprint.

### **Details**
Upon initiating the application, the user has an option to either login into an existing account or sign up for a new account.
1. Logging in:
   * To login in, a user would have to enter their registered name (case-insensitive) and their password (case-sensitive).
2. Signing up:
   * To sign up, a user would have to enter information for account creation. This includes their name, address, account type and password. There are set minimum criterias that your password will have to meet to be accepted. An error message will pop up indicating that the password is not sufficient.
  
There are several account types that a customer may choose from, those are Checking (this is the default account type), Savings and Moneymarket. If no account type is set during registeration or adding a new account, the default is applied. In addition, a customer may only own up to a maximum of three banking accounts. Customers have the option to move money across their accounts and pay other registered customers. All customers have the ability to withdraw and deposit into their chosen account. In addition, they are also able to take out or provide Microloans to other registered members.

Bank staff members have the ability to delete customers from the bank's database, delete a specific account for a customer and generate reports and transactions history. 

### **Technicalities**
Depending on the IDE used, the application could be started from the run/debug options within the IDE. Please ensure that the server is initiated before the client. If the application is being run from the CLI (command line interface), enter the following:
1. `javac newbank/server/*.java`
2. `java newbank.server.NewBankServer`
3. `javac newbank/client/*.java`
4. `java newbank.client.ExampleClient`

There are a few main commands available in order to interact with the bank, those are:
* `NEWACCOUNT <account type>`
  * NEWACCOUNT checking - Will create a new Checking account with 0 balance.
* `SHOWMYACCOUNTS`
  * Will display all accounts and their information for the customer.
* `MOVE <amount> <from> <to>`
  * MOVE 5000 checking savings - Will move 5000 from Checkings account to Savings account.
* `DEPOSIT <amount> <to account type>`
  * DEPOSIT 1000 moneymarket - Will deposit 1000 into Moneymarket account.
* `WITHDRAW <amount> <from account type>`
  * WITHDRAW 500 savings - Will withdraw 500 from Savings account.
* `SHOWTRANSACTIONS`
  * SHOWTRANSACTIONS - Will display all incoming and outgoing transactions from this account.
* `PAY <amount> <from account type> <to customer name>`
  * PAY 5500 checking john - Will pay 5500 deducted from Checkings paid to John's default account which is always Checkings.
* `MICROLOAN <amount> <from account type> <to customer name>`
  * MICROLOAN 5500 checking john - Will send 1000 deducted from Checking to John's default account which is always Checkings.
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

The implementation of Microloans is based on a class that inherits the class Transactions. Transactions & TransactionsHandler classes process and store details about a transaction. These include transaction time, ID, sender, receiver, message and message. Microloans inherits Transactions and implements additional attributes inclduing interest rate, instalments, repayment deadline and maximum permitted loan amount. Various rules and restrictions are set in place to allow specfic customers to offer Microloans based on a minimum sit criteria. For instance, a minimum number of deposits over the account's history and funds available.

The calculator of loan repayments with interest rate is based on [this source.](https://www.kasasa.com/blog/how-to-calculate-loan-payments-in-3-easy-steps)

### **Future features TODO - beyond the project timeline**
Since our project had a duration of six weeks, we had a number of tickets that we as a team thought would be useful to implement had our project gone over the six-week limit. Some of those features were:
1. Re-design the server/client implementation to handle multiple concurrent customers.
1. Securly protect the Database to prevent multiple customers in different sessions from submitting multiple requests to the same data point.
1. Upgrade the database infrastructure from a CSV file to a dedicated SQL-based data storage.
1. Introduce and address security and data concerns.
1. Implement two-step verification for customers.
1. Implement password recovery.
1. Address and adhere to data protection laws and GDPR.
1. Implement standing orders.
1. Implement direct debits.
1. Improve system performance & reliability.





