import java.io.*;
import java.util.Arrays;

/*
 * Negative numbers in an array signify an account does not exist
 * TODO
 */
public class BackEnd {
	static BufferedReader reader;
	static BufferedWriter bw;
	// Two arrays, one for account balance, and one for account name. The index of the array is the account number.
	private static int[] accountBalance = new int[99999999];
	private static String[] accountNameList = new String[99999999];
	private static String mtsfName;
	private static String oldMaster;
	private static String newMaster;
	private static String newValAccounts;
	
	public static void main(String[] args) {
		Arrays.fill(accountBalance, -1);
		// Update our global variables
		mtsfName = args[0];
		oldMaster = args[1];
		newMaster = args[2];
		newValAccounts = args[3];
		createText(newMaster);
		createText(newValAccounts);
		writeToFile("00000000",newValAccounts); // Write the top part has to be 8 zeros
		readMAFLine(oldMaster); // Read in the old master file to update our arrays
		readMTSF(mtsfName); // Read in the merged transaction file
		newMAF(); // Generate our new master accounts file
		currentAccounts(); // Generates our new valid accounts file
	} // End main
	
	// Creates an empty text file 
		public static void createText(String fileName) {
			{
				try {
					File file = new File(fileName);
					if (file.createNewFile()) {
						// System.out.println("File is created!");
					} else {
						// System.out.println("File already exists.");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} // End createText
	
	/**
	 * Goes through the master accounts file, line by line
	 * @param fileName
	 */
	public static void readMAFLine(String fileName) {
		BufferedReader br = null;
		try {
			String currentLine;
			br = new BufferedReader(new FileReader(fileName)); // Imports
																// accounts file
			while ((currentLine = br.readLine()) != null) { // takes the current
					mafToArray(currentLine);
			} // end while
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} // close it
	} // End readMAFLine
	
	/** 
	 * Gives both of our arrays their values from reading in the master accounts file, line by line.
	 * This should be called by the method that cycles through the master accounts file.
	 * @param line
	 */
	public static void mafToArray(String line){
		String[] tokens = line.split(" ");
		// Set the account balance 
		accountBalance[(Integer.parseInt(tokens[0]))] = Integer.parseInt(tokens[1]);
		// Set the name to the same index
		accountNameList[Integer.parseInt(tokens[0])] = tokens[2];
	} // End mafToArray
	
	/**
	 * Read in merged Transaction summary file line by line
	 * Moves control to the redirect function after reading in a single line
	 */
	public static void readMTSF(String fileName){
		BufferedReader br = null;
		try {
			String currentLine;
			br = new BufferedReader(new FileReader(fileName)); // Imports
																// MTSF file
			while ((currentLine = br.readLine()) != null) { // takes the current
					redirect(currentLine); // Bring it to the other function that is our road map
			} // end while
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} // close it
	} // End reatMTSF
	
	/**
	 * Redirects based on the first two accounts read 
	 * Passes in a substring that moves to the number
	 * @param transaction
	 */
	public static void redirect(String transaction) {
		if (transaction.substring(0, 1).equals("DE")) {
			deposit(transaction.substring(3), 0); // begin from the number not
													// the space
		} else if (transaction.substring(0, 1).equals("WD")) {
			withdraw(transaction.substring(3));
		} else if (transaction.substring(0, 1).equals("DL")) {
			delete(transaction.substring(3));
		} else if (transaction.substring(0, 1).equals("TR")) {
			transfer(transaction.substring(3));
		} else if (transaction.substring(0, 1).equals("CR")) {
			create(transaction.substring(3));
		}
	} // End redirect

	/**
	 * Handles deposit
	 * Sets the values in our global arrays so that they can be updated at the end of the session
	 * @param deposit
	 * @param whichAccount
	 * @return
	 */
	public static boolean deposit(String deposit, int whichAccount) {
		// Turns values from TSF line into integers
		int tsfArray[] = str2Data(deposit);
		// Check to see if the deposit would go over the limit
		if (tsfArray[whichAccount] + tsfArray[2] > 99999999) {
			System.out.println("Error, deposit exceeds account balance limit (999999.99)");
			return false;
		} else { // Deposit the money
			accountBalance[tsfArray[whichAccount]] = accountBalance[tsfArray[whichAccount]] + tsfArray[2];
			return true;
		}
	} // End deposit

	/**
	 * Handles withdraw, looks at second account number
	 * Sets the values in our global arrays so that they can be updated at the end of the session
	 * @param withdraw
	 */
	public static boolean withdraw(String withdraw) {
		int tsfArray[] = str2Data(withdraw);
		// If withdrawing the money would leave you at something less than 0
		if (tsfArray[1] - tsfArray[2] < 0) {
			System.out.println("Error, cannot withdraw more than balance " + tsfArray[0]);
			return false;
		} else { // Withdraw the money
			accountBalance[tsfArray[1]] = accountBalance[tsfArray[1]] - tsfArray[2];
			return true;
		}
	} // End withdraw

	/**
	 * Handles things for delete
	 * Sets the values in our global arrays so that they can be updated at the end of the session
	 * @param delete
	 */
	public static void delete(String delete) {
		int values[] = str2Data(delete);
		String[] tokens = delete.split(" ");
		if (tokens[3].equals(accountNameList[values[0]])){
			if (values[0] > 0) { // If the account has money it in it
				System.out.println("Error, cannot delete an account that has money in it " + values[0]);
			} else if (values[0] == -1) { // If it does not exist
				System.out.println("Error, the selected account does not exist");
			} else {
				values[0] = -1; // Delete the account
			}
		} // End good account name
		else{
			System.out.println("The names do not match");
		}
	}// End delete

	/**
	 * Handles things for transfer
	 * Sets the values in our global arrays so that they can be updated at the end of the session
	 * @param transfer
	 */
	public static void transfer(String transfer) {
		// Does the withdraw
		boolean withdrawSucc = withdraw(transfer);
		// Checks if it withdrew successfully
		if (withdrawSucc == true) {
			// Does the deposit
			boolean depositSucc = deposit(transfer, 0);
			// If it failed, then re-deposit into original account
			if (depositSucc == false) {
				deposit(transfer, 1);
			}
		}
	}// End transfer

	/**
	 * Handles things for create 
	 * Sets the values in our global arrays so that they can be updated at the end of the session
	 * Error checks for if the account already exists. 
	 * @param create
	 */
	public static void create(String create) {
		int tsfArray[] = str2Data(create);
		String[] tokens = create.split(" ");
		// Check if the account already exists
		if (accountBalance[tsfArray[0]] >= 0) {
			System.out.println("Error, account already exists");
		} else {
			// Set the account balance and the account name. 
			accountBalance[tsfArray[0]] = 0;
			accountNameList[tsfArray[0]] = tokens[3];
		}
	}// End create
	
	/** 
	 * Converts our strings into integer values in an array 
	 * Array is returned and the array is used in the transactions
	 * @param transactionLine
	 * @return
	 */
	public static int[] str2Data(String transactionLine) {
		// Make an array to store the elements
		int transactionValues[] = new int[3];
		String[] tokens = transactionLine.split(" ");
		// First account number
		transactionValues[0] = Integer.parseInt(tokens[0]);
		// Second account number
		transactionValues[1] = Integer.parseInt(tokens[1]);
		// Amount in cents
		transactionValues[2] = Integer.parseInt(tokens[2]);
		return transactionValues;
	} // End str2Data
	
	/**
	 * writeToFile is used to write to our exisiting .txt file
	 * Which file is determined by the argument fileName that is passed in
	 * @param sentence
	 * @param fileName
	 */
	public static void writeToFile(String sentence, String fileName){
		BufferedWriter bw = null;
	      try {
	         // Put new accounts
	         bw = new BufferedWriter(new FileWriter(fileName, true));
	         bw.write(sentence);
	         bw.newLine(); // newLine() used for formatting purposes, to separate each account number with a line break.
	         // bw.flush();
	      } catch (IOException e) {
	    	 e.printStackTrace();
	      } finally {            
	    	 if (bw != null) try {
	    		bw.close();
	    	 } catch (IOException e) {
	    	 }
	      }
	} // End writeToFile
	
	/** 
	 * Generates our new accounts list
	 * Goes through our array
	 * For every index that has a balance >= 0
	 * writes that index (which is equivalent to the account number) to our text file
	 */
	public static void currentAccounts(){
		for(int i=10000000; i < 99999999; i++){
			if (accountBalance[i] >=0) // ie) if there exists an account
				writeToFile(Integer.toString(i),newValAccounts);
		}
	} // End currentAccounts
	
	/**
	 * Generates our new Master accounts file 
	 * Like the currentAccounts method, goes through the entire array.
	 */
	public static void newMAF(){
		writeToFile("",newMaster);
		for(int i = 10000000; i < 99999999; i++){
			if(accountBalance[i] >=0)
				writeToFile(createMAFLine(i,accountBalance[i],accountNameList[i]),newMaster);
		} // Read array to make our new master account file 
	} // End newMAF
	
	/**
	 * Generates our master account file line 
	 * To be called by the method that generates the new master accounts file
	 * @param accountN
	 * @param balance
	 * @param name
	 * @return
	 */
	public static String createMAFLine(int accountN, int balance, String name){
		String line = ("" + accountN + " " + balance + " " + name);
		return line;
	} // End createMAFLine
}// End BackEnd
