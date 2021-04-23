package Accounting;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import Casino.Logger;

public class Transaction {
	private Logger logger = new Logger(false);
	LocalDateTime date = LocalDateTime.now();;
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	
	String firstName, lastName, action, formatDateTime;
	double change, balance;
	int counter;


	public Transaction(String action, double change, double balance) {
		setTransaction(action, change, balance);
	}

	public Transaction(String action, int counter, double change, double balance) {
		setTransaction(action, counter, change, balance);
	}

	public Transaction(String firstName, String lastName, String action, double change, double balance) {
		setTransaction(firstName, lastName, action, change, balance);
	}

	public void setTransaction(String action, double change, double balance) {
		formatDateTime = date.format(formatter); 
		this.action = action;
		this.change = change;
		this.balance = balance;
		this.logger.logFormat(String.format("%25s %-20s %7s %9s \n", "Date", "Description", "Amount", "Balance"));
		this.logger.logFormat(String.format("%25s %-20s %7.2f %9.2f \n", formatDateTime, action, change, balance));
	}

	public void setTransaction(String action, int counter, double change, double balance) {
		formatDateTime = date.format(formatter); 
		this.action = action;
		this.counter = counter;	//what round the game is on
		this.change = change;
		this.balance = balance;
		this.logger.logFormat(String.format("%25s %-20s %5s %7s %9s \n", "Date", "Description", "Round", "Amount", "Balance"));
		this.logger.logFormat(String.format("%25s %-20s %5d %7.2f %9.2f \n", formatDateTime, action, counter, change, balance));
	}

	public void setTransaction(String firstName, String lastName, String action, double change, double balance) {
		formatDateTime = date.format(formatter); 
		this.firstName = firstName;
		this.lastName = lastName;
		this.action = action;
		this.change = change;
		this.balance = balance;
		this.logger.logFormat(String.format("%25s %-15s %-15s %-20s %7s %7s \n", "Date", "First Name", "Last Name", "Description", "Amount", "Balance"));
		this.logger.logFormat(String.format("%25s %-15s %-15s %-20s %7.2f %7.2f \n", formatDateTime, firstName, lastName, action, change, balance));
	}

	public String getTransaction() {
		return "Hello World from Transactions";
	}

	public String toString() {
		return "Hello World from toString Transactions";
	}
}
