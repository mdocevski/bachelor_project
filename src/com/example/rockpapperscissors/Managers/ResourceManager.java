package com.example.rockpapperscissors.Managers;

import java.io.IOException;

import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.Engine;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.font.StrokeFont;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.util.adt.color.Color;
import org.andengine.util.debug.Debug;

import com.example.rockpapperscissors.CONSTANTS;
import com.example.rockpapperscissors.RPS_activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;

public class ResourceManager extends Object {

	// ====================================================
	// CONSTANTS
	// ====================================================
	private static final ResourceManager INSTANCE = new ResourceManager();

	// ====================================================
	// VARIABLES
	// ====================================================
	// We include these objects in the resource manager for
	// easy accessibility across our project.
	public Engine engine;
	public Context context;
	public float cameraWidth;
	public float cameraHeight;
	public float cameraScaleFactorX;
	public float cameraScaleFactorY;

	private RPS_activity RPS_Activity;

	// The resource variables listed should be kept public, allowing us easy
	// access
	// to them when creating new Sprite and Text objects and to play sound
	// files.
	// ======================== Game Resources ================= //
	public static ITextureRegion gameBackgroundTextureRegion;

	// ======================== Menu Resources ================= //
	public static ITextureRegion menuBackgroundTextureRegion;

	// =================== Shared Game and Menu Resources ====== //
	public static ITiledTextureRegion buttonTiledTextureRegion;
	public static ITiledTextureRegion threeStateTiledTextureRegion;
	public static ITextureRegion friendlyKnightTextureRegion;
	public static ITextureRegion friendlyDuckTextureRegion;
	public static ITextureRegion friendlyWitchTextureRegion;
	public static ITextureRegion enemyKnightTextureRegion;
	public static ITextureRegion enemyDuckTextureRegion;
	public static ITextureRegion enemyWitchTextureRegion;
	public static ITextureRegion fieldTextureRegion;
	public static ITextureRegion availableFieldTextureRegion;
	public static ITextureRegion coinsTextureRegion;
	public static ITextureRegion oneCoinTextureRegion;
	public static ITextureRegion twoCoinsTextureRegion;
	public static ITextureRegion threeCoinsTextureRegion;
	public static ITextureRegion impassableTextureRegion;
	public static ITextureRegion healthBackgroundTextureRegion;
	public static ITextureRegion healthForegroundTextureRegion;
	public static ITextureRegion[] tutorialImages = new ITextureRegion[CONSTANTS.TUTORIAL_PIC_NUM];
	public static Sound clickSound;
	public static Font fontDefault32Bold;
	public static StrokeFont fontStronke36BlueBold;
	public static Font fontDefault108StrokeBold;
	public static Font fontDefault18;
	public static Font fontStronke36RedBold;

	// This variable will be used to revert the TextureFactory's default path
	// when we change it.
	private String mPreviousAssetBasePath = "";

	// ====================================================
	// CONSTRUCTOR
	// ====================================================
	private ResourceManager() {
	}

	// ====================================================
	// GETTERS & SETTERS
	// ====================================================
	// Retrieves a global instance of the ResourceManager
	public static ResourceManager getInstance() {
		return INSTANCE;
	}

	// ====================================================
	// PUBLIC METHODS
	// ====================================================
	// Setup the ResourceManager
	public void setup(final Engine pEngine, final Context pContext,
			final float pCameraWidth, final float pCameraHeight,
			final float pCameraScaleX, final float pCameraScaleY) {
		engine = pEngine;
		context = pContext;
		cameraWidth = pCameraWidth;
		cameraHeight = pCameraHeight;
		cameraScaleFactorX = pCameraScaleX;
		cameraScaleFactorY = pCameraScaleY;
	}

	// Loads all game resources.
	public static void loadGameResources() {
		getInstance().loadGameTextures();
		getInstance().loadSharedResources();
	}

	// Loads all menu resources
	public static void loadMenuResources() {
		getInstance().loadMenuTextures();
		getInstance().loadTutorialTextures();
		getInstance().loadSharedResources();
	}

	// Unloads all game resources.
	public static void unloadGameResources() {
		getInstance().unloadGameTextures();
	}

	// Unloads all menu resources
	public static void unloadMenuResources() {
		getInstance().unloadMenuTextures();
		getInstance().unloadTutorialTextures();
	}

	// Unloads all shared resources
	public static void unloadSharedResources() {
		getInstance().unloadSharedTextures();
		getInstance().unloadSounds();
		getInstance().unloadFonts();
	}

	// ====================================================
	// PRIVATE METHODS
	// ====================================================
	// Loads resources used by both the game scenes and menu scenes
	private void loadSharedResources() {
		loadSharedTextures();
		loadSounds();
		loadFonts();
	}

