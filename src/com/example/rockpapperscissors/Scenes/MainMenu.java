package com.example.rockpapperscissors.Scenes;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;

import com.example.rockpapperscissors.SwarmConsts;
import com.example.rockpapperscissors.Managers.ResourceManager;
import com.example.rockpapperscissors.Managers.SceneManager;
import com.swarmconnect.Swarm;
import com.swarmconnect.SwarmActiveUser;
import com.swarmconnect.SwarmLeaderboard;
import com.swarmconnect.delegates.SwarmLoginListener;

public class MainMenu extends ManagedMenuScene {

	private static final MainMenu INSTANCE = new MainMenu();

	public static MainMenu getInstance() {
		return INSTANCE;
	}

	public MainMenu() {
		this.setOnSceneTouchListenerBindingOnActionDownEnabled(true);
		this.setTouchAreaBindingOnActionDownEnabled(true);
		this.setTouchAreaBindingOnActionMoveEnabled(true);
		
	}

	// No loading screen means no reason to use the following methods.
	@Override
	public Scene onLoadingScreenLoadAndShown() {
		return null;
	}

	@Override
	public void onLoadingScreenUnloadAndHidden() {
	}

	// The objects that will make up our Main Menu
	private Sprite BackgroundSprite;
	private ButtonSprite PlayButton;
	private Text PlayButtonText;
	private ButtonSprite OptionsButton;
	private Text OptionsButtonText;
	private Text TitleText;
	private ButtonSprite LoginButton;
	private Text LoginButtonText;
	private Text LogedInAsText;
	private ButtonSprite LeaderboardButton;
	private Text LeaderboardButtonText;

	@Override
	public void onLoadScene() {
		// Load the menu resources
		ResourceManager.loadMenuResources();

		// Create the background
		BackgroundSprite = new Sprite(
				ResourceManager.getInstance().cameraWidth / 2f,
				ResourceManager.getInstance().cameraHeight / 2f,
				ResourceManager.menuBackgroundTextureRegion,
				ResourceManager.getInstance().engine
						.getVertexBufferObjectManager());
		BackgroundSprite
				.setScaleX(ResourceManager.getInstance().cameraWidth / 800f);
		BackgroundSprite
				.setScaleY(ResourceManager.getInstance().cameraHeight / 480f);
		BackgroundSprite.setZIndex(-5000);
		this.attachChild(BackgroundSprite);

		// Create a Play button. Notice that the Game scenes, unlike menus, are
		// not referred to in a static way.
		PlayButton = new ButtonSprite(
				(ResourceManager.getInstance().cameraWidth - ResourceManager.buttonTiledTextureRegion
						.getTextureRegion(0).getWidth()) / 2f,
				(ResourceManager.getInstance().cameraHeight - ResourceManager.buttonTiledTextureRegion
						.getTextureRegion(0).getHeight()) * (1f / 3f),
				ResourceManager.buttonTiledTextureRegion.getTextureRegion(0),
				ResourceManager.buttonTiledTextureRegion.getTextureRegion(1),
				ResourceManager.getInstance().engine
						.getVertexBufferObjectManager());
		PlayButtonText = new Text(0, 0, ResourceManager.fontDefault32Bold,
				"PLAY",
				ResourceManager.getInstance().engine
						.getVertexBufferObjectManager());
		PlayButtonText.setPosition((PlayButton.getWidth()) / 2,
				(PlayButton.getHeight()) / 2);
		PlayButton.attachChild(PlayButtonText);
//		PlayButton.setScale(
//				1 / ResourceManager.getInstance().cameraScaleFactorX,
//				1 / ResourceManager.getInstance().cameraScaleFactorY);
		this.attachChild(PlayButton);
		PlayButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(ButtonSprite pButtonSprite,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				// Create a new GameLevel and show it using the SceneManager.
				// And play a click.
				SceneManager.getInstance().showScene(new GameLevel());
				ResourceManager.clickSound.play();
			}
		});
		this.registerTouchArea(PlayButton);

		// Create an Option button. Notice that the SceneManager is being told
		// to not pause the scene while the OptionsLayer is open.
		OptionsButton = new ButtonSprite(PlayButton.getX()
				+ PlayButton.getWidth(), PlayButton.getY(),
				ResourceManager.buttonTiledTextureRegion.getTextureRegion(0),
				ResourceManager.buttonTiledTextureRegion.getTextureRegion(1),
				ResourceManager.getInstance().engine
						.getVertexBufferObjectManager());
