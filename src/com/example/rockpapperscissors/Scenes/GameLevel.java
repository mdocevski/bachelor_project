package com.example.rockpapperscissors.Scenes;

import java.util.ArrayList;

import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;

import android.app.AlertDialog;
import android.content.DialogInterface;

import com.example.rockpapperscissors.BytePair;
import com.example.rockpapperscissors.CONSTANTS;
import com.example.rockpapperscissors.DecisionMaking.CompressedGameField;
import com.example.rockpapperscissors.DecisionMaking.CompressedState;
import com.example.rockpapperscissors.DecisionMaking.Move;
import com.example.rockpapperscissors.DecisionMaking.Thinker;
import com.example.rockpapperscissors.Managers.GameManager;
import com.example.rockpapperscissors.Managers.ResourceManager;
import com.example.rockpapperscissors.Managers.SceneManager;
import com.example.rockpapperscissors.fields.Available;
import com.example.rockpapperscissors.fields.Figure;
import com.example.rockpapperscissors.fields.PieceFactory;
import com.example.rockpapperscissors.fields.GameField;

public class GameLevel extends ManagedGameScene implements
		IOnSceneTouchListener {

	private float clickX;
	private float clickY;
	private boolean figureSelected = false;
	private Figure selectedFigure = null;
	private boolean buyMode = false;

	GameField gameField;
	Text coinCountText;
	Text scoreText;

	Available[][] availablesField = new Available[CONSTANTS.GAMEFIELD_COLUMNS][CONSTANTS.GAMEFIELD_ROWS];
	private int selectedPositionY;
	private int selectedPositionX;
	private boolean usedAFigure;
	private Figure figureUsed;

	@Override
	public void onUnloadScene() {
		SceneManager.getInstance().clearActiveGameLevel();
		super.onUnloadScene();
	}

	@Override
	public void onLoadScene() {
		SceneManager.getInstance().setActiveGameLevel(this);
		super.onLoadScene();
		setOnSceneTouchListener(this);
		setOnSceneTouchListenerBindingOnActionDownEnabled(true);
		GameManager.getInstance().initGame();
		gameField = GameManager.getInstance().getGameField();
		gameField.setDisplaySceneToAllAndAttach(this);
		gameField.renderVisualState();

		// DRAW THE FIELD, SETUP AND HIDE AVAILABILITY DISPLAY ENETITIES
		float currentX = CONSTANTS.startX;
		float currentY = CONSTANTS.startY;
		for (int i = 0; i < CONSTANTS.GAMEFIELD_COLUMNS; i++) {
			for (int j = 0; j < CONSTANTS.GAMEFIELD_ROWS; j++) {
				Sprite field = new Sprite(currentX, currentY,
						ResourceManager.fieldTextureRegion,
						ResourceManager.getInstance().engine
								.getVertexBufferObjectManager());
				field.setScaleX(CONSTANTS.scale);
				field.setScaleY(CONSTANTS.scale);
				field.setZIndex(-100);
				this.attachChild(field);
				Available available = new Available(currentX, currentY, i, j,
						ResourceManager.availableFieldTextureRegion,
						ResourceManager.getInstance().engine
								.getVertexBufferObjectManager());
				available.setScale(CONSTANTS.scale);
				available.setVisible(false);
				this.attachChild(available);
				availablesField[i][j] = available;
				currentY += CONSTANTS.increment;
			}
			currentX += CONSTANTS.increment;
			currentY = CONSTANTS.startY;
		}

		initHUDElements();
	}

	private void initHUDElements() {
		ButtonSprite turnButton = new ButtonSprite(0f, 0f,
				ResourceManager.buttonTiledTextureRegion.getTextureRegion(0),
				ResourceManager.buttonTiledTextureRegion.getTextureRegion(1),
				ResourceManager.getInstance().engine
						.getVertexBufferObjectManager());
		turnButton.setScale(
				1 / ResourceManager.getInstance().cameraScaleFactorX,
				1 / ResourceManager.getInstance().cameraScaleFactorY);
		turnButton.setPosition(
				MainMenuButton.getX()
						+ (MainMenuButton.getWidth() / 2 + turnButton
								.getWidth() / 2)
						/ ResourceManager.getInstance().cameraScaleFactorX,
				(turnButton.getHeight() * turnButton.getScaleY()) / 2f);
		turnButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(ButtonSprite pButtonSprite,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				// Play the click sound and show the Options Layer.
				ResourceManager.clickSound.play();
				ResourceManager.getInstance().getRPSActivity()
						.runOnUiThread(new Runnable() {
							DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									switch (which) {
									case DialogInterface.BUTTON_POSITIVE:
										SceneManager.getInstance()
												.getActiveGameLevel().endTurn();
										break;

									case DialogInterface.BUTTON_NEGATIVE:
										// No button clicked
										break;
									}
								}
							};

							@Override
							public void run() {

								AlertDialog.Builder ab = new AlertDialog.Builder(
										ResourceManager.getInstance()
												.getRPSActivity());
								ab.setMessage("End turn?")
										.setPositiveButton("Yes",
												dialogClickListener)
										.setNegativeButton("No",
												dialogClickListener).show();

							}
						});

				// endTurn();

			}

		});

		Text turnButtonText = new Text(0, 0, ResourceManager.fontDefault32Bold,
				"END TURN",
				ResourceManager.getInstance().engine
						.getVertexBufferObjectManager());
		turnButtonText.setPosition((turnButton.getWidth()) / 2,
				(turnButton.getHeight()) / 2);
		turnButton.attachChild(turnButtonText);
		GameHud.attachChild(turnButton);
		GameHud.registerTouchArea(turnButton);
		coinCountText = new Text(0, 0, ResourceManager.fontStronke36BlueBold,
				"COINS: " + String.valueOf(gameField.getPlayerCoins()), 10,
				ResourceManager.getInstance().engine
						.getVertexBufferObjectManager());
		coinCountText.setScale(
				1 / ResourceManager.getInstance().cameraScaleFactorX,
				1 / ResourceManager.getInstance().cameraScaleFactorY);
		coinCountText.setPosition(
				NewUnitButton.getX()
						- (NewUnitButton.getWidth() / 2 + coinCountText
								.getWidth() / 2 + 20)
						/ ResourceManager.getInstance().cameraScaleFactorX,
				(NewUnitButton.getHeight() / 2) / ResourceManager.getInstance().cameraScaleFactorY);
		GameHud.attachChild(coinCountText);
		scoreText = new Text(0, 0, ResourceManager.fontStronke36BlueBold,
				String.valueOf(GameManager.getInstance().getPlayerScore()), 6,
				ResourceManager.getInstance().engine
						.getVertexBufferObjectManager());
		scoreText.setScale(
				1 / ResourceManager.getInstance().cameraScaleFactorX,
				1 / ResourceManager.getInstance().cameraScaleFactorY);
		scoreText.setPosition(24 + scoreText.getWidth() / 2,
				turnButton.getHeight() * 1.5f);
		GameHud.attachChild(scoreText);
		NewUnitButton.setEnabled(false);
	}

	private void adjustScoreText() {
		if (scoreText != null) {
			scoreText.setText(String.valueOf(GameManager.getInstance()
					.getPlayerScore()));
		}
	}

	private BytePair convertToFieldCoordinates(float clickX, float clickY) {
		byte positionX = (byte) ((clickX - (CONSTANTS.startX * ResourceManager
				.getInstance().cameraScaleFactorX)) / (CONSTANTS.increment * ResourceManager
				.getInstance().cameraScaleFactorX));
		byte positionY = (byte) ((clickY - (CONSTANTS.startY * ResourceManager
				.getInstance().cameraScaleFactorY)) / (CONSTANTS.increment * ResourceManager
				.getInstance().cameraScaleFactorY));
		return new BytePair(positionX, positionY);
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		if (pSceneTouchEvent.isActionDown()) {
			clickX = pSceneTouchEvent.getX();
			clickY = pSceneTouchEvent.getY();
			BytePair touchLocation = convertToFieldCoordinates(clickX, clickY);
			int positionX = (int) touchLocation.x;
			int positionY = (int) touchLocation.y;
			if (inBounds(positionX, positionY))
				if (!figureSelected) {
					if (null != gameField.getAt(positionX, positionY)
							.asFigure()
							&& gameField.getAt(positionX, positionY)
									.isFriendly()
							&& gameField.getAt(positionX, positionY).asFigure()
									.getRemainingMoves() > 0) {
						if (!usedAFigure
								|| (figureUsed.getIndexX() == positionX && figureUsed
										.getIndexY() == positionY)) {
							select(positionX, positionY);
							setAvailables();
						}
					}
				} else {
					if (isAvailable(positionX, positionY)) {
						if (gameField.getAt(positionX, positionY).isEmpty()) {
							if (buyMode) {
								buyFigure(selectedFigure, positionX, positionY);

								adjustCoinCountText();
								buyMode = false;
							} else {
								moveFigureFromTo(selectedPositionX,
										selectedPositionY, positionX, positionY);
							}
						} else if (gameField.getAt(positionX, positionY)
								.isOpponent() && selectedFigure.canAttack()) {
							attackFieldFromTo(selectedPositionX,
									selectedPositionY, positionX, positionY);
						} else if (gameField.getAt(positionX, positionY)
								.isCoins()) {
							collectCoinsFromTo(selectedPositionX,
									selectedPositionY, positionX, positionY);
						}
						usedAFigure = true;
						figureUsed = selectedFigure;
						NewUnitButton.setEnabled(false);
					}
					resetAvailables();
					figureSelected = false;
					selectedFigure = null;
				}
			if (gameField.isGameOver()) {
				initiateGameOver();
			}

		}
		return super.onSceneTouchEvent(pScene, pSceneTouchEvent);
	}

	private void select(int positionX, int positionY) {
		figureSelected = true;
		selectedPositionX = positionX;
		selectedPositionY = positionY;
		selectedFigure = gameField.getAt(positionX, positionY).asFigure();
	}

	private void resetAvailables() {
		for (int i = 0; i < CONSTANTS.GAMEFIELD_COLUMNS; i++)
			for (int j = 0; j < CONSTANTS.GAMEFIELD_ROWS; j++) {
				availablesField[i][j].resetAvailable();
				availablesField[i][j].resetDistanceFromSelectedFigure();
				availablesField[i][j].setVisible(false);
			}
	}

	private boolean isAvailable(int pPositionX, int pPositionY) {
		return availablesField[pPositionX][pPositionY].isAvailable();
	}

	private void setAvailables() {

		int remainingMoves = selectedFigure.getRemainingMoves();

		ArrayList<Available> connectedFields = new ArrayList<Available>();
		ArrayList<Available> newConnectedFields = new ArrayList<Available>();

		if (inBounds(selectedPositionX - 1, selectedPositionY)
				&& gameField.getAt(selectedPositionX - 1, selectedPositionY)
						.isAvailable()
				&& (!(gameField.getAt(selectedPositionX - 1, selectedPositionY)
						.isOpponent() && !selectedFigure.canAttack()))) {
			setAvailableFieldVisible(selectedPositionX - 1, selectedPositionY,
					1);
			connectedFields
					.add((Available) availablesField[selectedPositionX - 1][selectedPositionY]);
		}
		if (inBounds(selectedPositionX + 1, selectedPositionY)
				&& gameField.getAt(selectedPositionX + 1, selectedPositionY)
						.isAvailable()
				&& (!(gameField.getAt(selectedPositionX + 1, selectedPositionY)
						.isOpponent() && !selectedFigure.canAttack()))) {
			setAvailableFieldVisible(selectedPositionX + 1, selectedPositionY,
					1);
			connectedFields
					.add((Available) availablesField[selectedPositionX + 1][selectedPositionY]);
		}
		if (inBounds(selectedPositionX, selectedPositionY - 1)
				&& gameField.getAt(selectedPositionX, selectedPositionY - 1)
						.isAvailable()
				&& (!(gameField.getAt(selectedPositionX, selectedPositionY - 1)
						.isOpponent() && !selectedFigure.canAttack()))) {
			setAvailableFieldVisible(selectedPositionX, selectedPositionY - 1,
					1);
			connectedFields
					.add((Available) availablesField[selectedPositionX][selectedPositionY - 1]);
		}
		if (inBounds(selectedPositionX, selectedPositionY + 1)
				&& gameField.getAt(selectedPositionX, selectedPositionY + 1)
						.isAvailable()
				&& (!(gameField.getAt(selectedPositionX, selectedPositionY + 1)
						.isOpponent() && !selectedFigure.canAttack()))) {
			setAvailableFieldVisible(selectedPositionX, selectedPositionY + 1,
					1);
			connectedFields
					.add((Available) availablesField[selectedPositionX][selectedPositionY + 1]);
		}

		for (int k = 2; k <= remainingMoves; k++) {
			for (int i = -k; i <= k; i++)
				for (int j = -k; j <= k; j++)
					if ((Math.abs(i) + Math.abs(j)) == k) {
						if (inBounds(selectedPositionX + i, selectedPositionY
								+ j)
								&& (gameField.getAt(selectedPositionX + i,
										selectedPositionY + j).isCoins() || (gameField
										.getAt(selectedPositionX + i,
												selectedPositionY + j)
										.isEmpty()))
								&& nextTo(
										availablesField[selectedPositionX + i][selectedPositionY
												+ j], connectedFields)) {
							setAvailableFieldVisible(selectedPositionX + i,
									selectedPositionY + j, k);
							newConnectedFields
									.add((Available) availablesField[selectedPositionX
											+ i][selectedPositionY + j]);
						}
					}
			ArrayList<Available> temp = connectedFields;
			connectedFields = newConnectedFields;
			newConnectedFields = temp;
			newConnectedFields.clear();
		}
	}

	private boolean nextTo(Available available,
			ArrayList<Available> connectedFields) {
		int x = available.mPositionX;
		int y = available.mPositionY;
		for (int i = 0; i < connectedFields.size(); i++) {
			if (x - 1 == connectedFields.get(i).mPositionX
					&& y == connectedFields.get(i).mPositionY
					&& !gameField.getAt(connectedFields.get(i).mPositionX,
							connectedFields.get(i).mPositionY).isFigure())
				return true;
			else if (x + 1 == connectedFields.get(i).mPositionX
					&& y == connectedFields.get(i).mPositionY
					&& !gameField.getAt(connectedFields.get(i).mPositionX,
							connectedFields.get(i).mPositionY).isFigure())
				return true;
			else if (x == connectedFields.get(i).mPositionX
					&& y - 1 == connectedFields.get(i).mPositionY
					&& !gameField.getAt(connectedFields.get(i).mPositionX,
							connectedFields.get(i).mPositionY).isFigure())
				return true;
			else if (x == connectedFields.get(i).mPositionX
					&& y + 1 == connectedFields.get(i).mPositionY
					&& !gameField.getAt(connectedFields.get(i).mPositionX,
							connectedFields.get(i).mPositionY).isFigure())
				return true;
		}
		return false;
	}

	public void moveFigureFromTo(int startX, int startY, int destX, int destY) {
		gameField.moveFigureFromTo(startX, startY, destX, destY);
		gameField.renderPosition(startX, startY);
		gameField.renderPosition(destX, destY);
	}

	public void attackFieldFromTo(int startX, int startY, int destX, int destY) {
		int figureType = gameField.getAt(destX, destY).asFigure().getType();
		int price = 0;
		switch (figureType) {
		case CONSTANTS.FRIENDLY_KNIGHT:
			price = CONSTANTS.KNIGHT_PRICE;
			break;
		case CONSTANTS.ENEMY_KNIGHT:
			price = CONSTANTS.KNIGHT_PRICE;
			break;
		case CONSTANTS.FRIENDLY_WITCH:
			price = CONSTANTS.WITCH_PRICE;
			break;
		case CONSTANTS.ENEMY_WITCH:
			price = CONSTANTS.WITCH_PRICE;
			break;
		case CONSTANTS.FRIENDLY_DUCK:
			price = CONSTANTS.DUCK_PRICE;
			break;
		case CONSTANTS.ENEMY_DUCK:
			price = CONSTANTS.DUCK_PRICE;
			break;
		}
		boolean destroyed = gameField.attackFigureFromTo(startX, startY, destX,
				destY);
		gameField.renderPosition(startX, startY);
		gameField.renderPosition(destX, destY);
		if (destroyed) {
			GameManager.getInstance().incPlayerScore(200 * price);
			adjustScoreText();
		}

	}

	private void buyFigure(Figure pFigure, int positionX, int positionY) {
		gameField.buyFigure(pFigure, positionX, positionY);
		gameField.renderPosition(positionX, positionY);
	}

	public void collectCoinsFromTo(int startX, int startY, int destX, int destY) {
		int count = gameField.getAt(destX, destY).asPickup().getQuantity();
		gameField.collectCoinsFromTo(startX, startY, destX, destY);
		gameField.renderPosition(startX, startY);
		gameField.renderPosition(destX, destY);
		adjustCoinCountText();
		GameManager.getInstance().incPlayerScore(100 * count);
		adjustScoreText();
	}

	public void adjustCoinCountText() {
		if (coinCountText != null) {
			String text = "COINS: "+ String.valueOf(gameField.getPlayerCoins());
			coinCountText.setText(text);
		}

	}

	public void setAvailableFieldVisible(int pPositionX, int pPositionY,
			int pDistance) {
		availablesField[pPositionX][pPositionY].setVisible(true);
		availablesField[pPositionX][pPositionY]
				.setDistanceFromSelectedFigure(pDistance);
		availablesField[pPositionX][pPositionY].setAvailable(true);
	}

	public boolean inBounds(int pPositionX, int pPositionY) {
		if (pPositionX >= 0 && pPositionX < CONSTANTS.GAMEFIELD_COLUMNS
				&& pPositionY >= 0 && pPositionY < CONSTANTS.GAMEFIELD_ROWS)
			return true;
		return false;
	}

	public void buyKnight() {
		selectedFigure = PieceFactory.makeFriendlyKnight(-1, -1, this);
		setAvailablesForBuying();
		buyMode = true;
		figureSelected = true;
	}

	public void buyWitch() {
		selectedFigure = PieceFactory.makeFriendlyWitch(-1, -1, this);
		setAvailablesForBuying();
		buyMode = true;
		figureSelected = true;
	}

	public void buyDuck() {
		selectedFigure = PieceFactory.makeFriendlyDuck(-1, -1, this);
		setAvailablesForBuying();
		buyMode = true;
		figureSelected = true;
	}

	private void setAvailablesForBuying() {
		for (int i = 0; i < CONSTANTS.PLANTABLE_COLUMNS; i++)
			for (int j = CONSTANTS.START_PLANTABLE_ROW; j <= CONSTANTS.END_PLANTABLE_ROW; j++)
				if (gameField.getAt(i, j).isEmpty())
					setAvailableFieldVisible(i, j, 0);
	}

	private void endTurn() {
		ArrayList<Move> list = Thinker.getBestMove(new CompressedState(
				CompressedGameField.compress(gameField), new ArrayList<Move>(),
				CONSTANTS.PLAYER));
		gameField.endTurn(CONSTANTS.PLAYER);
		gameField.executeMoves(list);
		gameField.endTurn(CONSTANTS.ENEMY);
		gameField.renderVisualState();
		adjustCoinCountText();
		adjustScoreText();
		if (gameField.isGameOver()) {
			initiateGameOver();
		}
		usedAFigure = false;
		figureUsed = null;
		GameManager.getInstance().incTurnCount();
		if (gameField.getPlayerPieces().size() < CONSTANTS.MAX_UNIT_COUNT) {
			NewUnitButton.setEnabled(true);
		}
	}

	private void initiateGameOver() {
		GameManager.getInstance().setGameOver(gameField.isGameOver());
		GameManager.getInstance().setWhoWon(gameField.whoWon());
		GameManager.getInstance().incPlayerScore(CONSTANTS.WIN_SCORE_BONUS);
		SceneManager.getInstance().showEndGameLayer(true);
	}
}
