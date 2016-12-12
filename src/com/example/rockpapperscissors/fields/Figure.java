package com.example.rockpapperscissors.fields;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;

import com.example.rockpapperscissors.CONSTANTS;
import com.example.rockpapperscissors.Managers.ResourceManager;

public class Figure extends FieldItem {

	int remainingMoves;
	int remainingHealth;
	int startingMoves;
	int startingHealth;

	boolean canAttack;
	
	public boolean canAttack() {
		return canAttack;
	}

	public void setCanAttack(boolean canAttack) {
		this.canAttack = canAttack;
	}

	public int getStartingMoves() {
		return startingMoves;
	}

	public void setStartingMoves(int startingMoves) {
		this.startingMoves = startingMoves;
	}

	public int getStartingHealth() {
		return startingHealth;
	}

	public void setStartingHealth(int startingHealth) {
		this.startingHealth = startingHealth;
	}

	public int getRemainingMoves() {
		return remainingMoves;
	}

	public void setRemainingMoves(int remainingMoves) {
		this.remainingMoves = remainingMoves;
	}

	public int getRemainingHealth() {
		return remainingHealth;
	}

	public void setRemainingHealth(int remainingHealth) {
		this.remainingHealth = remainingHealth;
	}

	Sprite healthBackground;
	Sprite[] healthSprites;

	public Figure(int indexX,int indexY, int pStartingMoves,
			int pStartingHealth, int pType,
			ITextureRegion pTextureRegion,
			Scene pScene) {
		super(indexX,indexY, pType, pTextureRegion, pScene);

		startingHealth = pStartingHealth;
		remainingHealth = startingHealth;
		startingMoves = pStartingMoves;
		remainingMoves = startingMoves;
		canAttack = true;
		
		int maxHealth = CONSTANTS.MAX_HEALTH;
		int healthBackgroundWidth = 28 * startingHealth / maxHealth;
		healthBackground = new Sprite(CONSTANTS.squareSize/2, 6,healthBackgroundWidth,6,
				ResourceManager.healthBackgroundTextureRegion,
				ResourceManager.getInstance().engine
						.getVertexBufferObjectManager());

		this.sprite.attachChild(healthBackground);
		healthBackground.setZIndex(900);
		float healthWidth = (healthBackgroundWidth - (startingHealth - 2f)) / startingHealth;
		float currentX = CONSTANTS.squareSize/2 - healthBackgroundWidth/2 + healthWidth/2;
		healthSprites = new Sprite[startingHealth];
		for (int i = 0; i < startingHealth; i++) {
			healthSprites[i] = new Sprite(currentX, 6, healthWidth, 4,
					ResourceManager.healthForegroundTextureRegion,
					ResourceManager.getInstance().engine
							.getVertexBufferObjectManager());
			this.sprite.attachChild(healthSprites[i]);
			healthSprites[i].setZIndex(1900);
			currentX += (healthWidth );

		}

	}



	public Figure(Figure source) {
		super(source);
		canAttack = source.canAttack;
		healthBackground = source.healthBackground;
		healthSprites = source.healthSprites;
		remainingHealth = source.remainingHealth;
		remainingMoves = source.remainingMoves;
		startingHealth = source.startingHealth;
		startingMoves = source.startingMoves;

	}

	public void decreaseRemainingMoves(int numDecreases) {
		remainingMoves -= numDecreases;
	}

	public void resetMoves() {
		remainingMoves = startingMoves;
	}
	
	public void resetCanAttack() {
		canAttack = true;
	}
	
	public void useAttack () {
		canAttack = false;
	}

	public void decreaseHealth(int i) {
		remainingHealth -= i;
		adjustHealthSprites();
	}
	
	@Override
	public void renderSpriteToPosition(float pX, float pY) {
		super.renderSpriteToPosition(pX, pY);
		adjustHealthSprites();
	}

	public void adjustHealthSprites() {
		for (int i = 0; i < startingHealth; i++) {
			if (i < remainingHealth) {
				healthSprites[i].setVisible(true);
			} else {
				healthSprites[i].setVisible(false);
			}
		}
	}
	
	public void newTurn () {
		resetCanAttack();
		resetMoves();
	}
	
}
