package de.richard_kellnberger.lwjgl.engine.graph;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryUtil;

public class Mesh {

	private final int vaoId;

	private final int posVboId;

	private final int textCoordsVboId;

	private final int idxVboId;

	private final int vertexCount;
	
	private Texture texture;

	public Mesh(float[] positions, float[] textCoords, int[] indices, Texture texture) {
		FloatBuffer posBuffer = null;
		FloatBuffer textCoordsBuffer = null;
		IntBuffer indicesBuffer = null;
		try {
			this.texture = texture;
			vertexCount = indices.length;

			vaoId = glGenVertexArrays();
			glBindVertexArray(vaoId);

			// Position VBO
			posVboId = glGenBuffers();
			posBuffer = MemoryUtil.memAllocFloat(positions.length);
			posBuffer.put(positions).flip();
			glBindBuffer(GL_ARRAY_BUFFER, posVboId);
			glBufferData(GL_ARRAY_BUFFER, posBuffer, GL_STATIC_DRAW);
			glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

			// Texture VBO
			textCoordsVboId = glGenBuffers();
			textCoordsBuffer = MemoryUtil.memAllocFloat(textCoords.length);
			textCoordsBuffer.put(textCoords).flip();
			glBindBuffer(GL_ARRAY_BUFFER, textCoordsVboId);
			glBufferData(GL_ARRAY_BUFFER, textCoordsBuffer, GL_STATIC_DRAW);
			glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

			// Index VBO
			idxVboId = glGenBuffers();
			indicesBuffer = MemoryUtil.memAllocInt(indices.length);
			indicesBuffer.put(indices).flip();
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, idxVboId);
			glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

			glBindBuffer(GL_ARRAY_BUFFER, 0);
			glBindVertexArray(0);
		} finally {
			if (posBuffer != null) {
				MemoryUtil.memFree(posBuffer);
			}
			if (textCoordsBuffer != null) {
				MemoryUtil.memFree(textCoordsBuffer);
			}
			if (indicesBuffer != null) {
				MemoryUtil.memFree(indicesBuffer);
			}
		}
	}

	public void render() {
		// Activate first texture unit
		glActiveTexture(GL_TEXTURE0);
		// Bind the Texture
		glBindTexture(GL_TEXTURE_2D, texture.getId());
		
		// Draw the mesh
		glBindVertexArray(vaoId);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		
		glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);
		
		// Restore state
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glBindVertexArray(0);
	}

	public int getVaoId() {
		return vaoId;
	}

	public int getVertexCount() {
		return vertexCount;
	}

	public void cleanUp() {
		glDisableVertexAttribArray(0);

		// Delete the VBOs
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glDeleteBuffers(posVboId);
		glDeleteBuffers(textCoordsVboId);
		glDeleteBuffers(idxVboId);

		// Delete the VAO
		glBindVertexArray(0);
		glDeleteVertexArrays(vaoId);
	}
}
