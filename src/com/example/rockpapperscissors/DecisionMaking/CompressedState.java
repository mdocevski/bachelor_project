package com.example.rockpapperscissors.DecisionMaking;

import java.util.ArrayList;

import com.example.rockpapperscissors.BytePair;
import com.example.rockpapperscissors.CONSTANTS;
import com.example.rockpapperscissors.Managers.GameManager;

public class CompressedState {
	int whoseTurn;
	CompressedGameField mGameField;
	byte[] usedFigure = null;
	ArrayList<Move> mMoves = new ArrayList<Move>();
	ArrayList<CompressedState> mFollowingStates = new ArrayList<CompressedState>();

	public CompressedState(CompressedGameField pGameField,
			ArrayList<Move> pMove, int pWhoseTurn) {
		mGameField = pGameField;
		whoseTurn = pWhoseTurn;
		mMoves = pMove;
	}

	public static CompressedState getNewInstace(CompressedState source) {
		CompressedState newState = new CompressedState(new CompressedGameField(
				source.mGameField), new ArrayList<Move>(source.mMoves),
				source.whoseTurn);
		return newState;
	}

	public byte[] getUsedFigure() {
		return usedFigure;
	}

	public void setUsedFigure(byte[] usedFigure) {
		this.usedFigure = usedFigure;
	}

	public double getValue() {
		double value;

		double playerValue = mGameField.getPlayerCoins();
		if (GameManager.getInstance().isGreedines()) {
			playerValue *= 2;
		}
		for (byte[] f : mGameField.getPlayerPieces()) {
			playerValue += evaluatePiece(f);
		}

		double opponentValue = mGameField.getOpponentCoins();
		if (GameManager.getInstance().isGreedines()) {
			opponentValue *= 2;
		}
		for (byte[] f : mGameField.getOpponentPieces()) {
			opponentValue += evaluatePiece(f);
		}

		value = opponentValue - playerValue;

		if (mGameField.isGameOver()) {
			if (mGameField.whoWon() == CONSTANTS.PLAYER)
				value -= CONSTANTS.WIN_SCORE_BONUS;
			if (mGameField.whoWon() == CONSTANTS.ENEMY)
				value += CONSTANTS.WIN_SCORE_BONUS;
		}

		// IF IT'S THE PLAYERS TURN THE VALUE SHOULD REPRESENT HIS POSITIN
		if (whoseTurn == CONSTANTS.ENEMY) {
			value *= -1;
		}
		// ELSE IT'S THE COMPUTERS TURN THE VALUE IS ALREADY REPRESENTED
		return value;
	}

	private double evaluatePiece(byte[] f) {
		double pieceValue = 0;
		switch (f[CompressedGameField.TYPE_FIELD_IDX]) {
		case CONSTANTS.FRIENDLY_KNIGHT:
		case CONSTANTS.ENEMY_KNIGHT:
			pieceValue = CONSTANTS.KNIGHT_PRICE;
			break;
		case CONSTANTS.FRIENDLY_WITCH:
		case CONSTANTS.ENEMY_WITCH:
			pieceValue = CONSTANTS.WITCH_PRICE;
			break;
		case CONSTANTS.FRIENDLY_DUCK:
		case CONSTANTS.ENEMY_DUCK:
			pieceValue = CONSTANTS.DUCK_PRICE;
			break;
		default:
			break;
		}
		pieceValue *= (1 + ((f[CompressedGameField.REMAINING_HEALTH_FIELD_IDX] / f[CompressedGameField.STARTING_HEALTH_FIELD_IDX])));
		return pieceValue;
	}

