package com.example.rockpapperscissors.DecisionMaking;

import com.example.rockpapperscissors.CONSTANTS;

public class MoveFactory {
	public static Move makeMoveTypeMove(int startX, int startY, int destX,
			int destY) {
		return new Move((byte) CONSTANTS.MOVE_TYPE_MOVE, (byte) startX,
				(byte) startY, (byte) destX, (byte) destY);
	}

	public static Move makeAttackTypeMove(int startX, int startY, int destX,
			int destY) {
		return new Move((byte) CONSTANTS.MOVE_TYPE_ATTACK, (byte) startX,
				(byte) startY, (byte) destX, (byte) destY);
	}

	public static Move makeCollectTypeMove(int startX, int startY, int destX,
			int destY) {
		return new Move((byte) CONSTANTS.MOVE_TYPE_COLLECT, (byte) startX,
				(byte) startY, (byte) destX, (byte) destY);
	}

	public static Move makeBuyTypeMove(int buyFigureType, int destX, int destY) {
		return new Move((byte)CONSTANTS.MOVE_TYPE_BUY,(byte)buyFigureType,(byte)destX,(byte)destY);
	}

}
