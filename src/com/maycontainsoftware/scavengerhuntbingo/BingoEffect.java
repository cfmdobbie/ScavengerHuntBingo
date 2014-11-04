package com.maycontainsoftware.scavengerhuntbingo;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

/**
 * @author Charlie
 */

public class BingoEffect implements Element {

	/** Tag for debug logging. */
//	private static final String TAG = BingoEffect.class.getName();
	
	private final Rectangle panel = new Rectangle();
	
	// Orthographic projection camera
	protected OrthographicCamera camera;
	
	private float midX;
	private float midY;

	private static final int DESIRED_SPACE = 400;
	private static final int PANEL_SIZE = 300;
	private static final int HALF_PANEL_SIZE = PANEL_SIZE / 2;
	
	private static final int ACTIVE_TEXT_AREA = 200;
	private static final int HALF_ACTIVE_TEXT_AREA = ACTIVE_TEXT_AREA / 2;
	
	private float textBlue = 1.0f;
	private float colorAngle = 0.0f;
	
	private String[] letters = new String[] {"B", "I", "N", "G", "O", "!"};
	private Rectangle[] letterRects = new Rectangle[letters.length];
	private float[] letterAngle = new float[letters.length];
	
	public BingoEffect(MyGame game) {
		// Set up camera
		camera = new OrthographicCamera();
		
		for(int i = 0 ; i < letters.length ; i++) {
			TextBounds bounds = game.font64.getBounds(letters[i]);
			letterRects[i] = new Rectangle();
			letterRects[i].setSize(bounds.width, bounds.height);
			letterAngle[i] = -i / 2.0f;
		}
	}
	
	public void render(MyGame game) {
		// Update ShapeRenderer's projection matrix
		game.shapeRenderer.setProjectionMatrix(camera.combined);
		
		// Background
		game.shapeRenderer.begin(ShapeType.Filled);
		game.shapeRenderer.setColor(Color.WHITE);
		game.shapeRenderer.rect(panel.x, panel.y, panel.width, panel.height);
		game.shapeRenderer.end();
		
		// Outline
		game.shapeRenderer.begin(ShapeType.Line);
		game.shapeRenderer.setColor(Color.BLACK);
		game.shapeRenderer.rect(panel.x, panel.y, panel.width, panel.height);
		game.shapeRenderer.end();
		
		// Text
		game.batch.setProjectionMatrix(camera.combined);
		game.batch.begin();
		game.font64.setColor(0.6f, 0.6f, textBlue, 1.0f);
		for(int i = 0 ; i < letters.length ; i++) {
			game.font64.draw(game.batch, letters[i], letterRects[i].x - letterRects[i].width / 2, letterRects[i].y + letterRects[i].height / 2);
		}
		game.batch.end();
	}

	/** Update triangle rotations wrt rotation speed */
	public void update(float delta) {
		// Update camera
		camera.update();
		
		colorAngle += delta;
		textBlue = MathUtils.sin(colorAngle) / 2 + 0.5f;
		
		updateLetters(delta);
	}

	private void updateLetters(float delta) {
		for(int i = 0 ; i < letters.length ; i++) {
			float x = midX - HALF_ACTIVE_TEXT_AREA + i * ACTIVE_TEXT_AREA / (letters.length - 1);
			letterAngle[i] = letterAngle[i] + delta * 2;
			float y = midY + MathUtils.sin(letterAngle[i]) * HALF_ACTIVE_TEXT_AREA;
			letterRects[i].setPosition(x, y);
		}
	}
	
	@Override
	public void resize(int displayWidth, int displayHeight) {
//		Gdx.app.log(TAG, "resize(): displayWidth = " + displayWidth + ", displayHeight = " + displayHeight);
		
		final int smallestDimension = Math.min(displayWidth, displayHeight);
		
		float cameraWidth = displayWidth / (float)smallestDimension * DESIRED_SPACE;
		float cameraHeight = displayHeight / (float)smallestDimension * DESIRED_SPACE;
		
		midX = cameraWidth / 2;
		midY = cameraHeight / 2;
		
		panel.setPosition(midX - HALF_PANEL_SIZE, midY - HALF_PANEL_SIZE);
		panel.setSize(PANEL_SIZE, PANEL_SIZE);
		
		// Update camera projection
		camera.setToOrtho(false, cameraWidth, cameraHeight);
	}
}
