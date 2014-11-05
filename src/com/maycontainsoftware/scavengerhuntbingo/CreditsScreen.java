package com.maycontainsoftware.scavengerhuntbingo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont.HAlignment;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class CreditsScreen extends MyScreen {

	// private static final String TAG = CreditsScreen.class.getName();

	// Useful colours
	private final Color enabled = new Color(0.92f, 0.70f, 0.33f, 1.0f);
	private final Color creditsColor = Color.WHITE;

	// Positions of text elements
	private final Rectangle backRect = new Rectangle();
	private final Rectangle creditsRect = new Rectangle();

	// Constants relating to menu options
	private static final String backText = "Back to Main Menu";
	final String creditsText = "" + "Copyright 2014,\n" + "May Contain Software.\n" + "\n" + "Built using libGDX.\n"
			+ "\n" + "UK Road Signs used under licence from UK Govt, " + "US Road Signs public domain via US DOT, "
			+ "letters/shapes made by May Contain Software, "
			+ "all other graphics public domain via OpenClipart.org.\n" + "";

	public CreditsScreen(MyGame game) {
		super(game, 500, 700);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {

		// Calculate size of back button
		TextBounds backBounds = game.font42.getBounds(backText);
		backRect.setSize(backBounds.width, backBounds.height);

		// Background is never fabulous when this screen resumes
		game.backgroundEffect.fabulous = false;
	}

	@Override
	protected void doResize(int displayWidth, int displayHeight) {

		// Set position all menu items
		final int screenPadding = 20;
		final float minx = gameArea.gameRect.x + screenPadding;
		final float maxx = gameArea.gameRect.x + gameArea.gameRect.width - screenPadding;

		backRect.setPosition(gameArea.gameRect.x + gameArea.gameRect.width / 2 - backRect.width / 2,
				gameArea.gameRect.y + screenPadding);

		creditsRect.setSize(maxx - minx, gameArea.gameRect.height - 3 * screenPadding - backRect.height);
		creditsRect.setPosition(minx, backRect.y + backRect.height + screenPadding);
	}

	@Override
	public void render(float delta) {

		// User input

		// Process any touch input
		if (Gdx.input.justTouched()) {
			// Get touch location
			Vector3 pos = new Vector3();
			pos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(pos);

			// Determine which menu option was touched
			if (backRect.contains(pos.x, pos.y)) {
				// Gdx.app.log(TAG, "Back!");
				game.setScreen(new MenuScreen(game));
				dispose();
				return;
			} else if (creditsRect.contains(pos.x, pos.y)) {
				// Don't know if I want this functionality or not! Removed for now
				// Gdx.app.log(TAG, "Toggle Fabulous!");
				// game.backgroundEffect.fabulous = !game.backgroundEffect.fabulous;
			}
		} else if (Gdx.input.isKeyPressed(Keys.BACK)) {
			// Gdx.app.log(TAG, "Back!");
			game.setScreen(new MenuScreen(game));
			dispose();
			return;
		}

		// Update game model

		game.backgroundEffect.update(delta);

		// Render screen

		// Clear screen
		game.backgroundEffect.clearColorBuffer();
		// Update camera
		camera.update();
		// Render background
		game.backgroundEffect.render(game);

		game.batch.setProjectionMatrix(camera.combined);

		// Start sprite render batch
		game.batch.begin();

		game.font42.setColor(enabled);
		game.font42.draw(game.batch, backText, backRect.x, backRect.y + backRect.height);

		game.font38.setColor(creditsColor);
		game.font38.drawWrapped(game.batch, creditsText, creditsRect.x, creditsRect.y + creditsRect.height,
				creditsRect.width, HAlignment.CENTER);

		// End sprite render batch
		game.batch.end();

		// If debug enabled, draw outlines of all important screen elements
		if (MyGame.DEBUG) {
			// Update ShapeRenderer's projection matrix
			game.shapeRenderer.setProjectionMatrix(camera.combined);

			// Start drawing lines
			game.shapeRenderer.begin(ShapeType.Line);

			game.shapeRenderer.setColor(Color.RED);

			// Outline game area
			Util.drawRect(game.shapeRenderer, gameArea.gameRect);
			// Outline text elements
			Util.drawRect(game.shapeRenderer, backRect);
			Util.drawRect(game.shapeRenderer, creditsRect);

			// End drawing lines
			game.shapeRenderer.end();
		}
	}
}
