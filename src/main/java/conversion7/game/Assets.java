package conversion7.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.AbsoluteFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.GdxRuntimeException;
import conversion7.engine.custom2d.TextureRegionColoredDrawable;
import conversion7.engine.utils.Utils;
import conversion7.game.classes.animals.oligocene.Amphicyonidae;
import conversion7.game.classes.animals.oligocene.Ekaltadeta;
import conversion7.game.classes.animals.oligocene.Eusmilus;
import conversion7.game.classes.animals.oligocene.Hyaenodon;
import conversion7.game.classes.animals.oligocene.Indricotherium;
import conversion7.game.classes.animals.oligocene.Mastodon;
import conversion7.game.classes.animals.oligocene.Mesohippus;
import conversion7.game.classes.animals.oligocene.Oreodontidae;
import conversion7.game.classes.animals.oligocene.Phorusrhacidae;
import conversion7.game.classes.animals.oligocene.Protoceras;
import conversion7.game.classes.animals.oligocene.Pyrotherium;
import conversion7.game.classes.animals.oligocene.Thylacoleo;
import conversion7.game.classes.australopitecus.Australopithecus;
import conversion7.game.classes.australopitecus.Paranthropus;
import conversion7.game.classes.theOldest.Ardipithecus;
import conversion7.game.classes.theOldest.ArdipithecusKadabba;
import conversion7.game.classes.theOldest.ArdipithecusRamidus;
import conversion7.game.classes.theOldest.Chororapithecus;
import conversion7.game.classes.theOldest.Dryopithecus;
import conversion7.game.classes.theOldest.Gorilla;
import conversion7.game.classes.theOldest.Orrorin;
import conversion7.game.classes.theOldest.OrrorinTugenensis;
import conversion7.game.classes.theOldest.Pan;
import conversion7.game.classes.theOldest.Pliopithecus;
import conversion7.game.classes.theOldest.Propliopithecus;
import conversion7.game.classes.theOldest.SahelanthropusTchadensis;
import conversion7.game.stages.battle.BattleSide;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.strings.CustomI18NBundle;
import conversion7.game.ui.UiLogger;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

import static com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;

public class Assets {

    private static final Logger LOG = Utils.getLoggerForClass();

    public static final String ICONS = "icons/";
    public static final String ANIMALS = "animals/";
    public static final String FONTS = "fonts/";

    public static final String RES_FOLDER = "resources/";
    public static final String STRINGS_FOLDER = RES_FOLDER + "strings/";
    public static final String MODELS_FOLDER = RES_FOLDER + "models/";
    public static final String ATLASES_FOLDER = RES_FOLDER + "atlases/";
    public static final String IMAGES_FOR_ATLAS_FOLDER = RES_FOLDER + "images-for-atlas/";
    public static final String FONTS_FOLDER = RES_FOLDER + FONTS;
    public static final String IMAGES_FOLDER = RES_FOLDER + "images/";
    public static final String PICTURES_FOLDER = IMAGES_FOLDER + "pictures/";
    public static final String SHADERS_FOLDER = RES_FOLDER + "shaders/";
    public static final String SHADERS_FABULA_FOLDER = SHADERS_FOLDER + "fabula/";
    public static final String IMAGES_ANIMALS_FOLDER = IMAGES_FOLDER + "animals/";
    public static final String IMAGES_ANIMALS_OLIGOCENE_FOLDER = IMAGES_ANIMALS_FOLDER + "oligocene/";

    public static final String WATER_REGIONS = "water";
    private static final String ASSET_NOT_LOADED_MSG = "Asset not loaded";

    public static BitmapFont font12_italic;
    public static BitmapFont font14;
    public static BitmapFont font18;
    public static BitmapFont font28;