	// ============================ LOAD TEXTURES (GAME) ================= //
	private void loadGameTextures() {
		// Store the current asset base path to apply it after we've loaded our
		// textures
		mPreviousAssetBasePath = BitmapTextureAtlasTextureRegionFactory
				.getAssetBasePath();
		// Set our game assets folder to "assets/gfx/game/"
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/");

		gameBackgroundTextureRegion = loadTextureRegion(
				gameBackgroundTextureRegion, "background.png", 800, 480);

		friendlyKnightTextureRegion = loadTextureRegion(
				friendlyKnightTextureRegion, "friendly_knight.png",
				CONSTANTS.squareSize, CONSTANTS.squareSize);
		friendlyDuckTextureRegion = loadTextureRegion(
				friendlyDuckTextureRegion, "friendly_duck.png",
				CONSTANTS.squareSize, CONSTANTS.squareSize);
		friendlyWitchTextureRegion = loadTextureRegion(
				friendlyWitchTextureRegion, "friendly_witch.png",
				CONSTANTS.squareSize, CONSTANTS.squareSize);

		enemyKnightTextureRegion = loadTextureRegion(enemyKnightTextureRegion,
				"enemy_knight.png", CONSTANTS.squareSize, CONSTANTS.squareSize);
		enemyDuckTextureRegion = loadTextureRegion(enemyDuckTextureRegion,
				"enemy_duck.png", CONSTANTS.squareSize, CONSTANTS.squareSize);
		enemyWitchTextureRegion = loadTextureRegion(enemyWitchTextureRegion,
				"enemy_witch.png", CONSTANTS.squareSize, CONSTANTS.squareSize);

		fieldTextureRegion = loadTextureRegion(fieldTextureRegion, "field.png",
				CONSTANTS.squareSize, CONSTANTS.squareSize);
		availableFieldTextureRegion = loadTextureRegion(
				availableFieldTextureRegion, "available_field.png",
				CONSTANTS.squareSize, CONSTANTS.squareSize);

		coinsTextureRegion = loadTextureRegion(coinsTextureRegion, "coins.png",
				CONSTANTS.squareSize, CONSTANTS.squareSize);
		oneCoinTextureRegion = loadTextureRegion(oneCoinTextureRegion,
				"one_coin.png", CONSTANTS.squareSize, CONSTANTS.squareSize);
		twoCoinsTextureRegion = loadTextureRegion(twoCoinsTextureRegion,
				"two_coins.png", CONSTANTS.squareSize, CONSTANTS.squareSize);
		threeCoinsTextureRegion = loadTextureRegion(threeCoinsTextureRegion,
				"three_coins.png", CONSTANTS.squareSize, CONSTANTS.squareSize);
		impassableTextureRegion = loadTextureRegion(impassableTextureRegion,
				"impassable.png", CONSTANTS.squareSize, CONSTANTS.squareSize);
		healthBackgroundTextureRegion = loadTextureRegion(
				healthBackgroundTextureRegion, "health_background.png", 36, 8);
		healthForegroundTextureRegion = loadTextureRegion(
				healthForegroundTextureRegion, "health_foreground.png", 12, 8);

		// Revert the Asset Path.
		BitmapTextureAtlasTextureRegionFactory
				.setAssetBasePath(mPreviousAssetBasePath);
	}

	// ============================ UNLOAD TEXTURES (GAME) =============== //
	private void unloadGameTextures() {
		unloadTextureRegion(gameBackgroundTextureRegion);
		unloadTextureRegion(friendlyKnightTextureRegion);
		unloadTextureRegion(friendlyDuckTextureRegion);
		unloadTextureRegion(friendlyWitchTextureRegion);
		unloadTextureRegion(enemyKnightTextureRegion);
		unloadTextureRegion(enemyDuckTextureRegion);
		unloadTextureRegion(enemyWitchTextureRegion);
		unloadTextureRegion(fieldTextureRegion);
		unloadTextureRegion(availableFieldTextureRegion);
		unloadTextureRegion(coinsTextureRegion);
		unloadTextureRegion(oneCoinTextureRegion);
		unloadTextureRegion(twoCoinsTextureRegion);
		unloadTextureRegion(threeCoinsTextureRegion);
		unloadTextureRegion(impassableTextureRegion);
		unloadTextureRegion(healthBackgroundTextureRegion);
		unloadTextureRegion(healthForegroundTextureRegion);
	}

	// ============================ LOAD TEXTURES (MENU) ================= //
	private void loadMenuTextures() {
		// Store the current asset base path to apply it after we've loaded our
		// textures
		mPreviousAssetBasePath = BitmapTextureAtlasTextureRegionFactory
				.getAssetBasePath();
		// Set our menu assets folder to "assets/gfx/menu/"
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/");

		menuBackgroundTextureRegion = loadTextureRegion(
				menuBackgroundTextureRegion, "starfield.png", 800, 480);

		// Revert the Asset Path.
		BitmapTextureAtlasTextureRegionFactory
				.setAssetBasePath(mPreviousAssetBasePath);
	}

