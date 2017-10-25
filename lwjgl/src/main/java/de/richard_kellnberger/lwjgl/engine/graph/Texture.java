package de.richard_kellnberger.lwjgl.engine.graph;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL30;

import static org.lwjgl.opengl.GL11.*;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;

public class Texture {

	private final int id;

	public Texture(String fileName) throws Exception {
		this(loadTexture(fileName));
	}

	public Texture(int id) {
		this.id = id;
	}

	public void bind() {
		glBindTexture(GL_TEXTURE_2D, id);
	}

	public int getId() {
		return id;
	}

	private static int loadTexture(String fileName) throws Exception {
		PNGDecoder decoder = new PNGDecoder(Texture.class.getResourceAsStream(fileName));

		ByteBuffer buf = ByteBuffer.allocateDirect(4 * decoder.getHeight() * decoder.getHeight());
		decoder.decode(buf, decoder.getWidth() * 4, Format.RGBA);
		buf.flip();

		// Create a new OpenGL texture
		int textureId = glGenTextures();
		// Bind the texture
		glBindTexture(GL_TEXTURE_2D, textureId);

		// Tell OpenGL how to unpack the RGBA bytes. Each component is 1 byte size
		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

		// glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		// glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

		// Upload the texture data
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, decoder.getWidth(), decoder.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE,
				buf);

		GL30.glGenerateMipmap(GL_TEXTURE_2D);
		return textureId;
	}

	public void cleanup() {
		glDeleteTextures(id);
	}
}
