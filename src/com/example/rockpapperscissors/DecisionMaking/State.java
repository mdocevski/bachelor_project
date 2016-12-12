package com.example.rockpapperscissors.DecisionMaking;

import java.util.ArrayList;

import com.example.rockpapperscissors.CONSTANTS;
import com.example.rockpapperscissors.fields.FieldItem;
import com.example.rockpapperscissors.fields.Figure;
import com.example.rockpapperscissors.fields.GameField;

public class State {
	int whoseTurn;
	GameField mGameField;
	double value;
	Figure usedFigure = null;
	ArrayList<Move> mMoves = new ArrayList<Move>();
	ArrayList<State> mFollowingStates = new ArrayList<State>();

	public State(GameField pGameField, ArrayList<Move> pMove, int pWhoseTurn) {
		mGameField = pGameField;
		whoseTurn = pWhoseTurn;
		mMoves = pMove;
	}

	public static State getNewInstace(State source) {
		State newState = new State(source.mGameField.getNewInstanceNoSprites(),
				new ArrayList<Move>(source.mMoves), source.whoseTurn);
		return newState;
	}

	public Figure getUsedFigure() {
		return usedFigure;
	}

	public void setUsedFigure(Figure usedFigure) {
		this.usedFigure = usedFigure;
	}

	public void evaluateState() {
		double playerValue = mGameField.getPlayerCoins();
		for (Figure f : mGameField.getPlayerPieces()) {
			double pieceValue = 0;
			switch (f.getType()) {
			case CONSTANTS.FRIENDLY_KNIGHT:
				pieceValue = CONSTANTS.KNIGHT_PRICE;
				break;
			case CONSTANTS.FRIENDLY_WITCH:
				pieceValue = CONSTANTS.WITCH_PRICE;
				break;
			case CONSTANTS.FRIENDLY_DUCK:
				pieceValue = CONSTANTS.DUCK_PRICE;
				break;
			default:
				break;
			}
			pieceValue *= (1.2d * (f.getRemainingHealth() / f
					.getStartingHealth()));
			// DOPUNI ZA ODNOS NA FIGURE / ENEMY FIGURES THAT IT CAN HURT
			playerValue += pieceValue;
		}
		double opponentValue = mGameField.getOpponentCoins();
		for (Figure f : mGameField.getOpponentPieces()) {
			double pieceValue = 0d;
			switch (f.getType()) {
			case CONSTANTS.ENEMY_KNIGHT:
				pieceValue = CONSTANTS.KNIGHT_PRICE;
				break;
			case CONSTANTS.ENEMY_WITCH:
				pieceValue = CONSTANTS.WITCH_PRICE;
				break;
			case CONSTANTS.ENEMY_DUCK:
				pieceValue = CONSTANTS.DUCK_PRICE;
				break;
			default:
				break;
			}

			pieceValue *= (1.2d * (f.getRemainingHealth() / f
					.getStartingHealth()));
			// DOPUNI ZA ODNOS NA FIGURE / ENEMY FIGURES THAT IT CAN HURT
			opponentValue += pieceValue;
		}

		if (whoseTurn == CONSTANTS.PLAYER)
			value = -(playerValue - opponentValue);
		else {
			value = playerValue - opponentValue;
		}
	}

