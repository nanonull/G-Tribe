package conversion7.engine;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import conversion7.game.Assets;

public class TexturePackerMain {

    public static void main(String[] args) throws Exception {
        TexturePacker.Settings settings = new TexturePacker.Settings();
        settings.combineSubdirectories = true;
        settings.useIndexes = false;
        settings.filterMin = Texture.TextureFilter.MipMapLinearLinear;
        settings.filterMag = Texture.TextureFilter.Linear;
        settings.maxWidth = 2048;
        TexturePacker.process(settings, Assets.IMAGES_FOR_ATLAS_FOLDER, Assets.ATLASES_FOLDER, "images");
    }
}