    public static LabelStyle labelStyle12_i_lightGreen;
    public static LabelStyle labelStyle12_i_whiteAndLittleGreen;
    public static LabelStyle labelStyle12_i_black;
    public static LabelStyle labelStyle14_lightGreen;
    public static LabelStyle labelStyle14yellow;
    public static LabelStyle labelStyle14red;
    public static LabelStyle labelStyle14orange;
    public static LabelStyle labelStyle14green;
    public static LabelStyle labelStyle14_whiteAndLittleGreen;
    public static LabelStyle labelStyle14black;
    public static LabelStyle labelStyle14blackWithBackground;
    public static LabelStyle labelStyle18yellow;

    public static TextureRegion pixelRed;
    public static TextureRegion pixelWhite;

    public static TextureRegion glow;
    public static TextureRegion temperature;
    public static TextureRegion apple;
    public static TextureRegion blob;
    public static TextureRegion eyeGreen;
    public static TextureRegion eyeRed;
    public static TextureRegion eyeGreenRed;
    public static TextureRegion armyIcon;
    public static TextureRegion homeIcon;
    public static TextureRegion patrolIcon;
    public static TextureRegion exploreIcon;
    public static TextureRegion followIcon;
    public static TextureRegion swordIcon;

    public static TextureRegion grass;
    public static TextureRegion sand;
    public static TextureRegion stone;
    public static TextureRegion mountain;
    public static TextureRegion water;

    public static final Map<BattleSide, TextureRegion> ACTOR_TEXTURES = new HashMap<>();
    public static final Map<Class<? extends Unit>, TextureRegion> CLASS_ICONS = new HashMap<>();
    public static final Map<Class<? extends Unit>, TextureRegion> ANIMAL_IMAGES = new HashMap<>();
    public static Skin uiSkin;
    public static TextureAtlas imagesAtlas;
    public static TextureAtlas liquidAtlas;

    public static AssetManager manager;
    public static CustomI18NBundle textResources;

    public static void loadAll() {
        loadDefaultSkin();
        loadManagedAssets();
        loadDefaultGraphic();

        FileHandle liquidAtlasHandle = Gdx.files.internal(ATLASES_FOLDER + "liquid.atlas");
        LOG.info("liquidAtlasHandle = " + liquidAtlasHandle);
        liquidAtlas = new TextureAtlas(liquidAtlasHandle);

        loadGameTextures();
        registerFonts();
        textResources = new CustomI18NBundle(Assets.STRINGS_FOLDER + "/text");
    }

