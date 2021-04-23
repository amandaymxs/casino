package Game;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import Accounting.Transaction;
import Casino.Logger;
import Casino.Player;
import Table.Deck;

public class Game {
	private Logger logger = new Logger(true);
	final DecimalFormat df = new DecimalFormat("#.00");
	static final Scanner userInput = new Scanner(System.in);

	public GameState g = new GameState();
	public RoundState r;

	ArrayList<Player> p = new ArrayList<Player>(); // Players in the game
	private ArrayList<Transaction> gameTracking = new ArrayList<Transaction>();

	private String[] communityCards = new String[5];
	private Deck deck;

	private int smallBlindOffset, bigBlindOffset, actingPlayer;
	private int boardCounter = 0; // count number of cards on board (community cards)
	private double smallBlindAmount = 2.00;
	private double bigBlindAmount = 4.00;

	// Welcome to Texas Holdem : Limited Holdem Edition.

	public Game(ArrayList<Player> players) {
		joinGame(players);
		this.r = new RoundState(this.p.size());
		this.deck = new Deck();
		this.logger.log("Success! 20004G: Game initialized");

	}

	public ArrayList<Player> getPlayers() {
		ArrayList<Player> pCopy = new ArrayList<Player>(p);
		return pCopy;
	}

	public int getNumberPlayers() {
		return p.size();
	}

	public void joinGame(ArrayList<Player> players) {
		if (g.getIsGameStarted()) {
			System.err.println("Error 10001G: New players cannot enter the game once the game has started");
		} else if (players.size() >= 2) {
			// Copy all the players at the table to p
			for (Player player : players) {
				if (this.p.contains(player)) { // if players are already p then skip
					continue;
				} else {
					this.p.add(player);
					player.setStatus();
				}
			}
			this.logger.log("Success! 20002T: There are " + p.size() + " players has been in the game!");
		} else {
			System.err.println("Error 10003T: Player count must be between 2 and 12.");
		}
	}

	public void startGame() {
		logger.log("Start game method Game line 72------------------");
		g.setIsGameStarted();
		System.out.println("Game line 74------------------");
		gameTracking.add(new Transaction("New Round", r.roundCounter(), 0.00, 0.00));
		setButtonBlinds(p.size(), g.getGameCounter());
		System.out.println("Game line 76------------------");
		System.out.println("Blinds will be withdrawn from small blind and big blind accounts:");
		collectForcedBets(g.smallBlind(), smallBlindAmount, "Small Blind");
		collectForcedBets(g.bigBlind(), bigBlindAmount, "Big Blind");
		logger.log("after collectForcedBets");
	}

	void setButtonBlinds(int numPlayers, int gameCounter) {
		if (numPlayers == 2) {
			smallBlindOffset = 0;
			bigBlindOffset = 1;
		} else {
			smallBlindOffset = 1;
			bigBlindOffset = 2;
		}
		g.setButtonHolder(p.get(gameCounter % p.size()));
		g.setSmallBlind(p.get((gameCounter + smallBlindOffset) % p.size()));
		g.setBigBlind(p.get((gameCounter + bigBlindOffset) % p.size()));
	}

	private void collectForcedBets(Player player, double amount, String description) {
		player.account.withdrawal(amount);
		r.setActiveBet(p.indexOf(player), amount);
		g.setPot(amount);
		gameTracking.add(new Transaction(player.getFirstName(), player.getLastName(), description, amount, g.pot()));
	}

	public void betting() { // players placing bets
		r.setDidRaise(false);
		do {
			// set up
			if (r.roundCounter() == 0 && r.actCounter() == 0) { // first player of pre-flop only
				if (p.size() == 2) {
					r.setFirstActingPlayer(0);
				} else {
					r.setFirstActingPlayer(3 % p.size());
				}
				actingPlayer = r.firstActingPlayer();
			} else if (r.roundCounter() > 0 && r.actCounter() == 0) { // first player of every other round
				if (p.contains(g.buttonHolder())) {
					r.setFirstActingPlayer(1); // if button holder is still in the game then it's the first player to
												// the left
				} else {
					r.setFirstActingPlayer(0); // if button holder is no longer in the game then it's the first player
												// in arraylist's turn
				}
				actingPlayer = r.firstActingPlayer();
			} else {
				actingPlayer = (r.firstActingPlayer() + r.actCounter()) % p.size();
			}
			logger.log("//////////BEFORE INPUT/////////////////////");
			logger.log("Game: " + g.getGameCounter() + ", Round: " + r.roundCounter() + ", ActCount: " + r.actCounter()
					+ ".");
			logger.log("Did bets: " + r.getDidBets());
			logger.log("ActiveBets: " + r.getActiveBets());
			if ((r.roundCounter() == 0 && g.bigBlind() == p.get(actingPlayer) && r.previousRaise() == 0.0)
					|| (r.roundCounter() > 0 && r.actCounter() == 0)) {
				System.out.println(
						p.get(actingPlayer).getFirstName() + ", enter if you would like to check, bet, or fold.");
			} else if (r.raiseCounter() < 3) {
				System.out.println(
						p.get(actingPlayer).getFirstName() + ", enter if you would like to call, raise, or fold.");
			} else {
				System.out.println(p.get(actingPlayer).getFirstName() + ", enter if you would like to call or fold.");
			}

			// user input
			String response = userInput.nextLine();
			logger.log("PREVIOUS RAISE: " + r.previousRaise());
			if (response.equalsIgnoreCase("fold")) {
				playerFold(actingPlayer);
				logger.log("Line 141: playerFold complete.");
				if (g.hasOneWinner(r.getActiveBetsSize(), g.pot())) {
					System.out.println(
							"Player " + p.get(0) + " is the winner! Congradulations " + p.get(0).getFirstName() + "!");
					// withdraw pot balance
					addTransactionHistory(0, "Winner", -(g.pot()), 0.00);
					// add pot balance to player's account
					p.get(0).account.loadAccount(g.pot());
					resetGame();
					g.gameCounter();
					break;
				}
				continue;
			} else if (response.equalsIgnoreCase("call") || response.equalsIgnoreCase("check")) {
				playerCall(actingPlayer);
				logger.log("Line 154: playerCall complete.");
			} else if ((response.equalsIgnoreCase("bet") || response.equalsIgnoreCase("raise"))
					&& r.raiseCounter() < 3) {
				playerBet(actingPlayer);
				logger.log("Line 158: playerBet complete.");
			} else { // error handle - or set default
				System.err.println("Error 1004G: Please enter a valid response.");
			}
			logger.log("**************AFTER INPUT********************");
		} while (!r.didAllPlayersBet() || !r.isPotEven());
		r.clearDidBets();
		r.clearRaiseCounter();
		r.clearActCounter();
		r.setPreviousRaise(0.0);
		if (r.roundCounter() == 5 && !g.hasOneWinner(r.getActiveBetsSize(), g.pot())) {
			// evaluate cards
			logger.log("End of Round. Who is the winner?");
//			resetGame();

//			g.gameCounter();
		}
	}

