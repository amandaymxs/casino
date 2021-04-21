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
	
	static GameState gameState = new GameState();
	static RoundState r;
	
	static ArrayList<Player> inGame = new ArrayList<Player>();
	private static ArrayList<Transaction> gameTracking = new ArrayList<Transaction>();

	private String[] communityCards = new String[5];
	private static Deck deck;


	private int communityCounter = 0;
	private double smallBlindAmount = 2.00;
	private double bigBlindAmount = 4.00;
	private double pot;
	private double previousRaise = 0;


	// Welcome to Texas Holdem : Limited Holdem Edition.

	public Game(ArrayList<Player> players) {
		joinGame(players);
		r = new RoundState(players.size());
		deck = new Deck();
		this.logger.log("Success! 20004G: Game initialized");
	}

	public void joinGame(ArrayList<Player> players) {
		if (gameState.getIsGameStarted()) {
			System.err.println("Error 1001G: New players cannot enter the game once the game has started");
		} else if (players.size() >= 2) {
			// Copy all the players at the table to inGame
			for (Player p : players) {
				if(inGame.contains(p)) {	//if players are already inGame then skip
					continue;
				} else {
				inGame.add(p);
				p.setStatus();
				}
			}
			this.logger.log("Success! 20002T: There are " + inGame.size() + " players has been in the game!");
		} else {
			System.err.println("Error 10003T: Player count must be between 2 and 12.");
		}
	}
	
	public void startGame() {
		gameState.setIsGameStarted();
		gameTracking.add(new Transaction("New Round", r.getRoundCounter(), 0.00, 0.00));
		r.setButtonBlinds(inGame.size(), gameState.getGameCounter());
		System.out.println("Blinds will be withdrawn from small blind and big blind accounts:");
		collectForcedBets(r.getSmallBlind(), smallBlindAmount, "Small Blind");
		collectForcedBets(r.getBigBlind(), bigBlindAmount, "Big Blind");
	}

	public ArrayList<Player> getPlayers() {
		ArrayList<Player> inGameCopy = new ArrayList<Player>(inGame);
		return inGameCopy;
	}

	public String getButtonBlind() {
		return ("Round " + r.getRoundCounter() + ": B	utton Holder is Player " + r.getButtonHolder() + ", Small Blind is Player "
				+ r.getSmallBlind() + ", and Big Blind is " + r.getBigBlind() + ".");
	}

	private void collectForcedBets(int blind, double amount, String description) {
		inGame.get(blind).account.setWithdrawal(amount);
		r.setRoundBet(blind, amount);
		this.logger.logFormat(String.format("%-15s %-15s %-20s %7.2f \n", inGame.get(blind).getFirstName(),
				inGame.get(blind).getLastName(), description, amount));
		setPot(blind);
		gameTracking.add(new Transaction(inGame.get(blind).getFirstName(), inGame.get(blind).getLastName(), description,
				amount, this.pot));
	}

	public void dealCards() {
		for (int i = 0; i < 2; i++) { // each player gets two cards
			for (Player p : inGame) {
				p.hand.addCard(deck.deal());
				logger.log(p.getFirstName() + "'s hand: " + p.hand);
			}
		}
	}
	
	public void bettingRound() { // players placing bets
		int currentPlayer;
		do {
			currentPlayer = getCurrentPlayer();
			logger.log("round counter: " + r.getRoundCounter());
			logger.log("It's " + inGame.get(currentPlayer).getFirstName() + "'s turn to bet.");
			if (r.getBetCounter() == 0 && r.getRaiseCounter() < 3) {
				System.out.println(inGame.get(currentPlayer).getFirstName()
						+ ", enter if you would like to call, raise, or fold.");
			}  else if (r.getRoundCounter() > 0 && r.getBetCounter() == 0){
				System.out.println(inGame.get(currentPlayer).getFirstName()
						+ ", enter if you would like to check, bet, or fold.");
			} else {
				System.out.println(
						inGame.get(currentPlayer).getFirstName() + ", enter if you would like to call or fold.");
			}
			logger.log("Current round bets: " + r.getRoundBets());
			String response = userInput.nextLine();
			if (response.equalsIgnoreCase("fold")) {
				logger.log("There are " + getNumberPlayers() + " of players in the game before fold.");
				playerFold(currentPlayer);
				logger.log("There are " + getNumberPlayers() + " of players in the game after fold.");
				if (r.hasOneWinner(this.pot)) {
					System.out.println("Player " + inGame.get(0) + " is the winner! Congradulations "
							+ inGame.get(0).getFirstName() + "!");
					// withdraw pot balance
					addTransactionHistory(0, "Winner", -this.pot, 0);
					// add pot balance to player's account
					inGame.get(0).account.loadAccount(this.pot);
					resetGame();
					break;
				}
			} else if (response.equalsIgnoreCase("call") || response.equalsIgnoreCase("check")) {
				playerCall(currentPlayer);
			} else if ((response.equalsIgnoreCase("bet") || response.equalsIgnoreCase("raise")) && r.getRaiseCounter() < 3) {
				playerBet(currentPlayer);
			} else { // error handle - or set default
				userInput.nextLine();
				System.err.println("Error 1004G: Please enter a valid response.");
			}
		} while (!r.didAllPlayersBet() || !r.isPotEven());
		r.clearRaiseCounter();
		previousRaise = 0;
//		bettingPlayerOffset++; // next round the first betting player is
		if (r.getRoundCounter() == 5 && !r.hasOneWinner(this.pot)) {
			// evaluate cards
			logger.log("End of Round. Who is the winner?");
			resetGame();
		}
	}

	public void dealBoard() {
		deck.deal(); // burn: first card dealt faced down
		if (bettingPlayerOffset == 1) { // end of pre-flop
			for (communityCounter = 0; communityCounter < 3; communityCounter++) {
				setCommunityCards(communityCounter, deck.deal());
			}
			communityCounter++;
		} else if (communityCounter < 5) { // make sure not more than five cards get pushed to the communityCards array
			setCommunityCards(communityCounter, deck.deal());
		}
		logger.log(getCommunityCards());
	}

	public int getNumberPlayers() {
		return inGame.size();
	}

	public String getCommunityCards() {
		String[] copyCommunityCards = Arrays.copyOf(communityCards, 5);
		return Arrays.toString(copyCommunityCards);
	}

	
	private void setCommunityCards(int index, String card) {
		communityCards[index] = card;
	}

	
	private int getCurrentPlayer() {
		logger.log("The button holder is : " + isButtonHolder + "and bettingPlayerOffset is : " + bettingPlayerOffset + ".");
		if(bettingPlayerOffset == 0) {
			return firstBettingPlayer + betCounter % inGame.size();
		} else if (isButtonHolder == inGame.get(0)) {	//if buttonHolder is still in game
			return 1;
		} else {	//buttonHolder is no longer in the game
			return 0;		//player next to button holder places first bet	
		}
	}
	
	private void playerFold(int player) {
		r.removeBets(player);
		addTransactionHistory(player, "Fold", 0.00, this.pot);
		inGame.get(player).setStatus();
		inGame.remove(player); // must be removed last
	}
	
	private void playerCall(int player) {
		double callAmount = r.getRoundBet((player + inGame.size() - 1) % inGame.size())
				- r.getRoundBet(player);
		if (callAmount == 0.00) {
			addTransactionHistory(player, "Check", 0.00, this.pot);
		} else {
			// deduct player's account
			inGame.get(player).account.setWithdrawal(callAmount);
			// add amount to pot
			double getRoundBets = r.getRoundBet(player) + callAmount;
			r.setRoundBet(player, getRoundBets);
			this.pot += callAmount;
			// add to game transaction history
			addTransactionHistory(player, "Call", callAmount, this.pot);
		}
		r.setBetCounter();
		r.setDidBet(player);
	}

	private void playerBet(int player){
		boolean caught = false;
		double raise;
		double minimumBet;
		if(r.getRoundCounter() <=1) {
			minimumBet = bigBlindAmount;
		} else {
			minimumBet = 2*bigBlindAmount;
		}
		do {
			System.out.println("Enter raise amount.");
			logger.log("Previous raise: " + previousRaise);
			if (userInput.hasNextInt()) {
				raise = userInput.nextInt();
				if (r.getRoundCounter() <= 1 && raise <= minimumBet && raise >= previousRaise) {
					caught = true;
					// deduct player's account
					double addToPot = r.getRoundBet((player + inGame.size() - 1) % inGame.size())
							- r.getRoundBet(player) + raise;
					inGame.get(player).account.setWithdrawal(addToPot);
					// add amount to pot
					r.setRoundBet(player, r.getRoundBet((player + inGame.size() - 1) % inGame.size()) + raise);
					this.pot += addToPot;
					// add to game transaction history
					String action = "Raised by $ " + df.format(raise);
					addTransactionHistory(player, action, addToPot, this.pot);
					previousRaise = raise;
					r.setBetCounter();
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

	private void addTransactionHistory(int player, String action, double amountChange, double pot) {
		gameTracking.add(new Transaction(inGame.get(player).getFirstName(), inGame.get(player).getLastName(), action,
				amountChange, pot));
	}

	public void resetGame() {

		deck.clearDeck();
		for(int i=0;i<communityCards.length;i++) {	//reset community cards to null
			communityCards[i] = null;
		}
		communityCounter = 0;	//reset community cards counter to 0
//		bettingPlayerOffset++; // next round the first betting player is ++
		r.clearBetCounter();
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
