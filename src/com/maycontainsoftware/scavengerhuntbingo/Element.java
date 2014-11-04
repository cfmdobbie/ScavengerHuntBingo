package com.maycontainsoftware.scavengerhuntbingo;

public interface Element {
	public void update(float delta);
	public void render(MyGame game);
	public void resize(int displayWidth, int displayHeight);
}
