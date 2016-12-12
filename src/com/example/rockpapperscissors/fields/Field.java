//package com.example.rockpapperscissors.fields;
//
//import com.example.rockpapperscissors.CONSTANTS;
//
//public class Field {
//	private int mType;
//	private FieldItem  mFieldItem = null;
//
//	public Field(Field pField){
//		mType = pField.mType;
//	}
//	
//	public int getType() {
//		return mType;
//	}
//
//	public void setType(int pType) {
//		this.mType = pType;
//		if(mFieldItem != null){
//			mFieldItem.setType(pType);
//			mFieldItem.adjustSprite();
//		}
//		
//	}
//
//	public FieldItem getFieldItem() {
//		return mFieldItem;
//	}
//
//	public void setFieldItem(FieldItem  pFigure) {
//		mFieldItem = pFigure;
//	}
//	
//	public Figure getFigure () {
//		if (mFieldItem != null && Figure.class == mFieldItem.getClass()){
//			return (Figure) mFieldItem;
//		}
//		return null;
//	}
//	
//	public boolean isFigure () {
//		if (mFieldItem != null && Figure.class == mFieldItem.getClass()) return true;
//		return false;
//		
//	}
//	
//	public boolean isPickup () {
//		if (mFieldItem != null && Pickup.class == mFieldItem.getClass()) return true;
//		return false;
//	}
//	
//	public void setPickup (Pickup pPickup) {
//		mFieldItem = pPickup;
//	}
//	
//	public Pickup getPickup () {
//		if (mFieldItem != null && Pickup.class == mFieldItem.getClass())
//			return (Pickup) mFieldItem;
//		return null;
//	}
//	
//	public void setFigure (Figure pFigure) {
//		mFieldItem = pFigure;
//	}
//
//	public Field() {
//		this.mType = CONSTANTS.EMPTY;
//	}
//
//	public boolean isFriendly() {
//		return (mType == CONSTANTS.FRIENDLY_SEXTANGLE
//				|| mType == CONSTANTS.FRIENDLY_SQUARE || mType == CONSTANTS.FRIENDLY_TRIANGLE);
//	}
//
//	public boolean isAvailable() {
//		return (mType == CONSTANTS.EMPTY || mType == CONSTANTS.ENEMY_SEXTANGLE
//				|| mType == CONSTANTS.ENEMY_SQUARE || mType == CONSTANTS.ENEMY_TRIANGLE || mType == CONSTANTS.COINS );
//	}
//
//	public boolean isEnemy () {
//		return (mType == CONSTANTS.ENEMY_SEXTANGLE
//				|| mType == CONSTANTS.ENEMY_SQUARE || mType == CONSTANTS.ENEMY_TRIANGLE);
//	}
//	
//	public boolean isCoins () {
//		return mType == CONSTANTS.COINS;
//	}
//
//	public boolean isEmpty () {
//		return mType == CONSTANTS.EMPTY;
//	}
//	
//	public void removeFieldItem () {
//		if (null != mFieldItem){
//			mFieldItem.detachSprite();
//			mFieldItem = null;
//		}
//	}
//	
//	public void empty () {
//		removeFieldItem();
//		mType = CONSTANTS.EMPTY;
//	}
//}
