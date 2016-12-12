package com.example.rockpapperscissors.Scenes;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;

import com.example.rockpapperscissors.CONSTANTS;
import com.example.rockpapperscissors.Managers.GameManager;
import com.example.rockpapperscissors.Managers.ResourceManager;
import com.example.rockpapperscissors.Managers.SceneManager;

public class NewUnitLayer extends ManagedLayer {
	private static final NewUnitLayer INSTANCE = new NewUnitLayer();
	private ButtonSprite buyKnightButton = null;
	private ButtonSprite buyDuckButton = null;
	private ButtonSprite buyWitchButton = null;

	public static NewUnitLayer getInstance() {
		return INSTANCE;
	}

	// Animates the layer to slide in from the top.
	IUpdateHandler SlideIn = new IUpdateHandler() {
		@Override
		public void onUpdate(float pSecondsElapsed) {
			if (NewUnitLayer.getInstance().getY() > ResourceManager
					.getInstance().cameraHeight / 2f) {
				NewUnitLayer
						.getInstance()
						.setPosition(
								NewUnitLayer.getInstance().getX(),
								Math.max(
										NewUnitLayer.getInstance().getY()
												- (3600 * (pSecondsElapsed)),
										ResourceManager.getInstance().cameraHeight / 2f));
			} else {
				NewUnitLayer.getInstance().unregisterUpdateHandler(this);
			}
		}

		@Override
		public void reset() {
		}
	};

	// Animates the layer to slide out through the top and tell the SceneManager
	// to hide it when it is off-screen;
	IUpdateHandler SlideOut = new IUpdateHandler() {
		@Override
		public void onUpdate(float pSecondsElapsed) {
			if (NewUnitLayer.getInstance().getY() < ResourceManager
					.getInstance().cameraHeight / 2f + 480f) {
				NewUnitLayer
						.getInstance()
						.setPosition(
								NewUnitLayer.getInstance().getX(),
								Math.min(
										NewUnitLayer.getInstance().getY()
												+ (3600 * (pSecondsElapsed)),
										ResourceManager.getInstance().cameraHeight / 2f + 480f));
			} else {
				NewUnitLayer.getInstance().unregisterUpdateHandler(this);
				SceneManager.getInstance().hideLayer();
			}
		}

		@Override
		public void reset() {
		}
	};