	// ============================ UNLOAD TEXTURES (MENU) =============== //
	private void unloadMenuTextures() {
		// background texture:
		unloadTextureRegion(menuBackgroundTextureRegion);
	}

	// ============================ LOAD TEXTURES (SHARED) ================= //
	private void loadSharedTextures() {
		// Store the current asset base path to apply it after we've loaded our
		// textures
		mPreviousAssetBasePath = BitmapTextureAtlasTextureRegionFactory
				.getAssetBasePath();
		// Set our shared assets folder to "assets/gfx/"
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		// button texture:

		buttonTiledTextureRegion = loadTiledTextureRegion(
				buttonTiledTextureRegion, "button01.png", 522, 74, 2, 1);

		threeStateTiledTextureRegion = loadTiledTextureRegion(
				threeStateTiledTextureRegion, "button02.png", 800, 74, 3, 1);

		// Revert the Asset Path.
		BitmapTextureAtlasTextureRegionFactory
				.setAssetBasePath(mPreviousAssetBasePath);
	}

	// ============================ UNLOAD TEXTURES (SHARED) ============= //
	private void unloadSharedTextures() {
		// button texture:
		unloadTextureRegion(buttonTiledTextureRegion);

	}

	// LOAD AND UNLOAD TUTORIAL TEXTURES

	private void loadTutorialTextures() {
		// Store the current asset base path to apply it after we've loaded our
		// textures
		mPreviousAssetBasePath = BitmapTextureAtlasTextureRegionFactory
				.getAssetBasePath();
		// Set our shared assets folder to "assets/gfx/"
		BitmapTextureAtlasTextureRegionFactory
				.setAssetBasePath("gfx/menu/tutorial/");

		for (int i = 0; i < CONSTANTS.TUTORIAL_PIC_NUM; i++) {
			tutorialImages[i] = loadTextureRegion(tutorialImages[i],
					String.valueOf(i + 1)+".jpg", 400, 240);
		}

		// Revert the Asset Path.
		BitmapTextureAtlasTextureRegionFactory
				.setAssetBasePath(mPreviousAssetBasePath);
	}

	private void unloadTutorialTextures() {
		for (int i = 0; i < CONSTANTS.TUTORIAL_PIC_NUM; i++) {
			unloadTextureRegion(tutorialImages[i]);
		}
	}

	// =========================== LOAD SOUNDS ======================== //
	private void loadSounds() {
		SoundFactory.setAssetBasePath("sounds/");
		if (clickSound == null) {
			try {
				// Create the clickSound object via the SoundFactory class
				clickSound = SoundFactory.createSoundFromAsset(
						engine.getSoundManager(), context, "click.mp3");
			} catch (final IOException e) {
				Log.v("Sounds Load", "Exception:" + e.getMessage());
			}
		}
	}

	// =========================== UNLOAD SOUNDS ====================== //
	private void unloadSounds() {
		if (clickSound != null)
			if (clickSound.isLoaded()) {
				// Unload the clickSound object. Make sure to stop it first.
				clickSound.stop();
				engine.getSoundManager().remove(clickSound);
				clickSound = null;
			}
	}

	// ============================ LOAD FONTS ========================== //
	private void loadFonts() {
		// Create the Font objects via FontFactory class
		if (fontDefault32Bold == null) {
			fontDefault32Bold = FontFactory.create(engine.getFontManager(),
					engine.getTextureManager(), 256, 256,
					Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32f,
					true, Color.WHITE_ARGB_PACKED_INT);
			fontDefault32Bold.load();
		}
		if (fontDefault108StrokeBold == null) {
			fontDefault108StrokeBold = FontFactory.createStroke(
					engine.getFontManager(), engine.getTextureManager(), 512,
					512, Typeface.create(Typeface.DEFAULT, Typeface.BOLD),
					108f, true, Color.WHITE_ARGB_PACKED_INT, 3, 0xFFFF8E00);
			fontDefault108StrokeBold.load();
		}
		if (fontStronke36BlueBold == null) {
			fontStronke36BlueBold = FontFactory.createStroke(
					engine.getFontManager(), engine.getTextureManager(), 512,
					512, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 36f,
					true, android.graphics.Color.WHITE, 2,
					android.graphics.Color.BLUE);
			fontStronke36BlueBold.load();
		}

		if (fontStronke36RedBold == null) {
			fontStronke36RedBold = FontFactory.createStroke(
					engine.getFontManager(), engine.getTextureManager(), 512,
					512, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 36f,
					true, android.graphics.Color.WHITE, 2,
					android.graphics.Color.RED);
			fontStronke36RedBold.load();
		}
		if (fontDefault18 == null) {
			fontDefault18 = FontFactory.create(engine.getFontManager(),
					engine.getTextureManager(), 512, 512,
					Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 18f,
					true, Color.WHITE_ARGB_PACKED_INT);
			fontDefault18.load();
		}
	}

