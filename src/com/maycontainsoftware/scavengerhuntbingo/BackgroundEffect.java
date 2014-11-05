package com.maycontainsoftware.scavengerhuntbingo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;

/**
 * An interesting background effect. This is composed of a number of triangles that rotate around the middle of the
 * screen, blended together
 * 
 * @author Charlie
 */

public class BackgroundEffect implements Element {

	/** Tag for debug logging. */
	// private static final String TAG = BackgroundEffect.class.getName();

	/** The (fixed) number of triangles to display */
	private static final short NUMBER_OF_TRIANGLES = 50;

	/** A selection of fabulous colours */
	private static final Color[] FABULOUS_COLORS = new Color[] { Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW,
			Color.CYAN, Color.MAGENTA, };

	/** Standard blue-ish colours */
	private final Color[] color = new Color[NUMBER_OF_TRIANGLES];

	/** Colours to use when in fabulous mode */
	private final Color[] fabulousColor = new Color[NUMBER_OF_TRIANGLES];

	/** The current bearing of the triangle in radians */
	private final float[] bearing = new float[NUMBER_OF_TRIANGLES];

	/** The size of the triangle in radians */
	private final float[] wedgeAngle = new float[NUMBER_OF_TRIANGLES];

	/** Speed in radians/second */
	private final float[] rotationDelta = new float[NUMBER_OF_TRIANGLES];

	/** Whether fabulous mode is enabled */
	public boolean fabulous = false;

	// Orthographic projection camera
	protected OrthographicCamera camera;

	// Middle of the screen
	private int midX;
	private int midY;

	public BackgroundEffect() {

		// Alpha to use for all triangles
		final float ALPHA = 0.25f;

		// Set up all initial data
		for (int i = 0; i < NUMBER_OF_TRIANGLES; i++) {
			// Standard colours are various shades of blue
			float rg = MathUtils.random();
			color[i] = new Color(rg, rg, 1.0f, ALPHA);

			// Fabulous colours are taken in order
			Color c = FABULOUS_COLORS[i % FABULOUS_COLORS.length];
			fabulousColor[i] = new Color(c.r, c.g, c.b, ALPHA);

			// Random bearing for the triangle
			bearing[i] = MathUtils.random(MathUtils.PI2);

			// Random size from 45 to 135 degrees
			wedgeAngle[i] = MathUtils.random(5, 90) * MathUtils.degreesToRadians;

			// Random rotation speed from
			rotationDelta[i] = MathUtils.random(-1.0f, 1.0f) * MathUtils.degreesToRadians * 5;
		}

		// Set up camera
		camera = new OrthographicCamera();
	}

	/** Clear colour buffer */
	public void clearColorBuffer() {
		Gdx.gl.glClearColor(0.4f, 0.4f, 0.4f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
	}

	/** Render the background effect */
	public void render(MyGame game) {
		// Pretty background effects
		Gdx.gl.glEnable(GL10.GL_BLEND);
		Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

		// Update ShapeRenderer's projection matrix
		game.shapeRenderer.setProjectionMatrix(camera.combined);
		// Draw background of card
		game.shapeRenderer.begin(ShapeType.Filled);

		final int TRIANGLE_SIZE = 10000;

		for (int i = 0; i < NUMBER_OF_TRIANGLES; i++) {
			game.shapeRenderer.setColor(fabulous ? fabulousColor[i] : color[i]);
			float x2 = midX + TRIANGLE_SIZE * MathUtils.sin(bearing[i]);
			float y2 = midY + TRIANGLE_SIZE * MathUtils.cos(bearing[i]);
			float x3 = midX + TRIANGLE_SIZE * MathUtils.sin(bearing[i] + wedgeAngle[i]);
			float y3 = midY + TRIANGLE_SIZE * MathUtils.cos(bearing[i] + wedgeAngle[i]);
			game.shapeRenderer.triangle(midX, midY, x2, y2, x3, y3);
		}

		// End drawing lines
		game.shapeRenderer.end();

		Gdx.gl.glDisable(GL10.GL_BLEND);
	}

	/** Update triangle rotations wrt rotation speed */
	public void update(float delta) {
		// Update camera
		camera.update();

		// Update triangle rotation
		final int FABULOUS_ACCELERATION = 20;
		for (int i = 0; i < NUMBER_OF_TRIANGLES; i++) {
			bearing[i] += rotationDelta[i] * delta * (fabulous ? FABULOUS_ACCELERATION : 1);
		}
	}

	@Override
	public void resize(int displayWidth, int displayHeight) {
		// Gdx.app.log(TAG, "resize(): displayWidth = " + displayWidth + ", displayHeight = " + displayHeight);

		// Update camera projection
		camera.setToOrtho(false, displayWidth, displayHeight);

		// Determine mid-point of screen
		midX = displayWidth / 2;
		midY = displayHeight / 2;
		// Gdx.app.log(TAG, "midX = " + midX);
		// Gdx.app.log(TAG, "midY = " + midY);
	}
}
