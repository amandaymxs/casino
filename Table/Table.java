package Table;

import java.util.ArrayList;
import Casino.Logger;
import Casino.Player;
import Game.Game;

public class Table {
	private Logger logger = new Logger(false);
	
	// Min 2 people at table, max 12 people at table
	ArrayList<Player> isSeated = new ArrayList<Player>(2); // List of Player objects
	public Game game;

	public void seat(Player player) {
		if (isSeated.contains(player)) {
			System.err.println("Error 10004T: Player " + player.getFirstName() + " is already seated at the table.");
		} else if (isSeated.size() < 12) {
			if (player.account.getBalance() >= 40.00) {
				this.isSeated.add(player);
				this.logger.log("Success! 20001T: Player " + player.getFirstName() + " has been seated at the table!");
			} else {
				System.err.println("Error 10001T: Player " + player.getFirstName()
						+ " must have a minimum balance of $40.00 to sit at table.");
			}
		} else {
			System.err.println("Error 10002T: Player count must be between 2 and 12.");
		}
	}

	public ArrayList<Player> getSeated() {
		ArrayList<Player> isSeatedCopy = new ArrayList<Player>(isSeated);
		return isSeatedCopy;
	}

	public void joinGame(ArrayList<Player> isSeated) {		//all players must join game at the same time
		if (isSeated.size() >= 2) {
			game = new Game(isSeated);
			this.logger.log("Success! 20002T: " + getSeated() + " has been seated at the table!");
		} else {
			System.err.println("Error 10003T: Player count must be between 2 and 12.");
		}
	}

	public String toString() {
		return ("There are " + isSeated.size() + " Players seated at the table.");
	}

}