	protected void listMovesForFigure(byte[] usedFigure) {
		byte startX = usedFigure[CompressedGameField.XINDEX_FIELD_IDX];
		byte startY = usedFigure[CompressedGameField.YINDEX_FIELD_IDX];
		CompressedState upState = null, downState = null, rightState = null, leftState = null;
		int remainingMoves = usedFigure[CompressedGameField.REMAINING_MOVES_FIELD_IDX];
		if (remainingMoves > 0) {
			// TRY RIGHT
			byte currentX = (byte) (startX + (byte) 1);
			byte currentY = startY;
			if (inBounds(currentX, currentY)) {
				// EDNA FIGURA PO POTEG
				rightState = doAction(startX, startY, currentX, currentY);
				if (null != rightState
						&& rightState.usedFigure[CompressedGameField.REMAINING_MOVES_FIELD_IDX] > 0) {
					rightState.listMovesForFigure(rightState.usedFigure);
				}
			}
			// TRY LEFT
			currentX = (byte) (startX - (byte) 1);
			currentY = startY;
			if (inBounds(currentX, currentY)) {
				leftState = doAction(startX, startY, currentX, currentY);

				if (null != leftState
						&& leftState.usedFigure[CompressedGameField.REMAINING_MOVES_FIELD_IDX] > 0) {
					leftState.listMovesForFigure(leftState.usedFigure);
				}
			}
			// TRY UP
			currentX = startX;
			currentY = (byte) (startY + 1);
			if (inBounds(currentX, currentY)) {
				upState = doAction(startX, startY, currentX, currentY);
				if (null != upState
						&& upState.usedFigure[CompressedGameField.REMAINING_MOVES_FIELD_IDX] > 0) {
					upState.listMovesForFigure(upState.usedFigure);
				}
			}
			// TRY DOWN
			currentX = startX;
			currentY = (byte) (startY - 1);
			if (inBounds(currentX, currentY)) {
				downState = doAction(startX, startY, currentX, currentY);
				if (null != downState
						&& downState.usedFigure[CompressedGameField.REMAINING_MOVES_FIELD_IDX] > 0) {
					downState.listMovesForFigure(downState.usedFigure);
				}
			}
			if (null != upState) {
				mFollowingStates.add(upState);
			}
			if (null != downState) {
				mFollowingStates.add(downState);
			}
			if (null != leftState) {
				mFollowingStates.add(leftState);
			}
			if (null != rightState) {
				mFollowingStates.add(rightState);
			}
		}
	}

	/*
	 * Treba da gu izvrzi navedenu akciju da napravi novu sostojbu i ako moze da
	 * pravi joste moves da gi napravi, i da gi dodade na tekvono izvrsen poteg,
	 * i da vrati sve nazad
	 */
	protected CompressedState doAction(byte startX, byte startY, byte destX,
			byte destY) {
		CompressedState newState = null;
		if (mGameField.isEmpty(destX, destY)
				&& notReturningToPrevious(destX, destY,
						(byte) CONSTANTS.MOVE_TYPE_MOVE)) {
			newState = getNewInstace(this);
			if (newState.usedFigure == null)
				newState.usedFigure = newState.mGameField.getAt(startX, startY);
			newState.mGameField.moveFigureFromTo(startX, startY, destX, destY);
			newState.mMoves.add(MoveFactory.makeMoveTypeMove(startX, startY,
					destX, destY));
		} else if (mGameField.isCoins(destX, destY)) {
			newState = getNewInstace(this);
			if (newState.usedFigure == null)
				newState.usedFigure = newState.mGameField.getAt(startX, startY);
			newState.mGameField
					.collectCoinsFromTo(startX, startY, destX, destY);
			newState.mMoves.add(MoveFactory.makeCollectTypeMove(startX, startY,
					destX, destY));
		} else if (mGameField.inDifferentTeam(startX, startY, destX, destY)
				&& mGameField.getAt(startX, startY)[CompressedGameField.CAN_ATTACK_FIELD_IDX] == 1) {
			newState = getNewInstace(this);
			if (newState.usedFigure == null)
				newState.usedFigure = newState.mGameField.getAt(startX, startY);
			newState.mGameField
					.attackFigureFromTo(startX, startY, destX, destY);
			newState.mMoves.add(MoveFactory.makeAttackTypeMove(startX, startY,
					destX, destY));
		} else {
			newState = null;
		}
		return newState;
	}

	private boolean notReturningToPrevious(byte destX, byte destY, byte moveType) {
		if (mMoves.size() == 0)
			return true;
		else {
			Move m = mMoves.get(mMoves.size() - 1);
			if (m.mStartX == destX && m.mStartY == destY
					&& moveType == m.mMoveType) {
				return false;
			} else {
				return true;
			}
		}
	}

	public boolean inBounds(byte pPositionX, byte pPositionY) {
		if (pPositionX >= 0 && pPositionX < CONSTANTS.GAMEFIELD_COLUMNS
				&& pPositionY >= 0 && pPositionY < CONSTANTS.GAMEFIELD_ROWS)
			return true;
		return false;
	}

	protected ArrayList<CompressedState> followingStateTreeToList() {
		ArrayList<CompressedState> rez = new ArrayList<CompressedState>();
		for (CompressedState s : mFollowingStates) {
			if (s.mFollowingStates.size() != 0) {
				rez.addAll(s.followingStateTreeToList());
			}
			rez.add(s);
		}
		return rez;
	}