    private static void loadGameTextures() {
        TextureRegion textureRegion;

        glow = new TextureRegion(imagesAtlas.findRegion("glow"));
        temperature = new TextureRegion(imagesAtlas.findRegion(ICONS + "temperature"));
        apple = new TextureRegion(imagesAtlas.findRegion(ICONS + "apple"));
        blob = new TextureRegion(imagesAtlas.findRegion(ICONS + "blob"));
        eyeGreen = new TextureRegion(imagesAtlas.findRegion(ICONS + "eye_green"));
        eyeRed = new TextureRegion(imagesAtlas.findRegion(ICONS + "eye_red"));
        eyeGreenRed = new TextureRegion(imagesAtlas.findRegion(ICONS + "eye_green_red"));
        armyIcon = new TextureRegion(imagesAtlas.findRegion(ICONS + "army"));
        homeIcon = new TextureRegion(imagesAtlas.findRegion(ICONS + "home"));
        patrolIcon = new TextureRegion(imagesAtlas.findRegion(ICONS + "patrol"));
        exploreIcon = new TextureRegion(imagesAtlas.findRegion(ICONS + "explore"));
        followIcon = new TextureRegion(imagesAtlas.findRegion(ICONS + "follow"));
        swordIcon = new TextureRegion(imagesAtlas.findRegion(ICONS + "sword"));

        // sun textures
        ACTOR_TEXTURES.put(BattleSide.LEFT, new TextureRegion(imagesAtlas.findRegion("sun")));
        ACTOR_TEXTURES.put(BattleSide.RIGHT, new TextureRegion(imagesAtlas.findRegion("sun_red")));
        ACTOR_TEXTURES.put(BattleSide.UP, new TextureRegion(imagesAtlas.findRegion("sun_blue")));
        ACTOR_TEXTURES.put(BattleSide.DOWN, new TextureRegion(imagesAtlas.findRegion("sun_green")));

        //land
        grass = new TextureRegion(imagesAtlas.findRegion("grass_128"));
        sand = new TextureRegion(imagesAtlas.findRegion("sand_128"));
        stone = new TextureRegion(imagesAtlas.findRegion("stone_128"));
        mountain = new TextureRegion(imagesAtlas.findRegion("mountain_64"));
        water = new TextureRegion(imagesAtlas.findRegion("water_128"));

        // unit icons
        textureRegion = new TextureRegion(imagesAtlas.findRegion(ICONS + "dryopitec_32"));
        CLASS_ICONS.put(Ardipithecus.class, textureRegion);
        CLASS_ICONS.put(ArdipithecusKadabba.class, textureRegion);
        CLASS_ICONS.put(ArdipithecusRamidus.class, textureRegion);
        CLASS_ICONS.put(Chororapithecus.class, textureRegion);
        CLASS_ICONS.put(Dryopithecus.class, textureRegion);
        CLASS_ICONS.put(Gorilla.class, textureRegion);
        CLASS_ICONS.put(Orrorin.class, textureRegion);
        CLASS_ICONS.put(OrrorinTugenensis.class, textureRegion);
        CLASS_ICONS.put(Pan.class, textureRegion);
        CLASS_ICONS.put(Pliopithecus.class, textureRegion);
        CLASS_ICONS.put(Propliopithecus.class, textureRegion);
        CLASS_ICONS.put(SahelanthropusTchadensis.class, textureRegion);

        textureRegion = new TextureRegion(imagesAtlas.findRegion(ICONS + "Australopithecus_32"));
        CLASS_ICONS.put(Australopithecus.class, textureRegion);
        CLASS_ICONS.put(Paranthropus.class, textureRegion);

        // animal icons
        CLASS_ICONS.put(Amphicyonidae.class, new TextureRegion(imagesAtlas.findRegion(ICONS + ANIMALS + "Amphicyonidae")));
        CLASS_ICONS.put(Ekaltadeta.class, new TextureRegion(imagesAtlas.findRegion(ICONS + ANIMALS + "Ekaltadeta")));
        CLASS_ICONS.put(Eusmilus.class, new TextureRegion(imagesAtlas.findRegion(ICONS + ANIMALS + "Eusmilus")));
        CLASS_ICONS.put(Hyaenodon.class, new TextureRegion(imagesAtlas.findRegion(ICONS + ANIMALS + "Hyaenodon")));
        CLASS_ICONS.put(Indricotherium.class, new TextureRegion(imagesAtlas.findRegion(ICONS + ANIMALS + "Indricotherium")));
        CLASS_ICONS.put(Mastodon.class, new TextureRegion(imagesAtlas.findRegion(ICONS + ANIMALS + "Mastodon")));
        CLASS_ICONS.put(Mesohippus.class, new TextureRegion(imagesAtlas.findRegion(ICONS + ANIMALS + "Mesohippus")));
        CLASS_ICONS.put(Oreodontidae.class, new TextureRegion(imagesAtlas.findRegion(ICONS + ANIMALS + "Oreodontidae")));
        CLASS_ICONS.put(Phorusrhacidae.class, new TextureRegion(imagesAtlas.findRegion(ICONS + ANIMALS + "Phorusrhacidae")));
        CLASS_ICONS.put(Protoceras.class, new TextureRegion(imagesAtlas.findRegion(ICONS + ANIMALS + "Protoceras")));
        CLASS_ICONS.put(Pyrotherium.class, new TextureRegion(imagesAtlas.findRegion(ICONS + ANIMALS + "Pyrotherium")));
        CLASS_ICONS.put(Thylacoleo.class, new TextureRegion(imagesAtlas.findRegion(ICONS + ANIMALS + "Thylacoleo")));

        // animal images
        ANIMAL_IMAGES.put(Amphicyonidae.class, new TextureRegion(new Texture(Gdx.files.internal(IMAGES_ANIMALS_OLIGOCENE_FOLDER + "Amphicyonidae.png"))));
        ANIMAL_IMAGES.put(Ekaltadeta.class, new TextureRegion(new Texture(Gdx.files.internal(IMAGES_ANIMALS_OLIGOCENE_FOLDER + "Ekaltadeta.png"))));
        ANIMAL_IMAGES.put(Eusmilus.class, new TextureRegion(new Texture(Gdx.files.internal(IMAGES_ANIMALS_OLIGOCENE_FOLDER + "Eusmilus.png"))));
        ANIMAL_IMAGES.put(Hyaenodon.class, new TextureRegion(new Texture(Gdx.files.internal(IMAGES_ANIMALS_OLIGOCENE_FOLDER + "Hyaenodon.png"))));
        ANIMAL_IMAGES.put(Indricotherium.class, new TextureRegion(new Texture(Gdx.files.internal(IMAGES_ANIMALS_OLIGOCENE_FOLDER + "Indricotherium.png"))));
        ANIMAL_IMAGES.put(Mastodon.class, new TextureRegion(new Texture(Gdx.files.internal(IMAGES_ANIMALS_OLIGOCENE_FOLDER + "Mastodon.png"))));
        ANIMAL_IMAGES.put(Mesohippus.class, new TextureRegion(new Texture(Gdx.files.internal(IMAGES_ANIMALS_OLIGOCENE_FOLDER + "Mesohippus.png"))));
        ANIMAL_IMAGES.put(Oreodontidae.class, new TextureRegion(new Texture(Gdx.files.internal(IMAGES_ANIMALS_OLIGOCENE_FOLDER + "Oreodontidae.png"))));
        ANIMAL_IMAGES.put(Phorusrhacidae.class, new TextureRegion(new Texture(Gdx.files.internal(IMAGES_ANIMALS_OLIGOCENE_FOLDER + "Phorusrhacidae.png"))));
        ANIMAL_IMAGES.put(Protoceras.class, new TextureRegion(new Texture(Gdx.files.internal(IMAGES_ANIMALS_OLIGOCENE_FOLDER + "Protoceras.png"))));
        ANIMAL_IMAGES.put(Pyrotherium.class, new TextureRegion(new Texture(Gdx.files.internal(IMAGES_ANIMALS_OLIGOCENE_FOLDER + "Pyrotherium.png"))));
        ANIMAL_IMAGES.put(Thylacoleo.class, new TextureRegion(new Texture(Gdx.files.internal(IMAGES_ANIMALS_OLIGOCENE_FOLDER + "Thylacoleo.png"))));
    }

