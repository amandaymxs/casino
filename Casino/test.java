package Casino;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.ArrayList;
@SuppressWarnings("serial")
public class test {

	public static void main(String[] args) {
		String[] sevenCard = { "2H", "3D", "4S", "5C", "6S", "7H", "8C" };
		final Map<String, Integer> prime = new LinkedHashMap<String, Integer>() {
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
		ArrayList<String> listKeys = new ArrayList<String>(keys);
//		for(String key: prime.keySet()) {
//			System.out.println("Key is: " + key + " and Value is: " + prime.get(key) + ".");
//		}

//		for(int i = 0; i < sevenCard.length; i++) {
//		System.out.println("Key is: " + prime.get(sevenCard[i].substring(0, 1))+ " and Value is: " + prime.get(sevenCard[i].substring(0, 1)) + ".");
//		}
//		
//		
//		for ( String index : sevenCard) {
//		    String key = index.substring(0, 1);
//		    Integer value = prime.get(index.substring(0, 1));
//		    System.out.println(value);
//		    // do something with key and/or tab
//		}
		
		System.out.println("listKeys: " + listKeys.toString());

		int handTotal = 1;
		int primeTotal = 0;
		int firstRank = 0;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 5; j++) {
				handTotal *= prime.get(sevenCard[i + j].substring(0, 1));
				if (j == 0) {
					firstRank = prime.get(sevenCard[i + j].substring(0, 1));
					primeTotal = firstRank;
					System.out.println("Rank: " + firstRank + " , primeTotal: " + primeTotal);
				} else {
					firstRank++;
					primeTotal *= prime.get(Integer.toString(firstRank));
					System.out.println("Rank: " + firstRank + " , primeTotal: " + primeTotal);
				}
			}
			System.out.println("handTotal: " + handTotal);
		}

	}

}