	public int getWhoseTurn() {
		return whoseTurn;
	}

	public void setWhoseTurn(int whoseTurn) {
		this.whoseTurn = whoseTurn;
	}

	public CompressedGameField getGameField() {
		return mGameField;
	}

	public void setGameField(CompressedGameField mGameField) {
		this.mGameField = mGameField;
	}

	public ArrayList<Move> getMoves() {
		return mMoves;
	}

	public void setMoves(ArrayList<Move> mMoves) {
		this.mMoves = mMoves;
	}

	public ArrayList<CompressedState> getFollowingStates() {
		return mFollowingStates;
	}

	public void setFollowingStates(ArrayList<CompressedState> mFollowingStates) {
		this.mFollowingStates = mFollowingStates;
	}

	public void listAllMoves() {
		if (whoseTurn == CONSTANTS.PLAYER) {
			for (byte[] startingPiece : mGameField.getPlayerPieces()) {
				listMovesForFigure(startingPiece);
			}
			listBuyMoves();
		} else {
			for (byte[] startingPiece : mGameField.getOpponentPieces()) {
				listMovesForFigure(startingPiece);
			}
			listBuyMoves();
		}
		mFollowingStates = followingStateTreeToList();
		return;
	}

	private void listBuyMoves() {
		ArrayList<BytePair> locations = null;
		if (whoseTurn == CONSTANTS.PLAYER
				&& mGameField.getPlayerPieces().size() < CONSTANTS.MAX_UNIT_COUNT) {
			locations = findPlantableLocations(whoseTurn);
			for (BytePair bp : locations) {
				if (mGameField.isEmpty(bp.x, bp.y)) {
					// IF A TRIANGLE CAN BE BOUGHT BUY ONE
					if (mGameField.getPlayerCoins() >= CONSTANTS.DUCK_PRICE) {
						CompressedState newState = getNewInstace(this);
						newState.mGameField.buyFigure(
								(byte) CONSTANTS.FRIENDLY_DUCK, bp.x, bp.y);
						newState.mMoves.add(MoveFactory.makeBuyTypeMove(
								CONSTANTS.FRIENDLY_DUCK, bp.x, bp.y));
						newState.usedFigure = null;
						mFollowingStates.add(newState);
					}
					// IF A SQUARE CAN BE BOUGHT BUY ONE
					if (mGameField.getPlayerCoins() >= CONSTANTS.WITCH_PRICE) {
						CompressedState newState = getNewInstace(this);
						newState.mGameField.buyFigure(
								(byte) CONSTANTS.FRIENDLY_WITCH, bp.x, bp.y);
						newState.mMoves.add(MoveFactory.makeBuyTypeMove(
								CONSTANTS.FRIENDLY_WITCH, bp.x, bp.y));
						newState.usedFigure = null;
						mFollowingStates.add(newState);
					}
					// IF A SEXTANGLE CAN BE BOUGHT BUY ONE
					if (mGameField.getPlayerCoins() >= CONSTANTS.KNIGHT_PRICE) {
						CompressedState newState = getNewInstace(this);
						newState.mGameField.buyFigure(
								(byte) CONSTANTS.FRIENDLY_KNIGHT, bp.x, bp.y);
						newState.mMoves.add(MoveFactory.makeBuyTypeMove(
								CONSTANTS.FRIENDLY_KNIGHT, bp.x, bp.y));
						newState.usedFigure = null;
						mFollowingStates.add(newState);
					}
				}
			}
		} else if (whoseTurn == CONSTANTS.ENEMY
				&& mGameField.getOpponentPieces().size() < CONSTANTS.MAX_UNIT_COUNT) {
			locations = findPlantableLocations(whoseTurn);
			for (BytePair bp : locations) {
				if (mGameField.isEmpty(bp.x, bp.y)) {
					// IF A TRIANGLE CAN BE BOUGHT BUY ONE
					if (mGameField.getOpponentCoins() >= CONSTANTS.DUCK_PRICE) {
						CompressedState newState = getNewInstace(this);
						newState.mGameField.buyFigure(
								(byte) CONSTANTS.ENEMY_DUCK, bp.x, bp.y);
						newState.mMoves.add(MoveFactory.makeBuyTypeMove(
								CONSTANTS.ENEMY_DUCK, bp.x, bp.y));
						newState.usedFigure = null;
						mFollowingStates.add(newState);
					}
					// IF A SQUARE CAN BE BOUGHT BUY ONE
					if (mGameField.getOpponentCoins() >= CONSTANTS.WITCH_PRICE) {
						CompressedState newState = getNewInstace(this);
						newState.mGameField.buyFigure(
								(byte) CONSTANTS.ENEMY_WITCH, bp.x, bp.y);
						newState.mMoves.add(MoveFactory.makeBuyTypeMove(
								CONSTANTS.ENEMY_WITCH, bp.x, bp.y));
						newState.usedFigure = null;
						mFollowingStates.add(newState);
					}
					// IF A SEXTANGLE CAN BE BOUGHT BUY ONE
					if (mGameField.getOpponentCoins() >= CONSTANTS.KNIGHT_PRICE) {
						CompressedState newState = getNewInstace(this);
						newState.mGameField.buyFigure(
								(byte) CONSTANTS.ENEMY_KNIGHT, bp.x, bp.y);
						newState.mMoves.add(MoveFactory.makeBuyTypeMove(
								CONSTANTS.ENEMY_KNIGHT, bp.x, bp.y));
						newState.usedFigure = null;
						mFollowingStates.add(newState);
					}
				}
			}
		}
	}