	@Override
	public void onLoadLayer() {
		// Create and attach a background that hides the Layer when touched.
		final float BackgroundX = 0f, BackgroundY = 0f;
		final float BackgroundWidth = 760f, BackgroundHeight = 440f;

		Rectangle smth = new Rectangle(BackgroundX, BackgroundY,
				BackgroundWidth, BackgroundHeight,
				ResourceManager.getInstance().engine
						.getVertexBufferObjectManager());
		smth.setColor(0f, 0f, 0f, 0.85f);
		this.attachChild(smth);

		ButtonSprite backButton = new ButtonSprite(0f, 0f,
				ResourceManager.buttonTiledTextureRegion.getTextureRegion(0),
				ResourceManager.buttonTiledTextureRegion.getTextureRegion(1),
				ResourceManager.getInstance().engine
						.getVertexBufferObjectManager());
		backButton.setScale(
				1 / ResourceManager.getInstance().cameraScaleFactorX,
				1 / ResourceManager.getInstance().cameraScaleFactorY);
		backButton.setPosition(0, -180);
		backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(ButtonSprite pButtonSprite,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				// Play the click sound and show the NewUnit Layer.
				ResourceManager.clickSound.play();
				onHideLayer();

			}
		});

		Text turnButtonText = new Text(0, 0, ResourceManager.fontDefault32Bold,
				"BACK",
				ResourceManager.getInstance().engine
						.getVertexBufferObjectManager());
		turnButtonText.setPosition((backButton.getWidth()) / 2,
				(backButton.getHeight()) / 2);
		backButton.attachChild(turnButtonText);

		this.attachChild(backButton);
		this.registerTouchArea(backButton);

		// Create the NewUnitLayerTitle text for the Layer.
		Text newUnitLayerTitle = new Text(0, 0,
				ResourceManager.fontDefault32Bold, "BUY NEW UNITS",
				ResourceManager.getInstance().engine
						.getVertexBufferObjectManager());
		newUnitLayerTitle.setPosition(0f, BackgroundHeight / 2f
				- newUnitLayerTitle.getHeight());
		this.attachChild(newUnitLayerTitle);

		// BIT FOR BUYING THE KNIGHT
		
		Sprite sprite = new Sprite(-240, 60, 96, 96,
				ResourceManager.friendlyKnightTextureRegion,
				ResourceManager.getInstance().engine
						.getVertexBufferObjectManager());
		this.attachChild(sprite);

		buyKnightButton = new ButtonSprite(0f, 0f,
				ResourceManager.threeStateTiledTextureRegion
						.getTextureRegion(0),
				ResourceManager.threeStateTiledTextureRegion
						.getTextureRegion(1),
				ResourceManager.threeStateTiledTextureRegion
						.getTextureRegion(2),
				ResourceManager.getInstance().engine
						.getVertexBufferObjectManager());
		buyKnightButton.setScale(
				1 / ResourceManager.getInstance().cameraScaleFactorX,
				1 / ResourceManager.getInstance().cameraScaleFactorY);
		buyKnightButton.setPosition(-240, -20);
		buyKnightButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(ButtonSprite pButtonSprite,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				// Play the click sound and show the NewUnit Layer.
				ResourceManager.clickSound.play();
				SceneManager.getInstance().getActiveGameLevel().buyKnight();
				onHideLayer();

			}
		});

		Text buyButtonText = new Text(0, 0, ResourceManager.fontDefault32Bold,
				"BUY: " + String.valueOf(CONSTANTS.KNIGHT_PRICE),
				ResourceManager.getInstance().engine
						.getVertexBufferObjectManager());
		buyButtonText.setPosition(buyKnightButton.getWidth() / 2,
				buyKnightButton.getHeight() / 2);
		buyKnightButton.attachChild(buyButtonText);
		if (GameManager.getInstance().getGameField().getPlayerCoins() >= CONSTANTS.KNIGHT_PRICE)
			buyKnightButton.setEnabled(true);
		else
			buyKnightButton.setEnabled(false);
		this.attachChild(buyKnightButton);
		this.registerTouchArea(buyKnightButton);


		String description = new String(
				CONSTANTS.KNIGHT_DESCRIPTION);
		Text text = new Text(-240, -90, ResourceManager.fontDefault18,
				description, description.length(),
				ResourceManager.getInstance().engine
						.getVertexBufferObjectManager());
		this.attachChild(text);

		// BIT FOR BUYING THE DUCK

		sprite = new Sprite(0, 60, 96, 96,
				ResourceManager.friendlyDuckTextureRegion,
				ResourceManager.getInstance().engine
						.getVertexBufferObjectManager());
		this.attachChild(sprite);

		buyDuckButton = new ButtonSprite(0f, 0f,
				ResourceManager.threeStateTiledTextureRegion
						.getTextureRegion(0),
				ResourceManager.threeStateTiledTextureRegion
						.getTextureRegion(1),
				ResourceManager.threeStateTiledTextureRegion
						.getTextureRegion(2),
				ResourceManager.getInstance().engine
						.getVertexBufferObjectManager());
		buyDuckButton.setScale(
				1 / ResourceManager.getInstance().cameraScaleFactorX,
				1 / ResourceManager.getInstance().cameraScaleFactorY);
		buyDuckButton.setPosition(0, -20);
		buyDuckButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(ButtonSprite pButtonSprite,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				// Play the click sound and show the NewUnit Layer.
				ResourceManager.clickSound.play();
				SceneManager.getInstance().getActiveGameLevel().buyDuck();
				onHideLayer();

			}
		});

		buyButtonText = new Text(0, 0, ResourceManager.fontDefault32Bold,
				"BUY: " + String.valueOf(CONSTANTS.DUCK_PRICE),
				ResourceManager.getInstance().engine
						.getVertexBufferObjectManager());
		buyButtonText.setPosition(buyDuckButton.getWidth() / 2,
				buyDuckButton.getHeight() / 2);
		buyDuckButton.attachChild(buyButtonText);
		if (GameManager.getInstance().getGameField().getPlayerCoins() >= CONSTANTS.DUCK_PRICE)
			buyDuckButton.setEnabled(true);
		else
			buyDuckButton.setEnabled(false);
		this.attachChild(buyDuckButton);
		this.registerTouchArea(buyDuckButton);

		description = new String(
				CONSTANTS.DUCK_DESCRITPION);
		text = new Text(0, -90, ResourceManager.fontDefault18, description,
				description.length(),
				ResourceManager.getInstance().engine
						.getVertexBufferObjectManager());
		this.attachChild(text);

		// BIT FOR BUYING THE WITCH
		
		sprite = new Sprite(240, 60, 96, 96,
				ResourceManager.friendlyWitchTextureRegion,
				ResourceManager.getInstance().engine
						.getVertexBufferObjectManager());
		this.attachChild(sprite);

		buyWitchButton = new ButtonSprite(0f, 0f,
				ResourceManager.threeStateTiledTextureRegion
						.getTextureRegion(0),
				ResourceManager.threeStateTiledTextureRegion
						.getTextureRegion(1),
				ResourceManager.threeStateTiledTextureRegion
						.getTextureRegion(2),
				ResourceManager.getInstance().engine
						.getVertexBufferObjectManager());
		buyWitchButton.setScale(
				1 / ResourceManager.getInstance().cameraScaleFactorX,
				1 / ResourceManager.getInstance().cameraScaleFactorY);
		buyWitchButton.setPosition(240, -20);
		buyWitchButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(ButtonSprite pButtonSprite,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				// Play the click sound and show the NewUnit Layer.
				ResourceManager.clickSound.play();
				SceneManager.getInstance().getActiveGameLevel().buyWitch();
				onHideLayer();

			}
		});

		buyButtonText = new Text(0, 0, ResourceManager.fontDefault32Bold,
				"BUY: " + String.valueOf(CONSTANTS.WITCH_PRICE),
				ResourceManager.getInstance().engine
						.getVertexBufferObjectManager());
		buyButtonText.setPosition(buyWitchButton.getWidth() / 2,
				buyWitchButton.getHeight() / 2);
		buyWitchButton.attachChild(buyButtonText);
		if (GameManager.getInstance().getGameField().getPlayerCoins() >= CONSTANTS.WITCH_PRICE)
			buyWitchButton.setEnabled(true);
		else
			buyWitchButton.setEnabled(false);
		this.attachChild(buyWitchButton);
		this.registerTouchArea(buyWitchButton);

		description = new String(
				CONSTANTS.WITCH_DESCRIPTION);
		text = new Text(240, -90, ResourceManager.fontDefault18, description,
				description.length(),
				ResourceManager.getInstance().engine
						.getVertexBufferObjectManager());
		this.attachChild(text);

		this.setPosition(ResourceManager.getInstance().cameraWidth / 2f,
				ResourceManager.getInstance().cameraHeight / 2f + 480f);
	}

	@Override
	public void onShowLayer() {
		if (GameManager.getInstance().getGameField().getPlayerCoins() >= CONSTANTS.KNIGHT_PRICE
				&& GameManager.getInstance().getGameField().getPlayerPieces()
						.size() < CONSTANTS.MAX_UNIT_COUNT)
			buyKnightButton.setEnabled(true);
		else
			buyKnightButton.setEnabled(false);
		if (GameManager.getInstance().getGameField().getPlayerCoins() >= CONSTANTS.WITCH_PRICE
				&& GameManager.getInstance().getGameField().getPlayerPieces()
						.size() < CONSTANTS.MAX_UNIT_COUNT)
			buyDuckButton.setEnabled(true);
		else
			buyDuckButton.setEnabled(false);

		if (GameManager.getInstance().getGameField().getPlayerCoins() >= CONSTANTS.DUCK_PRICE
				&& GameManager.getInstance().getGameField().getPlayerPieces()
						.size() < CONSTANTS.MAX_UNIT_COUNT)
			buyWitchButton.setEnabled(true);
		else
			buyWitchButton.setEnabled(false);
		this.registerUpdateHandler(SlideIn);
	}

	@Override
	public void onHideLayer() {
		this.registerUpdateHandler(SlideOut);
	}

	@Override
	public void onUnloadLayer() {
	}
}
