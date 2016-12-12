package com.example.rockpapperscissors.DecisionMaking;

import java.util.ArrayList;
import com.example.rockpapperscissors.CONSTANTS;
import com.example.rockpapperscissors.fields.FieldItem;
import com.example.rockpapperscissors.fields.Figure;
import com.example.rockpapperscissors.fields.GameField;
import com.example.rockpapperscissors.fields.Pickup;

public class CompressedGameField {
	public static final byte NUM_FIELDS = 9;
	public static final byte TYPE_FIELD_IDX = 0;
	public static final byte STARTING_HEALTH_FIELD_IDX = 1;
	public static final byte STARTING_MOVES_FIELD_IDX = 2;
	public static final byte REMAINING_MOVES_FIELD_IDX = 3;
	public static final byte REMAINING_HEALTH_FIELD_IDX = 4;
	public static final byte QUANTITY_FIELD_IDX = 5;
	public static final byte XINDEX_FIELD_IDX = 6;
	public static final byte YINDEX_FIELD_IDX = 7;
	public static final byte CAN_ATTACK_FIELD_IDX = 8;

	private byte[][][] gameField = new byte[CONSTANTS.GAMEFIELD_COLUMNS][CONSTANTS.GAMEFIELD_ROWS][NUM_FIELDS];
	private ArrayList<byte[]> playerPieces = new ArrayList<byte[]>(CONSTANTS.MAX_UNIT_COUNT);
	private ArrayList<byte[]> opponentPieces = new ArrayList<byte[]>(CONSTANTS.MAX_UNIT_COUNT);
	private ArrayList<byte[]> pickups = new ArrayList<byte[]>(CONSTANTS.MAX_COIN_COUNT);

	private int initialNumPickups;

	private int playerCoins;
	private int opponentCoins;
	private int minPiecePrice = Math.min(CONSTANTS.DUCK_PRICE,
			Math.min(CONSTANTS.KNIGHT_PRICE, CONSTANTS.WITCH_PRICE));

	public CompressedGameField() {
	}

	public static CompressedGameField compress(GameField pGameField) {
		CompressedGameField newGameField = new CompressedGameField();
		FieldItem fi;
		for (byte i = 0; i < CONSTANTS.GAMEFIELD_COLUMNS; i++) {
			for (byte j = 0; j < CONSTANTS.GAMEFIELD_ROWS; j++) {
				fi = pGameField.getAt(i, j);
				for (byte k = 0; k < NUM_FIELDS; k++) {
					newGameField.gameField[i][j][k] = 0;
				}
				newGameField.gameField[i][j][TYPE_FIELD_IDX] = (byte) fi
						.getType();
				newGameField.gameField[i][j][XINDEX_FIELD_IDX] = i;
				newGameField.gameField[i][j][YINDEX_FIELD_IDX] = j;
				if (pGameField.getAt(i, j).isFigure()) {
					Figure f = fi.asFigure();
					newGameField.gameField[i][j][STARTING_HEALTH_FIELD_IDX] = (byte) f
							.getStartingHealth();
					newGameField.gameField[i][j][STARTING_MOVES_FIELD_IDX] = (byte) f
							.getStartingMoves();
					newGameField.gameField[i][j][REMAINING_HEALTH_FIELD_IDX] = (byte) f
							.getRemainingHealth();
					newGameField.gameField[i][j][REMAINING_MOVES_FIELD_IDX] = (byte) f
							.getRemainingMoves();
					newGameField.gameField[i][j][CAN_ATTACK_FIELD_IDX] = (byte) (f
							.canAttack() ? 1 : 0);
					if (f.isPlayer()) {
						newGameField.playerPieces
								.add(newGameField.gameField[i][j]);
					} else {
						newGameField.opponentPieces
								.add(newGameField.gameField[i][j]);

					}
				}
				if (pGameField.getAt(i, j).isPickup()) {
					Pickup p = fi.asPickup();
					newGameField.gameField[i][j][QUANTITY_FIELD_IDX] = (byte) p
							.getQuantity();
					newGameField.pickups.add(newGameField.gameField[i][j]);
				}
			}
		}

		newGameField.playerCoins = pGameField.getPlayerCoins();
		newGameField.opponentCoins = pGameField.getOpponentCoins();
		newGameField.initialNumPickups = newGameField.pickups.size();
		return newGameField;
	}