	public void changeTurn() {
		if (whoseTurn == CONSTANTS.PLAYER) {
			mGameField.endTurn(whoseTurn);
			whoseTurn = CONSTANTS.ENEMY;
		} else {
			mGameField.endTurn(whoseTurn);
			whoseTurn = CONSTANTS.PLAYER;
		}
	}

	public boolean isGameOver() {
		return mGameField.isGameOver();
	}

	public RootTreeNode gameFieldToTreeForPiece(byte[] pRootPiece) {
		return new RootTreeNode(pRootPiece, (byte) 0,
				new byte[CONSTANTS.GAMEFIELD_COLUMNS][CONSTANTS.GAMEFIELD_ROWS])
				.fieldToTree();
	}

	public class RootTreeNode extends TreeNode {

		public TreeNode closestPickup = null;
		public byte closestPickupDistance = Byte.MAX_VALUE;
		public byte closestPickupHopsDistance = Byte.MAX_VALUE;
		public TreeNode closestWeakerEnemy = null;
		public byte closestWeakerEnemyDistance = Byte.MAX_VALUE;
		public byte closestWeakerEnemyHopsDistance = Byte.MAX_VALUE;
		public TreeNode closestEqualEnemy = null;
		public byte closestEqualEnemyDistance = Byte.MAX_VALUE;
		public byte closestEqualEnemyHopsDistance = Byte.MAX_VALUE;
		public TreeNode closestStrongerEnemy = null;
		public byte closestStrongerEnemyDistance = Byte.MAX_VALUE;
		public byte closestStrongerEnemyHopsDistance = Byte.MAX_VALUE;
		public TreeNode furthestFriendly = null;
		public byte furthestFriendlyDistance = Byte.MIN_VALUE;
		public byte furthestFriendlyHopsDistance = Byte.MIN_VALUE;
		public TreeNode closestFriendly = null;
		public byte closestFriendlyDistance = Byte.MAX_VALUE;
		public byte closestFriendlyHopsDistance = Byte.MAX_VALUE;

		public ArrayList<TreeNode> reachableEnemies = new ArrayList<TreeNode>();
		public ArrayList<TreeNode> reachablePickups = new ArrayList<TreeNode>();
		public float avgEnemyDistance = 0f;
		public byte numReachableEnemies = 0;

		public RootTreeNode(byte[] pNode, byte pDepth, byte[][] pCheckedFields) {
			super(pNode, pDepth, pCheckedFields);
		}

