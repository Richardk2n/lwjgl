package de.richard_kellnberger.lwjgl.game;

import static org.lwjgl.glfw.GLFW.*;

import org.joml.Vector2f;
import org.joml.Vector3f;

import de.richard_kellnberger.lwjgl.engine.GameItem;
import de.richard_kellnberger.lwjgl.engine.IGameLogic;
import de.richard_kellnberger.lwjgl.engine.MouseInput;
import de.richard_kellnberger.lwjgl.engine.Window;
import de.richard_kellnberger.lwjgl.engine.graph.Camera;
import de.richard_kellnberger.lwjgl.engine.graph.Mesh;
import de.richard_kellnberger.lwjgl.engine.graph.OBJLoader;
import de.richard_kellnberger.lwjgl.engine.graph.Texture;

public class DummyGame implements IGameLogic {

	private static final float CAMERA_POS_STEP = 0.2f;
	private static final float MOUSE_SENSITIVITY = 0.4f;
	
    private final Renderer renderer;
    
    private final Camera camera;
    
    private final Vector3f cameraInc;
    
    private Mesh mesh;
    
    private GameItem[] gameItems;
    
    public DummyGame() {
        renderer = new Renderer();
    	camera = new Camera();
    	cameraInc = new Vector3f(0, 0, 0);
    }
    
    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);
        Texture texture = new Texture("/textures/grassblock.png");
        mesh = OBJLoader.loadMesh("/models/cube.obj");
        mesh.setTexture(texture);
        gameItems = new GameItem[10000];
        for(int i = 0; i<100; i++) {
        	for(int j = 0; j<100; j++) {
        		gameItems[i*100+j] = new GameItem(mesh);
        		gameItems[i*100 +j].setPosition(i, 0, -j);
        	}
        }
    }

    @Override
    public void input(Window window, MouseInput mouseInput) {
    	cameraInc.set(0, 0, 0);
		if (window.isKeyPressed(GLFW_KEY_W)) {
			cameraInc.z = -1;
		} else if (window.isKeyPressed(GLFW_KEY_S)) {
			cameraInc.z = 1;
		}
		if (window.isKeyPressed(GLFW_KEY_A)) {
			cameraInc.x = -1;
		} else if (window.isKeyPressed(GLFW_KEY_D)) {
			cameraInc.x = 1;
		}
		if (window.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
			cameraInc.y = -1;
		} else if (window.isKeyPressed(GLFW_KEY_SPACE)) {
			cameraInc.y = 1;
		}
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
        // Update camera position
    	camera.movePosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);
        
        // Update camera based on mouse
    	if(mouseInput.isRightButtonPressed()) {
    		Vector2f rotVec = mouseInput.getDisplVec();
    		camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
    	}
    }

    @Override
    public void render(Window window) {
        renderer.render(window, camera, gameItems);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        mesh.cleanUp();
    }

}