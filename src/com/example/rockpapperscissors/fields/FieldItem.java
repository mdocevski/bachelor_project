package com.example.rockpapperscissors.fields;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.texture.region.ITextureRegion;

import com.example.rockpapperscissors.CONSTANTS;
import com.example.rockpapperscissors.Managers.GameManager;
import com.example.rockpapperscissors.Managers.ResourceManager;

public class FieldItem {

	protected int type;
	protected Sprite sprite;
	protected ITextureRegion textureRegion;
	protected Scene scene;
	protected int mIndexX = 1, mIndexY = 1;

	// public FieldItem(int pPositionX, int pPositionY, int pType, float pX,
	// float pY, ITextureRegion pTextureRegion) {
	// type = pType;
	// textureRegion = pTextureRegion;
	// }

	public int getIndexX() {
		return mIndexX;
	}

	public void setIndexX(int mIndexX) {
		this.mIndexX = mIndexX;
	}

	public int getIndexY() {
		return mIndexY;
	}

	public void setIndexY(int mIndexY) {
		this.mIndexY = mIndexY;
	}

	public FieldItem() {
		type = CONSTANTS.EMPTY;
		textureRegion = ResourceManager.fieldTextureRegion;
		sprite = new Sprite(-CONSTANTS.increment, -CONSTANTS.increment,
				textureRegion,
				ResourceManager.getInstance().engine
						.getVertexBufferObjectManager());

	}

	public FieldItem(FieldItem source) {
		type = source.type;
		sprite = source.sprite;
		scene = source.scene;
		textureRegion = source.textureRegion;
		mIndexX = source.mIndexX;
		mIndexY = source.mIndexY;
	}

	public FieldItem(Scene pScene) {
		type = CONSTANTS.EMPTY;
		textureRegion = ResourceManager.fieldTextureRegion;
		sprite = new Sprite(-CONSTANTS.increment, -CONSTANTS.increment,
				textureRegion,
				ResourceManager.getInstance().engine
						.getVertexBufferObjectManager());
		scene = pScene;
	}

	public FieldItem(int pIndexX, int pIndexY, int pType,
			ITextureRegion pTextureRegion, Scene pScene) {
		mIndexX = pIndexX;
		mIndexY = pIndexY;
		type = pType;
		textureRegion = pTextureRegion;
		sprite = new Sprite(-CONSTANTS.increment, -CONSTANTS.increment,
				textureRegion,
				ResourceManager.getInstance().engine
						.getVertexBufferObjectManager());
//		Text idxXText = new Text(6, 6, ResourceManager.fontDefault18,
//				String.valueOf(mIndexX),
//				ResourceManager.getInstance().engine
//						.getVertexBufferObjectManager());
//		Text idxYText = new Text(idxXText.getX() + idxXText.getWidth() + 2, 6,
//				ResourceManager.fontDefault18, String.valueOf(mIndexY),
//				ResourceManager.getInstance().engine
//						.getVertexBufferObjectManager());
//		sprite.attachChild(idxYText);
//		sprite.attachChild(idxXText);
		scene = pScene;
	}

	public int getType() {
		return type;
	}

	public void setType(int pType) {
		type = pType;

	}

	public Scene getScene() {
		return scene;
	}

	public void setScene(Scene scene) {
		this.scene = scene;
	}

	public ITextureRegion getTextureRegion() {
		return textureRegion;
	}

	public void setTextureRegion(ITextureRegion textureRegion) {
		this.textureRegion = textureRegion;
	}

	public void renderSpriteToPosition(float pX, float pY) {
		if (sprite == null) {
			sprite = new Sprite(pX, pY, textureRegion,
					ResourceManager.getInstance().engine
							.getVertexBufferObjectManager());
			sprite.setScale(CONSTANTS.scale);
			attachSprite();
		} else {
			if (!sprite.hasParent())
				attachSprite();
			sprite.setPosition(pX, pY);
		}
	}

	public void detachSprite() {
		sprite.setPosition(-CONSTANTS.increment, -CONSTANTS.increment);
		sprite.setVisible(false);
		sprite.detachSelf();
	}

	public void attachSprite() {
		scene.attachChild(sprite);
	}

	public Sprite getSprite() {
		return sprite;
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

	public Figure asFigure() {
		if (Figure.class == getClass()) {
			return (Figure) this;
		}
		return null;
	}

	public boolean isFigure() {
		if (Figure.class == getClass())
			return true;
		return false;

	}

	public boolean isPickup() {
		if (Pickup.class == getClass())
			return true;
		return false;
	}

	public Pickup asPickup() {
		if (Pickup.class == getClass())
			return (Pickup) this;
		return null;
	}

	public void registerTouchAreaToScene() {
		scene.registerTouchArea(sprite);
	}

	public void unRegisterTouchAreaToScene() {
		scene.unregisterTouchArea(sprite);
	}

	public boolean isFriendly() {
		return (type == CONSTANTS.FRIENDLY_KNIGHT
				|| type == CONSTANTS.FRIENDLY_WITCH || type == CONSTANTS.FRIENDLY_DUCK);
	}

	public boolean isAvailable() {
		return (type == CONSTANTS.EMPTY || type == CONSTANTS.ENEMY_KNIGHT
				|| type == CONSTANTS.ENEMY_WITCH
				|| type == CONSTANTS.ENEMY_DUCK || type == CONSTANTS.COINS);
	}

	public boolean isOpponent() {
		return (type == CONSTANTS.ENEMY_KNIGHT || type == CONSTANTS.ENEMY_WITCH || type == CONSTANTS.ENEMY_DUCK);
	}

	public boolean isPlayer() {
		return (type == CONSTANTS.FRIENDLY_KNIGHT
				|| type == CONSTANTS.FRIENDLY_WITCH || type == CONSTANTS.FRIENDLY_DUCK);
	}

	public static boolean inSameTeam(FieldItem pFieldItem1,
			FieldItem pFieldItem2) {
		return ((pFieldItem1.isPlayer() && pFieldItem2.isPlayer()) || (pFieldItem1
				.isOpponent() && pFieldItem2.isOpponent()));
	}

	public static boolean inDifferentTeam(FieldItem pFieldItem1,
			FieldItem pFieldItem2) {
		return ((pFieldItem1.isPlayer() && pFieldItem2.isOpponent()) || (pFieldItem1
				.isOpponent() && pFieldItem2.isPlayer()));
	}

	public boolean isCoins() {
		return type == CONSTANTS.COINS;
	}

	public boolean isEmpty() {
		return type == CONSTANTS.EMPTY;
	}

}