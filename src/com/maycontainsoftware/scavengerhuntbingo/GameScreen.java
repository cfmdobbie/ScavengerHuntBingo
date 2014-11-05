package com.maycontainsoftware.scavengerhuntbingo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class GameScreen extends MyScreen {

	// private static final String TAG = GameScreen.class.getName();

	// Texture containing the tileset for this game
	private final Texture subjectTexture;
	// The subtextures from the subject tileset
	private final TextureRegion[] subjectTextures;

	// Variables relating to back button
	private static final Color BACK_TEXT_COLOR = new Color(0.92f, 0.70f, 0.33f, 1.0f);
	private static final String BACK_TEXT = "Back to Main Menu";
	private final Rectangle backTextRect = new Rectangle();

	// The different areas of the card
	// Rectangle representing the whole card
	private final Rectangle cardRect = new Rectangle();
	// The header panel
	private final Rectangle headerRect = new Rectangle();
	// Each tile represented as a Rectangle
	private final Rectangle[] tileRect;

	// Constants required for drawing the card
	private static final int BORDER_SIZE = 10;
	private static final int HEADER_HEIGHT = 64;
	// There are boardSize * boardSize tiles
	private final int boardSize;
	private final int numberOfTiles;
	// Size of each tile (squared!)
	private float tileSize;

	// Variables relating to card header
	private static final Color HEADER_TEXT_COLOR = new Color(0.60f, 0.60f, 1.0f, 1.0f);
	private static final String HEADER_TEXT = "Scavenger Hunt Bingo!";
	private final Rectangle headerTextRect = new Rectangle();

	// Game state

	// Mapping from subtexture in the tileset to the tile on the screen
	int[] tileTextureMapping;

	// Tiles that have already been seen
	boolean[] seen;

	// Whether the game has been won
	boolean gameWon = false;

	// Winning "BINGO!" effect
	private BingoEffect bingoEffect;
	private boolean bingoDismissed = false;

	public GameScreen(MyGame game) {
		super(game, 500, 700);
		// Gdx.app.log(TAG, "<init>()");

		// Whether this is a new game or we're continuing an old game
		// we will always have a board size and subject

		// Process the board size

		boardSize = game.prefs.getInteger(MyGame.PREF_BOARD_SIZE, 5);
		numberOfTiles = boardSize * boardSize;
		// Gdx.app.log(TAG, "boardSize = " + boardSize);

		// Create rectangles for all tiles
		tileRect = new Rectangle[numberOfTiles];
		for (int i = 0; i < tileRect.length; i++) {
			tileRect[i] = new Rectangle();
		}

		// Process the subject

		// Get subject
		final String subject = game.prefs.getString(MyGame.PREF_SUBJECT);
		// Gdx.app.log(TAG, "subject = " + subject);

		// Pull card texture for the selected subject
		FileHandle fileHandle = Gdx.files.internal(subject + ".png");
		// Gdx.app.log(TAG, "" + fileHandle);
		// TODO: Improve missing-file handling in next version
		// If files have been renamed, the following line can throw an Exception.
		// Solution might be to create the FileHandle in MyGame and check whether it
		// exists before resuming a game, otherwise clearing state and going to main menu
		subjectTexture = new Texture(fileHandle);
		// Smoothly interpolate texture data to avoid jaggies and other artifacts
		subjectTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		// Chop card texture into individual tiles
		// All textures contain 5x5 = 25 tiles
		subjectTextures = new TextureRegion[25];
		final float regionSize = 1.0f / 5.0f;
		// Gdx.app.log(TAG, "regionSize = " + regionSize);
		for (int i = 0; i < 25; i++) {
			final int x = i % 5;
			final int y = i / 5;
			subjectTextures[i] = new TextureRegion(subjectTexture, x * regionSize, y * regionSize,
					(x + 1) * regionSize, (y + 1) * regionSize);
		}

		// Create arrays for game state
		seen = new boolean[numberOfTiles];
		tileTextureMapping = new int[numberOfTiles];

		// Determine whether a game was in progress
		final boolean gameInProgress = game.prefs.getBoolean(MyGame.PREF_GAME_IN_PROGRESS, false);
		// Gdx.app.log(TAG, "gameInProgress = " + gameInProgress);
		if (!gameInProgress) {
			// This is a new game!
			// Because we want to save/restore state in pause/resume, we're not going to do it here as well
			// So just save a default starting state to preferences and leave resume() to load it all again

			// All "seen" values start out as false
			for (int i = 0; i < numberOfTiles; i++) {
				game.prefs.putBoolean("seen." + i, false);
			}

			// Mapping from subtexture to tile
			List<Integer> textureIndices = new ArrayList<Integer>();
			for (int i = 0; i < 25; i++) {
				textureIndices.add(i);
			}
			Collections.shuffle(textureIndices, MathUtils.random);
			for (int i = 0; i < numberOfTiles; i++) {
				game.prefs.putInteger("subtexture." + i, textureIndices.get(i));
			}
		}

		// Save that the game is definitely now in progress
		game.prefs.putBoolean(MyGame.PREF_GAME_IN_PROGRESS, true);
		game.prefs.flush();

		this.bingoEffect = new BingoEffect(game);
	}

	@Override
	public void pause() {
		// Gdx.app.log(TAG, "pause()");

		// Save game state

		// Tiles that have been seen
		for (int i = 0; i < numberOfTiles; i++) {
			game.prefs.putBoolean("seen." + i, seen[i]);
		}

		// Mapping from subtexture to tile
		for (int i = 0; i < numberOfTiles; i++) {
			game.prefs.putInteger("subtexture." + i, tileTextureMapping[i]);
		}

		// Make sure these get saved!
		game.prefs.flush();
	}

	@Override
	public void resume() {
		// Gdx.app.log(TAG, "resume()");

		// At point of resume, game is *always* in progress and has valid state, because we set it in <init>
		// Restore game state

		// Tiles that have been seen
		for (int i = 0; i < numberOfTiles; i++) {
			seen[i] = game.prefs.getBoolean("seen." + i, false);
		}

		// Mapping from subtexture to tile
		for (int i = 0; i < numberOfTiles; i++) {
			tileTextureMapping[i] = game.prefs.getInteger("subtexture." + i, 0);
		}

		// Set size of back button
		TextBounds backBounds = game.font32.getBounds(BACK_TEXT);
		backTextRect.setSize(backBounds.width, backBounds.height);
		// Gdx.app.log(TAG, "backRect.height = " + backTextRect.height);

		// Set size of header
		TextBounds headerBounds = game.font42.getBounds(HEADER_TEXT);
		headerTextRect.setSize(headerBounds.width, headerBounds.height);
		// Gdx.app.log(TAG, "headerRect.height = " + headerTextRect.height);
	}

	@Override
	protected void doResize(int displayWidth, int displayHeight) {
		// Gdx.app.log(TAG, "doResize()");

		// Extra padding required between screen elements
		final int screenPadding = 20;

		// X-coordinate of the middle of the screen
		final float midx = gameArea.gameRect.x + (gameArea.gameRect.width / 2);

		// Position of back button
		backTextRect.setPosition(midx - backTextRect.width / 2, gameArea.gameRect.y + screenPadding);

		// Set size and position of entire card

		final float cardWidth = gameArea.gameRect.width - 2 * screenPadding;
		final float cardHeight = cardWidth + HEADER_HEIGHT + BORDER_SIZE;
		cardRect.setSize(cardWidth, cardHeight);
		final float cardX = gameArea.gameRect.x + screenPadding;
		final float cardY = gameArea.gameRect.y + gameArea.gameRect.height / 2 - cardHeight / 2;
		cardRect.setPosition(cardX, cardY);

		// Header Rectangle
		headerRect.setPosition(cardRect.x + BORDER_SIZE, cardRect.y + cardRect.height - BORDER_SIZE - HEADER_HEIGHT);
		headerRect.setSize(cardRect.width - 2 * BORDER_SIZE, HEADER_HEIGHT);

		// Position of card header
		headerTextRect.setPosition(midx - headerTextRect.width / 2, cardRect.y + cardWidth + HEADER_HEIGHT / 2
				- headerTextRect.height / 2);

		// Determine tile size and update sizes of tile Rectangles
		tileSize = (cardRect.width - (boardSize + 1) * BORDER_SIZE) / boardSize;
		for (int i = 0; i < numberOfTiles; i++) {
			tileRect[i].setSize(tileSize);
		}
		// Set positions of all tile Rectangles
		float top = cardRect.y + cardRect.height;
		float left = cardRect.x;
		for (int i = 0; i < numberOfTiles; i++) {
			int x = i % boardSize;
			int y = i / boardSize;
			tileRect[i].setPosition(left + BORDER_SIZE + x * (tileSize + BORDER_SIZE), top - BORDER_SIZE
					- HEADER_HEIGHT - (BORDER_SIZE + tileSize) * (y + 1));
		}

		bingoEffect.resize(displayWidth, displayHeight);
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

			// Determine whether the back button was pressed
			if (backTextRect.contains(pos.x, pos.y)) {
				// Gdx.app.log(TAG, "Back!");
				game.setScreen(new MenuScreen(game));
				dispose();
				return;
			}

			if (!gameWon) {
				// Determine whether a tile was pressed
				for (int i = 0; i < numberOfTiles; i++) {
					if (tileRect[i].contains(pos.x, pos.y)) {
						// Gdx.app.log(TAG, "Tile " + i + "!");
						// Toggle the touched tile
						seen[i] = !seen[i];
					}
				}

				// Check for a win condition
				if (isGameWon()) {
					// Game has been won
					gameWon = true;

					// Toggle the fabulous flag!
					game.backgroundEffect.fabulous = true;

					// Mark that a game is no longer in progress
					game.prefs.putBoolean(MyGame.PREF_GAME_IN_PROGRESS, false);
					game.prefs.flush();
				}

			} else {
				bingoDismissed = true;
			}

			// Otherwise, don't can what was pressed - going to ignore it!

		} else if (Gdx.input.isKeyPressed(Keys.BACK)) {
			// Gdx.app.log(TAG, "Back!");
			game.setScreen(new MenuScreen(game));
			dispose();
			return;
		}

		// Update game model

		game.backgroundEffect.update(delta);
		if (gameWon) {
			bingoEffect.update(delta);
		}

		// Render screen

		// Clear colour buffer
		game.backgroundEffect.clearColorBuffer();
		// Update camera
		camera.update();
		// Render the pretty background effect
		game.backgroundEffect.render(game);

		// Draw card - first the filled areas

		game.shapeRenderer.setProjectionMatrix(camera.combined);
		game.shapeRenderer.begin(ShapeType.Filled);
		// Card background - light gray
		game.shapeRenderer.setColor(Color.LIGHT_GRAY);
		Util.drawRect(game.shapeRenderer, cardRect);
		// Everything else is in white
		game.shapeRenderer.setColor(Color.WHITE);
		// Header rect
		Util.drawRect(game.shapeRenderer, headerRect);
		// Tiles
		for (int i = 0; i < numberOfTiles; i++) {
			game.shapeRenderer.rect(tileRect[i].x, tileRect[i].y, tileRect[i].width, tileRect[i].height);
		}
		game.shapeRenderer.end();

		// Draw card - next the outlines

		game.shapeRenderer.setProjectionMatrix(camera.combined);
		game.shapeRenderer.begin(ShapeType.Line);
		// All outlines are in black
		game.shapeRenderer.setColor(Color.BLACK);
		// Entire card border
		Util.drawRect(game.shapeRenderer, cardRect);
		// Header rect border
		Util.drawRect(game.shapeRenderer, headerRect);
		// Tile borders
		for (int i = 0; i < numberOfTiles; i++) {
			game.shapeRenderer.rect(tileRect[i].x, tileRect[i].y, tileRect[i].width, tileRect[i].height);
		}
		game.shapeRenderer.end();

		// Render all textured elements with a SpriteBatch

		game.batch.setProjectionMatrix(camera.combined);
		game.batch.begin();

		// Back button
		game.font32.setColor(BACK_TEXT_COLOR);
		game.font32.draw(game.batch, BACK_TEXT, backTextRect.x, backTextRect.y + backTextRect.height);

		// Header text
		game.font42.setColor(HEADER_TEXT_COLOR);
		game.font42.draw(game.batch, HEADER_TEXT, headerTextRect.x, headerTextRect.y + headerTextRect.height);

		// Draw all tile textures
		for (int i = 0; i < numberOfTiles; i++) {
			if (!seen[i]) {
				game.batch.setColor(Color.WHITE);
			} else {
				game.batch.setColor(1.0f, 1.0f, 1.0f, 0.1f);
			}
			game.batch.draw(subjectTextures[tileTextureMapping[i]], tileRect[i].x, tileRect[i].y, tileRect[i].width,
					tileRect[i].height);
		}

		// End sprite render batch
		game.batch.end();

		// If debug enabled, draw outlines of all important screen elements
		if (MyGame.DEBUG) {
			// Update ShapeRenderer's projection matrix
			game.shapeRenderer.setProjectionMatrix(camera.combined);

			// Start drawing lines
			game.shapeRenderer.begin(ShapeType.Line);

			// Draw defined game area
			game.shapeRenderer.setColor(Color.RED);
			Util.drawRect(game.shapeRenderer, gameArea.gameRect);
			// Draw outlines around text
			Util.drawRect(game.shapeRenderer, backTextRect);
			Util.drawRect(game.shapeRenderer, headerTextRect);

			// End drawing lines
			game.shapeRenderer.end();
		}

		if (gameWon && !bingoDismissed) {
			bingoEffect.render(game);
		}
	}

	@Override
	public void dispose() {
		// backgroundTexture.dispose();
		// backgroundTexture = null;
	}

	private boolean isGameWon() {
		boolean allSeen;
		// Check for any horizontal wins
		for (int y = 0; y < boardSize; y++) {
			allSeen = true;
			for (int x = 0; x < boardSize; x++) {
				int i = x % boardSize + y * boardSize;
				if (!seen[i]) {
					allSeen = false;
				}
			}
			if (allSeen) {
				return true;
			}
		}
		// Check for any vertical wins
		for (int x = 0; x < boardSize; x++) {
			allSeen = true;
			for (int y = 0; y < boardSize; y++) {
				int i = x % boardSize + y * boardSize;
				if (!seen[i]) {
					allSeen = false;
				}
			}
			if (allSeen) {
				return true;
			}
		}
		// Check for any diagonal wins; two lines to check
		// Top-left to bottom-right
		allSeen = true;
		for (int j = 0; j < boardSize; j++) {
			int i = j % boardSize + j * boardSize;
			if (!seen[i]) {
				allSeen = false;
			}
		}
		if (allSeen) {
			return true;
		}
		// Top-right to bottom-left
		allSeen = true;
		for (int j = 0; j < boardSize; j++) {
			int i = (boardSize - j - 1) % boardSize + j * boardSize;
			if (!seen[i]) {
				allSeen = false;
			}
		}
		if (allSeen) {
			return true;
		}

		return false;
	}
}