    public static void loadDefaultGraphic() {
        FileHandle imagesAtlasHandle = Gdx.files.internal(ATLASES_FOLDER + "images.atlas");
        LOG.info("imagesAtlasHandle = " + imagesAtlasHandle);
        imagesAtlas = new TextureAtlas(imagesAtlasHandle);

        pixelRed = new TextureRegion(imagesAtlas.findRegion("1px_red"));
        pixelWhite = new TextureRegion(imagesAtlas.findRegion("1px_white"));
    }

    public static void loadDefaultSkin() {
        uiSkin = new Skin(Gdx.files.internal(ATLASES_FOLDER + "uiskin.json"));
        changeFontSpaceX(uiSkin.getFont("default-font").getData(), 0.77f);
    }

    public static void loadManagedAssets() {
        manager = new AssetManager(new AbsoluteFileHandleResolver());
        manager.load(MODELS_FOLDER + "knight.g3db", Model.class);
        manager.load(MODELS_FOLDER + "tree.g3db", Model.class);
        manager.load(MODELS_FOLDER + "stone.g3db", Model.class);

        for (PictureAsset pictureAsset : PictureAsset.values()) {
            manager.load(PICTURES_FOLDER + pictureAsset.getFileName(), Texture.class);
        }

        manager.finishLoading();
    }

