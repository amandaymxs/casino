package Game;

import java.util.ArrayList;
import java.util.Arrays;

import Casino.Logger;
import Casino.Player;

public class RoundState {
	private Logger logger = new Logger(true);
	private Player buttonHolder, smallBlind, bigBlind;

	private ArrayList<Boolean> didBets = new ArrayList<Boolean>();
	private ArrayList<Double> roundBets = new ArrayList<Double>();

	private int firstActingPlayer;		//first acting player in the round
	private int actCounter = 0; 	// how many bets have been made in the current round
	private int roundCounter = 0; 	// counter for number of complete rounds (pre-flop: 0, flop: 1, turn: 2, river:
											// 3, showdown: 4)
	private int raiseCounter = 0;	// how many raises have been made in the current round (maximum of 3 raises)
	private double previousRaise = 0;		//previous player's raise in current round

	RoundState(int numPlayers) {
		for (int index = 0; index < numPlayers; index++) {
			didBets.add(false);
			roundBets.add(0.00);
		}
	}

	// Button Holder rotates to left after every round
	// Direction: left is represented by +1 therefore clockwise is positive
	// Small blind: paid by person sitting directly left of the button holder
	// Big blind: paid by person sitting directly left of the small blind. Big blind
	// is usually 2*small blind amount
	// In heads+up game (2 players only) the button holder plays small blind while
	// the opponent plays big blind.

	private int getCurrentPlayer() {
		logger.log("The button holder is : " + isButtonHolder + "and bettingPlayerOffset is : " + bettingPlayerOffset + ".");
		if(bettingPlayerOffset == 0) {		//current player of pre-flop
			return firstBettingPlayer + actCounter % p.size();
		} else if (isButtonHolder == p.get(0)) {	//if buttonHolder is still in game
			return 1;
		} else {	//buttonHolder is no longer in the game
			return 0;		//player next to button holder places first bet	
		}
	}
	
	void setButtonHolder(Player player) {
		this.buttonHolder = player;
	}

	Player buttonHolder() { // return copy?
		return this.buttonHolder;
	}
	
	void setSmallBlind(Player player) {
		this.smallBlind = player;
	}
	
	Player smallBlind() {
		return this.smallBlind;
	}
	
	void setBigBlind(Player player) {
		this.bigBlind = player;
	}
	
	Player bigBlind() {
		return this.bigBlind;
	}
	
	void setFirstActingPlayer(int firstActingPlayer) {
		this.firstActingPlayer = firstActingPlayer;
	}
	
	int firstActingPlayer() {
		return this.firstActingPlayer;
	}
	
	void setRoundCounter() {
		this.roundCounter++;
	}
	
	int roundCounter() {
		return this.roundCounter;
	}
	
	void clearRoundCounter() {
		this.roundCounter = 0;
	}
	
	void setActCounter() {
		this.actCounter++;
	}
	
	int actCounter() {
		return this.actCounter;
	}
	
	void clearActCounter() {
		this.actCounter = 0;
	}
	
	void setRaiseCounter() {
		this.raiseCounter++;
	}
	
	int raiseCounter() {
		return this.raiseCounter;
	}
	
	void clearRaiseCounter() {
		this.raiseCounter = 0;
	}
	
	void setDidBet(int index) {
		didBets.set(index, !didBets.get(index));
	}
	
	void setRoundBet(int index, double amount) {
		roundBets.set(index, amount);
	}
	
	double roundBet(int player) {
		return roundBets.get(player);	//do i need to copy array to return deep copy of element?
	}
	
	String getRoundBets() {
		return Arrays.toString(roundBets.toArray());
	}
	
	void removeBets(int player) {
		didBets.remove(player);
		roundBets.remove(player);
	}

	void clearBetStates(int numPlayers) {
		this.didBets.clear();
		this.roundBets.clear();
	}
	
	void setPreviousRaise() {
		this.previousRaise++;
	}
	
	double previousRaise() {
		return this.previousRaise;
	}
	
	void clearPreviousRaise() {
		this.previousRaise = 0;
	}
	
	boolean didAllPlayersBet() {
		for (boolean b : didBets) {
			if (!b) {
				logger.log("Did all players bet? : " + false);
				return false;
			}
		}
		logger.log("Did all players bet? : " + true);
		return true;
	}
	
	boolean isPotEven() { // do all players have the same amount in pot?
		for (Double element : roundBets) {
			if (!element.equals(roundBets.get(0))) {
				logger.log("Is Pot Even? : " + false);
				return false;
			}
		}
		logger.log("Is Pot Even? : " + true);
		return true;
	}
	
	boolean hasOneWinner(double pot) {
		if (didBets.size() == 1 && pot > 0) {
			logger.log("has winner? : " + true);
			return true;
		}
		logger.log("has winner? : " + false);
		return false;
	}
	
}
