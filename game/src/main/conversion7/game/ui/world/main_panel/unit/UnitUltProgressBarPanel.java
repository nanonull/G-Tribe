package conversion7.game.ui.world.main_panel.unit;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import conversion7.engine.custom2d.HBox;
import conversion7.engine.custom2d.TextureRegionColoredDrawable;
import conversion7.game.Assets;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.ui.ClientUi;
import conversion7.game.ui.hint.PopupHintPanel;
import conversion7.game.ui.utils.UiUtils;
import conversion7.game.ui.world.UnitInWorldIndicatorIconsPanel;

public class UnitUltProgressBarPanel extends HBox {
    private static final Color BACK_COLOR_ON = UiUtils.alpha(0.3f, UnitInWorldIndicatorIconsPanel.ULT_COLOR);
    private static final Color BACK_COLOR_OFF = Color.CLEAR;

    private final ProgressBar progressBar;
    TextureRegionColoredDrawable backDrawable;
    Label ultLabel;

    public UnitUltProgressBarPanel() {
        PopupHintPanel.assignHintTo(this, "Inspired unit can use additional powerful skills");
        backDrawable = new TextureRegionColoredDrawable(BACK_COLOR_OFF, Assets.pixel);
        setBackground(backDrawable);
        ultLabel = addLabel("Inspiration: ", Assets.labelStyle14white2).getActor();
        progressBar = new ProgressBar(0, 100, 1, false, UnitParametersBasePanelType1.BAR_STYLE_YELLOW_PURPLE);
        addProgressBar(progressBar, ClientUi.SMALL_PROGRESS_BAR_WIDTH).padRight(ClientUi.SPACING);
    }

    public void load(AbstractSquad squad) {
        int ultPercent = squad.getInspirationPercent();
        progressBar.setValue(ultPercent);
        if (squad.hasUltReady()) {
            backDrawable.setColor(BACK_COLOR_ON);
//            ultLabel.setStyle(Assets.labelStyle14white2);
        } else {
            backDrawable.setColor(BACK_COLOR_OFF);
//            ultLabel.setStyle(Assets.labelStyle14orange);
        }
        show();
    }

}
