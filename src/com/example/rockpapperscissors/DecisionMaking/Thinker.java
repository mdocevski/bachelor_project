package com.example.rockpapperscissors.DecisionMaking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.example.rockpapperscissors.BytePair;
import com.example.rockpapperscissors.CONSTANTS;
import com.example.rockpapperscissors.DecisionMaking.CompressedState.RootTreeNode;
import com.example.rockpapperscissors.DecisionMaking.CompressedState.TreeNode;
import com.example.rockpapperscissors.Managers.GameManager;

public class Thinker {

	private static Pair abNegamax(CompressedState state, int maxDepth,
			int currentDepth, double alpha, double beta) {
		// Check if we're done recursing
		if (state.isGameOver() || currentDepth == maxDepth) {
			return new Pair(state.getValue(), state);
		}
		// Otherwise bubble up values from below

		CompressedState bestMove = null;
		double bestScore = CONSTANTS.NEGATIVE_INFINITY;
		double currentScore;
		CompressedState newState = CompressedState.getNewInstace(state);
		newState.mMoves = new ArrayList<Move>();
		newState.changeTurn();

		// Go through each move
		newState.listAllMoves();
		for (CompressedState s : newState.getFollowingStates()) {
			// Recurse
			Pair recursionResult = abNegamax(s, maxDepth, currentDepth + 1,
					-beta, -Math.max(alpha, bestScore));
			currentScore = -recursionResult.score;

			// Update the best score
			if (currentScore > bestScore) {
				bestScore = currentScore;
				bestMove = s;
				// If we're outside the bounds, then prune: exite immediately
				if (bestScore >= beta) {
					return new Pair(bestScore, bestMove);
				}
			}
		}
		return new Pair(bestScore, bestMove);
	}

