package Game;

import java.util.Arrays;
import java.util.LinkedHashMap;
import Casino.Logger;

@SuppressWarnings("serial")
public class Rank {
	Logger logger = new Logger(true);
	String[] sevenCard = new String[7];
	private static final LinkedHashMap<String, Integer> prime=new LinkedHashMap<String,Integer>(){{put("2",2);put("3",3);put("4",5);put("5",7);put("6",11);put("7",13);put("8",17);put("9",19);put("10",23);put("J",29);put("Q",31);put("K",37);put("A",41);}};

	Rank(String[] communityCards) {
		setSevenCard(communityCards);
	}

	void getSevenCard() {
		logger.log("Seven Card: " + sevenCard.toString());
	}

	void prime() {
		int handTotal = 1;
		int primeTotal = 0;
		int firstRank = 0;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 5; j++) {
				handTotal *= prime.get(sevenCard[i + j].substring(0, 1));
				if (j == 0) {
					firstRank = prime.get(sevenCard[i + j].substring(0, 1));
					primeTotal = firstRank;
					logger.log("Rank: " + firstRank + " , primeTotal: " + primeTotal);
				} else {
					firstRank++;
					primeTotal *= prime.get(Integer.toString(firstRank));
					logger.log("Rank: " + firstRank + " , primeTotal: " + primeTotal);
				}
			}
			System.out.println("handTotal: " + handTotal);
		}
		if (handTotal == primeTotal) {
//				return i;
		}
		handTotal = 1;
		primeTotal = 1;
	}


	void setSevenCard(String[] cards) {
		if (sevenCardLength() + cards.length <= 7) {
			if (cards.length == 5) {
				System.arraycopy(cards, 0, sevenCard, 0, cards.length);

			} else {
				System.arraycopy(cards, 0, sevenCard, 5, cards.length);
				Arrays.sort(sevenCard);
			}
			logger.log("Success! 20001R: sevenCard: " + sevenCard.toString());
		} else {
			System.out.println("Error 10001R");
			logger.log("Error 10001R: sevenCard has length " + sevenCardLength() + " , and cards has length "
					+ cards.length);
		}
	}

	int sevenCardLength() {
		for (int i = 0; i < sevenCard.length; i++) {
			if (sevenCard[i] == null) {
				return i;
			}
		}
		return 7;
	}

	void clearSevenCard() {
		Arrays.fill(sevenCard, null);
		logger.log("Success! 20002R: SevenCard cleared: " + sevenCard.toString());
	}

	public String toString() {
		return "Hello World";
	}
}