	protected void mineMovesFromFigure(Figure usedFigure) {

		// ArrayList<Move> moves = new ArrayList<Move>();
		// ArrayList<State> states = new ArrayList<State>();

		int startX = usedFigure.getIndexX();
		int startY = usedFigure.getIndexY();
		State upState = null, downState = null, rightState = null, leftState = null;
		int remainingMoves = usedFigure.getRemainingMoves();
		if (remainingMoves > 0) {
			// TRY RIGHT
			int currentX = startX + 1;
			int currentY = startY;
			if (inBounds(currentX, currentY)) {
				rightState = doAction(startX, startY, currentX, currentY);
				// if (null != rightState
				// && rightState.usedFigure.getRemainingMoves() > 0) {
				// currentX = rightState.getUsedFigure().getIndexX();
				// currentY = rightState.getUsedFigure().getIndexY();
				// if (rightState.usedFigure.getRemainingMoves() > 0) {
				// rightState.mineMovesFromFigure(currentX, currentY);
				// }
				// }
				if (null != rightState) {
					if (rightState.whoseTurn == CONSTANTS.PLAYER) {
						for (Figure f : rightState.mGameField.getPlayerPieces()) {
							if (f.getRemainingMoves() > 0) {
								rightState.mineMovesFromFigure(f);
							}
						}
					}
					if (rightState.whoseTurn == CONSTANTS.ENEMY) {
						for (Figure f : rightState.mGameField
								.getOpponentPieces()) {
							if (f.getRemainingMoves() > 0) {
								rightState.mineMovesFromFigure(f);
							}
						}
					}
				}
			}

			// TRY LEFT
			currentX = startX - 1;
			currentY = startY;
			if (inBounds(currentX, currentY)) {
				leftState = doAction(startX, startY, currentX, currentY);
				// if (null != leftState
				// && leftState.usedFigure.getRemainingMoves() > 0) {
				// currentX = leftState.getUsedFigure().getIndexX();
				// currentY = leftState.getUsedFigure().getIndexY();
				// if (leftState.usedFigure.getRemainingMoves() > 0) {
				// leftState.mineMovesFromFigure(currentX, currentY);
				// }
				// }
				if (null != leftState) {
					if (leftState.whoseTurn == CONSTANTS.PLAYER) {
						for (Figure f : leftState.mGameField.getPlayerPieces()) {
							if (f.getRemainingMoves() > 0) {
								leftState.mineMovesFromFigure(f);
							}
						}
					}
					if (leftState.whoseTurn == CONSTANTS.ENEMY) {
						for (Figure f : leftState.mGameField
								.getOpponentPieces()) {
							if (f.getRemainingMoves() > 0) {
								leftState.mineMovesFromFigure(f);
							}
						}
					}
				}
			}

			// TRY UP
			currentX = startX;
			currentY = startY + 1;
			if (inBounds(currentX, currentY)) {
				upState = doAction(startX, startY, currentX, currentY);
				// if (null != upState
				// && upState.usedFigure.getRemainingMoves() > 0) {
				// currentX = upState.getUsedFigure().getIndexX();
				// currentY = upState.getUsedFigure().getIndexY();
				// if (upState.usedFigure.getRemainingMoves() > 0) {
				// upState.mineMovesFromFigure(currentX, currentY);
				// }
				// }
				if (null != upState) {
					if (upState.whoseTurn == CONSTANTS.PLAYER) {
						for (Figure f : upState.mGameField.getPlayerPieces()) {
							if (f.getRemainingMoves() > 0) {
								upState.mineMovesFromFigure(f);
							}
						}
					}
					if (upState.whoseTurn == CONSTANTS.ENEMY) {
						for (Figure f : upState.mGameField.getOpponentPieces()) {
							if (f.getRemainingMoves() > 0) {
								upState.mineMovesFromFigure(f);
							}
						}
					}
				}

			}

			// TRY DOWN
			currentX = startX;
			currentY = startY - 1;
			if (inBounds(currentX, currentY)) {
				downState = doAction(startX, startY, currentX, currentY);
				// if (null != downState
				// && downState.usedFigure.getRemainingMoves() > 0) {
				// currentX = downState.getUsedFigure().getIndexX();
				// currentY = downState.getUsedFigure().getIndexY();
				// if (downState.usedFigure.getRemainingMoves() > 0) {
				// downState.mineMovesFromFigure(currentX, currentY);
				// }
				// }
				if (null != downState) {
					if (downState.whoseTurn == CONSTANTS.PLAYER) {
						for (Figure f : downState.mGameField.getPlayerPieces()) {
							if (f.getRemainingMoves() > 0) {
								downState.mineMovesFromFigure(f);
							}
						}
					}
					if (downState.whoseTurn == CONSTANTS.ENEMY) {
						for (Figure f : downState.mGameField
								.getOpponentPieces()) {
							if (f.getRemainingMoves() > 0) {
								downState.mineMovesFromFigure(f);
							}
						}
					}
				}
			}

			if (null != upState) {
				mFollowingStates.add(upState);
				// if (mMoves.size() > 0) {
				// ArrayList<Move> combinedMoves = combine(mMoves,
				// upState.mMoves);
				// upState.mMoves = combinedMoves;
				// }
			}
			if (null != downState) {
				mFollowingStates.add(downState);
				// if (mMoves.size() > 0) {
				// ArrayList<Move> combinedMoves = combine(mMoves,
				// downState.mMoves);
				// downState.mMoves = combinedMoves;
				// }
			}
			if (null != leftState) {
				mFollowingStates.add(leftState);
				// if (mMoves.size() > 0) {
				// ArrayList<Move> combinedMoves = combine(mMoves,
				// leftState.mMoves);
				// leftState.mMoves = combinedMoves;
				// }
			}
			if (null != rightState) {
				mFollowingStates.add(rightState);
				// if (mMoves.size() > 0) {
				// ArrayList<Move> combinedMoves = combine(mMoves,
				// rightState.mMoves);
				// rightState.mMoves = combinedMoves;
				// }
			}

		}
		return;

	}

