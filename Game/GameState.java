package Game;

import Casino.Logger;
import Casino.Player;

public class GameState {
	private Logger logger = new Logger(true);
	private int gameCounter;		// counter for complete games (winner takes pot)
	private Player buttonHolder, smallBlind, bigBlind;
	private boolean isGameStarted = false;	//did the game start?
	public boolean endGame = false;
	private double pot; // balance of pot
	
	public GameState() {
		this.gameCounter = 0;
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
	
	public void gameCounter() {
		this.gameCounter++;
	}
	
	public int getGameCounter() {
		return this.gameCounter;
	}
	
	public void clearGameCounter() {
		this.gameCounter = 0;
	}
	
	public void setIsGameStarted() {
		this.isGameStarted = !isGameStarted;
	}
	
	public boolean getIsGameStarted() {
		return this.isGameStarted;
	}
	
	public void setEndGame(boolean bool) {
		this.endGame = bool;
	}
	
	public boolean isEndGame() {
		return this.endGame;
	}
	
	boolean hasOneWinner(int numPlayers, double pot) {
		if (numPlayers == 1 && pot > 0) {
			setEndGame(true);
			logger.log("has winner? : " + isEndGame());
			gameCounter++;
			return isEndGame();
		}
		logger.log("has winner? : " + isEndGame());
		return isEndGame();
	}
	
	void setPot(double change) {
		this.pot += change;
	}

	public double pot() {
		return this.pot;
	}
}
