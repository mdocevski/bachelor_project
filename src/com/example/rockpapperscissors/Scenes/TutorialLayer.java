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

public class TutorialLayer extends ManagedLayer {
	private static final TutorialLayer INSTANCE = new TutorialLayer();
	private ButtonSprite NextButton;
	private ButtonSprite PreviousButton;
	private int displayedTutorialImage = 0;
	private Sprite[] TutorialImages = new Sprite[CONSTANTS.TUTORIAL_PIC_NUM];

	public static TutorialLayer getInstance() {
		return INSTANCE;
	}

	// Animates the layer to slide in from the top.
	IUpdateHandler SlideIn = new IUpdateHandler() {
		@Override
		public void onUpdate(float pSecondsElapsed) {
			if (TutorialLayer.getInstance().getY() > ResourceManager
					.getInstance().cameraHeight / 2f) {
				TutorialLayer
						.getInstance()
						.setPosition(
								TutorialLayer.getInstance().getX(),
								Math.max(
										TutorialLayer.getInstance().getY()
												- (3600 * (pSecondsElapsed)),
										ResourceManager.getInstance().cameraHeight / 2f));
			} else {
				TutorialLayer.getInstance().unregisterUpdateHandler(this);
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
			if (TutorialLayer.getInstance().getY() < ResourceManager
					.getInstance().cameraHeight / 2f + 480f) {
				TutorialLayer
						.getInstance()
						.setPosition(
								TutorialLayer.getInstance().getX(),
								Math.min(
										TutorialLayer.getInstance().getY()
												+ (3600 * (pSecondsElapsed)),
										ResourceManager.getInstance().cameraHeight / 2f + 480f));
			} else {
				TutorialLayer.getInstance().unregisterUpdateHandler(this);
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

		for (int i = 0; i < CONSTANTS.TUTORIAL_PIC_NUM; i++) {
			TutorialImages[i] = new Sprite(0, 20,
					ResourceManager.tutorialImages[i],
					ResourceManager.getInstance().engine
							.getVertexBufferObjectManager());
			TutorialImages[i].setScale(
					1 / ResourceManager.getInstance().cameraScaleFactorX,
					1 / ResourceManager.getInstance().cameraScaleFactorY);
			TutorialImages[i].setVisible(false);
			this.attachChild(TutorialImages[i]);
		}

		PreviousButton = new ButtonSprite(0f, 0f,
				ResourceManager.buttonTiledTextureRegion.getTextureRegion(0),
				ResourceManager.buttonTiledTextureRegion.getTextureRegion(1),
				ResourceManager.getInstance().engine
						.getVertexBufferObjectManager());

		PreviousButton.setScale(
				1 / ResourceManager.getInstance().cameraScaleFactorX,
				1 / ResourceManager.getInstance().cameraScaleFactorY);

		float posX, posY;
		posX = backButton.getX() - backButton.getWidth() / 2
				- PreviousButton.getWidth() / 2 - 10;
		posY = backButton.getY();
		PreviousButton.setPosition(posX, posY);
		PreviousButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(ButtonSprite pButtonSprite,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				showPreviousImage();
			}
		});

		Text previousButtonText = new Text(0, 0,
				ResourceManager.fontDefault32Bold, "PREV",
				ResourceManager.getInstance().engine
						.getVertexBufferObjectManager());
		previousButtonText.setPosition((PreviousButton.getWidth()) / 2,
				(PreviousButton.getHeight()) / 2);

		PreviousButton.attachChild(previousButtonText);
		this.attachChild(PreviousButton);
		this.registerTouchArea(PreviousButton);

		NextButton = new ButtonSprite(0f, 0f,
				ResourceManager.buttonTiledTextureRegion.getTextureRegion(0),
				ResourceManager.buttonTiledTextureRegion.getTextureRegion(1),
				ResourceManager.getInstance().engine
						.getVertexBufferObjectManager());
		NextButton.setScale(
				1 / ResourceManager.getInstance().cameraScaleFactorX,
				1 / ResourceManager.getInstance().cameraScaleFactorY);
		posX = backButton.getX() + backButton.getWidth() / 2
				+ PreviousButton.getWidth() / 2 + 10;
		posY = backButton.getY();
		NextButton.setPosition(posX, posY);
		NextButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(ButtonSprite pButtonSprite,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				showNextImage();
			}
		});

		Text nextButtonText = new Text(0, 0, ResourceManager.fontDefault32Bold,
				"NEXT",
				ResourceManager.getInstance().engine
						.getVertexBufferObjectManager());
		nextButtonText.setPosition((NextButton.getWidth()) / 2,
				(NextButton.getHeight()) / 2);
		NextButton.attachChild(nextButtonText);

		this.attachChild(NextButton);
		this.registerTouchArea(NextButton);

		this.attachChild(backButton);
		this.registerTouchArea(backButton);

		// Create the OptionsLayerTitle text for the Layer.
		Text TutorialLayerTitle = new Text(0, 0,
				ResourceManager.fontDefault32Bold, "TUTORIAL",
				ResourceManager.getInstance().engine
						.getVertexBufferObjectManager());
		TutorialLayerTitle.setPosition(0f, BackgroundHeight / 2f
				- TutorialLayerTitle.getHeight());
		this.attachChild(TutorialLayerTitle);

		this.setPosition(ResourceManager.getInstance().cameraWidth / 2f,
				ResourceManager.getInstance().cameraHeight / 2f + 480f);
	}

	protected void showPreviousImage() {
		TutorialImages[displayedTutorialImage].setVisible(false);
		displayedTutorialImage--;
		if (displayedTutorialImage < 0) {
			displayedTutorialImage = CONSTANTS.TUTORIAL_PIC_NUM - 1;
		}
		TutorialImages[displayedTutorialImage].setVisible(true);
	}

	protected void showNextImage() {
		TutorialImages[displayedTutorialImage].setVisible(false);
		displayedTutorialImage++;
		if (displayedTutorialImage == CONSTANTS.TUTORIAL_PIC_NUM) {
			displayedTutorialImage = 0;
		}
		TutorialImages[displayedTutorialImage].setVisible(true);

	}

	@Override
	public void onShowLayer() {
		this.registerUpdateHandler(SlideIn);
		displayedTutorialImage = 0;
		TutorialImages[displayedTutorialImage].setVisible(true);
	}

	@Override
	public void onHideLayer() {
		this.registerUpdateHandler(SlideOut);
	}

	@Override
	public void onUnloadLayer() {
	}
}