//		OptionsButton.setScale(
//				1 / ResourceManager.getInstance().cameraScaleFactorX,
//				1 / ResourceManager.getInstance().cameraScaleFactorY);
		OptionsButtonText = new Text(0, 0, ResourceManager.fontDefault32Bold,
				"OPTIONS",
				ResourceManager.getInstance().engine
						.getVertexBufferObjectManager());
		OptionsButtonText.setPosition((OptionsButton.getWidth()) / 2,
				(OptionsButton.getHeight()) / 2);
		OptionsButton.attachChild(OptionsButtonText);
		this.attachChild(OptionsButton);
		OptionsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(ButtonSprite pButtonSprite,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				// Show the OptionsLayer and play a click.
				SceneManager.getInstance().showOptionsLayer(false);
				ResourceManager.clickSound.play();
			}
		});
		this.registerTouchArea(OptionsButton);


		if (Swarm.isSwarmEnabled())
			if (Swarm.isLoggedIn()) {
				showLogedInState();
			} else {
				showNotLogedInState();
			}

		LeaderboardButton = new ButtonSprite(
				(PlayButton.getX() + OptionsButton.getX()) / 2,
				PlayButton.getY() + PlayButton.getHeight(),
				ResourceManager.buttonTiledTextureRegion.getTextureRegion(0),
				ResourceManager.buttonTiledTextureRegion.getTextureRegion(1),
				ResourceManager.getInstance().engine
						.getVertexBufferObjectManager());
		LeaderboardButtonText = new Text(0, 0,
				ResourceManager.fontDefault32Bold, "LEADERBOARDS",
				ResourceManager.getInstance().engine
						.getVertexBufferObjectManager());
		LeaderboardButton.attachChild(LeaderboardButtonText);
		LeaderboardButtonText.setPosition(LeaderboardButton.getWidth() / 2,
				LeaderboardButton.getHeight() / 2);
//		LeaderboardButton.setScale(
//				1 / ResourceManager.getInstance().cameraScaleFactorX,
//				1 / ResourceManager.getInstance().cameraScaleFactorY);
		this.attachChild(LeaderboardButton);
		LeaderboardButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(ButtonSprite pButtonSprite,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (!Swarm.isInitialized()) {
					Swarm.init(ResourceManager.getInstance().getRPSActivity(),
							SwarmConsts.App.APP_ID, SwarmConsts.App.APP_AUTH,
							leaderboardsButtonListner);
				}
				SwarmLeaderboard.showLeaderboard(11633);
			}
		});
		this.registerTouchArea(LeaderboardButton);
		// Create a title
		TitleText = new Text(0, 0, ResourceManager.fontDefault108StrokeBold,
				"MiniMax\n Attacks",
				ResourceManager.getInstance().engine
				.getVertexBufferObjectManager());
		TitleText.setPosition((ResourceManager.getInstance().cameraWidth) / 2,
				LeaderboardButton.getY() + 20 + TitleText.getHeight()/2);
