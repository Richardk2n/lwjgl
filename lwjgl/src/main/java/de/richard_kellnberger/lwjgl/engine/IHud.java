package de.richard_kellnberger.lwjgl.engine;

public interface IHud {

	GameItem[] getGameItems();
	
	default void cleanup() {
		GameItem[] gameItems = getGameItems();
		for(GameItem gameItem : gameItems) {
			gameItem.getMesh().cleanUp();
		}
	}
}