	// ============================ UNLOAD FONTS ======================== //
	private void unloadFonts() {
		// Unload the fonts
		if (fontDefault32Bold != null) {
			fontDefault32Bold.unload();
			fontDefault32Bold = null;
		}
		if (fontDefault108StrokeBold != null) {
			fontDefault108StrokeBold.unload();
			fontDefault108StrokeBold = null;
		}
		if (fontStronke36BlueBold != null) {
			fontStronke36BlueBold.unload();
			fontStronke36BlueBold = null;
		}
		if (fontStronke36RedBold != null) {
			fontStronke36RedBold.unload();
			fontStronke36RedBold = null;
		}
		if (fontDefault18 != null) {
			fontDefault18.unload();
			fontDefault18 = null;
		}

	}

	@SuppressWarnings("unused")
	private ITextureRegion loadTextureRegion(ITextureRegion pTextureRegion,
			final String pPath, final int pAtlasWidth, final int pAtlasHeight,
			final int pBorderSpacing, final int pSourceSpacing,
			final int pSourcePadding) {
		if (pTextureRegion == null) {
			BuildableBitmapTextureAtlas texture = new BuildableBitmapTextureAtlas(
					engine.getTextureManager(), pAtlasWidth, pAtlasHeight);
			pTextureRegion = BitmapTextureAtlasTextureRegionFactory
					.createFromAsset(texture, context, pPath);
			try {
				texture.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
						pBorderSpacing, pSourceSpacing, pSourcePadding));
				texture.load();
			} catch (TextureAtlasBuilderException e) {
				Debug.e(e);
			}
		}
		return pTextureRegion;
	}

	private ITextureRegion loadTextureRegion(ITextureRegion pTextureRegion,
			final String pPath, final int pAtlasWidth, final int pAtlasHeight) {
		if (pTextureRegion == null) {
			BuildableBitmapTextureAtlas texture = new BuildableBitmapTextureAtlas(
					engine.getTextureManager(), pAtlasWidth, pAtlasHeight);
			pTextureRegion = BitmapTextureAtlasTextureRegionFactory
					.createFromAsset(texture, context, pPath);
			try {
				texture.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
						0, 0, 0));
				texture.load();
			} catch (TextureAtlasBuilderException e) {
				Debug.e(e);
			}
		}
		return pTextureRegion;
	}

	private ITiledTextureRegion loadTiledTextureRegion(
			ITiledTextureRegion pTiledTextureRegion, final String pPath,
			final int pWidth, final int pHeight, final int pTileColumns,
			final int pTileRows) {
		if (pTiledTextureRegion == null) {
			BuildableBitmapTextureAtlas texture = new BuildableBitmapTextureAtlas(
					engine.getTextureManager(), pWidth, pHeight);
			pTiledTextureRegion = BitmapTextureAtlasTextureRegionFactory
					.createTiledFromAsset(texture, context, pPath,
							pTileColumns, pTileRows);
			try {
				texture.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
						0, 1, 4));
				texture.load();
			} catch (TextureAtlasBuilderException e) {
				Debug.e(e);
			}
		}
		return pTiledTextureRegion;
	}

	@SuppressWarnings("unused")
	private ITiledTextureRegion loadTiledTextureRegion(
			ITiledTextureRegion pTiledTextureRegion, final String pPath,
			final int pWidth, final int pHeight, final int pTileColumns,
			final int pTileRows, final int pBorderSpacing,
			final int pSourceSpacing, final int pSourcePadding) {
		if (pTiledTextureRegion == null) {
			BuildableBitmapTextureAtlas texture = new BuildableBitmapTextureAtlas(
					engine.getTextureManager(), pWidth, pHeight);
			pTiledTextureRegion = BitmapTextureAtlasTextureRegionFactory
					.createTiledFromAsset(texture, context, pPath,
							pTileColumns, pTileRows);
			try {
				texture.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
						pBorderSpacing, pSourceSpacing, pSourcePadding));
				texture.load();
			} catch (TextureAtlasBuilderException e) {
				Debug.e(e);
			}
		}
		return pTiledTextureRegion;
	}

	private void unloadTextureRegion(ITextureRegion pTextureRegion) {
		if (pTextureRegion != null) {
			if (pTextureRegion.getTexture().isLoadedToHardware()) {
				pTextureRegion.getTexture().unload();
				pTextureRegion = null;
			}
		}
	}

	public void setRPSActivity(RPS_activity rps_activity) {
		this.RPS_Activity = rps_activity;
	}

	public Activity getRPSActivity() {
		return RPS_Activity;
	}
}