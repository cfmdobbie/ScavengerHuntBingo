package com.maycontainsoftware.scavengerhuntbingo;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class MyGame extends Game {

	/** Tag for debug logging. */
	// private static final String TAG = MyGame.class.getName();

	/** Name of preferences file for state persistence. */
	private static final String PREFERENCES_NAME = "com.maycontainsoftware.scavengerhuntbingo";

	// Preferences keys
	static final String PREF_GAME_IN_PROGRESS = "game_in_progress";
	static final String PREF_SUBJECT = "subject";
	static final String PREF_BOARD_SIZE = "board_size";

	/** Debug flag for bounds-rendering etc. */
	public static final boolean DEBUG = false;

	// Drawing Engines
	public SpriteBatch batch;
	public ShapeRenderer shapeRenderer;

	// Fonts
	public BitmapFont font32;
	public BitmapFont font38;
	public BitmapFont font42;
	public BitmapFont font48;
	public BitmapFont font64;

	// Data persistence
	public Preferences prefs;

	// Background effect
	BackgroundEffect backgroundEffect;

	private AssetManager manager;

	@Override
	public void create() {
		// Gdx.app.log(TAG, "create()");

		// Create drawing engines
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();

		// Create asset manager
		manager = new AssetManager();

		// Start loading fonts
		final int[] fontSizes = new int[] { 32, 38, 42, 48, 64 };
		for (final int fontSize : fontSizes) {
			manager.load("impact" + fontSize + ".fnt", BitmapFont.class);
		}

		// Finish loading all assets in manager
		manager.finishLoading();

		// Save references to bitmap fonts
		font32 = manager.get("impact32.fnt", BitmapFont.class);
		font38 = manager.get("impact38.fnt", BitmapFont.class);
		font42 = manager.get("impact42.fnt", BitmapFont.class);
		font48 = manager.get("impact48.fnt", BitmapFont.class);
		font64 = manager.get("impact64.fnt", BitmapFont.class);

		// Data persistence
		prefs = Gdx.app.getPreferences(PREFERENCES_NAME);

		// Back button support
		Gdx.input.setCatchBackKey(true);

		// Pretty background effect
		backgroundEffect = new BackgroundEffect();

		// Load the initial screen
		boolean gameInProgress = prefs.getBoolean(PREF_GAME_IN_PROGRESS, false);
		if (gameInProgress) {
			// Jump straight to game screen
			this.setScreen(new GameScreen(this));
		} else {
			// Start on the Main Menu Screen
			this.setScreen(new MenuScreen(this));
		}
	}

	@Override
	public void dispose() {
		// Gdx.app.log(TAG, "dispose()");

		// Dispose of graphical engines
		batch.dispose();
		shapeRenderer.dispose();

		// Dispose of fonts
		font32.dispose();
		font38.dispose();
		font42.dispose();
		font48.dispose();
		font64.dispose();

		super.dispose();
	}

	@Override
	public void resize(int width, int height) {
		// Gdx.app.log(TAG, "resize(): width = " + width + ", height = " + height);
		super.resize(width, height);
		backgroundEffect.resize(width, height);
	}

	@Override
	public void pause() {
		// Gdx.app.log(TAG, "pause()");
		super.pause();
	}

	@Override
	public void resume() {
		// Gdx.app.log(TAG, "resume()");
		super.resume();
	}
}
