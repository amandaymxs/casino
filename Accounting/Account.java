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
			this.deposit = deposit; // saves new buy-in replaces last buy-in
			logger.log("Success! 20001A: Deposit of " + df.format(deposit) + " has been added to the account!");
			setBalance(this.deposit);
	}

	public double getLastLoad() {
		return this.deposit; // returns the last buy-in player made
	}

	public void withdrawal(double withdrawal) {
if (withdrawal > this.balance) {
			System.err.println("Error A1003: Insufficient funds.");
			throw new IllegalArgumentException();
		} else {
			this.withdrawal = withdrawal; // saves new withdrawal replaces last withdrawal
			logger.log("Success! 20002A: Withdrawal of " + df.format(withdrawal) + " has been deducted from the account!");
			setBalance(-this.withdrawal);
		}
	}

	public double getWithdrawal() {
		return this.withdrawal; // returns the last withdrawal player made
	}

	private void setBalance(double transaction) {
		this.balance += transaction;
		if (transaction > 0.00) { // transaction: buy-in
			this.action = "Buy-in";
		} else { // transaction: withdrawal
			this.action = "Withdrawal of $" + df.format(Math.abs(transaction));
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
