package de.richard_kellnberger.lwjgl.game;

import static org.lwjgl.glfw.GLFW.*;

import org.joml.Vector2f;
import org.joml.Vector3f;

import de.richard_kellnberger.lwjgl.engine.IGameLogic;
import de.richard_kellnberger.lwjgl.engine.MouseInput;
import de.richard_kellnberger.lwjgl.engine.Scene;
import de.richard_kellnberger.lwjgl.engine.SceneLight;
import de.richard_kellnberger.lwjgl.engine.Window;
import de.richard_kellnberger.lwjgl.engine.graph.Camera;
import de.richard_kellnberger.lwjgl.engine.graph.Material;
import de.richard_kellnberger.lwjgl.engine.graph.Mesh;
import de.richard_kellnberger.lwjgl.engine.graph.OBJLoader;
import de.richard_kellnberger.lwjgl.engine.graph.Renderer;
import de.richard_kellnberger.lwjgl.engine.graph.Texture;
import de.richard_kellnberger.lwjgl.engine.graph.lights.DirectionalLight;
import de.richard_kellnberger.lwjgl.engine.graph.weather.Fog;
import de.richard_kellnberger.lwjgl.engine.items.GameItem;
import de.richard_kellnberger.lwjgl.engine.items.SkyBox;
import de.richard_kellnberger.lwjgl.engine.items.Terrain;

public class DummyGame implements IGameLogic {

    private static final float MOUSE_SENSITIVITY = 0.2f;

    private final Vector3f cameraInc;

    private final Renderer renderer;

    private final Camera camera;

    private Scene scene;
    
    private Hud hud;

    private float lightAngle;

    private static final float CAMERA_POS_STEP = 0.05f;
    
    private Terrain terrain;

    public DummyGame() {
        renderer = new Renderer();
        camera = new Camera();
        cameraInc = new Vector3f(0.0f, 0.0f, 0.0f);
        lightAngle = -90;
    }

    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);

        scene = new Scene();

        float skyBoxScale = 50.0f;
        float terrainScale = 10;
        int terrainSize = 3;
        float minY = -0.1f;
        float maxY = 0.1f;
        int textInc = 40;
        terrain = new Terrain(terrainSize, terrainScale, minY, maxY, "/textures/heightmap3.png", "/textures/terrain.png", textInc);
        scene.setGameItems(terrain.getGameItems());
        
        scene.setFog(new Fog(true, new Vector3f(0.5f, 0.5f, 0.5f), 0.15f));
        

        // Setup  GameItems
        float reflectance = 0.65f;
        Texture normalMap = new Texture("/textures/rock_normals.png");

        Mesh quadMesh1 = OBJLoader.loadMesh("/models/quad.obj");
        Texture texture = new Texture("/textures/rock.png");
        Material quadMaterial1 = new Material(texture, reflectance);
        quadMesh1.setMaterial(quadMaterial1);
        GameItem quadGameItem1 = new GameItem(quadMesh1);
        quadGameItem1.setPosition(-3f, 0, 0);
        quadGameItem1.setScale(2.0f);
        quadGameItem1.setRotation(90, 0, 0);

        Mesh quadMesh2 = OBJLoader.loadMesh("/models/quad.obj");
        Material quadMaterial2 = new Material(texture, reflectance);
        quadMaterial2.setNormalMap(normalMap);
        quadMesh2.setMaterial(quadMaterial2);
        GameItem quadGameItem2 = new GameItem(quadMesh2);
        quadGameItem2.setPosition(3f, 0, 0);
        quadGameItem2.setScale(2.0f);
        quadGameItem2.setRotation(45, 0, 0);

        scene.setGameItems(new GameItem[]{quadGameItem1, quadGameItem2});

        // Setup  SkyBox
        SkyBox skyBox = new SkyBox("/models/skybox.obj", "/textures/skybox.png");
        skyBox.setScale(skyBoxScale);
        scene.setSkyBox(skyBox);

        // Setup Lights
        setupLights();

        // Create HUD
        hud = new Hud("DEMO");

        camera.getPosition().x = 0.0f;
        camera.getPosition().z = 0.0f;
        camera.getPosition().y = -0.2f;
        camera.getRotation().x = 10.f;
        
        
    }
    
    private void setupLights() {
        SceneLight sceneLight = new SceneLight();
        scene.setSceneLight(sceneLight);

        // Ambient Light
        sceneLight.setAmbientLight(new Vector3f(1.0f, 1.0f, 1.0f));

        // Directional Light
        float lightIntensity = 1.0f;
        Vector3f lightPosition = new Vector3f(-1, 0, 0);
        sceneLight.setDirectionalLight(new DirectionalLight(new Vector3f(1, 1, 1), lightPosition, lightIntensity));
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
        // Update camera based on mouse            
        if (mouseInput.isRightButtonPressed()) {
            Vector2f rotVec = mouseInput.getDisplVec();
            camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
            
            // Update HUD compass
            hud.rotateCompass(camera.getRotation().y);
        }

        // Update camera position
        camera.movePosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);
        // Check if there has been a collision. If true, set the y position to the minimum height
        Vector3f pos = camera.getPosition();
        float height = terrain.getHeight(pos);
        if(pos.y < height + 1) {
        	camera.setPosition(pos.x, height + 1, pos.z); //TODO +1 is kind of bullshit
        }
        
        SceneLight sceneLight = scene.getSceneLight();

        // Update directional light direction, intensity and colour
        DirectionalLight directionalLight = sceneLight.getDirectionalLight();
        lightAngle += 1.1f;
        if (lightAngle > 90) {
            directionalLight.setIntensity(0);
            if (lightAngle >= 360) {
                lightAngle = -90;
            }
            sceneLight.getAmbientLight().set(0.3f, 0.3f, 0.4f);
        } else if (lightAngle <= -80 || lightAngle >= 80) {
            float factor = 1 - (float) (Math.abs(lightAngle) - 80) / 10.0f;
            sceneLight.getAmbientLight().set(factor, factor, factor);
            directionalLight.setIntensity(factor);
            directionalLight.getColor().y = Math.max(factor, 0.9f);
            directionalLight.getColor().z = Math.max(factor, 0.5f);
        } else {
            sceneLight.getAmbientLight().set(1, 1, 1);
            directionalLight.setIntensity(1);
            directionalLight.getColor().x = 1;
            directionalLight.getColor().y = 1;
            directionalLight.getColor().z = 1;
        }
        double angRad = Math.toRadians(lightAngle);
        directionalLight.getDirection().x = (float) Math.sin(angRad);
        directionalLight.getDirection().y = (float) Math.cos(angRad);
    }

    @Override
    public void render(Window window) {
    	hud.updateSize(window);
        renderer.render(window, camera, scene, hud);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        if (hud != null) {
            hud.cleanup();
        }
    }

}