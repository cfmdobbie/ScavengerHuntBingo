package com.maycontainsoftware.scavengerhuntbingo;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;

public abstract class MyScreen implements Screen {

	// Last seen display dimensions
	private int displayWidth = -1;
	private int displayHeight = -1;

	// The active game area
	protected GameArea gameArea;

	// Orthographic projection camera
	protected OrthographicCamera camera;

	// The Game object
	protected MyGame game;

	public MyScreen(MyGame game, int gameWidth, int gameHeight) {

		// Store reference to game object
		this.game = game;

		// Set up camera
		camera = new OrthographicCamera();

		// Set up game area
		gameArea = new GameArea(gameWidth, gameHeight);
	}

	/**
	 * The resize() method is called very frequently. We store the last-used display width and height so unnecessary calls can be ignored.
	 */
	@Override
	public void resize(int width, int height) {
		if (width != this.displayWidth || height != this.displayHeight) {

			// Remember new display dimensions
			this.displayWidth = width;
			this.displayHeight = height;

			// Update game area
			gameArea.resize(width, height);

			// Update camera projection
			camera.setToOrtho(false, gameArea.sceneRect.width, gameArea.sceneRect.height);

			// Run subclass resize logic
			doResize(width, height);
		}
	}

	/** Method subclasses need to implement to contain resize logic. */
	protected abstract void doResize(int displayWidth, int displayHeight);

	/**
	 * When a Screen is activated via Game.setScreen(), show() is called. However, every other time a Screen is made visible, resume() is
	 * called. We assume there's nothing we want to do on *first* display that we wouldn't also want to do on subsequent displays, so the
	 * show() method simply calls resume().
	 */
	@Override
	public final void show() {
		resume();
	}

	@Override
	public void hide() {
		pause();
	}
	
	@Override
	public void dispose() {
		// No-op
	}
}
