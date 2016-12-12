package com.example.rockpapperscissors.fields;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Semaphore;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.FadeInModifier;
import org.andengine.entity.modifier.FadeOutModifier;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.JumpModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.text.Text;

import android.annotation.SuppressLint;
import android.util.Log;

import com.example.rockpapperscissors.CONSTANTS;
import com.example.rockpapperscissors.DecisionMaking.Move;
import com.example.rockpapperscissors.Managers.GameManager;
import com.example.rockpapperscissors.Managers.ResourceManager;

@SuppressLint("UseValueOf")
public class GameField {
	private FieldItem[][] gameField;
	private Scene displayScene;
	private ArrayList<Figure> playerPieces = new ArrayList<Figure>();
	private ArrayList<Figure> opponentPieces = new ArrayList<Figure>();
	private ArrayList<Pickup> pickups = new ArrayList<Pickup>();
	private int playerCoins;
	private int opponentCoins;
	private int minPiecePrice = Math.min(
			Math.min(CONSTANTS.KNIGHT_PRICE, CONSTANTS.WITCH_PRICE),
			CONSTANTS.DUCK_PRICE);
	private Semaphore lock = new Semaphore(1);

	public GameField() {
		gameField = new FieldItem[CONSTANTS.GAMEFIELD_COLUMNS][CONSTANTS.GAMEFIELD_ROWS];
		for (int i = 0; i < CONSTANTS.GAMEFIELD_COLUMNS; i++)
			for (int j = 0; j < CONSTANTS.GAMEFIELD_ROWS; j++)
				gameField[i][j] = PieceFactory.makeEmpty(i, j, displayScene);
	}

	protected GameField(GameField source) {
		gameField = new FieldItem[CONSTANTS.GAMEFIELD_COLUMNS][CONSTANTS.GAMEFIELD_ROWS];
		for (int i = 0; i < CONSTANTS.GAMEFIELD_COLUMNS; i++)
			for (int j = 0; j < CONSTANTS.GAMEFIELD_ROWS; j++) {
				if (source.gameField[i][j].isFigure()) {
					gameField[i][j] = new Figure(
							source.gameField[i][j].asFigure());
				} else if (source.gameField[i][j].isPickup()) {
					gameField[i][j] = new Pickup(
							source.gameField[i][j].asPickup());
					pickups.add(gameField[i][j].asPickup());
				} else {
					gameField[i][j] = new FieldItem(source.gameField[i][j]);
				}
			}
		displayScene = source.displayScene;
		playerCoins = source.playerCoins;
		opponentCoins = source.opponentCoins;
		assignPiecesToArrays();
	}

	private void assignPiecesToArrays() {
		if (opponentPieces != null) {
			opponentPieces.clear();
		} else {
			opponentPieces = new ArrayList<Figure>();
		}
		if (playerPieces != null) {
			playerPieces.clear();
		} else {
			playerPieces = new ArrayList<Figure>();
		}
		if (pickups != null) {
			pickups.clear();
		} else {
			pickups = new ArrayList<Pickup>();
		}
		for (int i = 0; i < CONSTANTS.GAMEFIELD_COLUMNS; i++)
			for (int j = 0; j < CONSTANTS.GAMEFIELD_ROWS; j++) {
				if (gameField[i][j].isFigure()) {
					if (isOpponents(gameField[i][j].asFigure())) {
						opponentPieces.add(gameField[i][j].asFigure());
					} else if (isPlayers(gameField[i][j].asFigure())) {
						playerPieces.add(gameField[i][j].asFigure());
					}
				} else if (gameField[i][j].isPickup()) {
					pickups.add(gameField[i][j].asPickup());
				}
			}
	}

	public FieldItem getAt(int pI, int pJ) {
		return gameField[pI][pJ];
	}

	public void setAt(int pI, int pJ, FieldItem pFieldItem) {
		gameField[pI][pJ] = pFieldItem;
		pFieldItem.setIndexX(pI);
		pFieldItem.setIndexY(pJ);
	}

	public void renderVisualState() {
		for (int i = 0; i < CONSTANTS.GAMEFIELD_COLUMNS; i++) {
			for (int j = 0; j < CONSTANTS.GAMEFIELD_ROWS; j++) {
				renderPosition(i, j);
			}
		}

	}

	public void renewMoves(int whooseTurn) {
		if (whooseTurn == CONSTANTS.PLAYER) {
			for (Figure f : playerPieces) {
				f.resetMoves();
				f.resetCanAttack();
			}
		} else {
			for (Figure f : opponentPieces) {
				f.resetMoves();
				f.resetCanAttack();
			}
		}

	}

