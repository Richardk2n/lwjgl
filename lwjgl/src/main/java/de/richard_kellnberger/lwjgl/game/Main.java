package de.richard_kellnberger.lwjgl.game;

import de.richard_kellnberger.lwjgl.engine.GameEngine;
import de.richard_kellnberger.lwjgl.engine.IGameLogic;

public class Main {
 
    public static void main(String[] args) {
        try {
            boolean vSync = true;
            IGameLogic gameLogic = new DummyGame();
            GameEngine gameEng = new GameEngine("GAME", vSync, gameLogic);
            gameEng.start();
        } catch (Exception excp) {
            excp.printStackTrace();
            System.exit(-1);
        }
    }
}