	public CompressedGameField(CompressedGameField source) {
		gameField = new byte[CONSTANTS.GAMEFIELD_COLUMNS][CONSTANTS.GAMEFIELD_ROWS][NUM_FIELDS];
		for (byte i = 0; i < CONSTANTS.GAMEFIELD_COLUMNS; i++)
			for (byte j = 0; j < CONSTANTS.GAMEFIELD_ROWS; j++)
				System.arraycopy(source.gameField[i][j], 0, gameField[i][j], 0,
						NUM_FIELDS);

		assignPiecesToArrays();
		playerCoins = source.playerCoins;
		opponentCoins = source.opponentCoins;
		initialNumPickups = source.initialNumPickups;
	}

	public static boolean isOpponent(byte[] piece) {
		return ((piece[TYPE_FIELD_IDX] == CONSTANTS.ENEMY_KNIGHT)
				|| (piece[TYPE_FIELD_IDX] == CONSTANTS.ENEMY_WITCH) || (piece[TYPE_FIELD_IDX] == CONSTANTS.ENEMY_DUCK));

	}

	public static boolean isPlayer(byte[] piece) {
		return ((piece[TYPE_FIELD_IDX] == CONSTANTS.FRIENDLY_KNIGHT)
				|| (piece[TYPE_FIELD_IDX] == CONSTANTS.FRIENDLY_WITCH) || (piece[TYPE_FIELD_IDX] == CONSTANTS.FRIENDLY_DUCK));
	}

	public static boolean isFigure(byte[] piece) {
		return (isPlayer(piece) || isOpponent(piece));
	}

	public static boolean inSameTeam(byte[] piece1, byte[] piece2) {
		return ((isPlayer(piece1) && isPlayer(piece2)) || (isOpponent(piece1) && isOpponent(piece2)));
	}

	public static boolean inDifferentTeam(byte[] piece1, byte[] piece2) {
		return ((isPlayer(piece1) && isOpponent(piece2)) || (isOpponent(piece1) && isPlayer(piece2)));
	}

	public static boolean isEmpty(byte[] piece) {
		return (piece[TYPE_FIELD_IDX] == CONSTANTS.EMPTY);
	}

	public static boolean isCoins(byte[] piece) {
		return (piece[TYPE_FIELD_IDX] == CONSTANTS.COINS);
	}

	public boolean isOpponent(byte idxX, byte idxY) {
		return ((gameField[idxX][idxY][TYPE_FIELD_IDX] == CONSTANTS.ENEMY_KNIGHT)
				|| (gameField[idxX][idxY][TYPE_FIELD_IDX] == CONSTANTS.ENEMY_WITCH) || (gameField[idxX][idxY][TYPE_FIELD_IDX] == CONSTANTS.ENEMY_DUCK));

	}

	public boolean isPlayer(byte idxX, byte idxY) {
		return ((gameField[idxX][idxY][TYPE_FIELD_IDX] == CONSTANTS.FRIENDLY_KNIGHT)
				|| (gameField[idxX][idxY][TYPE_FIELD_IDX] == CONSTANTS.FRIENDLY_WITCH) || (gameField[idxX][idxY][TYPE_FIELD_IDX] == CONSTANTS.FRIENDLY_DUCK));
	}

	public boolean isFigure(byte idxX, byte idxY) {
		return (isPlayer(gameField[idxX][idxY]) || isOpponent(gameField[idxX][idxY]));
	}

	public boolean inSameTeam(byte idxX1, byte idxY1, byte idxX2, byte idxY2) {
		return ((isPlayer(gameField[idxX1][idxY1]) && isPlayer(gameField[idxX2][idxY2])) || (isOpponent(gameField[idxX1][idxY1]) && isOpponent(gameField[idxX2][idxY2])));
	}

	public boolean inDifferentTeam(byte idxX1, byte idxY1, byte idxX2,
			byte idxY2) {
		return ((isPlayer(gameField[idxX1][idxY1]) && isOpponent(gameField[idxX2][idxY2])) || (isOpponent(gameField[idxX1][idxY1]) && isPlayer(gameField[idxX2][idxY2])));
	}

	public boolean isEmpty(byte idxX, byte idxY) {
		return (gameField[idxX][idxY][TYPE_FIELD_IDX] == CONSTANTS.EMPTY);
	}

	public boolean isCoins(byte idxX, byte idxY) {
		return (gameField[idxX][idxY][TYPE_FIELD_IDX] == CONSTANTS.COINS);
	}
	
