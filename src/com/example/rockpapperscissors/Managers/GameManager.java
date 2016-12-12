package com.example.rockpapperscissors.Managers;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.rockpapperscissors.CONSTANTS;
import com.example.rockpapperscissors.fields.GameField;

public class GameManager {

	/*
	 * Since this class is a singleton, we must declare an instance of this
	 * class within itself. The singleton will be instantiated a single time
	 * during the course of an application's full life-cycle
	 */
	private static GameManager INSTANCE = new GameManager();;

	private GameField mGameField;

	private int playerScore;

	private int turnCount;

	private boolean gameOver = false;

	private int whoWon = -1;

	// Include a 'filename' for our shared preferences
	private static final String PREFS_NAME = "GAME_USERDATA";

	private static final String AGRESSION_KEY = "agressionKey";
	private static final String GREEDINES_KEY = "greedinesKey";

	private int agression;
	private boolean greedines;

	private SharedPreferences mSettings;
	private SharedPreferences.Editor mEditor;

	public synchronized void init() {
		if (mSettings == null) {
			/*
			 * Retrieve our shared preference file, or if it's not yet created
			 * (first application execution) then create it now
			 */
			mSettings = ResourceManager.getInstance().context
					.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
			/*
			 * Define the editor, used to store data to our preference file
			 */
			mEditor = mSettings.edit();


			agression = mSettings.getInt(AGRESSION_KEY, CONSTANTS.NEUTRAL_AGRESSION);


			greedines = mSettings.getBoolean(GREEDINES_KEY, true);
		}
	}

	public int getAgression() {
		return agression;
	}

	public void setAgression(int pAgression) {
		this.agression = pAgression;
		/* Edit our shared preferences unlockedLevels key, setting its
		* value our new mUnlockedLevels value
		*/
		mEditor.putInt(AGRESSION_KEY, pAgression);
		/* commit() must be called by the editor in order to save
		* changes made to the shared preference data
		*/
		mEditor.commit();
	}

	public boolean isGreedines() {
		return greedines;
	}

	public void setGreedines(boolean pGreedines) {
		this.greedines = pGreedines;
		/* Edit our shared preferences unlockedLevels key, setting its
		* value our new mUnlockedLevels value
		*/
		mEditor.putBoolean(GREEDINES_KEY, pGreedines);
		/* commit() must be called by the editor in order to save
		* changes made to the shared preference data
		*/
		mEditor.commit();
	}

	public boolean isGameOver() {
		return gameOver;
	}

	public void setGameOver(boolean gameOver) {
		this.gameOver = gameOver;
	}

	public int getWhoWon() {
		return whoWon;
	}

	public void setWhoWon(int whoWon) {
		this.whoWon = whoWon;
	}

	// The constructor does not do anything for this singleton
	GameManager() {
		init();
	}

	/*
	 * For a singleton class, we must have some method which provides access to
	 * the class instance. getInstance is a static method, which means we can
	 * access it globally (within other classes). If the GameManager has not yet
	 * been instantiated, we create a new one.
	 */
	public static GameManager getInstance() {
		return INSTANCE;
	}

	public void initGame() {
		mGameField = new GameField();
		mGameField.initStartOfGame();
		playerScore = 0;
		turnCount = 1;
		whoWon = -1;
		gameOver = false;
	}

	public int getTurnCount() {
		return turnCount;
	}

	public void setTurnCount(int turnCount) {
		this.turnCount = turnCount;
	}

	public void resetGame() {
	}

	public GameField getGameField() {
		return mGameField;
	}

	public int getPlayerScore() {
		return playerScore;
	}

	public void setPlayerScore(int pPlayerScore) {
		playerScore = pPlayerScore;
	}

	public void incPlayerScore(int pInc) {
		playerScore += pInc;
	}

	public void incTurnCount() {
		turnCount++;
	}

	public void cycleGreedines() {
		setGreedines(!isGreedines());
		
	}

	public void cycleAgression() {
		switch(getAgression()){
		case CONSTANTS.AGRESSIVE_AGRESSION:
			setAgression(CONSTANTS.DEFENSIVE_AGRESSION);
			break;
		case CONSTANTS.NEUTRAL_AGRESSION:
			setAgression(CONSTANTS.AGRESSIVE_AGRESSION);
			break;
		case CONSTANTS.DEFENSIVE_AGRESSION:
			setAgression(CONSTANTS.NEUTRAL_AGRESSION);
			break;
		default:
			setAgression(CONSTANTS.NEUTRAL_AGRESSION);
		}
		
	}
}