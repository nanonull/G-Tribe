package conversion7.game.ui.world.main_panel.unit;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import conversion7.engine.custom2d.table.Panel;
import conversion7.game.Assets;
import conversion7.game.stages.world.objects.unit.AbstractSquad;

public class UnitAdditionalInfoPanel extends Panel {

    private Label taskLabel = new Label("", Assets.labelStyle14white2);
    private Label unitsHaveNoAPLabel = new Label("", Assets.labelStyle14white2);

    public UnitAdditionalInfoPanel() {

    }

    public void load(AbstractSquad squad) {
        clear();

        if (squad == null) {
            return;
        }

        if (squad.getActiveTask() != null) {
            taskLabel.setText("Task: " + squad.getActiveTask().getDescription());
            addLabel(taskLabel);
            row();
        }

        int unitsCouldMove = squad.canMove() ? 1 : 0;
        int unitsAll = 1;
        int unitsCouldNotMove = unitsAll - unitsCouldMove;

        if (unitsCouldNotMove > 0) {
            addLabel(unitsHaveNoAPLabel);
            row();
            if (unitsCouldNotMove == unitsAll) {
                unitsHaveNoAPLabel.setText("All units could not move");
            } else {
                unitsHaveNoAPLabel.setText(unitsAll - unitsCouldNotMove + "/" + unitsAll + " unit(s) could move");
            }
        }
    }
}
