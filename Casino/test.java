package Casino;

import Table.Card;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Collections;

@SuppressWarnings("serial")
public class test {

	static ArrayList<Card> sevenCard = new ArrayList<Card>(7);
    Card card;
	
	public static void main(String[] args) {
		for (int i = 7; i > 0; i--) {
			sevenCard.add(new Card(String.valueOf(i), "S"));
		}
		System.out.println(sevenCard.get(0).suit());
		
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

//		System.out.println(getHand());
//		System.out.println(sevenCard.get(0)[0].toString());
//		System.out.println(Integer.valueOf(sevenCard.get(0)[0].toString()));

//		System.out.println(getHand());
//		for(String[] array: sevenCard) {
//			System.out.println(Arrays.toString(array));
//		}

//		System.out.println(Arrays.deepToString(sevenCard.toArray()));

	}


}
