package Game;

import java.util.Arrays;

import Casino.Logger;

public class Hand {

	private Logger logger = new Logger(false);
	private String[] cards;
	private int cardCounter;

	public Hand() {
		cards = new String[2];
		cardCounter = 0;
	}

	public Hand(String card) {
		hasTwoCards();
		addCard(card);
	}

	public void addCard(String card) {
		if (hasTwoCards()) {
			System.out.println("Error 1001C: Player is already holding two cards.");
			throw new IllegalArgumentException();
		} else {
			cards[cardCounter] = card;
			cardCounter++;
			this.logger.log("Success! New card " + card + " was added to player's hand");
		}
	}
	
	public boolean hasTwoCards() {
		if (cardCounter == 2) {
			return true;
		}
		return false;
	}

	public void clearHand() {
		for (String card : this.cards) {
			card = null;
		}
		cardCounter = 0;
	}

	public String toString() {
		return Arrays.toString(cards);
	}
}