	public void repairFieldItemIndecies() {
		for (int i = 0; i < CONSTANTS.GAMEFIELD_COLUMNS; i++)
			for (int j = 0; j < CONSTANTS.GAMEFIELD_ROWS; j++) {
				gameField[i][j].setIndexX(i);
				gameField[i][j].setIndexY(j);
			}
	}

	public void initStartOfGame() {
		Figure figure = PieceFactory.makeFriendlyWitch(0, 3, displayScene);
		replaceAt(0, 2, figure);
		playerPieces.add(figure);
		figure = PieceFactory.makeFriendlyDuck(0, 4, displayScene);
		replaceAt(0, 3, figure);
		playerPieces.add(figure);
		figure = PieceFactory.makeFriendlyKnight(0, 2, displayScene);
		replaceAt(0, 1, figure);
		playerPieces.add(figure);
		figure = PieceFactory.makeEnemyWitch(CONSTANTS.GAMEFIELD_COLUMNS - 1,
				3, displayScene);
		replaceAt(CONSTANTS.GAMEFIELD_COLUMNS - 1, 2, figure);
		opponentPieces.add(figure);
		figure = PieceFactory.makeEnemyKnight(CONSTANTS.GAMEFIELD_COLUMNS - 1,
				4, displayScene);
		replaceAt(CONSTANTS.GAMEFIELD_COLUMNS - 1, 3, figure);
		opponentPieces.add(figure);
		figure = PieceFactory.makeEnemyDuck(CONSTANTS.GAMEFIELD_COLUMNS - 1, 2,
				displayScene);
		replaceAt(CONSTANTS.GAMEFIELD_COLUMNS - 1, 1, figure);
		opponentPieces.add(figure);
		Random r = new Random();

		int numPickups = CONSTANTS.MAX_COIN_COUNT;
		int quantityLeft = 0;

		// GENERATE FOR LEFT SIDE
		while (true) {
			int indexX = 3 - Math.abs(r.nextInt(3));
			int indexY = 2 + Math.abs(r.nextInt(3)) - Math.abs(r.nextInt(3));
			while (gameField[indexX][indexY].getType() != CONSTANTS.EMPTY) {
				indexX = 3 - Math.abs(r.nextInt(3));
				indexY = 2 + Math.abs(r.nextInt(3)) - Math.abs(r.nextInt(3));
			}
			int quantity = 1 + Math.abs(r.nextInt(3));
			while (quantity > numPickups) {
				quantity = 1 + Math.abs(r.nextInt(3));
			}
			quantityLeft += quantity;
			numPickups -= quantity;
			Pickup pickup = PieceFactory.makeCoins(indexX, indexY, quantity,
					displayScene);
			replaceAt(indexX, indexY, pickup);
			pickups.add(gameField[indexX][indexY].asPickup());
			if ((quantityLeft >= CONSTANTS.MAX_COIN_COUNT / 2
					- CONSTANTS.MAX_COIN_SIZE / 2)
					&& (quantityLeft <= CONSTANTS.MAX_COIN_COUNT / 2
							+ CONSTANTS.MAX_COIN_SIZE / 2))
				break;
		}
		// GENERATE FOR RIGHT SIDE
		while (numPickups > 0) {
			int indexX = 4 + Math.abs(r.nextInt(3));
			int indexY = 2 + Math.abs(r.nextInt(3)) - Math.abs(r.nextInt(3));
			while (gameField[indexX][indexY].getType() != CONSTANTS.EMPTY) {
				indexX = 4 + Math.abs(r.nextInt(3));
				indexY = 2 + Math.abs(r.nextInt(3)) - Math.abs(r.nextInt(3));
			}
			int quantity = 1 + Math.abs(r.nextInt(3));
			while (quantity > numPickups) {
				quantity = 1 + Math.abs(r.nextInt(3));
			}
			numPickups -= quantity;
			Pickup pickup = PieceFactory.makeCoins(indexX, indexY, quantity,
					displayScene);
			replaceAt(indexX, indexY, pickup);
			pickups.add(gameField[indexX][indexY].asPickup());
		}
		playerCoins = 0;
		opponentCoins = 0;

		int numImpassables = 3 + (Math.abs(r.nextInt(2)));
		for (int i = 0; i < numImpassables; i++) {
			int indexX = 4 + Math.abs(r.nextInt(4)) - Math.abs(r.nextInt(3));
			int indexY = 2 + Math.abs(r.nextInt(3)) - Math.abs(r.nextInt(3));
			while (gameField[indexX][indexY].getType() != CONSTANTS.EMPTY) {
				indexX = 4 + Math.abs(r.nextInt(4)) - Math.abs(r.nextInt(3));
				indexY = 2 + Math.abs(r.nextInt(3)) - Math.abs(r.nextInt(3));
			}
			FieldItem fieldItem = new FieldItem(indexX, indexY,
					CONSTANTS.IMPASSABLE,
					ResourceManager.impassableTextureRegion, displayScene);
			replaceAt(indexX, indexY, fieldItem);
		}

	}

