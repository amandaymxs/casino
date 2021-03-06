package Game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import Casino.Logger;
import Casino.Player;

public class RoundState {
	private Logger logger = new Logger(true);

	private ArrayList<Boolean> didBets = new ArrayList<Boolean>();
	private ArrayList<Double> activeBets = new ArrayList<Double>();

	private int firstActingPlayer, lastAggressor; // first acting player in the round
	private int actCounter = 0; // how many bets have been made in the current round
	private int roundCounter = 0; // counter for number of complete rounds (pre-flop: 0, flop: 1, turn: 2, river:
									// 3, showdown: 4)
	private int raiseCounter = 0; // how many raises have been made in the current round (maximum of 3 raises)
	private double previousRaise = 0.0; // previous player's raise in current round
	private boolean didRaise;

	RoundState(int numPlayers) {
		for (int index = 0; index < numPlayers; index++) {
			didBets.add(false);
			activeBets.add(0.00);
		}
	}

	// Button Holder rotates to left after every round
	// Direction: left is represented by +1 therefore clockwise is positive
	// Small blind: paid by person sitting directly left of the button holder
	// Big blind: paid by person sitting directly left of the small blind. Big blind
	// is usually 2*small blind amount
	// In heads+up game (2 players only) the button holder plays small blind while
	// the opponent plays big blind.

	void setFirstActingPlayer(int firstActingPlayer) {
		this.firstActingPlayer = firstActingPlayer;
	}

	int firstActingPlayer() {
		return this.firstActingPlayer;
	}
	
	void setLastAggressor(int lastAggressor) {
		this.lastAggressor = lastAggressor;
	}
	
	int lastAggressor() {
		return this.lastAggressor;
	}

	public void setRoundCounter() {
		this.roundCounter++;
	}

	public int roundCounter() {
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

	void setDidBet(int index, boolean bool) {
		didBets.set(index, bool);
	}

	String getDidBets() {
		return Arrays.toString(didBets.toArray());
	}

	void clearDidBets() {
		Collections.fill(didBets, Boolean.FALSE);
	}

	void setActiveBet(int index, double amount) {
		activeBets.set(index, amount);
	}

	double activeBet(int player) {
		return activeBets.get(player); // do i need to copy array to return deep copy of element?
	}

	String getActiveBets() {
		return Arrays.toString(activeBets.toArray());
	}

	int getActiveBetsSize() {
		return activeBets.size();
	}

	void removeBets(int player) {
		didBets.remove(player);
		activeBets.remove(player);
	}

	void clearBetStates() {
		this.didBets.clear();
		this.activeBets.clear();
	}

	void setPreviousRaise(Double raise) {
		this.previousRaise = raise;
	}

	double previousRaise() {
		return this.previousRaise;
	}
	
	void setDidRaise(boolean bool) {
		this.didRaise = bool;
	}
	
	boolean didRaise() {
		return this.didRaise;
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
		for (Double activeBet : activeBets) {
			if (!activeBet.equals(activeBets.get(0))) {
				logger.log("Is Pot Even? : " + false);
				return false;
			}
		}
		logger.log("Is Pot Even? : " + true);
		return true;
	}
	
	public String toString() {
		return "Round: " + roundCounter();
	}
}
