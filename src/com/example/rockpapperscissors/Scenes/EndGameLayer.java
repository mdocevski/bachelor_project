package com.example.rockpapperscissors.Scenes;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;
import org.andengine.entity.text.Text;

import com.example.rockpapperscissors.CONSTANTS;
import com.example.rockpapperscissors.SwarmConsts;
import com.example.rockpapperscissors.Managers.GameManager;
import com.example.rockpapperscissors.Managers.ResourceManager;
import com.example.rockpapperscissors.Managers.SceneManager;
import com.swarmconnect.Swarm;
import com.swarmconnect.SwarmActiveUser;
import com.swarmconnect.SwarmLeaderboard;
import com.swarmconnect.delegates.SwarmLoginListener;

public class EndGameLayer extends ManagedLayer {
	private static EndGameLayer INSTANCE = new EndGameLayer();

	private Text scoreText;
	private Text turnCountText;
	ButtonSprite backButton;
	private Text EndGameLayerTittle;
	ButtonSprite MainMenuButton;
	Text MainMenuButtonText;

	public static EndGameLayer getInstance() {
		return INSTANCE;
	}

	// Animates the layer to slide in from the top.
	IUpdateHandler SlideIn = new IUpdateHandler() {
		@Override
		public void onUpdate(float pSecondsElapsed) {
			if (EndGameLayer.getInstance().getY() > ResourceManager
					.getInstance().cameraHeight / 2f) {
				EndGameLayer
						.getInstance()
						.setPosition(
								EndGameLayer.getInstance().getX(),
								Math.max(
										EndGameLayer.getInstance().getY()
												- (3600 * (pSecondsElapsed)),
										ResourceManager.getInstance().cameraHeight / 2f));
			} else {
				EndGameLayer.getInstance().unregisterUpdateHandler(this);
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
			if (EndGameLayer.getInstance().getY() < ResourceManager
					.getInstance().cameraHeight / 2f + 480f) {
				EndGameLayer
						.getInstance()
						.setPosition(
								EndGameLayer.getInstance().getX(),
								Math.min(
										EndGameLayer.getInstance().getY()
												+ (3600 * (pSecondsElapsed)),
										ResourceManager.getInstance().cameraHeight / 2f + 480f));
			} else {
				EndGameLayer.getInstance().unregisterUpdateHandler(this);
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


		// Create the OptionsLayerTitle text for the Layer.
		EndGameLayerTittle = new Text(0, 0, ResourceManager.fontDefault32Bold,
				"END GAME", 30,
				ResourceManager.getInstance().engine
						.getVertexBufferObjectManager());
		EndGameLayerTittle.setPosition(0f, BackgroundHeight / 2f
				- EndGameLayerTittle.getHeight());
		this.attachChild(EndGameLayerTittle);

		this.setPosition(ResourceManager.getInstance().cameraWidth / 2f,
				ResourceManager.getInstance().cameraHeight / 2f + 480f);
		turnCountText = new Text(0, 0, ResourceManager.fontDefault32Bold,
				"TURN COUNT: "
						+ String.valueOf(GameManager.getInstance()
								.getTurnCount()), 26,
				ResourceManager.getInstance().engine
						.getVertexBufferObjectManager());
		turnCountText
				.setPosition(
						EndGameLayerTittle.getX(),
						EndGameLayerTittle.getY()
								- EndGameLayerTittle.getHeight() - 24);
		this.attachChild(turnCountText);

		scoreText = new Text(0, 0, ResourceManager.fontDefault32Bold, "SCORE: "
				+ String.valueOf(GameManager.getInstance().getTurnCount()), 26,
				ResourceManager.getInstance().engine
						.getVertexBufferObjectManager());
		scoreText.setPosition(turnCountText.getX(), turnCountText.getY()
				- turnCountText.getHeight() - 24);
		this.attachChild(scoreText);

		ButtonSprite submitButton = new ButtonSprite(0f, 0f,
				ResourceManager.buttonTiledTextureRegion.getTextureRegion(0),
				ResourceManager.buttonTiledTextureRegion.getTextureRegion(1),
				ResourceManager.getInstance().engine
						.getVertexBufferObjectManager());
		submitButton.setPosition(-0.5f*submitButton.getWidth() ,-40f);
		submitButton.setScale(
				1 / ResourceManager.getInstance().cameraScaleFactorX,
				1 / ResourceManager.getInstance().cameraScaleFactorY);
		submitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(ButtonSprite pButtonSprite,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				ResourceManager.clickSound.play();
				if (!Swarm.isInitialized()) {
					Swarm.init(ResourceManager.getInstance().getRPSActivity(),
							SwarmConsts.App.APP_ID, SwarmConsts.App.APP_AUTH,
							mySwarmLoginListener);
				} else {
					SwarmLeaderboard.submitScore(11633, GameManager
							.getInstance().getPlayerScore());
					SceneManager.getInstance().showMainMenu();
				}
			}
		});
		this.attachChild(submitButton);
		Text submitButtonText = new Text(0, 0,
				ResourceManager.fontDefault32Bold, "SUBMIT",
				ResourceManager.getInstance().engine
						.getVertexBufferObjectManager());
		submitButtonText.setPosition(submitButton.getWidth() / 2,
				submitButton.getHeight() / 2);
		submitButton.attachChild(submitButtonText);
		this.registerTouchArea(submitButton);

		MainMenuButton = new ButtonSprite(0f, 0f,
				ResourceManager.buttonTiledTextureRegion.getTextureRegion(0),
				ResourceManager.buttonTiledTextureRegion.getTextureRegion(1),
				ResourceManager.getInstance().engine
						.getVertexBufferObjectManager());
		MainMenuButton.setPosition(
				submitButton.getX() + submitButton.getWidth(),
				submitButton.getY());
		MainMenuButton.setScale(
				1 / ResourceManager.getInstance().cameraScaleFactorX,
				1 / ResourceManager.getInstance().cameraScaleFactorY);
		MainMenuButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(ButtonSprite pButtonSprite,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				// Play the click sound and show the Main Menu.
				ResourceManager.clickSound.play();
				SceneManager.getInstance().showMainMenu();
			}
		});