	public void setDisplayScene(Scene pDisplayScene) {
		displayScene = pDisplayScene;
	}

	public void renderPosition(int positionX, int positionY) {
		getAt(positionX, positionY).renderSpriteToPosition(
				positionX * CONSTANTS.increment + CONSTANTS.startX,
				positionY * CONSTANTS.increment + CONSTANTS.startY);

	}

	public void moveFigureFromTo(int startX, int startY, int destX, int destY) {
		// animateMove(startX, startY, destX, destY);
		int distance = Math.abs(startX - destX) + Math.abs(startY - destY);
		swapFieldItems(startX, startY, destX, destY);
		renderPosition(destX, destY);
		gameField[destX][destY].asFigure().decreaseRemainingMoves(distance);
	}

	private void animateMove(int startX, int startY, int destX, int destY) {
		int distance = Math.abs(startX - destX) + Math.abs(startY - destY);

		Figure movingFigure = getAt(startX, startY).asFigure();
		FieldItem targetSpace = getAt(destX, destY);
		float fromX = movingFigure.getSprite().getX();
		float fromY = movingFigure.getSprite().getY();
		float toX = targetSpace.getSprite().getX();
		float toY = targetSpace.getSprite().getY();
		float duration = distance * 0.5f;
		MoveModifier mod = new MoveModifier(duration, fromX, fromY, toX, toY) {
			int startingIndex;
			@Override
			protected void onModifierStarted(IEntity pItem) {
				super.onModifierStarted(pItem);
				startingIndex = pItem.getZIndex();
				pItem.setZIndex(Integer.MAX_VALUE);
			}

			@Override
			protected void onModifierFinished(IEntity pItem) {
				super.onModifierFinished(pItem);
				pItem.setZIndex(startingIndex);
			}
		};
		movingFigure.getSprite().registerEntityModifier(mod);
	}

	private void animateDamage(int destX, int destY, int damage) {
		FieldItem p = getAt(destX, destY);
		float moveFromX = p.getSprite().getX();
		float moveFromY = p.getSprite().getY();
		float moveToX = moveFromX;
		float moveToY = moveFromY + p.getSprite().getWidth() / 2;
		MoveModifier moveModifier = new MoveModifier(1, moveFromX, moveFromY,
				moveToX, moveToY);
		FadeInModifier fadeIn = new FadeInModifier(0.25f);
		DelayModifier delayModifier = new DelayModifier(0.5f);
		FadeOutModifier fadeOut = new FadeOutModifier(0.25f);
		SequenceEntityModifier fadeModifier = new SequenceEntityModifier(
				fadeIn, delayModifier, fadeOut);
		ParallelEntityModifier popOutModifier = new ParallelEntityModifier(
				moveModifier, fadeModifier);
		Text quantityText;
		if (damage > 0) {
			quantityText = new Text(moveFromX, moveFromY,
					ResourceManager.fontStronke36RedBold, "-"
							+ String.valueOf(damage),
					ResourceManager.getInstance().engine
							.getVertexBufferObjectManager());
			displayScene.attachChild(quantityText);
		} else {
			quantityText = new Text(moveFromX, moveFromY,
					ResourceManager.fontDefault18, "imune",
					ResourceManager.getInstance().engine
							.getVertexBufferObjectManager());
		}
		quantityText.registerEntityModifier(popOutModifier);
	}

