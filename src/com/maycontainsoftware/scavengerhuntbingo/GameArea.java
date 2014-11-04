package com.maycontainsoftware.scavengerhuntbingo;

import com.badlogic.gdx.math.Rectangle;

class GameArea {
//	private static final String TAG = GameArea.class.getName();
	
	// Size/shape of game world - defines game coordinates
	final int gameWidth, gameHeight;
	final float gameAspect;
	
	// Size/shape of display area
	int displayWidth, displayHeight;
	float displayAspect;
	
	// Display area in game coordinates
	final Rectangle sceneRect = new Rectangle();
	
	// Game area in game coordinates
	final Rectangle gameRect = new Rectangle();
	
	// Excess borders in game coordinates
	final Rectangle topRect = new Rectangle();
	final Rectangle bottomRect = new Rectangle();
	final Rectangle leftRect = new Rectangle();
	final Rectangle rightRect = new Rectangle();
	
	public GameArea(int gameWidth, int gameHeight) {
		
//		Gdx.app.log(TAG, "<init>(" + gameWidth + ", " + gameHeight + ")");
		
		// Set size/shape of game world
		this.gameWidth = gameWidth;
		this.gameHeight = gameHeight;
		this.gameAspect = gameWidth / (float)gameHeight;
		
		gameRect.setSize(gameWidth,  gameHeight);
		topRect.setWidth(gameWidth);
		bottomRect.setWidth(gameWidth);
		rightRect.setHeight(gameHeight);
		leftRect.setHeight(gameHeight);
	}
	
	public void resize(int width, int height) {
//		Gdx.app.log(TAG, "resize(): width = " + width + ", height = " + height);
		
		// Update size/shape of display device
		this.displayWidth = width;
		this.displayHeight = height;
		this.displayAspect = displayWidth / (float)displayHeight;
		
		// Calculate new scene size
		int sceneWidth = 0;
		int sceneHeight = 0;
		
		if(gameAspect < displayAspect) {
			// Display is wider than the game
			sceneWidth = (int)(displayAspect * gameHeight);
			sceneHeight = gameHeight;
		} else if(gameAspect > displayAspect) {
			// Display is taller than the game
			sceneWidth = gameWidth;
			sceneHeight = (int)(gameWidth / displayAspect);
		} else {
			// Display exactly matches game
			sceneWidth = gameWidth;
			sceneHeight = gameHeight;
		}
		
		// Update scene Rectangle
		sceneRect.setSize(sceneWidth, sceneHeight);
		// Update game Rectangle
		gameRect.setPosition((sceneWidth - gameWidth) / 2, (sceneHeight - gameHeight) / 2);
		// Top border
		topRect.setHeight(gameRect.y);
		topRect.x = gameRect.x;
		topRect.y = gameRect.y + gameHeight;
		// Bottom border
		bottomRect.setHeight(gameRect.y);
		bottomRect.x = gameRect.x;
		// Right border
		rightRect.setWidth(gameRect.x);
		rightRect.x = gameRect.x + gameWidth;
		rightRect.y = gameRect.y;
		// Left border
		leftRect.setWidth(gameRect.x);
		leftRect.y = gameRect.y;
	}
}
