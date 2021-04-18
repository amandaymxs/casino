package Game;

import java.util.ArrayList;
import java.util.Scanner;

import Accounting.Transaction;
import Casino.Logger;
import Casino.Player;
import Table.Deck;

public class Game {
	Logger logger = new Logger(true);
	static ArrayList<Player> inGame;
	static Deck deck = new Deck();
	static ArrayList<Transaction> gameTracking = new ArrayList<Transaction>();
	static ArrayList<Boolean> didBet;
	static ArrayList<Double> roundBets; // order does not matter

	static Scanner userInput = new Scanner(System.in);

	static int buttonHolder, smallBlind, bigBlind, bettingPlayer, buttonOffset, smallBlindOffset, bigBlindOffset,
			bettingPlayerOffset, roundCounter;

	double pot;
	static double smallBlindAmount = 2.00;
	static double bigBlindAmount = 4.00;

	boolean isGameStart = false;

	// static ArrayList<Card> deckOfCards = new ArrayList<Card>();
	// Welcome to Texas Holdem : Limited Holdem Edition.

	public Game(ArrayList<Player> players) {
		setPlayers(players);
		didBet = new ArrayList<Boolean>(inGame.size());
		roundBets = new ArrayList<Double>(inGame.size());
		this.logger.log("Game initialized");
	}

	public void setPlayers(ArrayList<Player> players) {
		if (isGameStart) {
			System.err.println("Error 1001G: New players cannot enter the game once the game has started");
		} else {
			// let inGame = same size as players
			inGame = new ArrayList<Player>(players.size());
			// Copy all the players at the table to a new arraylist
			for (Player p : players) {
				inGame.add(p);
				p.setStatus(true);
			}

			gameTracking.add(new Transaction("New Round", roundCounter, 0.00, 0.00)); // (action, round, change, pot)
			// initialize counter to zero
			isGameStart = true;
			setButtonBlind();
		}
	}

	public ArrayList<Player> getPlayers() {
		ArrayList<Player> inGameCopy = new ArrayList<Player>(inGame);
		return inGameCopy;
	}

	// Button Holder rotates to left after every round
	// Direction: left is represented by +1 therefore clockwise is positive
	// Small blind: paid by person sitting directly left of the button holder
	// Big blind: paid by person sitting directly left of the small blind. Big blind
	// is usually 2*small blind amount
	// In heads+up game (2 players only) the button holder plays small blind while
	// the opponent plays big blind.
	private static void setButtonBlind() {
		if (inGame.size() == 2) {
			smallBlindOffset = 0;
			bigBlindOffset = 1;
			bettingPlayer = 0; // first betting player
		} else {
			smallBlindOffset = 1;
			bigBlindOffset = 2;
			bettingPlayer = 3; // first betting player
		}
		buttonHolder = roundCounter % inGame.size();
		smallBlind = (roundCounter + smallBlindOffset) % inGame.size();
		bigBlind = (roundCounter + bigBlindOffset) % inGame.size();
		bettingPlayer = (roundCounter + bettingPlayerOffset) % inGame.size();
	}

	public String getButtonBlind() {
		return ("Round " + roundCounter + ": Button Holder is Player " + buttonHolder + ", Small Blind is Player "
				+ smallBlind + ", and Big Blind is " + bigBlind + ".");
	}

	public void playForcedBets() {
		inGame.get(smallBlind).account.setBet(smallBlindAmount);
		setPot(smallBlindAmount);
		gameTracking.add(new Transaction(inGame.get(smallBlind).getFirstName(), inGame.get(smallBlind).getLastName(),
				"Small Blind", smallBlindAmount, pot));
		inGame.get(bigBlind).account.setBet(bigBlindAmount);
		setPot(bigBlindAmount);
		gameTracking.add(new Transaction(inGame.get(bigBlind).getFirstName(), inGame.get(bigBlind).getLastName(),
				"Big Blind", bigBlindAmount, pot));
	}

	public void dealCards() {
		for (int i = 0; i < 2; i++) { 		// each player gets two cards
		for (Player p : inGame) {
				p.hand.addCard(deck.deal());
				logger.log(p.hand.toString());
			}
		}
	}

	boolean didAllPlayersBet() {
		for (boolean b : didBet) {
			if (!b) {
				return false;
			}
		}
		return true;
	}

