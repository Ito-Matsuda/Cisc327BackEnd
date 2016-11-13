import java.io.*;
import java.util.Arrays;

/*
 * Negative numbers in an array signify an account does not exist
 * TODO Still need to make sure that backend updates all the sthuff
 * Need to create a new Master accounts file
 * and create a new valid account list
 */
public class BackEnd {
	static BufferedReader reader;
	static BufferedWriter bw;
	private static int[] accountBalance = new int[99999999];

	public static void main(String[] args) {
		// Make it so that all arrays have a -1? does this even work
		// Should not initialize it here or else everytime we run the program it
		// becomes negative ones
		// DANGEROUS FILLING IT HERE???
		Arrays.fill(accountBalance, -1);
	}// end main

	// Creates an empty text file to be used as the new valid accounts list
	public static void createText() {
		{
			try {
				File file = new File("accounts.txt");
				if (file.createNewFile()) {
					// System.out.println("File is created!");
				} else {
					// System.out.println("File already exists.");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	} // end createText
	
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
	      } finally {                       // terminalOutput.txt
	    	 if (bw != null) try {
	    		bw.close();
	    	 } catch (IOException e) {
	    	 }
	      }
	}
	
	/** 
	 * Generates our new accounts list
	 */
	public static void currentAccounts(){ // will write to our accounts
		for(int i=10000000; i < 99999999; i++){
			if (accountBalance[i] >=0){ // ie) if there exists an account
				writeToFile(Integer.toString(i),"accounts.txt");
			}
		}
	}
	
	/**
	 * Generates our new Master accounts summary file 
	 * 
	 */
	public static void newMAF(){
		writeToFile("","masterAccountsFile.txt");
		
	}
	
	public static String createMAFLine(int accountN, int balance, String name){
		String line = ("" + accountN + " " + balance + " " + name);
		return line;
	}
	
//	public static String[] readMAFLine(String line){
//		String[] tokens = line.split(" ");
//		return tokens;
//	}
	
	public static boolean checkAccount(int accountNum){
		
		return false;
	}
	
	/**
	 * Searches for specific account number in master accounts file
	 * @param fileName
	 */
	public static boolean searchFile(String fileName, int accountNumber) {
		BufferedReader br = null;
		try {
			String currentLine;
			br = new BufferedReader(new FileReader(fileName)); // Imports
																// accounts file
			while ((currentLine = br.readLine()) != null) { // takes the current
					String[] tokens = currentLine.split(" ");
					if (Integer.parseInt(tokens[0]) == accountNumber){
						return true;
					}
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
		return false;
	} // end readMTSF

	// Redirects based on the first two accounts read
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
	} // end redirect

	/**
	 * CHECKING FOR ERRORS IS DONE ELSEWHERE !!!!
	 * 
	 * @param deposit
	 */

	// Handles things for deposits
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
	} // end deposit

	/**
	 * Handles withdraw, looks at second account number
	 * 
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
	} // end withdraw

	// Handles things for delete
	public static void delete(String delete) {
		int values[] = str2Data(delete);
		if (values[0] > 0) { // If the account has money it in it
			System.out.println("Error, cannot delete an account that has money in it " + values[0]);
		} else if (values[0] == -1) { // If it does not exist
			System.out.println("Error, the selected account does not exist");
		} else {
			values[0] = -1; // Delete the account
		}
	}// end delete

	/**
	 * Handles things for transfer
	 * 
	 * @param transfer
	 *            WHAT IF deposit or withdraw doesnt work? we need to redeposit
	 *            the money also have to implement that transfer has same
	 *            restrictions on amount per day
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
	}// end transfer

	/**
	 * Handles things for create Have to update thingamajiger
	 * 
	 * @param create
	 */
	public static void create(String create) {
		int tsfArray[] = str2Data(create);
		// Check if the account already exists
		if (accountBalance[tsfArray[0]] >= 0) {
			System.out.println("Error, account already exists");
		} else {
			accountBalance[tsfArray[0]] = 0;
		}
	}// end create

	// Converts our strings into integer values in an array
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
	}

	
}// end BackEnd
