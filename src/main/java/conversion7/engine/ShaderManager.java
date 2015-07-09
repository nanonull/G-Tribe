package conversion7.engine;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.bitfire.utils.ShaderLoader;

@Deprecated
public class ShaderManager {
    protected ShaderProgram currentShader = null;
    public String currentShaderIdn = null;
    private ObjectMap<String, ShaderProgram> shaders = new ObjectMap<>();
    public int currentTextureId = 0;

    public ShaderProgram getCurrent() {
        if (currentShader != null)
            return currentShader;
        throw new GdxRuntimeException("No current shader set in ShaderManager!");
    }

    public int getCurrentTextureId() {
        return currentTextureId++;
    }

    public ShaderProgram get(String name) {
        if (shaders.containsKey(name))
            return shaders.get(name);
        throw new GdxRuntimeException("No shader named '" + name + "' in ShaderManager!");
    }

    public void add(String shaderName) {
        ShaderProgram shaderProgram = ShaderLoader.fromFile(shaderName, shaderName);
        shaders.put(shaderName, shaderProgram);
    }

    /**
     * Call this to start rendering using given shader.
     */
    public ShaderProgram begin(String shadIdn) {
        if (currentShader != null)
            throw new IllegalArgumentException("Previous shader '" + currentShaderIdn + "' not finished! Call end() before another begin().");

        ShaderProgram res = get(shadIdn);
        if (res != null) {
            currentShader = res;
            currentShaderIdn = shadIdn;
            currentTextureId = 0;
            res.begin();
        } else {
            throw new IllegalArgumentException("Shader '" + shadIdn + "' not found!");
        }
        return res;
    }

    /**
     * Call this to finish rendering using current shader.
     */
    public void end() {
        if (currentShader != null) {
            currentShader.end();
            currentShader = null;
            currentShaderIdn = null;
        }
    }

    /* passing uniforms to current shader */

    /**
     * Sets the given attribute
     *
     * @param name   the name of the attribute
     * @param value1 the first value
     * @param value2 the second value
     * @param value3 the third value
     * @param value4 the fourth value
     */
    public ShaderProgram setAttributef(String name, float value1, float value2,
                                       float value3, float value4) {
        if (currentShader != null) {
            currentShader.setAttributef(name, value1, value2, value3, value4);
            return currentShader;
        } else
            throw new IllegalArgumentException(
                    "Can't set uniform before calling begin()!");
    }

    public ShaderProgram setUniform1fv(String name, float[] values, int offset,
                                       int length) {
        if (currentShader != null) {
            currentShader.setUniform1fv(name, values, offset, length);
            return currentShader;
        } else
            throw new IllegalArgumentException(
                    "Can't set uniform before calling begin()!");
    }

    public ShaderProgram setUniform2fv(String name, float[] values, int offset,
                                       int length) {
        if (currentShader != null) {
            currentShader.setUniform2fv(name, values, offset, length);
            return currentShader;
        } else
            throw new IllegalArgumentException(
                    "Can't set uniform before calling begin()!");
    }

    public ShaderProgram setUniform3fv(String name, float[] values, int offset,
                                       int length) {
        if (currentShader != null) {
            currentShader.setUniform3fv(name, values, offset, length);
            return currentShader;
        } else
            throw new IllegalArgumentException(
                    "Can't set uniform before calling begin()!");
    }

    public ShaderProgram setUniform4fv(String name, float[] values, int offset,
                                       int length) {
        if (currentShader != null) {
            currentShader.setUniform4fv(name, values, offset, length);
            return currentShader;
        } else
            throw new IllegalArgumentException(
                    "Can't set uniform before calling begin()!");
    }

    /**
     * Sets the uniform with the given name.
     *
     * @param name  the name of the uniform
     * @param value the value
     */
    public ShaderProgram setUniformf(String name, float value) {
        if (currentShader != null) {
            currentShader.setUniformf(name, value);
            return currentShader;
        } else
            throw new IllegalArgumentException(
                    "Can't set uniform before calling begin()!");
    }

    /**
     * Sets the uniform with the given name.
     *
     * @param name   the name of the uniform
     * @param value1 the first value
     * @param value2 the second value
     */
    public ShaderProgram setUniformf(String name, float value1, float value2) {
        if (currentShader != null) {
            currentShader.setUniformf(name, value1, value2);
            return currentShader;
        } else
            throw new IllegalArgumentException(
                    "Can't set uniform before calling begin()!");
    }

    /**
     * Sets the uniform with the given name.
     *
     * @param name   the name of the uniform
     * @param value1 the first value
     * @param value2 the second value
     * @param value3 the third value
     */
    public ShaderProgram setUniformf(String name, float value1, float value2,
                                     float value3) {
        if (currentShader != null) {
            currentShader.setUniformf(name, value1, value2, value3);
            return currentShader;
        } else
            throw new IllegalArgumentException(
                    "Can't set uniform before calling begin()!");
    }

    /**
     * Sets the uniform with the given name.
     *
     * @param name   the name of the uniform
     * @param value1 the first value
     * @param value2 the second value
     * @param value3 the third value
     * @param value4 the fourth value
     */
    public ShaderProgram setUniformf(String name, float value1, float value2,
                                     float value3, float value4) {
        if (currentShader != null) {
            currentShader.setUniformf(name, value1, value2, value3, value4);
            return currentShader;
        } else
            throw new IllegalArgumentException(
                    "Can't set uniform before calling begin()!");
    }