	public void dealCards() {
		for (int i = 0; i < 2; i++) { // each player gets two cards
			for (Player player : this.p) {
				player.hand.addCard(deck.deal());
				logger.log(player.getFirstName() + "'s hand: " + player.hand);
			}
		}
	}

	public void dealBoard() {
		deck.deal(); // burn: first card dealt faced down
		if (boardCounter == 0) { // end of pre-flop
			for (int i = 0; i < 3; i++) {
				setCommunityCards(i, deck.deal());
				boardCounter++;
			}
		} else if (boardCounter < 5) { // make sure not more than five cards get pushed to the communityCards array
			setCommunityCards(boardCounter, deck.deal());
			boardCounter++;
		} else {
			System.out.println("Error 10008G: There are already 5 board cards.");
		}
		logger.log(getCommunityCards());
	}

	private void setCommunityCards(int index, String card) {
		communityCards[index] = card;
	}

	public String getCommunityCards() {
		String[] copyCommunityCards = Arrays.copyOf(communityCards, 5);
		return Arrays.toString(copyCommunityCards);
	}

	private void playerFold(int player) {
		logger.log("Before Fold Num Players: " + getNumberPlayers() + ".");
		r.removeBets(player);
		addTransactionHistory(player, "Fold", 0.00, g.pot());
		p.get(player).setStatus();
		p.remove(player); // must be removed last
		logger.log("After Fold Num Players: " + getNumberPlayers() + ".");
	}

	private void playerCall(int player) {
		double callAmount = r.activeBet((player + p.size() - 1) % p.size()) - r.activeBet(player);
		if (callAmount == 0.00) {
			addTransactionHistory(player, "Check", 0.00, g.pot());
		} else {
			// deduct player's account
			p.get(player).account.withdrawal(callAmount);
			// add amount to pot
			r.setActiveBet(player, r.activeBet(player) + callAmount);
			g.setPot(callAmount);
			// add to game transaction history
			addTransactionHistory(player, "Call", callAmount, g.pot());
		}
		r.setActCounter();
		r.setDidBet(player, true);
	}

	private void playerBet(int player) {
		double raise = bigBlindAmount;
		if (r.roundCounter() >= 2) {
			raise = 2 * bigBlindAmount;
		}
		// deduct player's account
		double addToPot = r.activeBet((player + p.size() - 1) % p.size()) - r.activeBet(player) + raise;
		p.get(player).account.withdrawal(addToPot);
		// add amount to pot
		r.setActiveBet(player, r.activeBet((player + p.size() - 1) % p.size()) + raise);
		g.setPot(addToPot);
		// add to game transaction history
		String action = "Raised by $ " + df.format(raise);
		addTransactionHistory(player, action, addToPot, g.pot());
		r.setPreviousRaise(raise);
		r.setActCounter();
		r.setRaiseCounter();
		r.clearDidBets();
		r.setDidBet(player, true);
		r.setDidRaise(true);
	}

	public void freeCard() {

	}

	private void addTransactionHistory(int player, String action, double amountChange, double pot) {
		gameTracking.add(
				new Transaction(p.get(player).getFirstName(), p.get(player).getLastName(), action, amountChange, pot));
	}

	public void resetGame() {

		deck.clearDeck();
		for (int i = 0; i < communityCards.length; i++) { // reset community cards to null
			communityCards[i] = null;
		}
		boardCounter = 0; // reset community cards counter to 0
		g.clearGameCounter(); // game positions ++ clockwise
		r.clearActCounter();
		r.clearBetStates();
		r.setPreviousRaise(0.0);
	}

	public String toString() {
		return "There are " + p.size() + " Players in the game and the pot is " + g.pot() + ".";
	}
}
