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

	GameState g = new GameState();
	RoundState r;

	ArrayList<Player> p = new ArrayList<Player>(); // Players in the game
	private ArrayList<Transaction> gameTracking = new ArrayList<Transaction>();

	private String[] communityCards = new String[5];
	private Deck deck;

	private int smallBlindOffset, bigBlindOffset, preflopFirstBet, actingPlayer;
	private int boardCounter = 0; // count number of cards on board (community cards)
	private double smallBlindAmount = 2.00;
	private double bigBlindAmount = 4.00;
	private double pot; // balance of pot

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
		g.setIsGameStarted();
		gameTracking.add(new Transaction("New Round", r.roundCounter(), 0.00, 0.00));
		setButtonBlinds(p.size(), g.getGameCounter());
		System.out.println("Blinds will be withdrawn from small blind and big blind accounts:");
		collectForcedBets(r.smallBlind(), smallBlindAmount, "Small Blind");
		collectForcedBets(r.bigBlind(), bigBlindAmount, "Big Blind");
//		System.out.println("button holder is :" + r.getIsButtonHolder());
	}

	void setButtonBlinds(int numPlayers, int gameCounter) {
		if (numPlayers == 2) {
			smallBlindOffset = 0;
			bigBlindOffset = 1;
			preflopFirstBet = 0; // first betting player
		} else {
			smallBlindOffset = 1;
			bigBlindOffset = 2;
			preflopFirstBet = 3; // first betting player
		}
		r.setButtonHolder(p.get(gameCounter % p.size()));
		r.setSmallBlind(p.get((gameCounter + smallBlindOffset) % p.size()));
		r.setBigBlind(p.get((gameCounter + bigBlindOffset) % p.size()));
	}

	private void collectForcedBets(Player player, double amount, String description) {
		player.account.setWithdrawal(amount);
		r.setActiveBet(p.indexOf(player), amount);
		this.logger.logFormat(String.format("%-15s %-15s %-20s %7.2f \n", player.getFirstName(), player.getLastName(),
				description, amount));
		setPot(amount);
		gameTracking.add(new Transaction(player.getFirstName(), player.getLastName(), description, amount, this.pot));
	}

	public void betting() { // players placing bets

		// set up
		if (r.roundCounter() == 0 && r.actCounter() == 0) { // first player of pre-flop only
			r.setFirstActingPlayer(3);
			actingPlayer = r.firstActingPlayer();
		} else if (r.roundCounter() > 0 && r.actCounter() == 0) { // first player of every other round
			if (p.contains(r.buttonHolder())) {
				r.setFirstActingPlayer(1); // if button holder is still in the game then it's the first player to the
											// left
			} else {
				r.setFirstActingPlayer(0); // if button holder is no longer in the game then it's the first player in
											// arraylist's turn
			}
			actingPlayer = r.firstActingPlayer();
		} else {
			actingPlayer = r.firstActingPlayer() + r.actCounter();
		}

		do {
			if (r.roundCounter() == 0 && r.bigBlind() == p.get(actingPlayer)) {
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
			if (response.equalsIgnoreCase("fold")) {
				playerFold(actingPlayer);
				if (r.hasOneWinner(this.pot)) {
					System.out.println(
							"Player " + p.get(0) + " is the winner! Congradulations " + p.get(0).getFirstName() + "!");
					// withdraw pot balance
					addTransactionHistory(0, "Winner", -this.pot, 0.00);
					// add pot balance to player's account
					p.get(0).account.loadAccount(this.pot);
					resetGame();
					break;
				}
				continue;
			} else if (response.equalsIgnoreCase("call") || response.equalsIgnoreCase("check")) {
				playerCall(actingPlayer);
			} else if ((response.equalsIgnoreCase("bet") || response.equalsIgnoreCase("raise"))
					&& r.raiseCounter() < 3) {
				playerBet(actingPlayer);
			} else { // error handle - or set default
				userInput.nextLine();
				System.err.println("Error 1004G: Please enter a valid response.");
			}
		} while (!r.didAllPlayersBet() || !r.isPotEven());
		r.clearRaiseCounter();
		r.setPreviousRaise(Double.valueOf(null));
//		bettingPlayerOffset++; // next round the first betting player is
		if (r.roundCounter() == 5 && !r.hasOneWinner(this.pot)) {
			// evaluate cards
			logger.log("End of Round. Who is the winner?");
			resetGame();
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
		addTransactionHistory(player, "Fold", 0.00, this.pot);
		p.get(player).setStatus();
		p.remove(player); // must be removed last
		logger.log("After Fold Num Players: " + getNumberPlayers() + ".");
	}

	private void playerCall(int player) {
		double callAmount = r.activeBet((player + p.size() - 1) % p.size()) - r.activeBet(player);
		if (callAmount == 0.00) {
			addTransactionHistory(player, "Check", 0.00, this.pot);
		} else {
			// deduct player's account
			p.get(player).account.setWithdrawal(callAmount);
			// add amount to pot
			r.setActiveBet(player, r.activeBet(player) + callAmount);
			this.pot += callAmount;
			// add to game transaction history
			addTransactionHistory(player, "Call", callAmount, this.pot);
		}
		r.setActCounter();
		r.setDidBet(player);
	}

	private void playerBet(int player) {
		boolean caught = false;
		double raise;
		double minimumBet;
		if (r.previousRaise() == null) {
			if (r.roundCounter() == 0.00) {
				minimumBet = 0.00;
			} else if (r.roundCounter() == 1) {
				minimumBet = bigBlindAmount;
			} else {
				minimumBet = 2 * bigBlindAmount;
			}
		} else {
			minimumBet = r.previousRaise().doubleValue();
		}
		do {
			System.out.println("Enter raise amount, the minimum amount is " + minimumBet);
			if (userInput.hasNextInt()) {
				raise = userInput.nextInt();
				if ((raise >= minimumBet) && 
						((r.roundCounter() == 0 && raise <= bigBlindAmount)
						|| (r.roundCounter() == 1 && raise >= bigBlindAmount)
						|| (r.roundCounter() >= 2 && raise >= 2 * bigBlindAmount))) {
					caught = true;
					// deduct player's account
					double addToPot = r.activeBet((player + p.size() - 1) % p.size()) - r.activeBet(player) + raise;
					p.get(player).account.setWithdrawal(addToPot);
					// add amount to pot
					r.setActiveBet(player, r.activeBet((player + p.size() - 1) % p.size()) + raise);
					this.pot += addToPot;
					// add to game transaction history
					String action = "Raised by $ " + df.format(raise);
					addTransactionHistory(player, action, addToPot, this.pot);
					r.setPreviousRaise(Double.valueOf(raise));
					r.setActCounter();
					r.setRaiseCounter();
					r.setDidBet(player);
					userInput.nextLine();
				}
			} else {
				userInput.nextLine();
				System.out.println("Error 10006G: Try again. Enter a valid raise amount");
			}
		} while (!caught);

	}


	private void setPot(double change) {
		this.pot += change;
	}

	public double getPot() {
		return this.pot;
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
		g.gameCounter(); // game positions ++ clockwise
		r.clearActCounter();
		r.clearPreviousRaise();
		r.clearBetStates();
		r.setPreviousRaise(Double.valueOf(null));
	}


	public String toString() {
		return "There are " + p.size() + " Players in the game and the pot is " + getPot() + ".";
	}
}
