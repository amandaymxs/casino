package Casino;

public class test {

	public static void main(String[] args) {
		Double[] roundBets = { 4.0, 4.0 };
		int currentPlayer = 1;
		int inGameSize = 2;

		System.out.println("currentplayer: " + currentPlayer);
		System.out.println("inGamesize: " + inGameSize);
		System.out.println("current player's round bets: " + roundBets[currentPlayer]);
		System.out.println(
				"get previous player's bet total: " + (roundBets[currentPlayer] + inGameSize % inGameSize));
		double callAmount = roundBets[currentPlayer + inGameSize % inGameSize] - roundBets[currentPlayer];
		System.out.println(2%2);
		System.out.println("call amount: " +  callAmount);
	}

}