	// returns wether the targetfigure was destroyed
	public boolean attackFigureFromTo(int startX, int startY, int destX,
			int destY) {
		// try{
		boolean destroyed = false;
		Figure attackingFigure = gameField[startX][startY].asFigure();
		Figure targetFigure = gameField[destX][destY].asFigure();
		attackingFigure.useAttack();
		if (advantage(attackingFigure.getType(), targetFigure.getType())) {
			targetFigure.decreaseHealth(2);
			animateDamage(destX, destY, 2);
		} else if (equals(attackingFigure.getType(), targetFigure.getType())) {
			targetFigure.decreaseHealth(1);
			animateDamage(destX, destY, 1);
		} else {
			animateDamage(destX, destY, 0);
		}
		if (targetFigure.getRemainingHealth() <= 0) {
			destroyed = true;
			if (isPlayers(targetFigure)) {
				playerPieces.remove(targetFigure);
			} else {
				opponentPieces.remove(targetFigure);
			}
			emptyField(destX, destY);
			renderPosition(destX, destY);
			moveFigureFromTo(startX, startY, destX, destY);
			if (isPlayers(attackingFigure)) {
				playerCoins += CONSTANTS.VANQUISH_REWARD;
			} else {
				opponentCoins += CONSTANTS.VANQUISH_REWARD;
			}
		}
		attackingFigure.decreaseRemainingMoves(2);
		// }
		// catch (Exception e){
		// e.printStackTrace();
		// }
		return destroyed;
	}

	public ArrayList<Figure> getPlayerPieces() {
		return playerPieces;
	}

	public void setPlayerPieces(ArrayList<Figure> playerPieces) {
		this.playerPieces = playerPieces;
	}

	public ArrayList<Figure> getOpponentPieces() {
		return opponentPieces;
	}

	public void setOpponentPieces(ArrayList<Figure> opponentPieces) {
		this.opponentPieces = opponentPieces;
	}

	public boolean isPlayers(Figure pFigure) {
		return (pFigure.getType() == CONSTANTS.FRIENDLY_KNIGHT
				|| pFigure.getType() == CONSTANTS.FRIENDLY_WITCH || pFigure
					.getType() == CONSTANTS.FRIENDLY_DUCK);
	}

	public boolean isOpponents(Figure pFigure) {
		return (pFigure.getType() == CONSTANTS.ENEMY_KNIGHT
				|| pFigure.getType() == CONSTANTS.ENEMY_WITCH || pFigure
					.getType() == CONSTANTS.ENEMY_DUCK);
	}

	private void emptyField(int destX, int destY) {
		Empty empty = PieceFactory.makeEmpty(destX, destY, displayScene);
		replaceAt(destX, destY, empty);
	}

	public void collectCoinsFromTo(int startX, int startY, int destX, int destY) {
		animateCollect(destX, destY);
		if (isPlayers(gameField[startX][startY].asFigure())) {
			playerCoins += gameField[destX][destY].asPickup().getQuantity();

		}
		if (isOpponents(gameField[startX][startY].asFigure())) {
			opponentCoins += gameField[destX][destY].asPickup().getQuantity();
		}
		pickups.remove(gameField[destX][destY].asPickup());
		emptyField(destX, destY);
		renderPosition(destX, destY);
		moveFigureFromTo(startX, startY, destX, destY);
	}

	private void animateCollect(int destX, int destY) {
		Pickup p = getAt(destX, destY).asPickup();
		float moveFromX = p.getSprite().getX();
		float moveFromY = p.getSprite().getY();
		float moveToX = moveFromX;
		float moveToY = moveFromY + p.getSprite().getWidth() / 2;
		MoveModifier moveModifier = new MoveModifier(1, moveFromX, moveFromY,
				moveToX, moveToY);
		FadeInModifier fadeIn = new FadeInModifier(0.25f);
		DelayModifier delayModifier = new DelayModifier(0.5f);
		FadeOutModifier fadeOut = new FadeOutModifier(0.25f);
		SequenceEntityModifier fadeModifier = new SequenceEntityModifier(
				fadeIn, delayModifier, fadeOut);
		ParallelEntityModifier popOutModifier = new ParallelEntityModifier(
				moveModifier, fadeModifier);

		int quantity = p.getQuantity();
		Text quantityText = new Text(moveFromX, moveFromY,
				ResourceManager.fontStronke36BlueBold, "+"
						+ String.valueOf(quantity),
				ResourceManager.getInstance().engine
						.getVertexBufferObjectManager());
		displayScene.attachChild(quantityText);
		quantityText.registerEntityModifier(popOutModifier);
	}

	public int getPlayerCoins() {
		return playerCoins;
	}

	public int getOpponentCoins() {
		return opponentCoins;
	}

	public void setOpponentCoins(int opponentCoins) {
		this.opponentCoins = opponentCoins;
	}

