package conversion7.engine.custom2d;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class TextureRegionColoredDrawable extends TextureRegionDrawable {

    private Color color;

    public TextureRegionColoredDrawable(Color color, TextureRegionDrawable textureRegionDrawable) {
        super(textureRegionDrawable);
        this.color = color;
    }

    public TextureRegionColoredDrawable(Color color, TextureRegion textureRegion) {
        this(color, new TextureRegionDrawable(textureRegion));
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public void draw(Batch batch, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation) {
        batch.setColor(color);
        super.draw(batch, x, y, originX, originY, width, height, scaleX, scaleY, rotation);
    }

    @Override
    public void draw(Batch batch, float x, float y, float width, float height) {
        batch.setColor(color);
        super.draw(batch, x, y, width, height);
    }
}
