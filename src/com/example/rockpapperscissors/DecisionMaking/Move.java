package com.example.rockpapperscissors.DecisionMaking;


public class Move {
	public byte mMoveType;
	public byte mStartX, mStartY, mDestX, mDestY;
	public byte mBuyFigureType;

	protected Move(byte pMoveType, byte startX, byte startY, byte destX, byte destY) {
		mMoveType = pMoveType;
		mStartX = startX;
		mStartY = startY;
		mDestX = destX;
		mDestY = destY;
	}

	public Move(Move pMove) {
		mMoveType = pMove.mMoveType;
		mStartX = pMove.mStartX;
		mStartY = pMove.mStartY;
		mDestX = pMove.mDestX;
		mDestY = pMove.mDestY;
	}

	public Move(byte moveTypeBuy, byte buyFigureType, byte destX, byte destY) {
		mMoveType = moveTypeBuy;
		mBuyFigureType = buyFigureType;
		mStartX = -1;
		mStartY = -1;
		mDestX = destX;
		mDestY = destY;
	}

}