	public void setPlayerCoins(int playerCoins) {
		this.playerCoins = playerCoins;
	}

	public void buyFigure(Figure pFigure, int positionX, int positionY) {
		int price = 0;
		switch (pFigure.getType()) {
		case CONSTANTS.FRIENDLY_KNIGHT:
		case CONSTANTS.ENEMY_KNIGHT:
			price = CONSTANTS.KNIGHT_PRICE;
			break;
		case CONSTANTS.FRIENDLY_WITCH:
		case CONSTANTS.ENEMY_WITCH:
			price = CONSTANTS.WITCH_PRICE;
			break;
		case CONSTANTS.FRIENDLY_DUCK:
		case CONSTANTS.ENEMY_DUCK:
			price = CONSTANTS.DUCK_PRICE;
			break;
		default:
			break;
		}
		replaceAt(positionX, positionY, pFigure);
		pFigure.setRemainingMoves(0);
		if (isPlayers(pFigure)) {
			setPlayerCoins(getPlayerCoins() - price);
			playerPieces.add(pFigure);
		}
		if (isOpponents(pFigure)) {
			setOpponentCoins(getOpponentCoins() - price);
			opponentPieces.add(pFigure);
		}
	}

	public void buyFigure(int figureType, int positionX, int positionY) {
		int price = 0;
		Figure pFigure;
		switch (figureType) {
		case CONSTANTS.FRIENDLY_KNIGHT:
			price = CONSTANTS.KNIGHT_PRICE;
			pFigure = PieceFactory.makeFriendlyKnight(positionX, positionY,
					displayScene);
			break;
		case CONSTANTS.ENEMY_KNIGHT:
			price = CONSTANTS.KNIGHT_PRICE;
			pFigure = PieceFactory.makeEnemyKnight(positionX, positionY,
					displayScene);
			break;
		case CONSTANTS.FRIENDLY_WITCH:
			price = CONSTANTS.WITCH_PRICE;
			pFigure = PieceFactory.makeFriendlyWitch(positionX, positionY,
					displayScene);
			break;
		case CONSTANTS.ENEMY_WITCH:
			price = CONSTANTS.WITCH_PRICE;
			pFigure = PieceFactory.makeEnemyWitch(positionX, positionY,
					displayScene);
			break;
		case CONSTANTS.FRIENDLY_DUCK:
			price = CONSTANTS.DUCK_PRICE;
			pFigure = PieceFactory.makeFriendlyDuck(positionX, positionY,
					displayScene);
			break;
		case CONSTANTS.ENEMY_DUCK:
			price = CONSTANTS.DUCK_PRICE;
			pFigure = PieceFactory.makeEnemyDuck(positionX, positionY,
					displayScene);
			break;
		default:
			return;
		}

		replaceAt(positionX, positionY, pFigure);
		pFigure.setRemainingMoves(0);
		if (isPlayers(pFigure)) {
			setPlayerCoins(getPlayerCoins() - price);
			playerPieces.add(pFigure);
		}
		if (isOpponents(pFigure)) {
			setOpponentCoins(getOpponentCoins() - price);
			opponentPieces.add(pFigure);
		}
	}

	private void replaceAt(int positionX, int positionY, FieldItem pFieldItem) {
		if (gameField[positionX][positionY] != null
				&& null != gameField[positionX][positionY].sprite) {
			gameField[positionX][positionY].detachSprite();
		}
		setAt(positionX, positionY, pFieldItem);
		pFieldItem.setIndexX(positionX);
		pFieldItem.setIndexY(positionY);
	}

	public void swapFieldItems(int i1, int j1, int i2, int j2) {
		FieldItem temp = gameField[i1][j1];
		gameField[i1][j1] = gameField[i2][j2];
		gameField[i2][j2] = temp;
		gameField[i1][j1].setIndexX(i1);
		gameField[i1][j1].setIndexY(j1);
		gameField[i2][j2].setIndexX(i2);
		gameField[i2][j2].setIndexY(j2);
	}

