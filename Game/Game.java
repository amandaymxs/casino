package Game;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;

import Accounting.Transaction;
import Casino.Logger;
import Casino.Player;
import Table.Deck;

public class Game {
	Logger logger = new Logger(true);
	DecimalFormat df = new DecimalFormat("#.00");
	static ArrayList<Player> inGame;
	static Deck deck;
	static ArrayList<Transaction> gameTracking = new ArrayList<Transaction>();
	static ArrayList<Boolean> didBet = new ArrayList<Boolean>();
	static ArrayList<Double> roundBets = new ArrayList<Double>();; // order does not matter

	static Scanner userInput = new Scanner(System.in);

	static int buttonHolder, smallBlind, bigBlind, bettingPlayer, buttonOffset, smallBlindOffset, bigBlindOffset,
			bettingPlayerOffset, roundCounter;

	double pot;
	static double smallBlindAmount = 2.00;
	static double bigBlindAmount = 4.00;

	boolean isGameStart = false;

	// static ArrayList<Card> deckOfCards = new ArrayList<Card>();
	// Welcome to Texas Holdem : Limited Holdem Edition.
//	
//	public Game() {
//		deck = new Deck();
//	}

	public Game(ArrayList<Player> players) {
		joinGame(players);
		for (int index = 0; index < inGame.size(); index++) {
			didBet.add(false);
			roundBets.add(0.00);
		}
		deck = new Deck();
		this.logger.log("Success! 20004G: Game initialized");
	}

	public void joinGame(ArrayList<Player> players) {
		if (isGameStart) {
			System.err.println("Error 1001G: New players cannot enter the game once the game has started");
		} else if (players.size() >= 2) {
			// let inGame = same size as players
			inGame = new ArrayList<Player>(players.size());
			this.logger.log("Success! 20002T: " + players.size() + " players has been seated at the table!");
			// Copy all the players at the table to a new arraylist
			for (Player p : players) {
				inGame.add(p);
				p.setStatus(true);
			}

			gameTracking.add(new Transaction("New Round", roundCounter, 0.00, 0.00)); // (action, round, change, pot)
			// initialize counter to zero
			isGameStart = true;
			setButtonBlind();
		} else {
			System.err.println("Error 10003T: Player count must be between 2 and 12.");
		}
	}

	public ArrayList<Player> getPlayers() {
		ArrayList<Player> inGameCopy = new ArrayList<Player>(inGame);
		return inGameCopy;
	}

	public String getButtonBlind() {
		return ("Round " + roundCounter + ": Button Holder is Player " + buttonHolder + ", Small Blind is Player "
				+ smallBlind + ", and Big Blind is " + bigBlind + ".");
	}

	public void collectForcedBets() {
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
		for (int i = 0; i < 2; i++) { // each player gets two cards
			for (Player p : inGame) {
				p.hand.addCard(deck.deal());
				logger.log(p.getFirstName() + "'s hand: " + p.hand);
			}
		}
	}