		private RootTreeNode fieldToTree() {
			byte hopSize = node[CompressedGameField.STARTING_MOVES_FIELD_IDX];
			checkedFields[node[CompressedGameField.XINDEX_FIELD_IDX]][node[CompressedGameField.YINDEX_FIELD_IDX]] = 1;
			nextStep();
			ArrayList<TreeNode> followers = new ArrayList<TreeNode>(
					followingNodes);
			ArrayList<TreeNode> nextFollowers = new ArrayList<TreeNode>();
			ArrayList<TreeNode> tempRef;
			byte currentDepth = (byte) (depth + 1);
			while (currentDepth < CONSTANTS.MAX_FIELD_DISTANCE) {
				for (TreeNode tn : followers) {
					byte tnHopDistance = (byte) Math.ceil((double) tn.depth
							/ hopSize);
					if (CompressedGameField.isCoins(tn.node)) {
						if (null == closestPickup
								|| (tnHopDistance == closestPickupHopsDistance && tn.node[CompressedGameField.QUANTITY_FIELD_IDX] > closestPickup.node[CompressedGameField.QUANTITY_FIELD_IDX])
								|| (tnHopDistance == closestPickupHopsDistance
										&& tn.node[CompressedGameField.QUANTITY_FIELD_IDX] == closestPickup.node[CompressedGameField.QUANTITY_FIELD_IDX] && tn.depth < closestPickupHopsDistance)) {
							closestPickup = tn;
							closestPickupDistance = tn.depth;
							closestPickupHopsDistance = (byte) Math
									.ceil((double) tn.depth / hopSize);
							reachablePickups.add(tn);
						}
					} else if (CompressedGameField.inDifferentTeam(node,
							tn.node)) {
						numReachableEnemies++;
						avgEnemyDistance += tn.depth;
						reachableEnemies.add(tn);

						if (mGameField.advantage(
								node[CompressedGameField.TYPE_FIELD_IDX],
								tn.node[CompressedGameField.TYPE_FIELD_IDX])) {
							if (null == closestWeakerEnemy
									|| (tnHopDistance == closestWeakerEnemyHopsDistance && tn.node[CompressedGameField.REMAINING_HEALTH_FIELD_IDX] < closestWeakerEnemy.node[CompressedGameField.REMAINING_HEALTH_FIELD_IDX])) {
								closestWeakerEnemy = tn;
								closestWeakerEnemyDistance = tn.depth;
								closestWeakerEnemyHopsDistance = (byte) Math
										.ceil((double) tn.depth / hopSize);
							}
						} else if (mGameField.equals(
								node[CompressedGameField.TYPE_FIELD_IDX],
								tn.node[CompressedGameField.TYPE_FIELD_IDX])) {
							if (null == closestEqualEnemy
									|| (tnHopDistance == closestEqualEnemyHopsDistance && tn.node[CompressedGameField.REMAINING_HEALTH_FIELD_IDX] < closestEqualEnemy.node[CompressedGameField.REMAINING_HEALTH_FIELD_IDX])) {
								closestEqualEnemy = tn;
								closestEqualEnemyDistance = tn.depth;
								closestEqualEnemyHopsDistance = (byte) Math
										.ceil((double) tn.depth / hopSize);
							}
						} else if (mGameField.advantage(
								tn.node[CompressedGameField.TYPE_FIELD_IDX],
								node[CompressedGameField.TYPE_FIELD_IDX])) {
							if (null == closestStrongerEnemy
									|| (tnHopDistance == closestStrongerEnemyHopsDistance && closestStrongerEnemy.node[CompressedGameField.REMAINING_HEALTH_FIELD_IDX] < tn.node[CompressedGameField.REMAINING_HEALTH_FIELD_IDX])) {
								closestStrongerEnemy = tn;
								closestStrongerEnemyDistance = tn.depth;
								closestStrongerEnemyHopsDistance = (byte) Math
										.ceil((double) tn.depth / hopSize);
							}
						}

					} else if (CompressedGameField.inSameTeam(node, tn.node)) {
						if (null == furthestFriendly
								|| (furthestFriendlyDistance <= tn.depth)) {
							furthestFriendly = tn;
							furthestFriendlyDistance = tn.depth;
							furthestFriendlyHopsDistance = (byte) Math
									.ceil((double) tn.depth / hopSize);
						}
						if (null == closestFriendly) {
							closestFriendly = tn;
							closestFriendlyDistance = tn.depth;
							closestFriendlyHopsDistance = (byte) Math
									.ceil((double) tn.depth / hopSize);
						}
					}
					tn.nextStep();
					nextFollowers.addAll(tn.followingNodes);
				}
				tempRef = followers;
				followers = nextFollowers;
				nextFollowers = tempRef;
				nextFollowers.clear();
				currentDepth++;
			}
			avgEnemyDistance /= numReachableEnemies;
			return this;
		}

		public boolean hasClosestPickup() {
			return closestPickup != null;
		}

		public boolean hasClosestWeakerEnemy() {
			return closestWeakerEnemy != null;
		}

		public boolean hasClosestEqualEnemy() {
			return closestEqualEnemy != null;
		}

		public boolean hasClosestStrongerEnemy() {
			return closestStrongerEnemy != null;
		}

