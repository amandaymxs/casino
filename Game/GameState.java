package Game;

public class GameState {

	private int gameCounter;		// counter for complete games (winner takes pot)
	private boolean isGameStarted;	//did the game start?
	
	public GameState() {
		this.gameCounter = 0;
		this.isGameStarted = false;
	}
	
	public void gameCounter() {
		this.gameCounter++;
	}
	
	public int getGameCounter() {
		return this.gameCounter;
	}
	
	public void setIsGameStarted() {
		this.isGameStarted = !isGameStarted;
	}
	
	public boolean getIsGameStarted() {
		return this.isGameStarted;
	}
}