	public boolean isImpassable(byte idxX, byte idxY) {
		return (gameField[idxX][idxY][TYPE_FIELD_IDX]==CONSTANTS.IMPASSABLE);
	}

	private void assignPiecesToArrays() {
		if (opponentPieces != null) {
			opponentPieces.clear();
		} else {
			opponentPieces = new ArrayList<byte[]>(CONSTANTS.MAX_UNIT_COUNT);
		}
		if (playerPieces != null) {
			playerPieces.clear();
		} else {
			playerPieces = new ArrayList<byte[]>(CONSTANTS.MAX_UNIT_COUNT);
		}
		if (pickups != null) {
			pickups.clear();
		} else {
			pickups = new ArrayList<byte[]>(CONSTANTS.MAX_COIN_COUNT);
		}
		for (byte i = 0; i < CONSTANTS.GAMEFIELD_COLUMNS; i++) {
			for (byte j = 0; j < CONSTANTS.GAMEFIELD_ROWS; j++) {
				if (isOpponent(gameField[i][j])) {
					opponentPieces.add(gameField[i][j]);
				} else if (isPlayer(gameField[i][j])) {
					playerPieces.add(gameField[i][j]);
				} else if (isCoins(gameField[i][j])) {
					pickups.add(gameField[i][j]);
				}
			}
		}
		return;
	}

	public byte[] getAt(int pI, int pJ) {
		return gameField[pI][pJ];
	}

	public void setAt(int pI, int pJ, byte[] pPiece) {
		gameField[pI][pJ] = pPiece;
	}

	private void resetMoves(byte[] piece) {
		piece[REMAINING_MOVES_FIELD_IDX] = piece[STARTING_MOVES_FIELD_IDX];
	}

	private void resetCanAttack(byte[] piece) {
		piece[CAN_ATTACK_FIELD_IDX] = 1;
	}

	public void renewMoves(int whooseTurn) {
		if (whooseTurn == CONSTANTS.PLAYER) {
			for (byte[] p : playerPieces) {
				resetMoves(p);
				resetCanAttack(p);
			}
		} else {
			for (byte[] p : opponentPieces) {
				resetMoves(p);
				resetCanAttack(p);
			}
		}

	}

	public void repairFieldItemIndecies() {
		for (byte i = 0; i < CONSTANTS.GAMEFIELD_COLUMNS; i++)
			for (byte j = 0; j < CONSTANTS.GAMEFIELD_ROWS; j++) {
				gameField[i][j][XINDEX_FIELD_IDX] = i;
				gameField[i][j][YINDEX_FIELD_IDX] = j;
			}
	}

	public void moveFigureFromTo(byte startX, byte startY, byte destX,
			byte destY) {
		byte distance = (byte) (Math.abs(startX - destX) + Math.abs(startY
				- destY));
		swapFieldItems(startX, startY, destX, destY);
		decreaseRemainingMoves(destX, destY, distance);
	}

	public void decreaseRemainingMoves(byte idxX, byte idxY, byte ammount) {
		gameField[idxX][idxY][REMAINING_MOVES_FIELD_IDX] -= ammount;
		if (gameField[idxX][idxY][REMAINING_MOVES_FIELD_IDX] < 0) {
			gameField[idxX][idxY][REMAINING_MOVES_FIELD_IDX] = 0;
		}
	}

	public void decreaseRemainingMoves(byte[] piece, byte ammount) {
		piece[REMAINING_MOVES_FIELD_IDX] -= ammount;
		if (piece[REMAINING_MOVES_FIELD_IDX] < 0) {
			piece[REMAINING_MOVES_FIELD_IDX] = 0;
		}
	}

	public void decreaseRemainingHealth(byte idxX, byte idxY, byte ammount) {
		gameField[idxX][idxY][REMAINING_HEALTH_FIELD_IDX] -= ammount;
		if (gameField[idxX][idxY][REMAINING_HEALTH_FIELD_IDX] < 0) {
			gameField[idxX][idxY][REMAINING_HEALTH_FIELD_IDX] = 0;
		}
	}

	public void decreaseRemainingHealth(byte[] piece, byte ammount) {
		piece[REMAINING_HEALTH_FIELD_IDX] -= ammount;
		if (piece[REMAINING_HEALTH_FIELD_IDX] < 0) {
			piece[REMAINING_HEALTH_FIELD_IDX] = 0;
		}
	}

