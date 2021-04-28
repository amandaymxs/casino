package Game;

import java.util.Arrays;
import java.util.ArrayList;

import Casino.Logger;

public class Hand {

	private Logger logger = new Logger(false);
	private ArrayList<String[]> cards;
	private int cardCounter;

	public Hand() {
		cards = new ArrayList<String[]>(2);
		cardCounter = 0;
	}

	public Hand(String[] card) {
		hasTwoCards();
		addCard(card);
	}

	public void addCard(String[] card) {
		if (hasTwoCards()) {
			System.out.println("Error 1001C: Player is already holding two cards.");
			throw new IllegalArgumentException();
		} else {
			cards.set(cardCounter, card);
			cardCounter++;
			this.logger.log("Success! New card " + card.toString() + " was added to player's hand");
		}
	}

	public String getHand() {
		ArrayList<String[]> copyCards = new ArrayList<String[]>(cards.size());
		for(String[] element : cards) {
			copyCards.add(element);
		}
//		return copySevenCard.toArray(); //return must be set to Object[]
		return Arrays.deepToString(copyCards.toArray());
	}

	public boolean hasTwoCards() {
		if (cardCounter == 2) {
			return true;
		}
		return false;
	}

	public void clearHand() {
		cards.clear();
		cardCounter = 0;
	}

	public String toString() {
		return Arrays.deepToString(cards.toArray());
	}
}