//		TitleText.setScale(
//				1 / ResourceManager.getInstance().cameraScaleFactorX,
//				1 / ResourceManager.getInstance().cameraScaleFactorY);
		this.attachChild(TitleText);
	}

	private void showNotLogedInState() {
		if (null != LoginButton)
			LoginButton.detachSelf();
		LoginButton = new ButtonSprite(PlayButton.getX(), OptionsButton.getY()
				- OptionsButton.getHeight(),
				ResourceManager.buttonTiledTextureRegion.getTextureRegion(0),
				ResourceManager.buttonTiledTextureRegion.getTextureRegion(1),
				ResourceManager.getInstance().engine
						.getVertexBufferObjectManager());
//		LoginButton.setScale(
//				1 / ResourceManager.getInstance().cameraScaleFactorX,
//				1 / ResourceManager.getInstance().cameraScaleFactorY);
		LoginButtonText = new Text(0, 0, ResourceManager.fontDefault32Bold,
				"LOG IN",
				ResourceManager.getInstance().engine
						.getVertexBufferObjectManager());
		LoginButtonText.setPosition(LoginButton.getWidth() / 2,
				LoginButton.getHeight() / 2);
		LoginButton.attachChild(LoginButtonText);
		this.attachChild(LoginButton);
		LoginButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(ButtonSprite pButtonSprite,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				synchronized (this) {
					if (!Swarm.isInitialized()) {
						Swarm.init(ResourceManager.getInstance()
								.getRPSActivity(), SwarmConsts.App.APP_ID,
								SwarmConsts.App.APP_AUTH, loginButtonListener);
					} else {
						Swarm.showLogin();
					}
				}
			}
		});
		this.registerTouchArea(LoginButton);
	}

	private void showLogedInState() {
		if (null != LoginButton)
			LoginButton.detachSelf();
		if (null != LogedInAsText)
			LogedInAsText.detachSelf();
		LogedInAsText = new Text(PlayButton.getX(), OptionsButton.getY()
				- OptionsButton.getHeight(), ResourceManager.fontDefault32Bold,
				Swarm.user.username,
				ResourceManager.getInstance().engine
						.getVertexBufferObjectManager());
		this.attachChild(LogedInAsText);

		LoginButton = new ButtonSprite(OptionsButton.getX(),
				LogedInAsText.getY(),
				ResourceManager.buttonTiledTextureRegion.getTextureRegion(0),
				ResourceManager.buttonTiledTextureRegion.getTextureRegion(1),
				ResourceManager.getInstance().engine
						.getVertexBufferObjectManager());
//		LoginButton.setScale(
//				1 / ResourceManager.getInstance().cameraScaleFactorX,
//				1 / ResourceManager.getInstance().cameraScaleFactorY);
		LoginButtonText = new Text(0, 0, ResourceManager.fontDefault32Bold,
				"LOG OUT",
				ResourceManager.getInstance().engine
						.getVertexBufferObjectManager());
		LoginButtonText.setPosition(LoginButton.getWidth() / 2,
				LoginButton.getHeight() / 2);
		LoginButton.attachChild(LoginButtonText);
		this.attachChild(LoginButton);
		LoginButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(ButtonSprite pButtonSprite,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				synchronized (this) {
					Swarm.logOut();
				}
			}
		});
		this.registerTouchArea(LoginButton);
	}

	@Override
	public void onShowScene() {
		if (Swarm.isLoggedIn()) {
			showLogedInState();
		} else {
			showNotLogedInState();
		}
	}

	@Override
	public void onHideScene() {
	}

	@Override
	public void onUnloadScene() {
	}

	private SwarmLoginListener loginButtonListener = new SwarmLoginListener() {

		// This method is called when the login process has started
		// (when a login dialog is displayed to the user).
		public void loginStarted() {
		}

		// This method is called if the user cancels the login process.
		public void loginCanceled() {
		}

		// This method is called when the user has successfully logged in.
		public void userLoggedIn(SwarmActiveUser user) {
			showLogedInState();
		}

		// This method is called when the user logs out.
		public void userLoggedOut() {
			showNotLogedInState();
		}

	};

	private SwarmLoginListener leaderboardsButtonListner = new SwarmLoginListener() {

		// This method is called when the login process has started
		// (when a login dialog is displayed to the user).
		public void loginStarted() {
		}

		// This method is called if the user cancels the login process.
		public void loginCanceled() {
		}

		// This method is called when the user has successfully logged in.
		public void userLoggedIn(SwarmActiveUser user) {
			showLogedInState();
			SwarmLeaderboard.showLeaderboard(11633);
		}

		// This method is called when the user logs out.
		public void userLoggedOut() {
			showNotLogedInState();
		}

	};
}