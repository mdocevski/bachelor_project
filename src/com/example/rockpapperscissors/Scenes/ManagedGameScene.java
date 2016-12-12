package com.example.rockpapperscissors.Scenes;

import org.andengine.engine.camera.SmoothCamera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.PinchZoomDetector;
import org.andengine.input.touch.detector.PinchZoomDetector.IPinchZoomDetectorListener;

import com.example.rockpapperscissors.Managers.ResourceManager;
import com.example.rockpapperscissors.Managers.SceneManager;

public abstract class ManagedGameScene extends ManagedScene implements
		IOnSceneTouchListener, IPinchZoomDetectorListener {
	// It is a good idea to place limits on zoom functionality
	private static final float MIN_ZOOM_FACTOR = 1.0f;
	private static final float MAX_ZOOM_FACTOR = 1.5f;
	protected ButtonSprite MainMenuButton ;
	protected ButtonSprite NewUnitButton;
	// Initial scene touch coordinates on ACTION_DOWN
	private float mInitialTouchX;
	private float mInitialTouchY;

	// This object will handle the zooming pending touch
	private PinchZoomDetector mPinchZoomDetector;

	private float mInitialTouchZoomFactor;

	// Create an easy to manage HUD that we can attach/detach when the game
	// scene is shown or hidden.
	public HUD GameHud = new HUD();
	public ManagedGameScene thisManagedGameScene = this;

	public ManagedGameScene() {
		// Let the Scene Manager know that we want to show a Loading Scene for
		// at least 2 seconds.
		this(2f);
	};

	public ManagedGameScene(float pLoadingScreenMinimumSecondsShown) {
		super(pLoadingScreenMinimumSecondsShown);
		// Setup the touch attributes for the Game Scenes.
		this.setOnSceneTouchListenerBindingOnActionDownEnabled(true);
		this.setTouchAreaBindingOnActionDownEnabled(true);
		this.setTouchAreaBindingOnActionMoveEnabled(true);
		// Scale the Game Scenes according to the Camera's scale factor.
		this.setScale(ResourceManager.getInstance().cameraScaleFactorX,
				ResourceManager.getInstance().cameraScaleFactorY);
		this.setPosition(ResourceManager.getInstance().cameraWidth / 2f,
				ResourceManager.getInstance().cameraHeight / 2f);
		this.setPosition(0f, 0f);
		GameHud.setScaleCenter(0f, 0f);
		GameHud.setScale(ResourceManager.getInstance().cameraScaleFactorX,
				ResourceManager.getInstance().cameraScaleFactorY);
	}

	// These objects will make up our loading scene.
	private Text LoadingText;
	private Scene LoadingScene;

	@Override
	public Scene onLoadingScreenLoadAndShown() {
		// Setup and return the loading screen.
		LoadingScene = new Scene();
		LoadingScene.setBackgroundEnabled(true);
		LoadingText = new Text(0, 0, ResourceManager.fontDefault32Bold,
				"Loading...",
				ResourceManager.getInstance().engine
						.getVertexBufferObjectManager());
		LoadingText.setPosition(
				LoadingText.getWidth() / 2f,
				ResourceManager.getInstance().cameraHeight
						- LoadingText.getHeight() / 2f);
		LoadingScene.attachChild(LoadingText);
		return LoadingScene;
	}

	@Override
	public void onLoadingScreenUnloadAndHidden() {
		// detach the loading screen resources.
		LoadingText.detachSelf();
		LoadingText = null;
		LoadingScene = null;
	}

	@Override
	public void onLoadScene() {
		// Load the resources to be used in the Game Scenes.
		ResourceManager.loadGameResources();
		setOnSceneTouchListener(this);
		setOnSceneTouchListenerBindingOnActionDownEnabled(true);

		/*
		 * Create and set the zoom detector to listen for touch events using
		 * this activity's listener
		 */
		mPinchZoomDetector = new PinchZoomDetector(this);

		// Enable the zoom detector
		mPinchZoomDetector.setEnabled(true);

		// Create a Sprite to use as the background.

		this.attachChild(new Sprite(
				ResourceManager.getInstance().cameraWidth / 2f, ResourceManager
						.getInstance().cameraHeight / 2f,
				ResourceManager.gameBackgroundTextureRegion, ResourceManager
						.getInstance().engine.getVertexBufferObjectManager()));
		this.getLastChild().setScale(
				ResourceManager.getInstance().cameraWidth / 800f,
				ResourceManager.getInstance().cameraHeight / 480f);

		// Setup the HUD Buttons and Button Texts.
		// Take note of what happens when the buttons are clicked.
		MainMenuButton = new ButtonSprite(0f, 0f,
				ResourceManager.buttonTiledTextureRegion.getTextureRegion(0),
				ResourceManager.buttonTiledTextureRegion.getTextureRegion(1),
				ResourceManager.getInstance().engine
						.getVertexBufferObjectManager());
		MainMenuButton.setWidth(0.8f * MainMenuButton.getWidth());
		MainMenuButton.setScale(
				1 / ResourceManager.getInstance().cameraScaleFactorX,
				1 / ResourceManager.getInstance().cameraScaleFactorY);
		MainMenuButton.setPosition(
				(MainMenuButton.getWidth() * MainMenuButton.getScaleX()) / 2f,
				(MainMenuButton.getHeight() * MainMenuButton.getScaleY()) / 2f);
		MainMenuButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(ButtonSprite pButtonSprite,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				// Play the click sound and show the Main Menu.
				ResourceManager.clickSound.play();
				SceneManager.getInstance().showEndGameLayer(true);
			}
		});

		Text MainMenuButtonText = new Text(MainMenuButton.getWidth() / 2,
				MainMenuButton.getHeight() / 2,
				ResourceManager.fontDefault32Bold, "MENU",
				ResourceManager.getInstance().engine
						.getVertexBufferObjectManager());
		MainMenuButton.attachChild(MainMenuButtonText);
		GameHud.attachChild(MainMenuButton);
		GameHud.registerTouchArea(MainMenuButton);

		NewUnitButton = new ButtonSprite(0f, 0f,
				ResourceManager.threeStateTiledTextureRegion
						.getTextureRegion(0),
				ResourceManager.threeStateTiledTextureRegion
						.getTextureRegion(1),
				ResourceManager.threeStateTiledTextureRegion
						.getTextureRegion(2),
				ResourceManager.getInstance().engine
						.getVertexBufferObjectManager());
		NewUnitButton.setWidth((float) (NewUnitButton.getWidth()*0.7));
		NewUnitButton.setScale(
				1 / ResourceManager.getInstance().cameraScaleFactorX,
				1 / ResourceManager.getInstance().cameraScaleFactorY);
		NewUnitButton
				.setPosition(
						800f - ((NewUnitButton.getWidth() * NewUnitButton
								.getScaleX()) / 2f),
						(NewUnitButton.getHeight() * NewUnitButton.getScaleY()) / 2f);
		NewUnitButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(ButtonSprite pButtonSprite,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				// Play the click sound and show the Options Layer.
				ResourceManager.clickSound.play();
				SceneManager.getInstance().showNewUnitLayer(true);
			}
		});

		Text NewUnitButtonText = new Text(0, 0,
				ResourceManager.fontDefault32Bold, "BUY",
				ResourceManager.getInstance().engine
						.getVertexBufferObjectManager());
		NewUnitButtonText.setPosition((NewUnitButton.getWidth()) / 2,
				(NewUnitButton.getHeight()) / 2);
		NewUnitButton.attachChild(NewUnitButtonText);
		GameHud.attachChild(NewUnitButton);
		GameHud.registerTouchArea(NewUnitButton);
	}

	@Override
	public void onShowScene() {
		// We want to wait to set the HUD until the scene is shown because
		// otherwise it will appear on top of the loading screen.
		ResourceManager.getInstance().engine.getCamera().setHUD(GameHud);
	}

	@Override
	public void onHideScene() {
		ResourceManager.getInstance().engine.getCamera().setHUD(null);
	}

	@Override
	public void onUnloadScene() {
		((SmoothCamera) ResourceManager.getInstance().engine.getCamera())
				.setZoomFactor(1.0f);
		ResourceManager.getInstance().engine.getCamera().setCenter(
				ResourceManager.getInstance().cameraWidth / 2,
				ResourceManager.getInstance().cameraHeight / 2);
		// detach and unload the scene.
		ResourceManager.getInstance().engine.runOnUpdateThread(new Runnable() {
			@Override
			public void run() {
				thisManagedGameScene.detachChildren();
				thisManagedGameScene.clearEntityModifiers();
				thisManagedGameScene.clearTouchAreas();
				thisManagedGameScene.clearUpdateHandlers();
			}
		});
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		// Pass scene touch events to the pinch zoom detector
		mPinchZoomDetector.onTouchEvent(pSceneTouchEvent);
		if (pSceneTouchEvent.isActionDown()) {
			// Obtain the initial touch coordinates when the scene is first
			// pressed
			mInitialTouchX = pSceneTouchEvent.getX();
			mInitialTouchY = pSceneTouchEvent.getY();
		}

		if (pSceneTouchEvent.isActionMove()) {
			// Calculate the offset touch coordinates
			final float touchOffsetX = mInitialTouchX - pSceneTouchEvent.getX();
			final float touchOffsetY = mInitialTouchY - pSceneTouchEvent.getY();

			// Apply the offset touch coordinates to the current camera
			// coordinates
			ResourceManager.getInstance().engine.getCamera().setCenter(
					ResourceManager.getInstance().engine.getCamera()
							.getCenterX() + touchOffsetX,
					ResourceManager.getInstance().engine.getCamera()
							.getCenterY() + touchOffsetY);
		}

		return true;
	}

	/*
	 * This method is fired when two fingers press down on the display
	 */
	@Override
	public void onPinchZoomStarted(PinchZoomDetector pPinchZoomDetector,
			TouchEvent pSceneTouchEvent) {
		// On first detection of pinch zooming, obtain the initial zoom factor
		mInitialTouchZoomFactor = ((SmoothCamera) ResourceManager.getInstance().engine
				.getCamera()).getZoomFactor();
	}

	/*
	 * This method is fired when two fingers are being moved around on the
	 * display, ie. in a pinching motion
	 */
	@Override
	public void onPinchZoom(PinchZoomDetector pPinchZoomDetector,
			TouchEvent pTouchEvent, float pZoomFactor) {

		/*
		 * On every sub-sequent touch event (after the initial touch) we offset
		 * the initial camera zoom factor by the zoom factor calculated by
		 * pinch-zooming
		 */
		final float newZoomFactor = mInitialTouchZoomFactor * pZoomFactor;

		// If the camera is within zooming bounds
		if (newZoomFactor < MAX_ZOOM_FACTOR && newZoomFactor > MIN_ZOOM_FACTOR) {
			// Set the new zoom factor
			((SmoothCamera) ResourceManager.getInstance().engine.getCamera())
					.setZoomFactor(newZoomFactor);
		}
	}

	/* This method is fired when fingers are lifted from the screen */
	@Override
	public void onPinchZoomFinished(PinchZoomDetector pPinchZoomDetector,
			TouchEvent pTouchEvent, float pZoomFactor) {

		// Set the zoom factor one last time upon ending the pinch-to-zoom
		// functionality
		final float newZoomFactor = mInitialTouchZoomFactor * pZoomFactor;

		// If the camera is within zooming bounds
		if (newZoomFactor < MAX_ZOOM_FACTOR && newZoomFactor > MIN_ZOOM_FACTOR) {
			// Set the new zoom factor
			((SmoothCamera) ResourceManager.getInstance().engine.getCamera())
					.setZoomFactor(newZoomFactor);
		}
	}
}