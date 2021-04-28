package Table;

public class Card {
	String rank, suit;
	
	public Card(String rank, String suit) {
		setRank(rank);
		setSuit(suit);
	}
	
	void setRank(String rank) {
		this.rank = rank;
	}
	
	public String rank() {
		return this.rank;
	}
	
	void setSuit(String suit) {
		this.suit = suit;
	}
	
	public String suit() {
		return suit;
	}
	
	public String toString() {
		return this.rank + this.suit;
	}
	
}
