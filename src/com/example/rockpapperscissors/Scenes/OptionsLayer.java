package com.example.rockpapperscissors.Scenes;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;
import org.andengine.entity.text.Text;

import com.example.rockpapperscissors.CONSTANTS;
import com.example.rockpapperscissors.Managers.GameManager;
import com.example.rockpapperscissors.Managers.ResourceManager;
import com.example.rockpapperscissors.Managers.SceneManager;

public class OptionsLayer extends ManagedLayer {
	private static final OptionsLayer INSTANCE = new OptionsLayer();
	private static Text AgressionText;
	private static Text GreedinesText;
	private ButtonSprite GreedinesButton;
	private ButtonSprite AgressionButton;

	public static OptionsLayer getInstance() {
		return INSTANCE;
	}

	// Animates the layer to slide in from the top.
	IUpdateHandler SlideIn = new IUpdateHandler() {
		@Override
		public void onUpdate(float pSecondsElapsed) {
			if (OptionsLayer.getInstance().getY() > ResourceManager
					.getInstance().cameraHeight / 2f) {
				OptionsLayer
						.getInstance()
						.setPosition(
								OptionsLayer.getInstance().getX(),
								Math.max(
										OptionsLayer.getInstance().getY()
												- (3600 * (pSecondsElapsed)),
										ResourceManager.getInstance().cameraHeight / 2f));
			} else {
				OptionsLayer.getInstance().unregisterUpdateHandler(this);
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
			if (OptionsLayer.getInstance().getY() < ResourceManager
					.getInstance().cameraHeight / 2f + 480f) {
				OptionsLayer
						.getInstance()
						.setPosition(
								OptionsLayer.getInstance().getX(),
								Math.min(
										OptionsLayer.getInstance().getY()
												+ (3600 * (pSecondsElapsed)),
										ResourceManager.getInstance().cameraHeight / 2f + 480f));
			} else {
				OptionsLayer.getInstance().unregisterUpdateHandler(this);
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

		ButtonSprite TutorialButton = new ButtonSprite(0f, 0f,
				ResourceManager.buttonTiledTextureRegion.getTextureRegion(0),
				ResourceManager.buttonTiledTextureRegion.getTextureRegion(1),
				ResourceManager.getInstance().engine
						.getVertexBufferObjectManager());
		TutorialButton.setScale(
				1 / ResourceManager.getInstance().cameraScaleFactorX,
				1 / ResourceManager.getInstance().cameraScaleFactorY);
		TutorialButton.setPosition(0, 90);
		TutorialButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(ButtonSprite pButtonSprite,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				SceneManager.getInstance().showTutorialLayer(true);
			}
		});
		this.attachChild(TutorialButton);
		this.registerTouchArea(TutorialButton);
		Text TutorialButtonText = new Text(0f, 0f,
				ResourceManager.fontDefault32Bold, "TUTORIAL",
				ResourceManager.getInstance().engine
						.getVertexBufferObjectManager());
		TutorialButtonText.setPosition(TutorialButton.getWidth() / 2,
				TutorialButton.getHeight() / 2);
		TutorialButton.attachChild(TutorialButtonText);

		Text description = new Text(0, 0, ResourceManager.fontDefault32Bold,
				"Opponent Traits",
				ResourceManager.getInstance().engine
						.getVertexBufferObjectManager());
		description.setScale(
				1 / ResourceManager.getInstance().cameraScaleFactorX,
				1 / ResourceManager.getInstance().cameraScaleFactorY);
		description.setPosition(0, 30);
		this.attachChild(description);
		AgressionButton = new ButtonSprite(0f, 0f,
				ResourceManager.buttonTiledTextureRegion.getTextureRegion(0),
				ResourceManager.buttonTiledTextureRegion.getTextureRegion(1),
				ResourceManager.getInstance().engine
						.getVertexBufferObjectManager());
		AgressionButton.setScale(
				1 / ResourceManager.getInstance().cameraScaleFactorX,
				1 / ResourceManager.getInstance().cameraScaleFactorY);
		AgressionButton.setPosition(-AgressionButton.getWidth() / 2, -20);
		AgressionButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(ButtonSprite pButtonSprite,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				cycleAgression();
			}
		});
		Text AgressionButtonText = new Text(0, 0,
				ResourceManager.fontDefault32Bold, "Agression",
				ResourceManager.getInstance().engine
						.getVertexBufferObjectManager());
		AgressionButtonText.setPosition(AgressionButton.getWidth() / 2,
				AgressionButton.getHeight() / 2);
		AgressionButton.attachChild(AgressionButtonText);

		this.attachChild(AgressionButton);
		this.registerTouchArea(AgressionButton);

