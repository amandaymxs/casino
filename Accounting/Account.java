package Accounting;

import java.util.ArrayList;
import Casino.Logger;

import java.text.DecimalFormat;

public class Account {
	private Logger logger = new Logger(false);

	DecimalFormat df = new DecimalFormat("#.00");
	ArrayList<Transaction> transactions = new ArrayList<Transaction>();
	String action;

	private double buyIn = 0; // last buyIn,
	private double bet = 0; // last bet played
	private double balance = 0;

	public Account(String newAccount) {
		transactions.add(new Transaction(newAccount, 0, 0));
	}

	public Account(double amount) {
		loadAccount(amount);
	}

	public void loadAccount(double buyIn) {
		if (buyIn < 40 || buyIn > 200) {
			System.err.println("Error A1001: Buy-in amount $" + buyIn
					+ "is exceeds range allowed. Please enter a buy in between $40 and $200");
			throw new IllegalArgumentException();
		} else {
			this.buyIn = buyIn; // saves new buy-in replaces last buy-in
			logger.log("Success! 20001A: Deposit of " + buyIn + " has been added to the account!");
			setBalance(this.buyIn);
		}
	}

	public double getLastLoad() {
		return this.buyIn; // returns the last buy-in player made
	}

	public void setBet(double bet) {
		if (bet > 4.00 * 4) { // maximum of three raises per round where $4 is the big blind already in the
								// pot therefore $4 + ($4 * 3 raises)
			System.err.println(
					"Error A1002: Bet amount of $" + bet + " invalid, bet must be in increments of $8 (big blind)");
			throw new IllegalArgumentException();
		} else if (bet > this.balance) {
			System.err.println("Error A1003: Insufficient funds.");
			throw new IllegalArgumentException();
		} else {
			this.bet = bet; // saves new bet replaces last bet
			logger.log("Success! 20002A: Withdrawal of " + bet + " has been deducted from the account!");
			setBalance(-this.bet);
		}
	}

	public double getBet() {
		return this.bet; // returns the last bet player made
	}

	private void setBalance(double transaction) {
		this.balance += transaction;
		String transactionString = df.format(Math.abs(transaction));
		if (transaction > 0.00) { // transaction: buy-in
			this.action = "Buy-in";
		} else { // transaction: bet
			this.action = "Bet of $" + transactionString;

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
