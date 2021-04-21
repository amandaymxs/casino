package Accounting;

import java.util.ArrayList;
import Casino.Logger;

import java.text.DecimalFormat;

public class Account {
	private Logger logger = new Logger(false);
	DecimalFormat df = new DecimalFormat("#.00");
	ArrayList<Transaction> transactions = new ArrayList<Transaction>();
	String action;

	private double deposit = 0; // last deposit,
	private double withdrawal = 0; // last withdrawal played
	private double balance = 0;

	public Account(String newAccount) {
		transactions.add(new Transaction(newAccount, 0, 0));
	}

	public Account(double amount) {
		loadAccount(amount);
	}

	public void loadAccount(double deposit) {
		if (deposit < 40 || deposit > 200) {
			System.err.println("Error A1001: Buy-in amount $" + deposit
					+ "is exceeds range allowed. Please enter a buy in withdrawalween $40 and $200");
			throw new IllegalArgumentException();
		} else {
			this.deposit = deposit; // saves new buy-in replaces last buy-in
			logger.log("Success! 20001A: Deposit of " + deposit + " has been added to the account!");
			setBalance(this.deposit);
		}
	}

	public double getLastLoad() {
		return this.deposit; // returns the last buy-in player made
	}

	public void setWithdrawal(double withdrawal) {
		if (withdrawal > 4.00 * 4) { // maximum of three raises per round where $4 is the big blind already in the
								// pot therefore $4 + ($4 * 3 raises)
			System.err.println(
					"Error A1002: Withdrawal amount of $" + withdrawal + " invalid, withdrawal must be in increments of $8 (big blind)");
			throw new IllegalArgumentException();
		} else if (withdrawal > this.balance) {
			System.err.println("Error A1003: Insufficient funds.");
			throw new IllegalArgumentException();
		} else {
			this.withdrawal = withdrawal; // saves new withdrawal replaces last withdrawal
			logger.log("Success! 20002A: Withdrawal of " + withdrawal + " has been deducted from the account!");
			setBalance(-this.withdrawal);
		}
	}

	public double getWithdrawal() {
		return this.withdrawal; // returns the last withdrawal player made
	}

	private void setBalance(double transaction) {
		this.balance += transaction;
		String transactionString = df.format(Math.abs(transaction));
		if (transaction > 0.00) { // transaction: buy-in
			this.action = "Buy-in";
		} else { // transaction: withdrawal
			this.action = "Withdrawal of $" + transactionString;

		}
		this.logger.log("Sucess 20003A: Balance has been updated. $" + transaction + " was added to the balance.");
		transactions.add(new Transaction(this.action, transaction, this.balance));
	}

	public double getBalance() {
		return this.balance;
	}

	public String toString() {
		// TO-DO: BUILD ACCOUNT STRING!!!//
		return "Hello World";
	}
}
