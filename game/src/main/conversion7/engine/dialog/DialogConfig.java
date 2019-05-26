package conversion7.engine.dialog;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.google.gson.Gson;
import conversion7.engine.utils.Utils;
import conversion7.game.Assets;
import conversion7.game.GdxgConstants;
import conversion7.game.ui.ClientUi;

public class DialogConfig {

    public static final Label.LabelStyle SCENE_PHRASE_LABEL_STYLE = Assets.labelStyle14orange;
    public static final Label.LabelStyle SPEAKER_PHRASE_LABEL_STYLE = Assets.labelStyle14green;

    public static final int WINDOW_HEIGHT = GdxgConstants.SCREEN_HEIGHT_IN_PX;
    public static final int WINDOW_WIDTH = GdxgConstants.SCREEN_WIDTH_IN_PX;
    public static final float PICTURE_VIEW_WIDTH = WINDOW_WIDTH * 0.4f;
    public static final float WINDOW_WIDTH_WITHOUT_PICTURE_VIEW = WINDOW_WIDTH - PICTURE_VIEW_WIDTH;
    public static final float LABEL_WIDTH = WINDOW_WIDTH_WITHOUT_PICTURE_VIEW * 0.9f;
    public static final int DEFAULT_PAD = 2;
    public static final int BUTTON_HEIGHT = ClientUi.DEFAULT_BUTTON_HEIGHT;
    /** Body has extra 2 pixels and could make e.g. scrollbar appear */
    public static final int MAGIC_COMPENSATE_PAD = ClientUi.MAGIC_COMPENSATE_PAD;
    public static final int SCROLL_LINE_SIZE = ClientUi.SCROLL_LINE_SIZE;
    public static final Gson GSON = Utils.GSON;
}
