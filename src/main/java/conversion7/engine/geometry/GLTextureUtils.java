package conversion7.engine.geometry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.BufferUtils;

import java.nio.IntBuffer;

public class GLTextureUtils {
    private static final IntBuffer buffer = BufferUtils.newIntBuffer(1);

    protected static int createGLHandle() {
        buffer.position(0);
        buffer.limit(buffer.capacity());
        Gdx.gl.glGenTextures(1, buffer);
        return buffer.get(0);
    }

}
