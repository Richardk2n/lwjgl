package de.richard_kellnberger.lwjgl.engine;

import de.richard_kellnberger.lwjgl.engine.graph.Material;
import de.richard_kellnberger.lwjgl.engine.graph.Mesh;
import de.richard_kellnberger.lwjgl.engine.graph.OBJLoader;
import de.richard_kellnberger.lwjgl.engine.graph.Texture;

public class SkyBox extends GameItem {

	public SkyBox(String objModel, String textureFile) throws Exception {
		super();
		Mesh skyBoxMesh = OBJLoader.loadMesh(objModel);
		Texture skyBoxTexture = new Texture(textureFile);
		skyBoxMesh.setMaterial(new Material(skyBoxTexture, 0.0f));
		setMesh(skyBoxMesh);
		setPosition(0, 0, 0);
	}
}
