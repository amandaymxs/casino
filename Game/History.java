package Game;
import java.util.Date;
import java.util.ArrayList;

class History {

		Date timestamp;
		String firstName,lastName,action;	//action: buy in or bet
		double amount,balance;	//amount: +- // player's new balance
		
	History(String firstName, String lastName, String action, double amount, double balance) {
		this.timestamp = new Date();
		this.firstName = firstName;
		this.lastName = lastName;
		this.action = action;
		this.amount = amount;
		this.balance = balance;
//////////whenever there's a change in the balance we should create an object that gets pushed into an array to track user history////////
	}
}
	
//	public boolean setData(Transactions newTransaction) {
//		transaction.add(newTransaction);
//		return true;
//	}
