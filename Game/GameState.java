package Game;

import Casino.Logger;

public class GameState {
	private Logger logger = new Logger(true);
	private int gameCounter;		// counter for complete games (winner takes pot)
	private boolean isGameStarted = false;	//did the game start?
	public boolean endGame = false;
	
	public GameState() {
		this.gameCounter = 0;
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
}
