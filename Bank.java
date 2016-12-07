import java.io.*;
import java.util.Arrays;
import java.util.regex.Pattern;

/*
 * CISC 327 Front End SimBank
 * Authors: Yilun Xiao, Michael Sakamoto, Jose Manuel Matsuda
 * We verify that this is of our own work.
 * 
 */

/*
 * CHANGELOG FROM ASSIGNMENT 2 VERSION
 * Now takes in arguments from command line for the accounts file name and the transaction summary file
 * Empty text files are now created for terminalOutput.txt and transActSummaryFile.txt
 * Append successful and unsuccessful commands to the terminalOutputfile
 * UNcommented transaction file methods
 * Formatted transaction summary file to correct verions (withdraw and create)
 * TODO
 */


/*
 * The Bank class governs all functionality in the Front End of SimBank. It contains the methods and functions that execute and run the banking system.
 */
	public class Bank {
		static BufferedReader reader;
		static BufferedWriter bw;
		private static boolean systemOn = true; // Allows consecutive bank sessions (allows login after logout instead of termination).
		private static boolean loggedIn = false; // Boolean Flag to prevent any commands before login.
		private static boolean agent = false; // Enables agent permissions.
		private static int[] newAccounts = new int[99999999];
		private static String accounts;
		private static String transactName;
		//Purpose is to call the starter function
		public static void main(String[] args){
			reader = new BufferedReader(new InputStreamReader(System.in));
			
			accounts = args[0];
			transactName = args[1];	
			starter(accounts);
		}
		
		//Creates empty text files for the transaction summary file, and for the errors output
		public static void createText(String fileName){
			{
		    	try {

			      File file = new File(fileName);

			      if (file.createNewFile()){
			        //System.out.println("File is created!");
			      }else{
			        //System.out.println("File already exists.");
			      }

		    	} catch (IOException e) {
			      e.printStackTrace();
			}
		}
	} //end createText
		
		/**
		 * Will take all the items that are printed out, good or bad, and appends it to a (pre-existing)text file
		 * 
		**/
		public static void writeToFile(String sentence){
			BufferedWriter bw = null;
		      try {
		         // Set Buffered Writer to Append New Accounts to accounts.txt
		         bw = new BufferedWriter(new FileWriter("terminalOutput.txt", true));
		         bw.write(sentence);
		         bw.newLine(); // newLine() used for formatting purposes, to separate each account number with a line break.
		         // bw.flush();
		      } catch (IOException e) {
		    	 e.printStackTrace();
		      } finally {                       // terminalOutput.txt
		    	 if (bw != null) try {
		    		bw.close();
		    	 } catch (IOException e) {
		    	 }
		      }
		}
		//Connects all the functions together
		public static void starter(String account){
			System.out.println("Bank Systems Online");
			//Assumes that it is in the workspace folder, no deeper
			File f = new File(transactName);
			//Check if "transactName".txt is already made
			if (!f.exists())
				createText(transactName);
			createText("terminalOutput.txt");
			while(systemOn == true){ // Banking session is active.
				System.out.println("Enter a command"); // Prompt user input.
				String commands = userInput();
				if(commands.equalsIgnoreCase("login"))
					login();
				else if(commands.equalsIgnoreCase("logout"))
					logout();
				else if(commands.equalsIgnoreCase("create") && loggedIn == true)
					create();
				else if(commands.equalsIgnoreCase("withdraw") && loggedIn == true)
					withdraw();
				else if(commands.equalsIgnoreCase("transfer") && loggedIn == true)
					transfer();
				else if (commands.equalsIgnoreCase("deposit") && loggedIn == true)
					deposit();
				else if (commands.equalsIgnoreCase("delete") && loggedIn == true)
					delete();
				else if(commands.equalsIgnoreCase("Exit"))
					systemOn = false;
				else{
					System.out.println("Must be logged in");
					writeToFile("Must be logged in");
				}
			} //end while 
		} //end starter()
		
		
		// The following functions are helpers that will be used multiple times within our main 'user command' methods.
		
		// Reads a line of input and returns it as a string
		public static String userInput(){
			String input = null;
				try {
					input = reader.readLine();
					if (input == null){
						System.exit(0);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			return input;
		} //end userInput()
		
		// Reads a line of input and returns it as a integer
		public static int intInput(){
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			// TRACER
			try {
				System.out.println(reader.readLine());
				writeToFile(reader.readLine());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//
			int input = 0;
				try {
					input = Integer.parseInt(reader.readLine());
				} 
				// Checks for Integer data type
				catch (NumberFormatException ex){
					System.out.println("Invalid non-integer input");
					writeToFile("Invalid non-integer input");
				}
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return input;
		} //end intInput()

		// Reads accounts.txt file line-by-line to compare and check for existing account numbers.
		public static boolean readAccounts(int accountNumber){
			BufferedReader br = null;
			try {
				String existingAccount;
				br = new BufferedReader(new FileReader(accounts)); // Imports accounts file
				while ((existingAccount = br.readLine()) != null) {
					int existingAcc = Integer.parseInt(existingAccount); // Changes String into Integer for arithmetic comparison to the user input account number.
					if (accountComparison(existingAcc, accountNumber)) { // Calls the accountComparison function
						return true; // Breaks from readAccounts() method and returns flag (account number exists within accounts.txt)
					}
					else
						continue; // Current account number comparison complete, reads new line in accounts.txt
				} //end while
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (br != null)br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return false; // Reader did not find the user input account number within accounts.txt.		
		} // end readAccounts()
		
		// Makes an arithmetic comparison between accounts.txt existing account numbers and user input account number.
		// Returns true if the user input account exists within accounts.txt
		public static boolean accountComparison(int existingAccount, int accountNumber) {
			if (existingAccount == accountNumber)
				return true;
			else
				return false;
		} //end accountComparison
		
		// Appends a new account to the existing accounts.txt
		public static void appendAccount(int accountNumber) {
			BufferedWriter bw = null;
		      try {
		         // Set Buffered Writer to Append New Accounts to accounts.txt
		         bw = new BufferedWriter(new FileWriter(accounts, true));
		         bw.write(Integer.toString(accountNumber));
		         bw.newLine(); // newLine() used for formatting purposes, to separate each account number with a line break.
		         // bw.flush();
		      } catch (IOException e) {
		    	 e.printStackTrace();
		      } finally {                       // Closes accounts.txt
		    	 if (bw != null) try {
		    		bw.close();
		    	 } catch (IOException e) {
		    	 }
		      } // end Try Catch Finally block
		}//end appendAccount
		
		// Ensures that the account name is in the correct format.
		// 0 = valid name, 1 = invalid name length, 2 = starts/ends with space, 3 = contains special character
		public static int checkName(String name){ 
			int nameLength = name.length();
			Pattern p = Pattern.compile("[^a-zA-Z0-9' ']"); // Creates comparison pattern to check that all characters are alphanumeric.
			if (nameLength < 3 || nameLength > 30){ // Checks for correct name length.
				System.out.println("Error, invalid name length.");
				writeToFile("Error, invalid name length");
				return 1;
			}
			else if(name.charAt(0) == ' ' || name.charAt(nameLength - 1) == ' '){ // Checks for names containing spaces at the start or end of the string.
				System.out.println("Error, cannot start or end with a space character.");
				writeToFile("Error, cannot start or end with a space character");
				return 2;
			}
			else if(p.matcher(name).find()){ // Checks for alphanumeric characters.
				System.out.println("Error, cannot contain special character(s)");
				writeToFile("Error, cannot contain special character(s)");
				return 3;
			}
			else
				return 0; // Returns 0 for a valid given name.
		} // end checkName
		
		// This method tests the validity of an account number.
		// Returns 0 for valid, 1 for invalid
		// Checks that account number begins with a non-zero and contains exactly 8 digits.
		public static int checkAccount(int accountNumber){
			if (accountNumber < 10000000 || accountNumber > 99999999)
				return 1;
			return 0;
		} //end checkAccount(int accountNumber)
		
		
		/*
		 * The following methods are the direct functions of SimBank. 
		 * These functions use our helper functions in order to complete requests according to user input.
		 */
		
		
		// Logs into the SimBank system and sets the login boolean to true (to allow usage of other functions), also assigns atm or agent mode to the session and provides errors for invalid commands.
		public static void login(){ 
			try {
				bw = new BufferedWriter(new FileWriter(transactName, true));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(loggedIn == true){ // Prevents logging in while already logged in.
				System.out.println("Error, already logged in");
				writeToFile("Error, already logged in");
			}
			else if(loggedIn == false){	
				System.out.println("Logged in");
				writeToFile("Logged in");
				System.out.println("Enter atm or agent");
				String agentorAtm = userInput(); // Accepts either 'atm' or 'agent' user input to determine session permission status.
				if(agentorAtm.equalsIgnoreCase("agent")){
					System.out.println("Accessed agent");
					writeToFile("Accessed agent");
					agent = true; // Assigns agent permissions to the session.
					loggedIn = true;
				}
				else if(agentorAtm.equalsIgnoreCase("atm")){
					System.out.println("Accessed atm"); // Assigns ATM permissions to the session.
					writeToFile("Accessed atm");
					loggedIn = true;
				}
				else{
					System.out.println("Error, expected \"atm\" or \"agent\"");		
					writeToFile("Error, expected \"atm\" or \"agent\"");
				}
			} 
		} //end login
		
		
		// Logs the user out of the session, also preventing multiple logout commands.
		public static void logout(){
			if(loggedIn == false){
				System.out.println("Error, not logged in");
				writeToFile("Error, not logged in");
			}
			else{
				Arrays.fill(newAccounts, 0, 99999999, 0); // Refreshes the list of accounts created in current session.
				System.out.println("Logout");
				writeToFile("Logout");
				loggedIn = false; // Logs user out of session.
				transactionSummaryFile("ES 00000000 00000000 000 ***"); // Prints ES Transaction Summary Line to Transactions Summary File.
				try {
					bw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} //end logout
		
		
		// Used to create a new account, catching errors such as non-agent permissions, or the account number already in existence.
		public static void create(){
			if (agent == true){ // Checks for agent permissions.
				System.out.println("Creating account, enter in an account number");
				writeToFile("Creating account, enter in an account number");
				int accountNumber = intInput(); // Calls intInput() function to obtain user integer input.
				if (checkAccount(accountNumber) == 1){ // Checks and validates account number format.
					System.out.println("Error, invalid digit size (must be 8 digits not starting with a zero)");
					writeToFile("Error, invalid digit size (must be 8 digits not starting with a zero)");
				}
				else if (readAccounts(accountNumber) == true){
					System.out.println("Error, account number already exists");
					writeToFile("Error, account number already exists");
				}
				else {
					System.out.println("Enter account name");
					writeToFile("Enter account name");
					String accountName = userInput(); // Calls userInput() - Ensures name between 3 and 30 characters
					int validation = checkName(accountName);
					if (validation == 0){ // Ensures account number is in valid format.
						appendAccount(accountNumber);
						System.out.println("Account accepted");
						writeToFile("Account accepted");
						newAccounts[accountNumber] = 1; // Used for checking new accounts
						transactionSummaryFile("CR" + " " + accountNumber + " 00000000 000 " + accountName); // Creates new transaction summary line for transaction summary file.
					}
				}
			} //end if agent
			else {// If you are not an agent:
				System.out.println("\"Create\" denied due to lack of permissions");
				writeToFile("\"Create\" denied due to lack of permissions");
			}
		} //end create 
		
		
		/* 
		 * Asks for an account number and name for deletion, catching errors such non-existent account numbers or invalid input formats.
		 * Because SimBank currently contains only front end, cannot reference back-end for account name comparison or account balance checking (to ensure account contains no money). 
		 */
		public static void delete(){
			if (agent == true){ // Delete requires agent permissions.
				System.out.println("Accessed delete, enter in account number");
				writeToFile("Accessed delete, enter in account number");
				int accntNumber = intInput(); // Obtains user input, as an integer (checking for valid integer input).
				if (readAccounts(accntNumber) == false){ // Checks to make sure account exists in accounts.txt.
					System.out.println("Account does not exist.");
					writeToFile("Account does not exist");
				}
				else if (checkAccount(accntNumber) == 1){ // Checks to make sure account number is in valid format.
					System.out.println("Error, account number is invalid.");
					writeToFile("Error, account number is invalid");
				}
				else if(newAccounts[accntNumber] ==1){
					System.out.println("Account too fresh to delete");
					writeToFile("Account too fresh to delete");
				}
				else {
					// Not checking balance yet, which is checked in the back-end.
					System.out.println("Please enter in account name:");
					writeToFile("Please enter in account name:");
					String accntName = userInput(); // Checks that account name matches user input. (Name normally stored in back-end.)
					if(checkName(accntName) == 0);
						System.out.println("Successfully deleted account " + accntNumber + ".");
						writeToFile("Successfully deleted account" + accntNumber +".");
						transactionSummaryFile("DL " + accntNumber + " 00000000 000 " + accntName); // Appends transaction line to transaction summary file.
					}
				} //end agent permissions
				else{
					System.out.println("Error, do not have permission to delete");
					writeToFile("Error, do not have permission to delete");
				}
			} //end delete method
		
		
		/* 
		 * Accessed by atm or agent, deposits a maximum of (100000) & (99999999) respectively.
		 * Catches errors if the account number is invalid format, does not exist, does not contain enough funds, or if deposit amount is a negative integer.
		 */
		public static void deposit(){
			int depositAmnt;
			System.out.println("Accessed deposit.\nPlease enter account number:");
			writeToFile("Accessed deposit.Please enter account number:");
			writeToFile("Please enter account number:");
			int accntNumber = intInput(); // Takes user input (account number) from intInput() function as integer
			if (checkAccount(accntNumber) == 1){ // Checks for valid account number format.
				System.out.println("Error, account number is invalid.");
				writeToFile("Error, account number is invalid.");
			}
			else if(newAccounts[accntNumber] == 1){
				System.out.println("Account too fresh to deposit");
				writeToFile("Account too fresh to deposit");
			}
			else if (readAccounts(accntNumber) == false) {// Checks for account in accounts.txt
				System.out.println("Account does not exist.");
				writeToFile("Account does not exist.");
			}
			else { // Account number is valid
					System.out.println("Enter in deposit amount:");
					writeToFile("Enter in deposit amount:");
					depositAmnt = intInput();
					
					// Go through account balances here: to be checked in the back-end
					// Check atm permissions 
					if (depositAmnt < 0){ 
						System.out.println("Error, cannot deposit a negative number");
						writeToFile("Error, cannot deposit a negative number");
					}
					else if (depositAmnt > 100000 && agent == false){
						System.out.println("Error, cannot deposit more than 1000.00 in atm mode.");
						writeToFile("Error, cannot deposit more than 1000.00 in atm mode.");
					}
					// Check agent permissions
					else if (depositAmnt > 99999999 && agent == true){
						System.out.println("Error, cannot deposit more than 999999.99 in agent mode.");
						writeToFile("Error, cannot deposit more than 999999.99 in agent mode.");
					}
					// Successful deposit (passes through validation filters)
					else {
						System.out.println("Succesfully deposited " + depositAmnt + ".");
						writeToFile("Succesfully deposited " + depositAmnt + ".");
						// accountsBalance[accntNumber] = accountsBalance[accntNumber] - depositAmnt;
						// To be implemented and tested in Back-End.
						String amountString = "";
						if (depositAmnt < 10){
							amountString = Integer.toString(depositAmnt);
							amountString = "00" + amountString;
						}
						else if (depositAmnt < 100){
							amountString = Integer.toString(depositAmnt);
							amountString = "0" + amountString;
						}
						else {
							amountString = Integer.toString(depositAmnt);
						}
						transactionSummaryFile("DE " + accntNumber + " 00000000 " + amountString + " ***"); // Appends transaction line to transaction summary file.
					}
				}//End big else
			} //End deposit function
		
		
		/* 
		 * Withdraw takes money out of the account, and catches errors such as a non-existing accounts or invalid format.
		*  Will also catch restrictions on the maximum withdrawal amount.
		*/
		public static void withdraw(){
			int withdrawAmnt;
			System.out.println("Accessed Withdraw.\nPlease enter account number:");
			writeToFile("Accessed Withdraw.");
			writeToFile("Please enter account number:");
			int accntNumber = intInput(); // Takes amount as an integer from user input.
			if (checkAccount(accntNumber) == 1){  // Checks to validate account number format.
				System.out.println("Error, account number is invalid.");
				writeToFile("Error, account number is invalid.");
			}
			else if(newAccounts[accntNumber] ==1){
				System.out.println("Account too fresh to withdraw");
				writeToFile("Account too fresh to withdraw");
			}
			else if(readAccounts(accntNumber) == false){ // Checks the existence of the account number in accounts.txt
				System.out.println("Account does not exist.");
				writeToFile("Account does not exist.");
			}
			else {
				System.out.println("Enter in Withdraw amount:");
				writeToFile("Enter in Withdraw amount:");
				withdrawAmnt = intInput();
				// Check current account balance (back-end)
				// if (withdrawAmnt > accountsBalance[accntNumber])
					// System.out.println("Error, not enough funds.");
				// Check atm permissions 
				if (withdrawAmnt < 0) {
					System.out.println("Error, cannot withdraw a negative number");
					writeToFile("Error, cannot withdraw a negative number");
				}
				else if (withdrawAmnt > 100000 && agent == false){
					System.out.println("Error, cannot withdraw more than 1000.00 in atm mode.");
					writeToFile("Error, cannot withdraw more than 1000.00 in atm mode.");
				}
				// Check agent permissions
				else if (withdrawAmnt > 99999999 && agent == true){
					System.out.println("Error, cannot withdraw more than 999999.99 in agent mode");
					writeToFile("Error, cannot withdraw more than 999999.99 in agent mode");
				}
				// Successful withdrawal.
				else {
					System.out.println("Succesfully withdrew " + withdrawAmnt + ".");
					writeToFile("Succesfully withdrew " + withdrawAmnt + ".");
					// accountsBalance[accntNumber] = accountsBalance[accntNumber] - withdrawAmnt;
					// Back-end account balance changes (subtracting withdrawal amount from account total balance).
					String amountString = "";
					if (withdrawAmnt < 10){
						amountString = Integer.toString(withdrawAmnt);
						amountString = "00" + amountString;
					}
					else if (withdrawAmnt < 100){
						amountString = Integer.toString(withdrawAmnt);
						amountString = "0" + amountString;
					}
					else{
						amountString = Integer.toString(withdrawAmnt);
						transactionSummaryFile("WD " + "00000000" + accntNumber + " " + amountString + " ***"); // Appends transaction line to transaction summary file.
					}
				}
			}//End big else
		}//End withdraw method
		
		
		/*
		 *  Checks validity of accounts for invalid format, existence in the accounts file, and if the accounts are the same.
		 *  Checks validity of transfer amount (data type, maximum amount based on permissions, and account balance).
		 */
		public static void transfer(){
			System.out.println("Enter in account number to transfer money from:");
			writeToFile("Enter in account number to transfer money from:");
			int accntNumber1 = intInput(); // Takes transfer withdrawal account number from user input.
			if (checkAccount(accntNumber1) == 1){ // Checks account number format.
				System.out.println("Error, account number invalid.");
				writeToFile("Error, account number invalid.");
			}
			else if(newAccounts[accntNumber1] ==1){
				System.out.println("Account too fresh to transfer");
				writeToFile("Account too fresh to transfer");
			}
			else if (readAccounts(accntNumber1) == false) {// Checks account number existence in accounts.txt.
				System.out.println("Error, account does not exist.");
				writeToFile("Error, account does not exist.");
			}
			//If transfer withdrawal account exists:
			else if (checkAccount(accntNumber1) == 0){
				System.out.println("Enter in account number to transfer money to:");
				writeToFile("Enter in account number to transfer money to:");
				int accntNumber2 = intInput(); // Takes transfer deposit account number from user input.
				if (checkAccount(accntNumber2)== 1){ // Checks account number for valid format.
					System.out.println("Error, account number invalid.");
					writeToFile("Error, account number invalid");
				}
				else if(newAccounts[accntNumber2] ==1){				
					System.out.println("Account too fresh to transfer");
					writeToFile("Account too fresh to transfer");
				}
				else if (readAccounts(accntNumber2) == false){ // Checks account number for existence in accounts.txt.
					System.out.println("Error, account does not exist.");
					writeToFile("Error, account does not exist.");
				}
				else if (accntNumber2 == accntNumber1){ // Checks to prevent transfer from and to the same account number.
					System.out.println("Error, transfer withdrawal and deposit account numbers cannot be the same.");
					writeToFile("Error, transfer withdrawal and deposit account numbers cannot be the same.");
				}
				// If transfer deposit account exists:
				else if (checkAccount(accntNumber2) == 0){
					if (agent == true) { // Checks for agent permissions to assign 999999.99 maximum transfer amount.
						System.out.println("Enter in transfer amount:");
						writeToFile("Enter in transfer amount:");
						int transferMoney = intInput();
					if (transferMoney < 0) {
						System.out.println("Error, cannot transfer a negative number");
						writeToFile("Error, cannot transfer a negative number");
					}
					else if (transferMoney > 99999999){ // Prevents breaching maximum transfer amount of agent permissions.
						System.out.println("Cannot transfer that amount of money.");
						writeToFile("Cannot transfer that amount of money.");
					}
					else {
						// Here, we would compare the total account balance to the transfer amount using the back-end master accounts file.
						System.out.println("Transfer successful!");
						writeToFile("Transfer successful!");
						String amountString = "";
						if (transferMoney < 10){
							amountString = Integer.toString(transferMoney);
							amountString = "00" + amountString;
						}
						else if (transferMoney < 100){
							amountString = Integer.toString(transferMoney);
							amountString = "0" + amountString;
						}
						else {
							amountString = Integer.toString(transferMoney);
						}
						transactionSummaryFile("TR " + accntNumber1 + " " + accntNumber2 + " " + amountString + " ***"); // Appends transaction line to transaction summary file.
					}
				}// end agent transfer
				else { // If atm permissions are assigned:
					System.out.println("Enter in transfer amount:");
					writeToFile("Enter in transfer amount:");
					int transferMoney = intInput();
					if (transferMoney < 0) {
						System.out.println("Error, cannot transfer a negative number");
						writeToFile("Error, cannot transfer a negative number");
					}
					else if (transferMoney > 10000000){
						System.out.println("Cannot transfer that amount of money with atm permissions.");
						writeToFile("Cannot transfer that amount of money with atm permissions.");
					}
					else {
						// Here, we would compare the total account balance to the transfer amount using the back-end master accounts file.
						System.out.println("Transfer successful!");
						writeToFile("Transfer successful!");
						String amountString = "";
						if (transferMoney < 10){
							amountString = Integer.toString(transferMoney);
							amountString = "00" + amountString;
						}
						else if (transferMoney < 100){
							amountString = Integer.toString(transferMoney);
							amountString = "0" + amountString;
						}
						else 
							amountString = Integer.toString(transferMoney);
						transactionSummaryFile("TR " + accntNumber1 + " " + accntNumber2 + " " + amountString + " ***"); // Appends transaction line to transaction summary file.
					}
				} // end atm transfer
			} //end else if for account number validation
		} //end deposit account number else
	}//end transfer
	
// Prints out the Transaction Summary File
		public static void transactionSummaryFile(String yourTransaction){
		      try {
		         bw.write(yourTransaction);
		         bw.newLine();
		         //bw.flush();
		      } catch (IOException e) {
		    	 e.printStackTrace();
		      } // end Try Catch Finally block 
		} // End transactionSummaryFile
} // end Bank.java