package com.maycontainsoftware.scavengerhuntbingo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont.HAlignment;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class HelpScreen extends MyScreen {

	// private static final String TAG = HelpScreen.class.getName();

	// Useful colours
	private final Color enabled = new Color(0.92f, 0.70f, 0.33f, 1.0f);
	private final Color creditsColor = Color.WHITE;

	// Positions of text elements
	private final Rectangle backRect = new Rectangle();
	private final Rectangle helpRect = new Rectangle();

	// Constants relating to menu options
	private static final String backText = "Back to Main Menu";
	final String helpText = "" + "Tap a square to clear it, clear a line in any direction to win!\n" + "\n"
			+ "Different cards are played in different situations - "
			+ "some are played on the street, walking in the woods, or while on a road trip, "
			+ "while the Shapes and Letters cards require more abstract thinking: "
			+ "Which items match the shapes? What letters do things start with?\n" + "\n"
			+ "Or make up your own rules!  Name fruit you've eaten, or countries that start with each letter!\n" + "";

	public HelpScreen(MyGame game) {
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

		helpRect.setSize(maxx - minx, gameArea.gameRect.height - 3 * screenPadding - backRect.height);
		helpRect.setPosition(minx, backRect.y + backRect.height + screenPadding);
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

		game.font32.setColor(creditsColor);
		game.font32.drawWrapped(game.batch, helpText, helpRect.x, helpRect.y + helpRect.height, helpRect.width,
				HAlignment.CENTER);

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
			Util.drawRect(game.shapeRenderer, helpRect);

			// End drawing lines
			game.shapeRenderer.end();
		}
	}
}
