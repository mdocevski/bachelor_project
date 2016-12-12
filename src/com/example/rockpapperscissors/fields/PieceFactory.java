package com.example.rockpapperscissors.fields;

import org.andengine.entity.scene.Scene;

import com.example.rockpapperscissors.fields.Figure;
import com.example.rockpapperscissors.CONSTANTS;
import com.example.rockpapperscissors.Managers.ResourceManager;

public class PieceFactory {

	public static Figure makeFriendlyWitch(int pIndexX, int pIndexY,
			Scene pScene) {
		Figure newFigure = new Figure(pIndexX, pIndexY,
				CONSTANTS.WITCH_STARTING_MOVES,
				CONSTANTS.WITCH_STARTING_HEALTH, CONSTANTS.FRIENDLY_WITCH,
				ResourceManager.friendlyWitchTextureRegion, pScene);
		newFigure.sprite.setScale(CONSTANTS.scale);
		newFigure.sprite.setZIndex(100);
		return newFigure;
	}

	public static Figure makeFriendlyKnight(int pIndexX, int pIndexY,
			Scene pScene) {
		Figure newFigure = new Figure(pIndexX, pIndexY,
				CONSTANTS.KNIGHT_STARTING_MOVES,
				CONSTANTS.KNIGHT_STARTING_HEALTH,
				CONSTANTS.FRIENDLY_KNIGHT,
				ResourceManager.friendlyKnightTextureRegion, pScene);
		newFigure.sprite.setScale(CONSTANTS.scale);
		newFigure.sprite.setZIndex(100);
		return newFigure;
	}

	public static Figure makeFriendlyDuck(int pIndexX, int pIndexY,
			Scene pScene) {
		Figure newFigure = new Figure(pIndexX, pIndexY,
				CONSTANTS.DUCK_STARTING_MOVES,
				CONSTANTS.DUCK_STARTING_HEALTH,
				CONSTANTS.FRIENDLY_DUCK,
				ResourceManager.friendlyDuckTextureRegion, pScene);
		newFigure.sprite.setScale(CONSTANTS.scale);
		newFigure.sprite.setZIndex(100);
		return newFigure;
	}

	public static Figure makeEnemyKnight(int pIndexX, int pIndexY,
			Scene pScene) {
		Figure newFigure = new Figure(pIndexX, pIndexY,
				CONSTANTS.KNIGHT_STARTING_MOVES,
				CONSTANTS.KNIGHT_STARTING_HEALTH, CONSTANTS.ENEMY_KNIGHT,
				ResourceManager.enemyKnightTextureRegion, pScene);
		newFigure.sprite.setScale(CONSTANTS.scale);
		newFigure.sprite.setZIndex(100);
		return newFigure;
	}

	public static Figure makeEnemyWitch(int pIndexX, int pIndexY, Scene pScene) {
		Figure newFigure = new Figure(pIndexX, pIndexY,
				CONSTANTS.WITCH_STARTING_MOVES,
				CONSTANTS.WITCH_STARTING_HEALTH, CONSTANTS.ENEMY_WITCH,
				ResourceManager.enemyWitchTextureRegion, pScene);
		newFigure.sprite.setScale(CONSTANTS.scale);
		newFigure.sprite.setZIndex(100);
		return newFigure;
	}

	public static Figure makeEnemyDuck(int pIndexX, int pIndexY,
			Scene pScene) {
		Figure newFigure = new Figure(pIndexX, pIndexY,
				CONSTANTS.DUCK_STARTING_MOVES,
				CONSTANTS.DUCK_STARTING_HEALTH, CONSTANTS.ENEMY_DUCK,
				ResourceManager.enemyDuckTextureRegion, pScene);
		newFigure.sprite.setScale(CONSTANTS.scale);
		newFigure.sprite.setZIndex(100);
		return newFigure;
	}

	public static Pickup makeCoins(int pIndexX, int pIndexY, int pQuantity,
			Scene pScene) {
		Pickup newPickup;
		if (pQuantity == 1) {
			newPickup = new Pickup(pIndexX, pIndexY, CONSTANTS.COINS,
					pQuantity, ResourceManager.oneCoinTextureRegion, pScene);
		} else if (pQuantity == 2) {
			newPickup = new Pickup(pIndexX, pIndexY, CONSTANTS.COINS,
					pQuantity, ResourceManager.twoCoinsTextureRegion, pScene);
		} else {
			newPickup = new Pickup(pIndexX, pIndexY, CONSTANTS.COINS,
					pQuantity, ResourceManager.threeCoinsTextureRegion, pScene);
		}

		newPickup.sprite.setScale(CONSTANTS.scale);
		newPickup.sprite.setZIndex(50);
		return newPickup;
	}

	public static Empty makeEmpty(int pIndexX, int pIndexY, Scene pScene) {
		Empty newEmpty = new Empty(pIndexX, pIndexY, pScene);
		newEmpty.sprite.setScale(CONSTANTS.scale);
		newEmpty.sprite.setZIndex(0);
		return newEmpty;
	}

}
