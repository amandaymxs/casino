package Game;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;

import Accounting.Transaction;
import Casino.Logger;
import Casino.Player;
import Table.Deck;

public class Game {
	private Logger logger = new Logger(true);
	final DecimalFormat df = new DecimalFormat("#.00");
	static ArrayList<Player> inGame;
	static Deck deck;
	static ArrayList<Transaction> gameTracking = new ArrayList<Transaction>();
	static ArrayList<Boolean> didBet = new ArrayList<Boolean>();
	static ArrayList<Double> roundBets = new ArrayList<Double>();; // order does not matter

	static final Scanner userInput = new Scanner(System.in);

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
		roundBets.set(smallBlind,smallBlindAmount);
		setPot(smallBlindAmount);
		gameTracking.add(new Transaction(inGame.get(smallBlind).getFirstName(), inGame.get(smallBlind).getLastName(),
				"Small Blind", smallBlindAmount, pot));
		inGame.get(bigBlind).account.setBet(bigBlindAmount);
		roundBets.set(bigBlind, bigBlindAmount);
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
		double previousRaise = 0; // default is bigBlind
		int currentPlayer;

		do {
			currentPlayer = bettingPlayer + betCounter % inGame.size();
			logger.log("bettingPlayer: " + bettingPlayer);
			logger.log("betCounter: " + betCounter);
			logger.log("currentPlayer " + currentPlayer);
			
			logger.log("Player " + currentPlayer + ", " + inGame.get(currentPlayer).getFirstName() + "'s turn to bet.");

			if (raiseCounter < 3) {
				System.out.println(inGame.get(currentPlayer).getFirstName()
						+ ", enter if you would like to call, raise, or fold.");
			} else {
				System.out.println(
						inGame.get(currentPlayer).getFirstName() + ", enter if you would like to call or fold.");
			}
			logger.log("Current round bets: " + roundBets.toString());
			String response = userInput.nextLine();
			if (response.equalsIgnoreCase("fold")) {
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
				userInput.nextLine();
			} else if (response.equalsIgnoreCase("call")) {
				double callAmount = roundBets.get((currentPlayer + inGame.size() - 1) % inGame.size()) - roundBets.get(currentPlayer);
				if ( callAmount == 0.00) {
					addTransactionHistory(currentPlayer, "Check", 0.00, this.pot);
				} else {
					// deduct player's account
					inGame.get(currentPlayer).account.setBet(callAmount);
					// add amount to pot
					double getRoundBets = roundBets.get(currentPlayer) + callAmount;
					roundBets.set(currentPlayer, getRoundBets);
					this.pot += callAmount;
					// add to game transaction history
					addTransactionHistory(currentPlayer, "Call", callAmount, this.pot);
				}
				betCounter++;
				didBet.set(currentPlayer, true);
				userInput.nextLine();
			} else if (response.equalsIgnoreCase("raise")) {
				boolean caught = false;
				double raise;
				do {
					System.out.println("Enter raise amount.");
					logger.log("Previous raise: " + previousRaise);
					if (userInput.hasNextInt()) {
						raise = userInput.nextInt();
						if (raise <= bigBlindAmount && raise >= previousRaise) {
							caught = true;
							// deduct player's account
							double addToPot = roundBets.get(currentPlayer + inGame.size() - 1 % inGame.size()) + raise
									- roundBets.get(currentPlayer);
							inGame.get(currentPlayer).account.setBet(addToPot);
							// add amount to pot
							roundBets.set(currentPlayer, roundBets.get(currentPlayer + inGame.size() - 1 % inGame.size()) + raise);
							this.pot += addToPot;
							// add to game transaction history
							String action = "Raised by $ " + df.format(raise);
							addTransactionHistory(currentPlayer, action, addToPot, this.pot);
							previousRaise = raise;
							betCounter++;
							didBet.set(currentPlayer, true);
						}
						userInput.nextLine();
					} else {
						userInput.nextLine();
						System.out.println("Enter a valid raise amount");
					}
				} while (!caught);
			} else { // error handle - or set default
				userInput.nextLine();
				System.err.println("Error 1004G: Please enter a valid response.");
			}
		} while (!didAllPlayersBet() || !isPotEven());
		betCounter++;
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

	private void addTransactionHistory(int player, String action, double amountChange, double pot) {
		gameTracking.add(new Transaction(inGame.get(player).getFirstName(), inGame.get(player).getLastName(), action,
				amountChange, pot));
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

	private void setPot(double change) {
		this.pot += change;
	}

	public double getPot() {
		return this.pot;
	}

	public String toString() {
		return "There are " + inGame.size() + " Players in the game.";
	}
}
