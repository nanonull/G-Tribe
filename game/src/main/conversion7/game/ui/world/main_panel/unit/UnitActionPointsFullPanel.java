package conversion7.game.ui.world.main_panel.unit;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import conversion7.engine.custom2d.HBox;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.ui.ClientUi;
import conversion7.game.ui.hint.PopupHintPanel;
import conversion7.game.ui.world.ActionPointsIndicatorsPanel;

import static conversion7.game.Assets.*;


public class UnitActionPointsFullPanel extends HBox {

    private final Label apLbl = new Label("", labelStyle14orange2);
    ActionPointsIndicatorsPanel indicatorsPanel;

    public UnitActionPointsFullPanel() {
        addLabel("AP: ", labelStyle14white2);
        addLabel(apLbl).padRight(ClientUi.SPACING);
        indicatorsPanel = new ActionPointsIndicatorsPanel();
        add(indicatorsPanel).center();
    }

    public void load(AbstractSquad squad) {
        indicatorsPanel.updateAP(squad.getMoveAp(), squad.getAttackAp());
        PopupHintPanel.assignHintTo(this, squad.getCurrentApHint());
        show();
    }

}