	public void useAttack(byte[] piece) {
		piece[CAN_ATTACK_FIELD_IDX] = 0;
	}

	// returns wether the targetfigure was destroyed
	public boolean attackFigureFromTo(byte startX, byte startY, byte destX,
			byte destY) {
		boolean destroyed = false;
		byte[] attackingPiece = gameField[startX][startY];
		byte[] targetPiece = gameField[destX][destY];
		useAttack(attackingPiece);
		if (advantage(attackingPiece[TYPE_FIELD_IDX],
				targetPiece[TYPE_FIELD_IDX])) {
			decreaseRemainingHealth(targetPiece, (byte) 2);
		} else if (equals(gameField[startX][startY][TYPE_FIELD_IDX],
				gameField[destX][destY][TYPE_FIELD_IDX])) {
			decreaseRemainingHealth(targetPiece, (byte) 1);
		} else {

		}
		if (targetPiece[REMAINING_HEALTH_FIELD_IDX] <= 0) {
			destroyed = true;
			if (isPlayer(targetPiece)) {
				playerPieces.remove(targetPiece);
			} else {
				opponentPieces.remove(targetPiece);
			}
			emptyField(targetPiece);
			moveFigureFromTo(startX, startY, destX, destY);
			if (isPlayer(attackingPiece)) {
				playerCoins += CONSTANTS.VANQUISH_REWARD;
			} else {
				opponentCoins += CONSTANTS.VANQUISH_REWARD;
			}
		}
		decreaseRemainingMoves(attackingPiece, (byte) 2);
		return destroyed;
	}

	public ArrayList<byte[]> getPickups() {
		return pickups;
	}

	public void setPickups(ArrayList<byte[]> pickups) {
		this.pickups = pickups;
	}

	public ArrayList<byte[]> getPlayerPieces() {
		return playerPieces;
	}

	public void setPlayerPieces(ArrayList<byte[]> playerPieces) {
		this.playerPieces = playerPieces;
	}

	public ArrayList<byte[]> getOpponentPieces() {
		return opponentPieces;
	}

	public void setOpponentPieces(ArrayList<byte[]> opponentPieces) {
		this.opponentPieces = opponentPieces;
	}

	public int getInitialNumPickups() {
		return initialNumPickups;
	}

	public void setInitialNumPickups(int initialNumPickups) {
		this.initialNumPickups = initialNumPickups;
	}

	private void emptyField(byte[] piece) {
		byte x = piece[XINDEX_FIELD_IDX];
		byte y = piece[YINDEX_FIELD_IDX];
		for (byte i = 0; i < NUM_FIELDS; i++) {
			piece[i] = 0;
		}
		piece[XINDEX_FIELD_IDX] = x;
		piece[YINDEX_FIELD_IDX] = y;
	}

	private void emptyField(byte destX, byte destY) {
		for (byte i = 0; i < NUM_FIELDS; i++) {
			gameField[destX][destY][i] = 0;
		}
		gameField[destX][destY][XINDEX_FIELD_IDX] = destX;
		gameField[destX][destY][YINDEX_FIELD_IDX] = destY;
	}

