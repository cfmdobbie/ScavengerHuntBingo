package com.maycontainsoftware.scavengerhuntbingo;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class Util {
	public static void drawRect(ShapeRenderer shapeRenderer, Rectangle rect) {
		shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
	}
}