    /** Define new model in {@link Assets#loadAll()} */
    public static Model getModel(String name) {
        return manager.get(MODELS_FOLDER + name + ".g3db", Model.class);
    }

    public static Texture getPicture(PictureAsset pictureAsset) {
        Texture texture = null;
        try {
            texture = manager.get(PICTURES_FOLDER + pictureAsset.getFileName(), Texture.class);
        } catch (GdxRuntimeException e) {
            if (e.getMessage().contains(ASSET_NOT_LOADED_MSG)) {
                UiLogger.addErrorLabel(ASSET_NOT_LOADED_MSG + ": " + pictureAsset);
            } else {
                throw new GdxRuntimeException("Error during getPicture: " + e.getMessage(), e);
            }
        }
        return texture;
    }

    public static final Color LIGHT_GREEN = Color.valueOf("94ed7e");
    public static final Color WHITE_WITH_INVISIBLE_GREEN = Color.valueOf("ecfeec");
    public static final Color LIGHT_YELLOW = Color.valueOf("fffeeb");
    public static final Color LIGHT_GRAY = Color.valueOf("d3d2c5");
    public static final Color RED = Color.valueOf("fa0a00");

    public static void registerFonts() {

        font12_italic = new BitmapFont(Gdx.files.internal(FONTS_FOLDER + "Consolas_12_italic.fnt"),
                new TextureRegion(imagesAtlas.findRegion(FONTS + "Consolas_12_italic")), false);

        font14 = new BitmapFont(Gdx.files.internal(FONTS_FOLDER + "Consolas_14.fnt"),
                new TextureRegion(imagesAtlas.findRegion(FONTS + "Consolas_14")), false);
        changeFontSpaceX(font14.getData(), 0.75f);

        font18 = new BitmapFont(Gdx.files.internal(FONTS_FOLDER + "Consolas_18.fnt"),
                new TextureRegion(imagesAtlas.findRegion(FONTS + "Consolas_18")), false);

        font28 = new BitmapFont(Gdx.files.internal(FONTS_FOLDER + "consolas_28.fnt"),
                new TextureRegion(imagesAtlas.findRegion(FONTS + "consolas_28")), false);

        labelStyle12_i_lightGreen = new LabelStyle(font12_italic, LIGHT_GREEN);
        labelStyle12_i_whiteAndLittleGreen = new LabelStyle(font12_italic, WHITE_WITH_INVISIBLE_GREEN);
        labelStyle12_i_black = new LabelStyle(font12_italic, Color.BLACK);
        labelStyle14_lightGreen = new LabelStyle(font14, LIGHT_GREEN);
        labelStyle14yellow = new LabelStyle(font14, Color.YELLOW);
        labelStyle14red = new LabelStyle(font14, RED);
        labelStyle14orange = new LabelStyle(font14, Color.ORANGE);
        labelStyle14green = new LabelStyle(font14, Color.GREEN);
        labelStyle14_whiteAndLittleGreen = new LabelStyle(font14, WHITE_WITH_INVISIBLE_GREEN);
        labelStyle14black = new LabelStyle(font14, Color.BLACK);
        labelStyle14blackWithBackground = new LabelStyle(font14, Color.BLACK);
        labelStyle14blackWithBackground.background = new TextureRegionColoredDrawable(
                Color.ORANGE, Assets.pixelWhite);
        labelStyle18yellow = new LabelStyle(font18, Color.YELLOW);

    }

    public static void changeFontSpaceX(BitmapFont.BitmapFontData data, float multipleOn) {
        for (BitmapFont.Glyph[] glyphs : data.glyphs) {
            if (glyphs != null) {
                for (BitmapFont.Glyph glyph : glyphs) {
                    if (glyph != null) {
                        glyph.xadvance *= multipleOn;
                    }
                }
            }
        }
    }

}
