package Casino;

import Accounting.Account;
import Game.Hand;

public class Player {
		Logger logger = new Logger(false);
		public Hand hand = new Hand();
		public Account account;
		
		private String firstName,lastName;
		boolean status = false; //in game?
		
		public Player(String firstName, String lastName) {
			setFirstName(firstName);
			setLastName(lastName);
			account = new Account("New Account");
		}
				
		public void setFirstName(String firstName) {
			if(firstName.length()<2) {
				System.err.println("Error 1001I: First name, " + firstName + "is too short. Try again");
				throw new IllegalArgumentException();
			}else {
				this.firstName = firstName.substring(0,1).toUpperCase() + firstName.substring(1);
			}
		}

		public String getFirstName() {
			return this.firstName;
		}

		public void setLastName(String lastName) {
			this.lastName = lastName.substring(0,1).toUpperCase() + lastName.substring(1);
		}
		
		public String getLastName() {
			return this.lastName;
		}
		
		public void setStatus() {
			this.status = !status;
			this.logger.log("Success! Player's status has been updated to " + this.status);
		}
		
		public boolean getStatus() {
			return this.status;
		}
		
		public String toString() {
			return (this.firstName + " " + this.lastName);
		}
}