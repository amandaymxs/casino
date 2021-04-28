package Game;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import Casino.Logger;
import Table.Card;

@SuppressWarnings("serial")
public class Rank {
	Logger logger = new Logger(true);
	ArrayList<Card> sevenCard = new ArrayList<Card>(7);
	private final Map<String, Integer> prime = new LinkedHashMap<String, Integer>() {
		{
			put("2", 2);
			put("3", 3);
			put("4", 5);
			put("5", 7);
			put("6", 11);
			put("7", 13);
			put("8", 17);
			put("9", 19);
			put("10", 23);
			put("J", 29);
			put("Q", 31);
			put("K", 37);
			put("A", 41);
		}
	};
	Set<String> keys = prime.keySet();
	List<String> listKeys = new ArrayList<String>(keys);

//	void setCardSuits() {
//		for (int i = 0; i < sevenCard.size(); i++) {
//			primeSuits[i] = sevenCard.get(i)[1];
//		}
//	}

	Rank(ArrayList<Card> communityCards) {
		setSevenCard(communityCards);
	}

	void setSevenCard(ArrayList<Card> cards) {
		if (sevenCardLength() + cards.size() <= 7) {
			if (cards.size() == 5) {
				System.arraycopy(cards, 0, sevenCard, 0, cards.size());

			} else {
				System.arraycopy(cards, 0, sevenCard, 5, cards.size());
//				Arrays.sort(sevenCard);
//				setCardSuits();
			}
			logger.log("Success! 20001R: sevenCard: " + sevenCard.toString());
		} else {
			System.out.println("Error 10001R");
			logger.log("Error 10001R: sevenCard has length " + sevenCardLength() + " , and cards has length "
					+ cards.size());
		}
	}

	void getSevenCard() {
		logger.log("Seven Card: " + sevenCard.toString());
	}

	int sevenCardLength() {
		for (int i = 0; i < sevenCard.size(); i++) {
			if (sevenCard.get(i) == null) {
				return i;
			}
		}
		return 7;
	}

	void clearSevenCard() {
		sevenCard.clear();
		logger.log("Success! 20002R: SevenCard cleared: " + sevenCard.toString());
	}

	void isStraight() {
		int handTotal = 1;
		int primeTotal = 0;
		int firstRank = 0;
		for (int i = 2; i >= 0; i++) { // start from highest to lowest
			for (int j = 0; j < 5; j++) {
				handTotal *= prime.get(sevenCard.get(i+j).toString());
				System.out.println("Rank: " + sevenCard.get(i+j).toString() + ", Hand Total: " + handTotal);
				if (j == 0) {
					firstRank = prime.get(sevenCard.get(i+j).toString());
					primeTotal = firstRank;
				} else {
					firstRank++;
					primeTotal *= prime.get(listKeys.get(firstRank));
				}
				System.out.println("handTotal: " + handTotal);
			}
			if (handTotal == primeTotal) { // check if this is a straight or royal
				// keep track of i!

				if (isFlush(i)) {
					if (prime.get(sevenCard.get(i).rank()) == 10) { // royal flush	//IS THIS INT OR STRING? IF STRING CHANGE TO .equals
						logger.log("Player has a royal flush");
						// evaluate suit
					} else { // straight flush
						logger.log("Player has a straight flush with high of " + prime.get(sevenCard.get(i+5).rank()));
					}
				} else { // straight
					logger.log("Player has a straight with a high of " + prime.get(sevenCard.get(i+5).rank()));
				}

			}
			handTotal = 1;
			primeTotal = 1;
		}
	}

	private boolean isFlush(int startingCard) {
		boolean sameSuit = true;
		for (int i = startingCard; i < startingCard + 5; i++) {

		}
		return true;
	}

	public String toString() {
		return "Hello World";
	}
}
