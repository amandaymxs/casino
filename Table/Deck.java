package Table;

import java.util.ArrayList;

import Casino.Logger;

public class Deck {
	Logger logger = new Logger(false);
	private enum suit { S, H, D, C };
	private static String[] rank = { "A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K" };
	private static ArrayList<String> deck = new ArrayList<String>(); // deck of cards

	public Deck() {
		resetDeck();
	}

	public void resetDeck() {
		// is arraylist is empty
		if (deck.size() > 0) {
			clearDeck();
		}
		// fill arraylist
		for (int i = 0; i < suit.values().length; i++) {
			for (int j = 0; j < rank.length; j++) {
				deck.add(rank[j]+suit.values()[i]);
			}
		}
		this.logger.log("Sucess 20001D: Deck has been successfully reset!");
	}
	
	public void clearDeck() {
			for (int i = deck.size() - 1; i > 0; i--) {
				deck.remove(i);
			}
			this.logger.log("Sucess 20002D: Deck has been cleared!");
	}

	public String deal() {
//		this.logger.log("Bbefore: " + deck);
		int card = (int) (Math.random() * deck.size());
//		this.logger.log("Success 20003D: Card number " + card + " was dealt");
//		this.logger.log("After: " + deck);
		return deck.remove(card);
	}
	
	public String toString() {
		return deck.toString();
	}
}
