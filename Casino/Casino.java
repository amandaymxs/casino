package Casino;

import java.util.ArrayList;
import Table.Table;

public class Casino {

	public static void main(String[] args) {

		Table TexasHoldemTable = new Table();
		ArrayList<Player> inCasino = new ArrayList<Player>(); // list of players in the casino

		inCasino.add(new Player("Charlotte", "Mann"));
		inCasino.add(new Player("Liam", "Lee"));
//		inCasino.add(new Player("Olivia", "Wong"));

		inCasino.get(0).account.loadAccount(100.00);
		inCasino.get(1).account.loadAccount(80);
//		inCasino.get(2).account.loadAccount(110);

		TexasHoldemTable.seat(inCasino.get(0));
		TexasHoldemTable.seat(inCasino.get(1));
//		TexasHoldemTable.seat(inCasino.get(2));

		System.out.println("Seated at table: " +TexasHoldemTable.getSeated());
		TexasHoldemTable.prepareGame();	//push everyone who's seating into the game
		TexasHoldemTable.game.startGame();
		
		TexasHoldemTable.game.dealCards();
		do{
			TexasHoldemTable.game.dealBoard();	//flop
			TexasHoldemTable.game.betting();	//flop betting
			if(TexasHoldemTable.game.g.isEndGame()) {
				break;
			}
			TexasHoldemTable.game.r.setRoundCounter();	//roundCounter++
		} while (!TexasHoldemTable.game.g.isEndGame() && TexasHoldemTable.game.r.roundCounter() < 3);
		if(!TexasHoldemTable.game.g.isEndGame() && TexasHoldemTable.game.r.roundCounter() == 4) {
			//do something
		}
		System.out.println(TexasHoldemTable);
		System.out.println(TexasHoldemTable.game);
	}

}

//players.add(new Player("Charlotte", "Mann"));
//players.add(new Player("Liam", "Lee"));
//player.add(new Player("Olivia", "Wong"));
//player.add(new Player("Noah", "Smith", 175));
//player.add(new Player("Emma", "Kim", 155));
//player.add(new Player("Oliver", "Wilson", 180));
//player.add(new Player("Ava", "Singh", 170));
//player.add(new Player("Amelia", "Ng", 120));
//player.add(new Player("Sophia", "Campbell", 130));
//player.add(new Player("James", "Nguyen", 140));
//player.add(new Player("Ethan", "Graham", 145));
//player.add(new Player("Harper", "Davies", 135));