	public void collectCoinsFromTo(byte startX, byte startY, byte destX,
			byte destY) {
		if (isPlayer(gameField[startX][startY])) {
			playerCoins += gameField[destX][destY][QUANTITY_FIELD_IDX];
		} else if (isOpponent(gameField[startX][startY])) {
			opponentCoins += gameField[destX][destY][QUANTITY_FIELD_IDX];
		}
		pickups.remove(gameField[destX][destY]);
		emptyField(destX, destY);
		moveFigureFromTo(startX, startY, destX, destY);
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

	public void buyFigure(byte figureType, byte positionX, byte positionY) {
		byte price = 0;
		byte startingHealth = 0;
		byte startingMoves = 0;
		switch (figureType) {
		case CONSTANTS.FRIENDLY_KNIGHT:
			price = CONSTANTS.KNIGHT_PRICE;
			startingHealth = CONSTANTS.KNIGHT_STARTING_HEALTH;
			startingMoves = CONSTANTS.KNIGHT_STARTING_MOVES;
			break;
		case CONSTANTS.ENEMY_KNIGHT:
			price = CONSTANTS.KNIGHT_PRICE;
			startingHealth = CONSTANTS.KNIGHT_STARTING_HEALTH;
			startingMoves = CONSTANTS.KNIGHT_STARTING_MOVES;
			break;
		case CONSTANTS.FRIENDLY_WITCH:
			price = CONSTANTS.WITCH_PRICE;
			startingHealth = CONSTANTS.WITCH_STARTING_HEALTH;
			startingMoves = CONSTANTS.WITCH_STARTING_MOVES;
			break;
		case CONSTANTS.ENEMY_WITCH:
			price = CONSTANTS.WITCH_PRICE;
			startingHealth = CONSTANTS.WITCH_STARTING_HEALTH;
			startingMoves = CONSTANTS.WITCH_STARTING_MOVES;
			break;
		case CONSTANTS.FRIENDLY_DUCK:
			price = CONSTANTS.DUCK_PRICE;
			startingHealth = CONSTANTS.DUCK_STARTING_HEALTH;
			startingMoves = CONSTANTS.DUCK_STARTING_MOVES;
			break;
		case CONSTANTS.ENEMY_DUCK:
			price = CONSTANTS.DUCK_PRICE;
			startingHealth = CONSTANTS.DUCK_STARTING_HEALTH;
			startingMoves = CONSTANTS.DUCK_STARTING_MOVES;
			break;
		default:
			return;
		}

		emptyField(positionX, positionY);
		gameField[positionX][positionY][TYPE_FIELD_IDX] = figureType;
		gameField[positionX][positionY][STARTING_HEALTH_FIELD_IDX] = startingHealth;
		gameField[positionX][positionY][STARTING_MOVES_FIELD_IDX] = startingMoves;
		gameField[positionX][positionY][REMAINING_HEALTH_FIELD_IDX] = startingHealth;
		gameField[positionX][positionY][REMAINING_MOVES_FIELD_IDX] = 0;
		gameField[positionX][positionY][XINDEX_FIELD_IDX] = positionX;
		gameField[positionX][positionY][YINDEX_FIELD_IDX] = positionY;
		gameField[positionX][positionY][CAN_ATTACK_FIELD_IDX] = 0;

		if (isPlayer(gameField[positionX][positionY])) {
			setPlayerCoins(getPlayerCoins() - price);
			playerPieces.add(gameField[positionX][positionY]);
		}
		if (isOpponent(gameField[positionX][positionY])) {
			setOpponentCoins(getOpponentCoins() - price);
			opponentPieces.add(gameField[positionX][positionY]);
		}
	}

	@SuppressWarnings("unused")
	private void replaceAt(byte positionX, byte positionY, byte[] piece) {
		setAt(positionX, positionY, piece);
		piece[XINDEX_FIELD_IDX] = positionX;
		piece[YINDEX_FIELD_IDX] = positionY;
	}

	public void swapFieldItems(byte i1, byte j1, byte i2, byte j2) {
		byte[] temp = gameField[i1][j1];
		gameField[i1][j1] = gameField[i2][j2];
		gameField[i2][j2] = temp;
		gameField[i1][j1][XINDEX_FIELD_IDX] = i1;
		gameField[i1][j1][YINDEX_FIELD_IDX] = j1;
		gameField[i2][j2][XINDEX_FIELD_IDX] = i2;
		gameField[i2][j2][YINDEX_FIELD_IDX] = j2;
		return;
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

	public void endTurn(int whooseTurn) {
		if (whooseTurn == CONSTANTS.PLAYER) {
			playerCoins += CONSTANTS.BASE_TURN_INCOME;
		} else {
			opponentCoins += CONSTANTS.BASE_TURN_INCOME;
		}
		renewMoves(whooseTurn);
	}

	public boolean isGameOver() {
		return ((playerPieces.size() == 0) || (opponentPieces.size() == 0));
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

	public boolean hasCoins() {
		return (pickups.size() > 0);
	}

	public byte minInterFigureDistance() {
		byte minInterFigureDistance = Byte.MAX_VALUE;
		for (byte[] opponentPiece : opponentPieces)
			for (byte[] playerPiece : playerPieces) {
				byte distance = (byte) (Math
						.abs(opponentPiece[XINDEX_FIELD_IDX]
								- playerPiece[XINDEX_FIELD_IDX]) + Math
						.abs(opponentPiece[YINDEX_FIELD_IDX]
								- playerPiece[YINDEX_FIELD_IDX]));
				if (distance < minInterFigureDistance)
					minInterFigureDistance = distance;
			}
		return minInterFigureDistance;
	}



}