		public boolean hasFurthestFriendly() {
			return furthestFriendly != null;
		}

		public boolean hasClosestFriendly() {
			return closestFriendly != null;
		}

	}

	public class TreeNode {
		public byte[] node;
		public ArrayList<TreeNode> followingNodes = new ArrayList<TreeNode>();
		public byte depth;
		public byte[][] checkedFields;

		public TreeNode(byte[] pNode, byte pDepth, byte[][] pCheckedFields) {
			node = pNode;
			depth = pDepth;
			checkedFields = pCheckedFields;
		}

		public void addFollowingNode(TreeNode pNode) {
			followingNodes.add(pNode);
		}

		public ArrayList<TreeNode> pathTo(TreeNode pTreeNode) {
			if (pTreeNode == this) {
				ArrayList<TreeNode> list = new ArrayList<TreeNode>();
				list.add(this);
				return list;
			} else {
				if (followingNodes.size() == 0) {
					return null;
				} else {
					for (TreeNode tn : followingNodes) {
						ArrayList<TreeNode> partialResult = tn
								.pathTo(pTreeNode);
						if (null != partialResult) {
							partialResult.add(this);
							return partialResult;
						}
					}
					return null;
				}
			}
		}

		protected void nextStep() {
			if (depth < CONSTANTS.MAX_FIELD_DISTANCE) {
				byte currentX = node[CompressedGameField.XINDEX_FIELD_IDX];
				byte currentY = node[CompressedGameField.YINDEX_FIELD_IDX];

				// TRY LEFT
				currentX = (byte) (node[CompressedGameField.XINDEX_FIELD_IDX] - 1);
				currentY = node[CompressedGameField.YINDEX_FIELD_IDX];
				checkAndAdd(currentX, currentY, (byte) (depth + 1));

				// TRY RIGHT
				currentX = (byte) (node[CompressedGameField.XINDEX_FIELD_IDX] + 1);
				currentY = node[CompressedGameField.YINDEX_FIELD_IDX];
				checkAndAdd(currentX, currentY, (byte) (depth + 1));

				// TRY DOWN
				currentX = node[CompressedGameField.XINDEX_FIELD_IDX];
				currentY = (byte) (node[CompressedGameField.YINDEX_FIELD_IDX] - 1);
				checkAndAdd(currentX, currentY, (byte) (depth + 1));

				// TRY UP
				currentX = node[CompressedGameField.XINDEX_FIELD_IDX];
				currentY = (byte) (node[CompressedGameField.YINDEX_FIELD_IDX] + 1);
				checkAndAdd(currentX, currentY, (byte) (depth + 1));

			}
		}

		protected void checkAndAdd(byte currentX, byte currentY, byte pDepth) {
			if (inBounds(currentX, currentY)) {
				synchronized (checkedFields) {
					if (checkedFields[currentX][currentY] == 0) {
						checkedFields[currentX][currentY] = 1;
						// if (mGameField.isEmpty(currentX, currentY)
						// || mGameField.isCoins(currentX, currentY)
						// || mGameField.isPlayer(currentX, currentY)) {
						if (!mGameField.isImpassable(currentX, currentY)) {
							addFollowingNode(new TreeNode(mGameField.getAt(
									currentX, currentY), pDepth, checkedFields));
						}
					}
				}
			}
		}
	}

	public ArrayList<BytePair> findPlantableLocations(int whoseTurn) {
		ArrayList<BytePair> locations = new ArrayList<BytePair>();
		if (whoseTurn == CONSTANTS.PLAYER) {
			for (byte x = 0; x < CONSTANTS.PLANTABLE_COLUMNS; x++) {
				for (byte y = CONSTANTS.START_PLANTABLE_ROW; y <= CONSTANTS.END_PLANTABLE_ROW; y++) {
					if (mGameField.isEmpty(x, y)) {
						locations.add(new BytePair(x, y));
					}
				}
			}
		} else if (whoseTurn == CONSTANTS.ENEMY) {
			for (byte x = CONSTANTS.GAMEFIELD_COLUMNS
					- CONSTANTS.PLANTABLE_COLUMNS; x <= CONSTANTS.GAMEFIELD_COLUMNS - 1; x++) {
				for (byte y = CONSTANTS.START_PLANTABLE_ROW; y <= CONSTANTS.END_PLANTABLE_ROW; y++) {
					if (mGameField.isEmpty(x, y)) {
						locations.add(new BytePair(x, y));
					}
				}
			}
		}
		return locations;
	}

}
