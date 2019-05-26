package conversion7.game.ui.world;

import com.badlogic.gdx.utils.Array;
import conversion7.engine.custom2d.TextureRegionColoredDrawable;
import conversion7.engine.custom2d.VBox;
import conversion7.game.Assets;
import conversion7.game.stages.world.objects.actions.AbstractAreaObjectAction;
import conversion7.game.ui.ClientUi;
import conversion7.game.ui.world.main_panel.unit.UnitActionsPanel;
import conversion7.game.ui.world.main_panel.unit.UnitActionsRowPanel;

public class DisabledActionsPanel extends VBox {
    UnitActionsRowPanel actionsRowPanel = new UnitActionsRowPanel();

    public DisabledActionsPanel() {
        setBackground(new TextureRegionColoredDrawable(ClientUi.PANEL_COLOR, Assets.pixel));
        pad(ClientUi.SPACING);
    }

    public void showFor(Array<AbstractAreaObjectAction> disabledActions) {
        clear();
        actionsRowPanel.clear();
        addSmallCloseButton();

        addLabel(UnitActionsPanel.ALL_ACTIONS_CURRENTLY_NOT_AVAILABLE_FOR_USE, Assets.labelStyle14white2);
        add(actionsRowPanel).grow();
        actionsRowPanel.horizontalGroups = false;
        actionsRowPanel.enableActions = false;
        actionsRowPanel.load(disabledActions);

        setX(50);
        setY(350);
        pack();
        show();
    }


}