	boolean isPotEven() { // do all players have the same amount in pot?
		for (Double element : roundBets) {
			if (!element.equals(roundBets.get(0))) {
				return false;
			}
		}
		return true;
	}

	void preFlopBet() { // players placing bets
		// Pre-Flop starting at player firstBet
		int betCounter = 0; // order does not matter
		int raiseCounter = 0; // maximum or 3 raises
		double previousBet = 0;
		int currentPlayer;

		while (!didAllPlayersBet() || !isPotEven() || !hasWinner()) {
			currentPlayer = bettingPlayer + betCounter % inGame.size();
			System.out.println("Player " + currentPlayer + "'s turn to bet.");
			if (raiseCounter < 3) {
				System.out.println(inGame.get(currentPlayer).getFirstName()
						+ ", enter if you would like to call, raise, or fold.");
			} else {
				System.out.println(
						inGame.get(currentPlayer).getFirstName() + ", enter if you would like to call or fold.");
			}
			String response = userInput.nextLine();
			if (response == "fold") {
				didBet.remove(currentPlayer);
				roundBets.remove(currentPlayer);
				inGame.remove(currentPlayer); // must be removed last
				if (hasWinner()) {
					// VERIFY FOLLOWING LINE
					int winner = 0;
					// set pot to client's account
					resetGame();
					deck.clearDeck();
					System.out.println("Congradulations " + inGame.get(winner).getFirstName() + ", Player "
							+ inGame.get(winner) + " is the winner!");
				}
			} else if (response == "call") {
				double callAmount = getMax(previousBet, betCounter); // find max on table to match
				// deduct from user's account
				inGame.get(currentPlayer).account.setBet(callAmount);
				// add to pot
				setPot(callAmount);
				// send transaction to accounting
				addTransactionHistory(currentPlayer, "Call", callAmount, getPot());

				previousBet = callAmount;
				betCounter++;
			} else if (response == "raise") { // RAISE CANNOT BE GREATER THAN BIG BLIND IN LIMITED HOLDEM
				System.out.println(
						inGame.get(currentPlayer).getFirstName() + ", enter the amount you would like to raise");
				double raise = (double) userInput.nextInt();
				if (raise(currentPlayer, previousBet, raise)) { // if raise input amount meets conditions,
					// calculate the difference between what player has placed in pot already and
					// how much they want to raise.
					double playerBetDifference = getMax(previousBet, betCounter) + raise - roundBets.get(currentPlayer);// sum
																														// what
																														// the
																														// player
					// will have to put
					// into pot
					// deduct from user's account
					inGame.get(currentPlayer).account.setBet(playerBetDifference);
					// add to pot
					setPot(playerBetDifference);
					// send transaction to accounting
					String action = "Raise by $" + raise;
					addTransactionHistory(currentPlayer, action, playerBetDifference, getPot());

					previousBet = raise;
					raiseCounter++;
					betCounter++;
				}
			} else { // error handle - or set default
				System.err.println("Error 1004G: Please enter a valid response.");
			}
		}
	}

	static double getMax(double previousBet, int betCounter) {
		double max; // find the max of Array and setting it to the bet amount - ORDER DOES NOT
		// MATTER!
		if (betCounter == 0) {
			max = bigBlindAmount;
			previousBet = max;
		} else {
			max = roundBets.get(0);
			for (int index = 1; index < roundBets.size() - 1; index++) {
				if (roundBets.get(index) > max) {
					max = roundBets.get(index);
				}
			}
		}
		return max;
	}

	static boolean raise(int player, double previousBet, double raiseAmount) {
		if (raiseAmount <= bigBlindAmount) {
			if (previousBet == 0 || raiseAmount >= previousBet) {
				return true;
			} else {
				System.err.println("Error 1004B: Raise must be greater the previous bet and less than big blind.");
			}
		}
		return false;
	}

	void addTransactionHistory(int player, String action, double amountChange, double pot) {
		gameTracking.add(new Transaction(inGame.get(player).getFirstName(), inGame.get(player).getLastName(), action,
				amountChange, pot));
	}

	boolean hasWinner() {
		if (inGame.size() == 1 && pot > 0) {
			return true;
		}
		return false;
	}

	void resetGame() {
		didBet.clear();
		roundBets.clear();
	}

	void setPot(double change) {
		this.pot += change;
	}

	double getPot() {
		return this.pot;
	}

	public String toString() {
		return "There are " + inGame.size() + " Players in the game.";
	}
}
