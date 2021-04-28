package Game;

import java.util.Arrays;
import java.util.ArrayList;

import Casino.Logger;
import Table.Card;

public class Hand {

	private Logger logger = new Logger(false);
	private ArrayList<Card> cards;
	private int cardCounter;

	public Hand() {
		cards = new ArrayList<Card>(2);
		cardCounter = 0;
	}

	public Hand(Card card) {
		hasTwoCards();
		addCard(card);
	}

	public void addCard(Card card) {
		if (hasTwoCards()) {
			System.out.println("Error 1001C: Player is already holding two cards.");
			throw new IllegalArgumentException();
		} else {
			cards.set(cardCounter, card);
			cardCounter++;
			this.logger.log("Success! New card " + card.toString() + " was added to player's hand");
		}
	}

	public ArrayList<Card> getHand() {
		ArrayList<Card> copyCards = new ArrayList<Card>(cards.size());
		for(Card element : cards) {
			copyCards.add(element);
		}
//		return copySevenCard.toArray(); //return must be set to Object[]
		return copyCards;
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