		MainMenuButtonText = new Text(MainMenuButton.getWidth() / 2,
				MainMenuButton.getHeight() / 2,
				ResourceManager.fontDefault32Bold, "MENU",
				ResourceManager.getInstance().engine
						.getVertexBufferObjectManager());
		MainMenuButton.attachChild(MainMenuButtonText);
		this.attachChild(MainMenuButton);
		this.registerTouchArea(MainMenuButton);
	}

	@Override
	public void onShowLayer() {
		this.registerUpdateHandler(SlideIn);
		if (GameManager.getInstance().isGameOver()) {
			String title = new String();
			if (GameManager.getInstance().getWhoWon() == CONSTANTS.PLAYER) {
				title = "GAME OVER YOU WON";
			} else if (GameManager.getInstance().getWhoWon() == CONSTANTS.ENEMY) {
				title = "GAME OVER YOU LOST";
			} else {
				title = "GAME OVER, DRAW";
			}
			EndGameLayerTittle.setText(title);
			MainMenuButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX,
						float pTouchAreaLocalY) {
					ResourceManager.clickSound.play();
					SceneManager.getInstance().showMainMenu();				
				}
			});
			MainMenuButtonText.setText("MENU");
		} else {
			EndGameLayerTittle.setText("END THE GAME?");
			MainMenuButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX,
						float pTouchAreaLocalY) {
					ResourceManager.clickSound.play();
					SceneManager.getInstance().hideLayer();					
				}
			});
			MainMenuButtonText.setText("BACK");
		}
		turnCountText.setText("TURNS: "
				+ String.valueOf(GameManager.getInstance().getTurnCount()));
		scoreText.setText("SCORE: "
				+ String.valueOf(GameManager.getInstance().getPlayerScore()));
	
	}

	@Override
	public void onHideLayer() {
		this.registerUpdateHandler(SlideOut);
		if (GameManager.getInstance().isGameOver()) {
			SceneManager.getInstance().showMainMenu();
		}
	}

	@Override
	public void onUnloadLayer() {
	}

	private SwarmLoginListener mySwarmLoginListener = new SwarmLoginListener() {

		// This method is called when the login process has started
		// (when a login dialog is displayed to the user).
		public void loginStarted() {
		}

		// This method is called if the user cancels the login process.
		public void loginCanceled() {
		}

		// This method is called when the user has successfully logged in.
		public void userLoggedIn(SwarmActiveUser user) {
			SwarmLeaderboard.submitScore(11633, GameManager.getInstance()
					.getPlayerScore());
			SceneManager.getInstance().showMainMenu();
		}

		// This method is called when the user logs out.
		public void userLoggedOut() {
		}

	};
}