	public boolean advantage(int pAttackingType, int pDefendingType) {
		if ((pAttackingType == CONSTANTS.FRIENDLY_KNIGHT && pDefendingType == CONSTANTS.ENEMY_DUCK)
				|| (pAttackingType == CONSTANTS.ENEMY_KNIGHT && pDefendingType == CONSTANTS.FRIENDLY_DUCK)
				|| (pAttackingType == CONSTANTS.FRIENDLY_WITCH && pDefendingType == CONSTANTS.ENEMY_KNIGHT)
				|| (pAttackingType == CONSTANTS.ENEMY_WITCH && pDefendingType == CONSTANTS.FRIENDLY_KNIGHT)
				|| (pAttackingType == CONSTANTS.FRIENDLY_DUCK && pDefendingType == CONSTANTS.ENEMY_WITCH)
				|| (pAttackingType == CONSTANTS.ENEMY_DUCK && pDefendingType == CONSTANTS.FRIENDLY_WITCH))
			return true;
		else
			return false;
	}

	public boolean equals(int pAttackingType, int pDefendingType) {
		if ((pAttackingType == CONSTANTS.FRIENDLY_KNIGHT && pDefendingType == CONSTANTS.ENEMY_KNIGHT)
				|| (pAttackingType == CONSTANTS.ENEMY_KNIGHT && pDefendingType == CONSTANTS.FRIENDLY_KNIGHT)
				|| (pAttackingType == CONSTANTS.FRIENDLY_WITCH && pDefendingType == CONSTANTS.ENEMY_WITCH)
				|| (pAttackingType == CONSTANTS.ENEMY_WITCH && pDefendingType == CONSTANTS.FRIENDLY_WITCH)
				|| (pAttackingType == CONSTANTS.FRIENDLY_DUCK && pDefendingType == CONSTANTS.ENEMY_DUCK)
				|| (pAttackingType == CONSTANTS.ENEMY_DUCK && pDefendingType == CONSTANTS.FRIENDLY_DUCK))
			return true;
		else
			return false;
	}

	public void setDisplaySceneToAllAndAttach(Scene pScene) {
		setDisplayScene(pScene);
		for (int i = 0; i < CONSTANTS.GAMEFIELD_COLUMNS; i++)
			for (int j = 0; j < CONSTANTS.GAMEFIELD_ROWS; j++) {
				gameField[i][j].setScene(pScene);
				gameField[i][j].attachSprite();
			}
	}

	public void setDisplaySceneToAll(Scene pScene) {
		setDisplayScene(pScene);
		for (int i = 0; i < CONSTANTS.GAMEFIELD_COLUMNS; i++)
			for (int j = 0; j < CONSTANTS.GAMEFIELD_ROWS; j++) {
				gameField[i][j].setScene(pScene);
			}
	}

	public void endTurn(int whooseTurn) {
		if (whooseTurn == CONSTANTS.PLAYER) {
			playerCoins += CONSTANTS.BASE_TURN_INCOME;
		} else {
			opponentCoins += CONSTANTS.BASE_TURN_INCOME;
		}
		renewMoves(whooseTurn);
	}

	/*
	 * Returns a copy of the game field using the copy constructor. warning the
	 * sprite and scene references are not safe, only creates copies of
	 * FieldItems and Piece arrays for use in the TurnProducers
	 */
	public GameField getNewInstanceNoSprites() {
		GameField newGameField = new GameField(this);
		for (int i = 0; i < CONSTANTS.GAMEFIELD_COLUMNS; i++)
			for (int j = 0; j < CONSTANTS.GAMEFIELD_ROWS; j++) {
				newGameField.gameField[i][j].sprite = null;
			}
		return newGameField;
	}

	public boolean isGameOver() {
		return (playerPieces.size() == 0 || opponentPieces.size() == 0);
	}

	public int whoWon() {
		if (!isGameOver()) {
			return -1;
		} else {
			if (playerPieces.size() == 0)
				return CONSTANTS.ENEMY;
			else if (opponentPieces.size() == 0)
				return CONSTANTS.PLAYER;
			else
				return -1;
		}
	}

	public void executeMoves(ArrayList<Move> moves) {
		for (Move m : moves) {
			switch (m.mMoveType) {
			case CONSTANTS.MOVE_TYPE_MOVE:
				moveFigureFromTo(m.mStartX, m.mStartY, m.mDestX, m.mDestY);
				break;
			case CONSTANTS.MOVE_TYPE_ATTACK:
				attackFigureFromTo(m.mStartX, m.mStartY, m.mDestX, m.mDestY);
				break;
			case CONSTANTS.MOVE_TYPE_COLLECT:
				collectCoinsFromTo(m.mStartX, m.mStartY, m.mDestX, m.mDestY);
				break;
			case CONSTANTS.MOVE_TYPE_BUY:
				buyFigure(m.mBuyFigureType, m.mDestX, m.mDestY);
				break;
			}
		}
	}

}