	// protected static ArrayList<Move> combine(ArrayList<Move> pMoves, Move
	// pMove) {
	// if (pMoves.size() > 0
	// && canCombine(pMoves.get(pMoves.size() - 1), pMove)) {
	// Move move = new Move(pMove.mMoveType,
	// pMoves.get(pMoves.size() - 1).mStartX, pMoves.get(pMoves
	// .size() - 1).mStartY, pMove.mDestX, pMove.mDestY);
	// ArrayList<Move> moves = new ArrayList<Move>();
	// ArrayList<Move> moves1 = new ArrayList<Move>(pMoves);
	// moves1.remove(pMoves.size() - 1);
	// moves.addAll(moves1);
	// moves.add(move);
	// return moves;
	// }
	// ArrayList<Move> moves = new ArrayList<Move>(pMoves);
	// moves.add(new Move(pMove));
	// return moves;
	// }
	//
	// protected static boolean canCombine(Move pMove1, Move pMove2) {
	// return (pMove1.mMoveType == CONSTANTS.MOVE_TYPE_MOVE
	// && ((pMove1.mDestX == pMove2.mStartX) && (pMove1.mDestY ==
	// pMove2.mStartY)) && (pMove2.mMoveType == CONSTANTS.MOVE_TYPE_MOVE ||
	// pMove2.mMoveType == CONSTANTS.MOVE_TYPE_COLLECT));
	// }

	/*
	 * Treba da gu izvrzi navedenu akciju da napravi novu sostojbu i ako moze da
	 * pravi joste moves da gi napravi, i da gi dodade na tekvono izvrsen poteg,
	 * i da vrati sve nazad
	 */
	protected State doAction(int startX, int startY, int destX, int destY) {
		State newState = null;
		if (mGameField.getAt(destX, destY).isEmpty()) {
			newState = getNewInstace(this);
			if (newState.usedFigure == null)
				newState.usedFigure = newState.mGameField.getAt(startX, startY)
						.asFigure();
			newState.mGameField.moveFigureFromTo(startX, startY, destX, destY);
			// newState.mMoves = combine(newState.mMoves, new Move(
			// CONSTANTS.MOVE_TYPE_MOVE, startX, startY, destX, destY));
			newState.mMoves.add(MoveFactory.makeMoveTypeMove(startX, startY,
					destX, destY));
		} else if (mGameField.getAt(destX, destY).isCoins()) {
			newState = getNewInstace(this);
			if (newState.usedFigure == null)
				newState.usedFigure = newState.mGameField.getAt(startX, startY)
						.asFigure();
			newState.mGameField
					.collectCoinsFromTo(startX, startY, destX, destY);
			// newState.mMoves = combine(newState.mMoves, new Move(
			// CONSTANTS.MOVE_TYPE_MOVE, startX, startY, destX, destY));
			newState.mMoves.add(MoveFactory.makeCollectTypeMove(startX, startY,
					destX, destY));
		} else if (FieldItem.inDifferentTeam(mGameField.getAt(startX, startY),
				mGameField.getAt(destX, destY))) {
			newState = getNewInstace(this);
			if (newState.usedFigure == null)
				newState.usedFigure = newState.mGameField.getAt(startX, startY)
						.asFigure();
			newState.mGameField.attackFigureFromTo(startX, startY, destX, destY);
			// newState.mMoves = combine(newState.mMoves, new Move(
			// CONSTANTS.MOVE_TYPE_MOVE, startX, startY, destX, destY));
			newState.mMoves.add(MoveFactory.makeAttackTypeMove(startX, startY,
					destX, destY));
		} else {
			newState = null;
		}

		return newState;
	}

	public boolean inBounds(int pPositionX, int pPositionY) {
		if (pPositionX >= 0 && pPositionX < CONSTANTS.GAMEFIELD_COLUMNS
				&& pPositionY >= 0 && pPositionY < CONSTANTS.GAMEFIELD_ROWS)
			return true;
		return false;
	}

	protected ArrayList<State> followingStateTreeToList() {
		ArrayList<State> rez = new ArrayList<State>();
		for (State s : mFollowingStates) {
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

	public GameField getGameField() {
		return mGameField;
	}

	public void setGameField(GameField mGameField) {
		this.mGameField = mGameField;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public ArrayList<Move> getMoves() {
		return mMoves;
	}

	public void setMoves(ArrayList<Move> mMoves) {
		this.mMoves = mMoves;
	}

	public ArrayList<State> getFollowingStates() {
		return mFollowingStates;
	}

	public void setFollowingStates(ArrayList<State> mFollowingStates) {
		this.mFollowingStates = mFollowingStates;
	}

	public void mineForMoves() {
		if (whoseTurn == CONSTANTS.PLAYER) {
			for (Figure f : mGameField.getPlayerPieces()) {
				mineMovesFromFigure(f);
			}
		} else {
			for (Figure f : mGameField.getOpponentPieces()) {
				mineMovesFromFigure(f);
			}
		}
		mFollowingStates = followingStateTreeToList();
		return;
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

}