	public static ArrayList<Move> getBestMove(CompressedState state) {
		// Get the result of a minimax run and return the move
		int maxDepth = CONSTANTS.BASE_MAX_DEPTH;
		int turnCount = GameManager.getInstance().getTurnCount();
		int dist = state.mGameField.minInterFigureDistance();
		if (turnCount < CONSTANTS.INITIAL_PHASE_TURN_NUM
				|| dist >= CONSTANTS.MIN_AB_INTERFIGURE_DISTANCE) {
			return heuristicMove(state);

		} else {
			Pair result = abNegamax(state, maxDepth, 0,
					Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
			if (result.state != null)
				return result.state.getMoves();
			else
				return new ArrayList<Move>();
		}
	}

	private static ArrayList<Move> heuristicMove(CompressedState state) {
		ArrayList<RootTreeNode> treeList = new ArrayList<RootTreeNode>();
		for (byte[] piece : state.mGameField.getOpponentPieces()) {
			RootTreeNode root = state.gameFieldToTreeForPiece(piece);
			treeList.add(root);
		}

		// FIRST CONSIDER THE NEED TO ACQUIRE NEW PIECES, AND WHETER IT'S
		// POSSIBLE
		double needForTriangle = 0d;
		double needForSquare = 0d;
		double needForSextangle = 0d;
		double checkChance = Math.random();
		// FIND WHETER YOU CAN PLACE A PIECE
		// IF A PLACE CANNOT BE FOUND CONSIDER NEED TO BE ZERO
		ArrayList<BytePair> list = state
				.findPlantableLocations(CONSTANTS.ENEMY);
		// IF A PLACE CAN BE FOUND CONSIDER EACH PIECE TYPE'S NEED
		if (list.size() > 0
				&& state.mGameField.getOpponentPieces().size() < CONSTANTS.MAX_UNIT_COUNT) {
			needForTriangle = needToBuyFigureType(state, CONSTANTS.ENEMY_DUCK);
			needForSquare = needToBuyFigureType(state, CONSTANTS.ENEMY_WITCH);
			needForSextangle = needToBuyFigureType(state,
					CONSTANTS.ENEMY_KNIGHT);
			// IF THE NEED FOR A FIQURE IS GREAT CHECK TO BUY WITH 100%
			// CERTAINTY,
			// OR IF IT'S NOT CHECK WITH SOME CHANCE
			if ((needForTriangle > 1 || needForSquare > 1 || needForSextangle > 1)
					|| (checkChance < CONSTANTS.CHECK_FOR_BUYING_CHANCE)) {
				// IF YOU NEED THE TRIANGLE THE MOST
				if (needForTriangle >= needForSquare
						&& needForTriangle >= needForSextangle
						&& state.mGameField.getOpponentCoins() > CONSTANTS.DUCK_PRICE) {
					ArrayList<Move> moves = new ArrayList<Move>();
					int idx = new Random().nextInt(list.size());
					moves.add(MoveFactory.makeBuyTypeMove(CONSTANTS.ENEMY_DUCK,
							list.get(idx).x, list.get(idx).y));
					return moves;
				} else
				// IF YOU NEED THE SQUARE THE MOST
				if (needForSquare >= needForTriangle
						&& needForSquare >= needForSextangle
						&& state.mGameField.getOpponentCoins() > CONSTANTS.WITCH_PRICE) {
					ArrayList<Move> moves = new ArrayList<Move>();
					int idx = new Random().nextInt(list.size());
					moves.add(MoveFactory.makeBuyTypeMove(
							CONSTANTS.ENEMY_WITCH, list.get(idx).x,
							list.get(idx).y));
					return moves;
				} else
				// IF YOU NEED THE SEXTANGLE THE MOST
				if (needForSextangle >= needForTriangle
						&& needForSextangle >= needForSquare
						&& state.mGameField.getOpponentCoins() > CONSTANTS.KNIGHT_PRICE) {
					ArrayList<Move> moves = new ArrayList<Move>();
					int idx = new Random().nextInt(list.size());
					moves.add(MoveFactory.makeBuyTypeMove(
							CONSTANTS.ENEMY_KNIGHT, list.get(idx).x,
							list.get(idx).y));
					return moves;
				}
			}
		}

		// SECOND CONSIDER COLLECTING PICKUPS
		if (state.mGameField.hasCoins() && hasReachablePickups(treeList)) {
			// IF THERE ARE PICKUPS THAT CAN BE COLLECTED THIS TURN, COLLECT
			// ONE WITH THE HIGHEST QUANTITY
			byte foundPickupsInFirstTurnIdx = findPickupsInFirstTurnIdx(treeList);
			if (foundPickupsInFirstTurnIdx != -1) {
				ArrayList<Move> result = new ArrayList<Move>();
				result.add(MoveFactory.makeCollectTypeMove(
						treeList.get(foundPickupsInFirstTurnIdx).node[CompressedGameField.XINDEX_FIELD_IDX],
						treeList.get(foundPickupsInFirstTurnIdx).node[CompressedGameField.YINDEX_FIELD_IDX],
						treeList.get(foundPickupsInFirstTurnIdx).closestPickup.node[CompressedGameField.XINDEX_FIELD_IDX],
						treeList.get(foundPickupsInFirstTurnIdx).closestPickup.node[CompressedGameField.YINDEX_FIELD_IDX]));
				return result;
			}
			// THERE ARE NO PICKUPS 1 HOP AWAY
			else {
				// IF IT CHANCE'S INTO CHASING PICKUPS CHASE PICKUPS, ELSE MAKE
				// A MOVE TOWARDS ENEMY
				double rnd = Math.random();
				double chance = 1d / 2d * (CONSTANTS.MAX_CHASE_PICKUPS_CHANCE * (1d + (state.mGameField
						.getPickups().size() / state.mGameField
						.getInitialNumPickups())));
				// IF THE COMPUTER CONTROLLED OPPONENT IS SET TO GREEDY, THAN
				// INCREASE CHANCE TO CHASE PICKUPS
				if (GameManager.getInstance().isGreedines()) {
					chance *= CONSTANTS.GREEDINES_INCREASE_CHANCE_FACTOR;
				}
				if (rnd < chance) {
					return makeAMoveForPickups(treeList);
				} else {
					return moveFromDistanceFactors(state, treeList);
				}
			}
		}
		// MAKE A MOVE ACCORDING TO DISTANCE FACTORS, PICKUPS ARE LOOKED OVER
		else {
			return moveFromDistanceFactors(state, treeList);
		}
	}

	private static double needToBuyFigureType(CompressedState state,
			int typeOfFigure) {
		double numType = 0d, numAdvantageOverEnemy = 0d;
		for (byte[] opponentPiece : state.mGameField.getOpponentPieces()) {
			if (opponentPiece[CompressedGameField.TYPE_FIELD_IDX] == typeOfFigure) {
				numType += opponentPiece[CompressedGameField.REMAINING_HEALTH_FIELD_IDX]
						/ opponentPiece[CompressedGameField.STARTING_HEALTH_FIELD_IDX];
			}
		}

		for (byte[] playerPiece : state.mGameField.getPlayerPieces()) {
			if (state.mGameField.advantage(typeOfFigure,
					playerPiece[CompressedGameField.TYPE_FIELD_IDX])) {
				numAdvantageOverEnemy += playerPiece[CompressedGameField.REMAINING_HEALTH_FIELD_IDX]
						/ playerPiece[CompressedGameField.STARTING_HEALTH_FIELD_IDX];
			}
		}

		if (numType == 0)
			numType = 1;
		return numAdvantageOverEnemy / numType;
	}

	private static ArrayList<Move> makeAMoveForPickups(
			ArrayList<RootTreeNode> treeList) {
		double[] desirabilityIndex = new double[treeList.size()];
		double totalDesirability = 0d;
		for (byte b = 0; b < treeList.size(); b++) {
			if (!treeList.get(b).hasClosestPickup()) {
				desirabilityIndex[b] = 0;
			} else {
				desirabilityIndex[b] = (double) treeList.get(b).closestPickup.node[CompressedGameField.QUANTITY_FIELD_IDX]
						/ treeList.get(b).closestPickupHopsDistance;
				desirabilityIndex[b] *= desirabilityIndex[b];
			}
			totalDesirability += desirabilityIndex[b];
		}
		// NORMALIZE DESIRABILITY
		desirabilityIndex[0] /= totalDesirability;
		for (byte b = 1; b < treeList.size(); b++) {
			desirabilityIndex[b] /= totalDesirability;
			desirabilityIndex[b] += desirabilityIndex[b - 1];
		}
		double rnd2 = Math.random();
		byte selection = -1;
		for (byte b = 0; b < treeList.size(); b++) {
			if (rnd2 <= desirabilityIndex[b]) {
				selection = b;
				break;
			}
		}
		ArrayList<TreeNode> list = treeList.get(selection).pathTo(
				treeList.get(selection).closestPickup);
		Collections.reverse(list);
		ArrayList<Move> moves = makeMovesFromPath(list, treeList.get(selection));
		return moves;
	}

	private static ArrayList<Move> moveFromDistanceFactors(
			CompressedState state, ArrayList<RootTreeNode> treeList) {
		// NO PICKUPS, OR CHANCED INTO MOVING, MOVE A FIGURE ACORDING TO
		// DISTANCE FACTORS
		double[] desirabilityFactor = new double[treeList.size()];
		// FIELD 1 IS AGRESSIVE ACTION, FIELD 2 IS DEFENSIVE ACTION
		double[][] desirabilityVector = new double[treeList.size()][2];
		double totalDesirability = 0d;
		for (byte b = 0; b < treeList.size(); b++) {
			RootTreeNode rtn = treeList.get(b);
			desirabilityFactor[b] = 0d;
			if (rtn.hasClosestWeakerEnemy()) {
				if (GameManager.getInstance().getAgression() == CONSTANTS.AGRESSIVE_AGRESSION) {
					desirabilityFactor[b] += 4 * rtn.closestWeakerEnemyHopsDistance;
				} else {
					desirabilityFactor[b] += 2 * rtn.closestWeakerEnemyHopsDistance;
				}
			}
			if (rtn.hasClosestEqualEnemy()) {
				desirabilityFactor[b] += rtn.closestEqualEnemyHopsDistance;
			}
			if (rtn.hasClosestStrongerEnemy()) {
				desirabilityFactor[b] += rtn.closestStrongerEnemyHopsDistance;
			}
			if (rtn.hasClosestFriendly()) {
				if (GameManager.getInstance().getAgression() == CONSTANTS.DEFENSIVE_AGRESSION) {
					desirabilityFactor[b] +=  2 * rtn.closestFriendlyHopsDistance;
				}
				desirabilityFactor[b] += rtn.closestFriendlyHopsDistance;
			}
			desirabilityFactor[b] *= desirabilityFactor[b];
			totalDesirability += desirabilityFactor[b];
		}
		// NORMALIZE DESIRABILITY
		desirabilityFactor[0] /= totalDesirability;
		for (byte b = 1; b < treeList.size(); b++) {
			desirabilityFactor[b] /= totalDesirability;
			desirabilityFactor[b] += desirabilityFactor[b - 1];
		}
		byte selection = -1;
		double rnd2 = Math.random();
		for (byte b = 0; b < treeList.size(); b++) {
			if (rnd2 <= desirabilityFactor[b]) {
				selection = b;
				break;
			}
		}
		if (selection != -1) {
			RootTreeNode rtn = treeList.get(selection);
			double agressiveChance = 1, defensiveChance = 1;
			if (rtn.hasClosestWeakerEnemy()) {
				if (GameManager.getInstance().getAgression() == CONSTANTS.AGRESSIVE_AGRESSION) {
					agressiveChance += 4 * rtn.closestWeakerEnemyHopsDistance;
				} else {
					agressiveChance += 2 * rtn.closestWeakerEnemyHopsDistance;
				}
			}
			if (rtn.hasClosestEqualEnemy()) {
				agressiveChance += rtn.closestEqualEnemyHopsDistance;

			}
			if (rtn.hasClosestStrongerEnemy()) {
				agressiveChance += rtn.closestStrongerEnemyHopsDistance;
			}
			if (rtn.hasClosestFriendly()) {
				if (GameManager.getInstance().getAgression() == CONSTANTS.DEFENSIVE_AGRESSION) {
					defensiveChance += 6 * rtn.closestFriendlyHopsDistance;
				} else {
					defensiveChance += 3 * rtn.closestFriendlyHopsDistance;
				}
			}
			agressiveChance /= (agressiveChance + defensiveChance);
			double chance = Math.random();
			if (chance < agressiveChance) {
				if (rtn.hasClosestWeakerEnemy()) {
					ArrayList<TreeNode> list = rtn
							.pathTo(rtn.closestWeakerEnemy);
					Collections.reverse(list);
					ArrayList<Move> moves = makeMovesFromPath(list, rtn);
					return moves;
				} else if (rtn.hasClosestEqualEnemy()) {
					ArrayList<TreeNode> list = rtn
							.pathTo(rtn.closestEqualEnemy);
					Collections.reverse(list);
					ArrayList<Move> moves = makeMovesFromPath(list, rtn);
					return moves;

				} else {
					return randomMove(treeList, state);
				}
			} else if (rtn.hasClosestFriendly()) {
				ArrayList<TreeNode> list = rtn.pathTo(rtn.closestFriendly);
				Collections.reverse(list);
				ArrayList<Move> moves = makeMovesFromPath(list, rtn);
				return moves;
			} else {
				return randomMove(treeList, state);
			}
		} else {
			return randomMove(treeList, state);
		}
	}

	private static byte findPickupsInFirstTurnIdx(
			ArrayList<RootTreeNode> treeList) {
		byte foundPickupsInFirstTurnIdx = -1;
		for (byte b = 0; b < treeList.size(); b++) {
			RootTreeNode rtn = treeList.get(b);
			if ((rtn.closestPickupHopsDistance == 1)
					&& ((foundPickupsInFirstTurnIdx == -1) || (rtn.node[CompressedGameField.QUANTITY_FIELD_IDX] < treeList
							.get(b).node[CompressedGameField.QUANTITY_FIELD_IDX])))

				foundPickupsInFirstTurnIdx = b;
		}
		return foundPickupsInFirstTurnIdx;
	}

	private static ArrayList<Move> randomMove(ArrayList<RootTreeNode> treeList,
			CompressedState state) {
		Random r = new Random();
		int i = r.nextInt(treeList.size());
		RootTreeNode rtn = treeList.get(i);
		int x, y;
		byte tryNum = 0;
		while (tryNum < 100) {
			x = r.nextInt(rtn.node[CompressedGameField.STARTING_MOVES_FIELD_IDX] + 1);
			y = r.nextInt(rtn.node[CompressedGameField.STARTING_MOVES_FIELD_IDX] + 1);
			if (r.nextBoolean()) {
				x *= (-1);
			}
			if (r.nextBoolean()) {
				y *= (-1);
			}
			if (x == 0 && y == 0) {
				return new ArrayList<Move>();
			}
			x += rtn.node[CompressedGameField.XINDEX_FIELD_IDX];
			y += rtn.node[CompressedGameField.YINDEX_FIELD_IDX];
			if (CompressedGameField.isCoins(state.mGameField.getAt(x, y))) {
				ArrayList<Move> list = new ArrayList<Move>();
				list.add(MoveFactory.makeCollectTypeMove(
						rtn.node[CompressedGameField.XINDEX_FIELD_IDX],
						rtn.node[CompressedGameField.YINDEX_FIELD_IDX], x, y));
				return list;
			}
			if (CompressedGameField.isEmpty(state.mGameField.getAt(x, y))) {
				ArrayList<Move> list = new ArrayList<Move>();
				list.add(MoveFactory.makeMoveTypeMove(
						rtn.node[CompressedGameField.XINDEX_FIELD_IDX],
						rtn.node[CompressedGameField.YINDEX_FIELD_IDX], x, y));
				return list;
			}
		}
		return new ArrayList<Move>();
	}

	private static ArrayList<Move> makeMovesFromPath(List<TreeNode> subList,
			RootTreeNode rootTreeNode) {
		ArrayList<Move> list = new ArrayList<Move>();
		byte hopSize = rootTreeNode.node[CompressedGameField.STARTING_MOVES_FIELD_IDX];
		for (byte b = 0; b < hopSize; b++) {
			if (CompressedGameField.isEmpty(subList.get(b + 1).node)) {
				list.add(MoveFactory.makeMoveTypeMove(
						subList.get(b).node[CompressedGameField.XINDEX_FIELD_IDX],
						subList.get(b).node[CompressedGameField.YINDEX_FIELD_IDX],
						subList.get(b + 1).node[CompressedGameField.XINDEX_FIELD_IDX],
						subList.get(b + 1).node[CompressedGameField.YINDEX_FIELD_IDX]));
			} else if (CompressedGameField.isCoins(subList.get(b + 1).node)) {
				list.add(MoveFactory.makeCollectTypeMove(
						subList.get(b).node[CompressedGameField.XINDEX_FIELD_IDX],
						subList.get(b).node[CompressedGameField.YINDEX_FIELD_IDX],
						subList.get(b + 1).node[CompressedGameField.XINDEX_FIELD_IDX],
						subList.get(b + 1).node[CompressedGameField.YINDEX_FIELD_IDX]));
			} else {
				break;
			}
		}
		return list;
	}

	private static boolean hasReachablePickups(ArrayList<RootTreeNode> treeList) {
		for (RootTreeNode rtn : treeList) {
			if (rtn.hasClosestPickup())
				return true;
		}
		return false;
	}

	private static class Pair {
		public double score = Double.NEGATIVE_INFINITY;
		public CompressedState state = null;

		public Pair(double pScore, CompressedState pState) {
			score = pScore;
			state = pState;
		}
	}
}