	public void preFlopBet() { // players placing bets
		// Pre-Flop starting at player firstBet
		int betCounter = 0; // order does not matter
		int raiseCounter = 0; // maximum or 3 raises
		double previousBet = bigBlindAmount; // default is bigBlind
		int currentPlayer;

		while (!didAllPlayersBet() || !isPotEven()) {
			currentPlayer = bettingPlayer + betCounter % inGame.size();
			logger.log("Player " + currentPlayer + ", " + inGame.get(currentPlayer).getFirstName() + "'s turn to bet.");
			if (raiseCounter < 3) {
				System.out.println(inGame.get(currentPlayer).getFirstName()
						+ ", enter if you would like to call, raise, or fold.");
			} else {
				System.out.println(
						inGame.get(currentPlayer).getFirstName() + ", enter if you would like to call or fold.");
			}
			String response = userInput.nextLine();
			if (response.equals("fold")) {
				didBet.remove(currentPlayer);
				roundBets.remove(currentPlayer);
				addTransactionHistory(currentPlayer, "Fold", 0.00, this.pot);
				inGame.remove(currentPlayer); // must be removed last
				if (hasWinner()) {
					// VERIFY FOLLOWING LINE
					// set pot to client's account
					System.out.println("Player " + inGame.get(0) + " is the winner! Congradulations "
							+ inGame.get(0).getFirstName() + "!");
					resetGame();
					deck.clearDeck();
				}
			} else if (response.equals("call")) {
				double callAmount = getMax(previousBet, betCounter); // find max on table to match
				// deduct from user's account
				logger.log("call amount: " + callAmount);
				logger.log("previousBet: " + previousBet);
				inGame.get(currentPlayer).account.setBet(callAmount);
				// add to pot
				setPot(callAmount);
				// send transaction to accounting
				if (callAmount == 0 && raiseCounter == 0) { // if currentPlayer placed the big blind, then call amount
															// will be $0
					logger.log("Call Amount for Big Blind Player: " + roundBets.get(currentPlayer) + ".");
					addTransactionHistory(currentPlayer, "Check", callAmount, getPot());
				} else {
					addTransactionHistory(currentPlayer, "Call", callAmount, getPot());
				}
				roundBets.set(currentPlayer, previousBet);
				didBet.set(currentPlayer, true);
				logger.log("previous bet: " + previousBet + ".");
				logger.log("round bets arraylist: " + roundBets.toString() + ".");
				logger.log("round bets arraylist: " + didBet.toString() + ".");
				previousBet = callAmount;
				betCounter++;
			} else if (response.equals("raise")) { // RAISE CANNOT BE GREATER THAN BIG BLIND IN LIMITED HOLDEM
				boolean wentToCatch = false;
				do {
					System.out.println(
							inGame.get(currentPlayer).getFirstName() + ", enter the amount you would like to raise");
					logger.log("previous bet: " + previousBet + ".");
					if (userInput.hasNextInt()) {
						double raise = (double) userInput.nextInt();
						wentToCatch = true;
						if (raise(raise, previousBet, raiseCounter)) { // if raise input amount meets conditions,
							// calculate the difference between what player has placed in pot already and
							// how much they want to raise.
							double playerBetDifference = getMax(previousBet, betCounter) + raise
									- roundBets.get(currentPlayer);
							inGame.get(currentPlayer).account.setBet(playerBetDifference);
							// add to pot
							setPot(playerBetDifference);
							// send transaction to accounting
							String transactionString = df.format(Math.abs(raise));
							String action = "Raise by $" + transactionString;
							addTransactionHistory(currentPlayer, action, playerBetDifference, getPot());

							previousBet = raise;
							raiseCounter++;
							betCounter++;
							userInput.nextLine();
						}
					} else {
						userInput.nextLine();
						System.err.println(
								"Error 1004B: Raise must be greater the previous bet and less than or equal to big blind.");
					}
				} while (!wentToCatch);

			} else { // error handle - or set default
				System.err.println("Error 1004G: Please enter a valid response.");
			}
		}

	}

	public boolean hasWinner() {
		if (inGame.size() == 1 && pot > 0) {
			logger.log("has winner? : " + true);
			return true;
		}
		logger.log("has winner? : " + false);
		return false;
	}

	public void resetGame() {
		didBet.clear();
		roundBets.clear();
	}

	public double getPot() {
		return this.pot;
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

	private boolean didAllPlayersBet() {
		for (boolean b : didBet) {
			if (!b) {
				logger.log("Did all players bet? : " + false);
				return false;
			}
		}
		logger.log("Did all players bet? : " + true);
		return true;
	}

	private boolean isPotEven() { // do all players have the same amount in pot?
		for (Double element : roundBets) {
			if (!element.equals(roundBets.get(0))) {
				logger.log("Is Pot Even? : " + false);
				return false;
			}
		}
		logger.log("Is Pot Even? : " + true);
		return true;
	}

	private static double getMax(double previousBet, int betCounter) {
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

	private static boolean raise(double raiseAmount, double previousBet, int raiseCounter) {
		if (raiseCounter == 0 && raiseAmount <= bigBlindAmount) {
			return true;
		} else if (raiseAmount <= bigBlindAmount && raiseAmount > previousBet) {
			return true;
		}
		return false;
	}

	private void addTransactionHistory(int player, String action, double amountChange, double pot) {
		gameTracking.add(new Transaction(inGame.get(player).getFirstName(), inGame.get(player).getLastName(), action,
				amountChange, pot));
	}

	private void setPot(double change) {
		this.pot += change;
	}

	public String toString() {
		return "There are " + inGame.size() + " Players in the game.";
	}
}
