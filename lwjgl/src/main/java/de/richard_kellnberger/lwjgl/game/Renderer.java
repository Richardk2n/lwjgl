package de.richard_kellnberger.lwjgl.game;

import org.joml.Matrix4f;

import de.richard_kellnberger.lwjgl.engine.GameItem;
import de.richard_kellnberger.lwjgl.engine.Utils;
import de.richard_kellnberger.lwjgl.engine.Window;
import de.richard_kellnberger.lwjgl.engine.graph.Camera;
import de.richard_kellnberger.lwjgl.engine.graph.Mesh;
import de.richard_kellnberger.lwjgl.engine.graph.ShaderProgram;
import de.richard_kellnberger.lwjgl.engine.graph.Transformation;

import static org.lwjgl.opengl.GL11.*;

public class Renderer {

	/**
	 * Field of View in Radians
	 */
	private static final float FOV = (float) Math.toRadians(60.0f);

	private static final float Z_NEAR = 0.01f;

	private static final float Z_FAR = 1000.f;

	private ShaderProgram shaderProgram;

	private Transformation transformation;

	public Renderer() {
		transformation = new Transformation();
	}

	public void init(Window window) throws Exception {
		// Create shader
		shaderProgram = new ShaderProgram();
		shaderProgram.createVertexShader(Utils.loadResource("/shaders/vertex.vs"));
		shaderProgram.createFragmentShader(Utils.loadResource("/shaders/fragment.fs"));
		shaderProgram.link();

		// Create uniforms for world and projection matrices
		shaderProgram.createUniform("projectionMatrix");
		shaderProgram.createUniform("modelViewMatrix");
		shaderProgram.createUniform("texture_sampler");
		
		// Create uniform for default colour and the flag that controls it
		shaderProgram.createUniform("colour");
		shaderProgram.createUniform("useColour");
	}

	public void clear() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}

	public void render(Window window, Camera camera, GameItem[] gameItems) {
		clear();

		if (window.isResized()) {
			glViewport(0, 0, window.getWidth(), window.getHeight());
			window.setResized(false);
		}

		shaderProgram.bind();

		// Update projection Matrix
		Matrix4f projectionMatrix = transformation.getProjectionMatrix(FOV, window.getWidth(), window.getHeight(),
				Z_NEAR, Z_FAR);
		shaderProgram.setUniform("projectionMatrix", projectionMatrix);
		
		// Update view Matrix
		Matrix4f viewMatrix = transformation.getViewMatrix(camera);

		shaderProgram.setUniform("texture_sampler", 0);
		
		// Render each gameItem
		for (GameItem gameItem : gameItems) {
			Mesh mesh = gameItem.getMesh();
			// Set model view matrix for this item
			Matrix4f modelViewMatrix = transformation.getModelViewMatrix(gameItem, viewMatrix);
			shaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
			// Render the mesh for this game item
			shaderProgram.setUniform("colour", mesh.getColor());
			shaderProgram.setUniform("useColour", mesh.isTextured() ? 0 : 1);
			mesh.render();
		}

		shaderProgram.unbind();
	}

	public void cleanup() {
		if (shaderProgram != null) {
			shaderProgram.cleanup();
		}
	}
}
