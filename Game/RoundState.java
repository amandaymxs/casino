package Game;

import java.util.ArrayList;

import Casino.Logger;
//import Casino.Player;

public class RoundState {
	private Logger logger = new Logger(true);
//	private Player isButtonHolder;

	private ArrayList<Boolean> didBets = new ArrayList<Boolean>();
	private ArrayList<Double> roundBets = new ArrayList<Double>();

	private int buttonHolder, smallBlind, bigBlind, firstBettingPlayer, smallBlindOffset, bigBlindOffset, bettingPlayerOffset;
	private int roundCounter = 0; // counter for number of complete rounds (pre-flop: 0, flop: 1, turn: 2, river:
											// 3, showdown: 4)
	private int betCounter = 0; // how many bets have been made in the current round
	private int raiseCounter = 0; // how many raises have been made in the current round (maximum of 3 raises)


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
	void setButtonBlinds(int numPlayers, int gameCounter) {
		if (numPlayers == 2) {
			smallBlindOffset = 0;
			bigBlindOffset = 1;
			firstBettingPlayer = 0; // first betting player
		} else {
			smallBlindOffset = 1;
			bigBlindOffset = 2;
//			firstBettingPlayer = 3; // first betting player
//			bettingPlayerOffset = 0; // pre-flop betting will be 0 offset, increment after every BETTING round
		}
//		setIsButtonHolder(buttonHolder);
		buttonHolder = gameCounter % didBets.size();
		smallBlind = (gameCounter + smallBlindOffset) % didBets.size();
		bigBlind = (gameCounter + bigBlindOffset) % didBets.size();
		firstBettingPlayer = (gameCounter + bettingPlayerOffset) % didBets.size();
	}
	
//	private void setIsButtonHolder(Player player) {
//		this.isButtonHolder = player;
//	}
//
//	Player getIsButtonHolder() { // return copy?
//		return this.isButtonHolder;
//	}
	
	int getButtonHolder() {
		return this.buttonHolder;
	}
	
	int getSmallBlind() {
		return this.smallBlind;
	}
	
	int getBigBlind() {
		return this.bigBlind;
	}
	
//	int getFirstBettingPlayer() {
//		return this.firstBettingPlayer;
//	}

	void setRoundCounter() {
		this.roundCounter++;
	}
	
	int getRoundCounter() {
		return this.roundCounter;
	}
	
	void setBetCounter() {
		this.betCounter++;
	}
	
	int getBetCounter() {
		return this.betCounter;
	}
	
	void setRaiseCounter() {
		this.raiseCounter++;
	}
	
	int getRaiseCounter() {
		return this.raiseCounter;
	}
	
	void setDidBet(int index) {
		didBets.set(index, !didBets.get(index));
	}
	
	void setRoundBet(int index, double amount) {
		roundBets.set(index, amount);
	}
	
	void removeBets(int player) {
		didBets.remove(player);
		roundBets.remove(player);
	}

	void clearBetStates(int numPlayers) {
		this.didBets.clear();
		this.roundBets.clear();
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
	
	boolean hasOneWinner(int sizePlayers, double pot) {
		if (sizePlayers == 1 && pot > 0) {
			logger.log("has winner? : " + true);
			return true;
		}
		logger.log("has winner? : " + false);
		return false;
	}
	
}
