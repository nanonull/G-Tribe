package conversion7.game.ui.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import conversion7.engine.custom2d.HBox;
import conversion7.engine.custom2d.TextureRegionColoredDrawable;
import conversion7.game.Assets;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.ui.ClientUi;
import conversion7.game.ui.utils.UiUtils;

public class HealthWidgetBar extends WidgetGroup {

    public static final Label.LabelStyle POWER_LABEL_STYLE = Assets.labelStyle12_i_whiteAndLittleGreen;
    public static final Label.LabelStyle DEF_LBL_STYLE;
    private static final float POWER_LBL_PAD = -5;
    public static final Color DEF_COLOR = new Color(Color.CYAN);
    private final Label powerLabel;
    private final Label defLabel;
    HBox powerLayer;
    ProgressBar hpBar;

    static {
        DEF_COLOR.a = 0.65f;
        DEF_LBL_STYLE = new Label.LabelStyle(Assets.labelStyle12_i_whiteAndLittleGreen);
        DEF_LBL_STYLE.fontColor = Color.BLACK;
        DEF_LBL_STYLE.background = new TextureRegionColoredDrawable(
                DEF_COLOR, Assets.pixel);
    }

    public HealthWidgetBar() {
        HBox layer1 = new HBox();
        addActor(layer1);

        hpBar = new ProgressBar(0, 100, 1, false, UnitIconWithInfoPanel.HP_BAR_STYLE);
        layer1.add(hpBar)
                .height(UnitIconWithInfoPanel.HP_BAR_HEIGHT)
                .width(UnitIconWithInfoPanel.ICON_SIZE)
                .center().top();

        Label.LabelStyle hpLblStyle = new Label.LabelStyle(POWER_LABEL_STYLE);
        hpLblStyle.fontColor = Color.BLACK;

        powerLayer = new HBox();
        addActor(powerLayer);

        powerLabel = powerLayer.addLabel("hp", hpLblStyle)
                .width(UnitIconWithInfoPanel.ICON_SIZE).getActor();
        powerLayer.add().width(ClientUi.HALF_SPACING);
        defLabel = powerLayer.addLabel("def", DEF_LBL_STYLE).getActor();

    }


    public void load(AbstractSquad squad) {
        powerLabel.setText(UiUtils.getNumberCode(squad.getCurrentPower()) + "/"
                + UiUtils.getNumberCode(squad.getMaxPower()));
        int armor = squad.getTotalArmor();
        if (armor > 0) {
            defLabel.setText(UiUtils.getNumberWithSign(armor));
        } else {
            defLabel.setText("");
        }

        hpBar.setValue(squad.getPowerPercent());
        hpBar.invalidateHierarchy();
    }

}
