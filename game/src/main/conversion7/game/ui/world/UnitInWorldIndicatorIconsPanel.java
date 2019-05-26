package conversion7.game.ui.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import conversion7.engine.custom2d.HBox;
import conversion7.engine.custom2d.TextureRegionColoredDrawable;
import conversion7.game.Assets;
import conversion7.game.stages.world.unit.hero_classes.HeroClass;
import conversion7.game.ui.utils.UiUtils;

public class UnitInWorldIndicatorIconsPanel extends HBox {

    private static final float ICON_WIDTH = 9;
    private static final float ICON_HEIGHT = UiUtils.getFontHeight(Assets.font14);
    private static final float FIX_ICON_PAD = -1;
    public static final Color ULT_COLOR = Color.YELLOW;
    public final static Label.LabelStyle heroStyle;
    public final static Label.LabelStyle ultStyle;
    public final static Label.LabelStyle visibilityStyle;
    public final static Label.LabelStyle shamanStyle;
    private static final Color VISIBLE_COLOR = UiUtils.alpha(0.4f, Color.BLUE);
    private static final Color SHAMAN_COLOR = UiUtils.alpha(0.8f, Color.RED);
    private Label heroIndicator;
    private Label ultIndicator;
    private Label visibilityIndicator;
    private Label shamanIndicator;

    static {
        BitmapFont font = Assets.font14;
        heroStyle = new Label.LabelStyle(font, Color.BLACK);
        heroStyle.background = new TextureRegionColoredDrawable(
                Color.ORANGE, Assets.pixel);

        ultStyle = new Label.LabelStyle(font, Color.BLACK);
        ultStyle.background = new TextureRegionColoredDrawable(
                ULT_COLOR, Assets.pixel);

        visibilityStyle = new Label.LabelStyle(font, Color.WHITE);
        visibilityStyle.background = new TextureRegionColoredDrawable(
                VISIBLE_COLOR, Assets.pixel);

        shamanStyle = new Label.LabelStyle(font, Color.WHITE);
        shamanStyle.background = new TextureRegionColoredDrawable(
                SHAMAN_COLOR, Assets.pixel);
    }

    public UnitInWorldIndicatorIconsPanel() {
        center().pad(0);
        defaults().center().space(0).pad(0)/*.padTop(1).padBottom(1)*/
                .grow().center();

        float scale = 0.95f;
        heroIndicator = addLabel("H", heroStyle).getActor();
        heroIndicator.setVisible(false);
        heroIndicator.setFontScale(scale);

        visibilityIndicator = addLabel("V", visibilityStyle).getActor();
        visibilityIndicator.setVisible(false);
        visibilityIndicator.setFontScale(scale);

        shamanIndicator = addLabel("S", shamanStyle).getActor();
        shamanIndicator.setVisible(false);
        shamanIndicator.setFontScale(scale);

        ultIndicator = addLabel("U", ultStyle).getActor();
        ultIndicator.setVisible(false);
        ultIndicator.setFontScale(scale);
    }

    public Label getHeroIndicator() {
        return heroIndicator;
    }

    public void setHeroIndicator(HeroClass heroClass) {
        if (heroClass == null) {
            heroIndicator.setVisible(false);
        } else {
            heroIndicator.setText(heroClass.get1stSymbol());
            heroIndicator.setVisible(true);
        }

    }

    public Label getUltIndicator() {
        return ultIndicator;
    }

    public void setUltIndicator(boolean status) {
        ultIndicator.setVisible(status);
    }

    public Label getVisibilityIndicator() {
        return visibilityIndicator;
    }

    public Label getShamanIndicator() {
        return shamanIndicator;
    }

    public void setShamanIndicator(boolean status) {
        shamanIndicator.setVisible(status);
    }

    public void setIsVisible(boolean isVisible) {
        visibilityIndicator.setVisible(isVisible);
    }

    public void resetCellSelectionDependentIndicators() {
        visibilityIndicator.setVisible(false);
    }
}
