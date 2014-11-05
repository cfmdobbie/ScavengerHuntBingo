package com.maycontainsoftware.scavengerhuntbingo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont.HAlignment;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class MenuScreen extends MyScreen {

	// private static final String TAG = MenuScreen.class.getName();

	private final Color enabled = new Color(0.92f, 0.70f, 0.33f, 1.0f);
	private final Color disabled = new Color(0.92f, 0.70f, 0.33f, 0.25f);

	// Positions of menu options
	private final Rectangle titleRect = new Rectangle();
	private final Rectangle newGameRect = new Rectangle();
	private final Rectangle continueRect = new Rectangle();
	private final Rectangle subjectRect = new Rectangle();
	private final Rectangle sizeRect = new Rectangle();
	private final Rectangle helpRect = new Rectangle();
	private final Rectangle creditsRect = new Rectangle();

	// Constants relating to menu options
	private static final String titleText = "Scavenger Hunt\nBingo!";
	private static final String newGameText = "New Game";
	private static final String continueText = "Continue";
	private static final String subjectText = "Card: ";
	private static final String sizeText = "Size: ";
	private static final String helpText = "Help";
	private static final String creditsText = "Credits";
	private static final String[] SUBJECTS = new String[] { "UK Town", "Nature", "UK Road Signs", "US Road Signs",
			"Fruit and Veg", "Letters", "Shapes" };
	private static final String[] SIZES = new String[] { "Small", "Medium", "Large" };

	// Variables relating to menu options
	private boolean continueEnabled;
	private int subject;
	private int size;

	private static final int UI_PADDING = 20;
	private static final int UI_PADDING_BIG = UI_PADDING * 4;

	private static final String PREF_SIZE_INDEX = "mm_size_index";
	private static final String PREF_SUBJECT_INDEX = "mm_subject_index";

	public MenuScreen(MyGame game) {
		super(game, 500, 700);
		// Gdx.app.log(TAG, "<init>()");
	}

	@Override
	public void pause() {
		// Gdx.app.log(TAG, "pause()");

		// Save preferences
		game.prefs.putInteger(PREF_SUBJECT_INDEX, subject);
		game.prefs.putInteger(PREF_SIZE_INDEX, size);
		game.prefs.flush();
	}

	@Override
	public void resume() {
		// Gdx.app.log(TAG, "resume()");

		// Restore preferences
		subject = game.prefs.getInteger(PREF_SUBJECT_INDEX, 0);
		size = game.prefs.getInteger(PREF_SIZE_INDEX, SIZES.length - 1);

		// Set sizes of all menu items
		TextBounds titleBounds = game.font64.getMultiLineBounds(titleText);
		titleRect.setSize(titleBounds.width, titleBounds.height);
		TextBounds newGameBounds = game.font48.getBounds(newGameText);
		newGameRect.setSize(newGameBounds.width, newGameBounds.height);
		TextBounds continueBounds = game.font48.getBounds(continueText);
		continueRect.setSize(continueBounds.width, continueBounds.height);
		TextBounds subjectBounds = game.font48.getBounds(subjectText + SUBJECTS[subject]);
		subjectRect.setSize(subjectBounds.width, subjectBounds.height);
		TextBounds sizeBounds = game.font48.getBounds(sizeText + SIZES[size]);
		sizeRect.setSize(sizeBounds.width, sizeBounds.height);
		TextBounds helpBounds = game.font32.getBounds(helpText);
		helpRect.setSize(helpBounds.width, helpBounds.height);
		TextBounds creditsBounds = game.font32.getBounds(creditsText);
		creditsRect.setSize(creditsBounds.width, creditsBounds.height);

		// Whether Continue button should be enabled
		continueEnabled = game.prefs.getBoolean(MyGame.PREF_GAME_IN_PROGRESS, false);

		// Background is never fabulous here
		game.backgroundEffect.fabulous = false;
	}

	@Override
	protected void doResize(int displayWidth, int displayHeight) {
		// Gdx.app.log(TAG, "doResize()");

		// Set position all UI elements

		// final float minx = gameArea.gameRect.x;
		final float midx = gameArea.gameRect.x + (gameArea.gameRect.width / 2);
		final float gameAreaTop = gameArea.gameRect.y + gameArea.gameRect.height;
		final float gameAreaBottom = gameArea.gameRect.y;
		final float gameAreaLeft = gameArea.gameRect.x;
		final float gameAreaRight = gameArea.gameRect.x + gameArea.gameRect.width;

		// Title text appears at the top
		titleRect.setPosition(midx - titleRect.width / 2, gameAreaTop - UI_PADDING - titleRect.height);

		subjectRect.setPosition(midx - subjectRect.width / 2, titleRect.y - UI_PADDING_BIG - subjectRect.height);
		sizeRect.setPosition(midx - sizeRect.width / 2, subjectRect.y - UI_PADDING - sizeRect.height);
		newGameRect.setPosition(midx - newGameRect.width / 2, sizeRect.y - UI_PADDING_BIG - newGameRect.height);
		continueRect.setPosition(midx - continueRect.width / 2, newGameRect.y - UI_PADDING - continueRect.height);
		creditsRect.setPosition(gameAreaLeft + UI_PADDING, gameAreaBottom + UI_PADDING);
		helpRect.setPosition(gameAreaRight - helpRect.width - UI_PADDING, gameAreaBottom + UI_PADDING);
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
			if (newGameRect.contains(pos.x, pos.y)) {
				// Gdx.app.log(TAG, "New Game!");
				// Store parameters for new game in prefs
				// Subject - a String that indicates the subject/tileset
				game.prefs.putString(MyGame.PREF_SUBJECT, SUBJECTS[subject]);
				// Board size - size runs from 3x3 to 5x5, with a zero-based index
				int boardSize = size + 3;
				game.prefs.putInteger(MyGame.PREF_BOARD_SIZE, boardSize);
				// Reset game in-progress flag
				game.prefs.putBoolean(MyGame.PREF_GAME_IN_PROGRESS, false);
				// Flush preferences
				game.prefs.flush();
				// Load the game screen
				game.setScreen(new GameScreen(game));
				dispose();
				return;
			} else if (continueEnabled && continueRect.contains(pos.x, pos.y)) {
				// Gdx.app.log(TAG, "Continue!");
				// If we're continuing then there must be a game in progress
				// The game screen should have enough information to resume it
				// So just load the game screen
				game.setScreen(new GameScreen(game));
				dispose();
				return;
			} else if (subjectRect.contains(pos.x, pos.y)) {
				// Gdx.app.log(TAG, "Subject!");
				subject = ++subject % SUBJECTS.length;
				TextBounds subjectBounds = game.font48.getBounds(subjectText + SUBJECTS[subject]);
				subjectRect.setSize(subjectBounds.width, subjectBounds.height);
				final float midx = gameArea.gameRect.x + (gameArea.gameRect.width / 2);
				subjectRect
						.setPosition(midx - subjectRect.width / 2, titleRect.y - UI_PADDING_BIG - subjectRect.height);
			} else if (sizeRect.contains(pos.x, pos.y)) {
				// Gdx.app.log(TAG, "Size!");
				size = ++size % SIZES.length;
				TextBounds sizeBounds = game.font48.getBounds(sizeText + SIZES[size]);
				sizeRect.setSize(sizeBounds.width, sizeBounds.height);
				final float midx = gameArea.gameRect.x + (gameArea.gameRect.width / 2);
				sizeRect.setPosition(midx - sizeRect.width / 2, subjectRect.y - UI_PADDING - sizeRect.height);
			} else if (helpRect.contains(pos.x, pos.y)) {
				// Gdx.app.log(TAG, "Help!");
				game.setScreen(new HelpScreen(game));
				dispose();
				return;
			} else if (creditsRect.contains(pos.x, pos.y)) {
				// Gdx.app.log(TAG, "Credits!");
				game.setScreen(new CreditsScreen(game));
				dispose();
				return;
			}
		} else if (Gdx.input.isKeyPressed(Keys.BACK)) {
			// Gdx.app.log(TAG, "Back!");
			Gdx.app.exit();
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

		// Render menu items
		game.batch.setProjectionMatrix(camera.combined);
		game.batch.begin();

		game.font64.setColor(Color.WHITE);
		game.font64.drawMultiLine(game.batch, titleText, titleRect.x, titleRect.y + titleRect.height, titleRect.width,
				HAlignment.CENTER);
		game.font48.setColor(enabled);
		game.font48.draw(game.batch, newGameText, newGameRect.x, newGameRect.y + newGameRect.height);
		game.font48
				.draw(game.batch, subjectText + SUBJECTS[subject], subjectRect.x, subjectRect.y + subjectRect.height);
		game.font48.draw(game.batch, sizeText + SIZES[size], sizeRect.x, sizeRect.y + sizeRect.height);
		game.font32.setColor(enabled);
		game.font32.draw(game.batch, creditsText, creditsRect.x, creditsRect.y + creditsRect.height);
		game.font32.draw(game.batch, helpText, helpRect.x, helpRect.y + helpRect.height);

		if (!continueEnabled) {
			game.font48.setColor(disabled);
		}
		game.font48.draw(game.batch, continueText, continueRect.x, continueRect.y + continueRect.height);

		game.batch.end();

		// If debug enabled, draw outlines of all important screen elements
		if (MyGame.DEBUG) {
			// Update ShapeRenderer's projection matrix
			game.shapeRenderer.setProjectionMatrix(camera.combined);

			// Start drawing lines
			game.shapeRenderer.begin(ShapeType.Line);

			game.shapeRenderer.setColor(Color.RED);

			// Draw defined game area
			Util.drawRect(game.shapeRenderer, gameArea.gameRect);
			// Draw all menu item Rectangles
			Util.drawRect(game.shapeRenderer, titleRect);
			Util.drawRect(game.shapeRenderer, newGameRect);
			Util.drawRect(game.shapeRenderer, continueRect);
			Util.drawRect(game.shapeRenderer, subjectRect);
			Util.drawRect(game.shapeRenderer, sizeRect);
			Util.drawRect(game.shapeRenderer, creditsRect);
			Util.drawRect(game.shapeRenderer, helpRect);

			// End drawing lines
			game.shapeRenderer.end();
		}
	}
}
