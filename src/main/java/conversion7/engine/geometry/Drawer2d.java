package conversion7.engine.geometry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import conversion7.engine.Gdxg;
import conversion7.game.Assets;

public class Drawer2d {

    public static void drawRect(int x, int y, int width, int height, int thickness) {
        Gdxg.spriteBatch.draw(Assets.pixelWhite, x, y, width, thickness);
        Gdxg.spriteBatch.draw(Assets.pixelWhite, x, y, thickness, height);
        Gdxg.spriteBatch.draw(Assets.pixelWhite, x, y + height - thickness, width, thickness);
        Gdxg.spriteBatch.draw(Assets.pixelWhite, x + width - thickness, y, thickness, height);
        Gdxg.spriteBatch.draw(Assets.pixelRed, 0, 0, 1, 1); // the last segment is not drawn without this trick
    }

    public static void drawLine(float x1, float y1, float x2, float y2, int thickness, Color color, Batch batch) {
        batch.setColor(color);
        float dx = x2 - x1;
        float dy = y2 - y1;
        int lineOrigin = thickness / 2;
        float angle = (float) Math.atan2(dy, dx) * MathUtils.radiansToDegrees;
        if (angle < 0) angle += 360;
        batch.draw(Assets.pixelWhite,
                x1, y1, lineOrigin, lineOrigin,
                Vector2.dst(x1, y1, x2, y2), thickness,
                1, 1, angle);
    }

    public static Texture getTextTexture(String text, int width, int height, BitmapFont font) {
        int border = 4;
        int padText = 2;
        FrameBuffer fb = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);
        fb.begin();
        Gdx.gl.glClearColor(0, 0, 0, 0.75f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdxg.spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
        Gdxg.spriteBatch.begin();
        Gdxg.spriteBatch.setColor(Color.valueOf("3A873F"));
        Drawer2d.drawRect(0, 0, width, height, border);
        font.setColor(Color.YELLOW);
        font.draw(Gdxg.spriteBatch, text, width / 3, height - border - padText);
        Gdxg.spriteBatch.end();
        fb.end();
        return fb.getColorBufferTexture();
    }
}