    /**
     * Sets the uniform with the given name.
     *
     * @param name  the name of the uniform
     * @param value the value
     */
    public ShaderProgram setUniformi(String name, int value) {
        if (currentShader != null) {
            currentShader.setUniformi(name, value);
            return currentShader;
        } else
            throw new IllegalArgumentException(
                    "Can't set uniform before calling begin()!");
    }

    /**
     * Sets the uniform with the given name.
     *
     * @param name   the name of the uniform
     * @param value1 the first value
     * @param value2 the second value
     */
    public ShaderProgram setUniformi(String name, int value1, int value2) {
        if (currentShader != null) {
            currentShader.setUniformi(name, value1, value2);
            return currentShader;
        } else
            throw new IllegalArgumentException(
                    "Can't set uniform before calling begin()!");
    }

    /**
     * Sets the uniform with the given name.
     *
     * @param name   the name of the uniform
     * @param value1 the first value
     * @param value2 the second value
     * @param value3 the third value
     */
    public ShaderProgram setUniformi(String name, int value1, int value2, int value3) {
        if (currentShader != null) {
            currentShader.setUniformi(name, value1, value2, value3);
            return currentShader;
        } else
            throw new IllegalArgumentException(
                    "Can't set uniform before calling begin()!");
    }

    /**
     * Sets the uniform with the given name.
     *
     * @param name   the name of the uniform
     * @param value1 the first value
     * @param value2 the second value
     * @param value3 the third value
     * @param value4 the fourth value
     */
    public ShaderProgram setUniformi(String name, int value1, int value2, int value3,
                                     int value4) {
        if (currentShader != null) {
            currentShader.setUniformi(name, value1, value2, value3, value4);
            return currentShader;
        } else
            throw new IllegalArgumentException(
                    "Can't set uniform before calling begin()!");
    }

    /**
     * Sets the uniform matrix with the given name.
     *
     * @param name   the name of the uniform
     * @param matrix the matrix
     */
    public ShaderProgram setUniformMatrix(String name, Matrix3 matrix) {
        if (currentShader != null) {
            currentShader.setUniformMatrix(name, matrix);
            return currentShader;
        } else
            throw new IllegalArgumentException(
                    "Can't set uniform before calling begin()!");
    }

    /**
     * Sets the uniform matrix with the given name.
     *
     * @param name      the name of the uniform
     * @param matrix    the matrix
     * @param transpose whether the uniform matrix should be transposed
     */
    public ShaderProgram setUniformMatrix(String name, Matrix3 matrix,
                                          boolean transpose) {
        if (currentShader != null) {
            currentShader.setUniformMatrix(name, matrix, transpose);
            return currentShader;
        } else
            throw new IllegalArgumentException(
                    "Can't set uniform before calling begin()!");
    }

    /**
     * Sets the uniform matrix with the given name.
     *
     * @param name   the name of the uniform
     * @param matrix the matrix
     */
    public ShaderProgram setUniformMatrix(String name, Matrix4 matrix) {
        if (currentShader != null) {
            currentShader.setUniformMatrix(name, matrix);
            return currentShader;
        } else
            throw new IllegalArgumentException(
                    "Can't set uniform before calling begin()!");
    }

    /**
     * Sets the uniform matrix with the given name.
     *
     * @param name      the name of the uniform
     * @param matrix    the matrix
     * @param transpose whether the matrix should be transposed
     */
    public ShaderProgram setUniformMatrix(String name, Matrix4 matrix,
                                          boolean transpose) {
        if (currentShader != null) {
            currentShader.setUniformMatrix(name, matrix, transpose);
            return currentShader;
        } else
            throw new IllegalArgumentException(
                    "Can't set uniform before calling begin()!");
    }

    /**
     * Sets the uniform with the given name, automatically binding the texture to a number.
     *
     * @param name the name of the uniform
     */
    public ShaderProgram setUniformTexture(String name, Texture value) {
        if (currentShader != null) {
            int texId = getCurrentTextureId();
            value.bind(texId);
            currentShader.setUniformi(name, texId);
            return currentShader;
        } else
            throw new IllegalArgumentException(
                    "Can't set uniform before calling begin()!");
    }

    /**
     * Sets the vertex attribute with the given name.
     *
     * @param name      the attribute name
     * @param size      the number of components, must be >= 1 and <= 4
     * @param type      the type, must be one of GL20.GL_BYTE, GL20.GL_UNSIGNED_BYTE, GL20.GL_SHORT,
     *                  GL20.GL_UNSIGNED_SHORT,GL20.GL_FIXED, or GL20.GL_FLOAT. GL_FIXED will not work on the desktop
     * @param normalize whether fixed point data should be normalized. Will not work on the desktop
     * @param stride    the stride in bytes between successive attributes
     * @param offset    byte offset into the vertex buffer object bound to GL20.GL_ARRAY_BUFFER.
     */
    public ShaderProgram setVertexAttribute(String name, int size, int type,
                                            boolean normalize, int stride, int offset) {
        if (currentShader != null) {
            currentShader.setVertexAttribute(name, size, type, normalize,
                    stride, offset);
            return currentShader;
        } else
            throw new IllegalArgumentException(
                    "Can't set uniform before calling begin()!");
    }

}
