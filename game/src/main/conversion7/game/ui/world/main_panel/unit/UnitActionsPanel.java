package conversion7.game.ui.world.main_panel.unit;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;
import conversion7.engine.Gdxg;
import conversion7.engine.custom2d.table.Panel;
import conversion7.engine.utils.Utils;
import conversion7.game.Assets;
import conversion7.game.stages.world.objects.actions.AbstractAreaObjectAction;
import conversion7.game.stages.world.objects.actions.AbstractSquadAction;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.ui.ClientUi;
import conversion7.game.ui.hint.PopupHintPanel;
import org.slf4j.Logger;

public class UnitActionsPanel extends Panel {
    private static final Logger LOG = Utils.getLoggerForClass();
    public static final String ALL_ACTIONS_CURRENTLY_NOT_AVAILABLE_FOR_USE =
            "All actions, currently not available for use";

    private Array<AbstractAreaObjectAction> disabledActions = new Array<>();
    private Array<AbstractAreaObjectAction> enabledActions = new Array<>();

    public void load(AbstractSquad squad) {
        if (squad.getTeam() != squad.getTeam().world.activeTeam) {
            hide();
            return;
        }

        clear();
        enabledActions.clear();
        disabledActions.clear();

        TextButton button = new TextButton("More", Assets.uiSkin);
        add(button).growY().padRight(ClientUi.SPACING);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdxg.clientUi.getDisabledActionsPanel().showFor(disabledActions);
            }
        });
        PopupHintPanel.assignHintTo(button, ALL_ACTIONS_CURRENTLY_NOT_AVAILABLE_FOR_USE);

        OrderedMap<Class<? extends AbstractAreaObjectAction>,
                AbstractAreaObjectAction> squadActions = squad.getActions();
        for (Class<? extends AbstractAreaObjectAction> key : squadActions.orderedKeys()) {
            AbstractAreaObjectAction absAction = squadActions.get(key);
            AbstractSquadAction action = (AbstractSquadAction) absAction;
            if (action.active) {
                enabledActions.add(action);
            } else {
                disabledActions.add(action);
            }
        }

        UnitActionsRowPanel actionsRowPanel = new UnitActionsRowPanel();
        add(actionsRowPanel).center().growY();
        actionsRowPanel.load(enabledActions);
        actionsRowPanel.toFront();

        show();
    }

}