		AgressionText = new Text(0, 0, ResourceManager.fontDefault32Bold, "",
				30,
				ResourceManager.getInstance().engine
						.getVertexBufferObjectManager());
		AgressionText.setPosition(AgressionText.getWidth() / 2,
				AgressionButton.getY());
		AgressionText.setScale(
				1 / ResourceManager.getInstance().cameraScaleFactorX,
				1 / ResourceManager.getInstance().cameraScaleFactorY);
		this.attachChild(AgressionText);

		GreedinesButton = new ButtonSprite(0f, 0f,
				ResourceManager.buttonTiledTextureRegion.getTextureRegion(0),
				ResourceManager.buttonTiledTextureRegion.getTextureRegion(1),
				ResourceManager.getInstance().engine
						.getVertexBufferObjectManager());
		GreedinesButton.setScale(
				1 / ResourceManager.getInstance().cameraScaleFactorX,
				1 / ResourceManager.getInstance().cameraScaleFactorY);
		GreedinesButton.setPosition(-GreedinesButton.getWidth() / 2, -90);
		GreedinesButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(ButtonSprite pButtonSprite,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				cycleGreedines();
			}
		});
		Text GreedinesButtonText = new Text(0, 0,
				ResourceManager.fontDefault32Bold, "Greedines",
				ResourceManager.getInstance().engine
						.getVertexBufferObjectManager());
		GreedinesButtonText.setPosition(GreedinesButton.getWidth() / 2,
				GreedinesButton.getHeight() / 2);
		GreedinesButton.attachChild(GreedinesButtonText);

		this.attachChild(GreedinesButton);
		this.registerTouchArea(GreedinesButton);

		GreedinesText = new Text(0, 0, ResourceManager.fontDefault32Bold, "",
				30,
				ResourceManager.getInstance().engine
						.getVertexBufferObjectManager());
		GreedinesText.setPosition(GreedinesText.getWidth() / 2,
				GreedinesButton.getY());
		GreedinesText.setScale(
				1 / ResourceManager.getInstance().cameraScaleFactorX,
				1 / ResourceManager.getInstance().cameraScaleFactorY);
		this.attachChild(GreedinesText);

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
				// Play the click sound and show the Options Layer.
				ResourceManager.clickSound.play();
				onHideLayer();

			}
		});

		Text backButtonText = new Text(0, 0, ResourceManager.fontDefault32Bold,
				"BACK",
				ResourceManager.getInstance().engine
						.getVertexBufferObjectManager());
		backButtonText.setPosition((backButton.getWidth()) / 2,
				(backButton.getHeight()) / 2);
		backButton.attachChild(backButtonText);

		this.attachChild(backButton);
		this.registerTouchArea(backButton);

		// Create the OptionsLayerTitle text for the Layer.
		Text OptionsLayerTitle = new Text(0, 0,
				ResourceManager.fontDefault32Bold, "OPTIONS",
				ResourceManager.getInstance().engine
						.getVertexBufferObjectManager());
		OptionsLayerTitle.setPosition(0f, BackgroundHeight / 2f
				- OptionsLayerTitle.getHeight());
		this.attachChild(OptionsLayerTitle);

		this.setPosition(ResourceManager.getInstance().cameraWidth / 2f,
				ResourceManager.getInstance().cameraHeight / 2f + 480f);
	}

	protected void cycleGreedines() {
		GameManager.getInstance().cycleGreedines();
		updateGreedinesText();
	}

	private void updateGreedinesText() {
		if (GameManager.getInstance().isGreedines()) {
			GreedinesText.setText("GREEDY");
		} else {
			GreedinesText.setText("NOT GREEDY");
		}
		GreedinesText.setPosition(GreedinesText.getWidth() / 2,
				GreedinesButton.getY());
	}

	protected void cycleAgression() {
		GameManager.getInstance().cycleAgression();
		updateAgressionText();
	}

	private void updateAgressionText() {
		switch (GameManager.getInstance().getAgression()) {
		case CONSTANTS.AGRESSIVE_AGRESSION:
			AgressionText.setText("AGRESSIVE");
			break;
		case CONSTANTS.NEUTRAL_AGRESSION:
			AgressionText.setText("NEUTRAL");
			break;
		case CONSTANTS.DEFENSIVE_AGRESSION:
			AgressionText.setText("DEFENSIVE");
			break;
		default:
			AgressionText.setText("ERROR");
		}
		AgressionText.setPosition(AgressionText.getWidth() / 2,
				AgressionButton.getY());
	}

	@Override
	public void onShowLayer() {
		this.registerUpdateHandler(SlideIn);
		updateAgressionText();
		updateGreedinesText();
	}

	@Override
	public void onHideLayer() {
		this.registerUpdateHandler(SlideOut);
	}

	@Override
	public void onUnloadLayer() {
	}
}