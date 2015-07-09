package conversion7.engine.custom2d;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class TiledTexture extends Actor {

    Texture tile;
    int tilesCountX;
    int tilesCountY;

    /**
     * Use wrapped texture for repeat
     */
    public TiledTexture(Texture tile, int tilesCountX, int tilesCountY) {
        this.tile = tile;
        this.tilesCountX = tilesCountX;
        this.tilesCountY = tilesCountY;

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

        batch.draw(tile, 0, 0,
                tile.getWidth() * tilesCountX,
                tile.getHeight() * tilesCountY,
                0, tilesCountY,
                tilesCountX, 0);

    }
}
