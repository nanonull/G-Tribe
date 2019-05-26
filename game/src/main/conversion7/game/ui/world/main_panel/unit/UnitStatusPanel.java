package conversion7.game.ui.world.main_panel.unit;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import conversion7.engine.custom2d.VBox;
import conversion7.game.Assets;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import org.apache.commons.lang3.StringUtils;

public class UnitStatusPanel extends VBox {

    Label nameLabel = new Label("", Assets.labelStyle14blackWithBackground);
    private final Label statusLbl = new Label("-", Assets.labelStyle14blackWithBackground);

    public UnitStatusPanel() {
        addLabel(statusLbl).padBottom(UnitParametersBasePanelType1.SPACE);
        addLabel(nameLabel);
    }

    public void load(AbstractSquad squad) {
        StringBuilder unitMainDescription = new StringBuilder()
                .append(squad.getGenderUi());
        String exceptionalStatusHint = squad.getExceptionalStatusHint();
        if (!StringUtils.isEmpty(exceptionalStatusHint)) {
            unitMainDescription.append(",").append(exceptionalStatusHint);
        }
        statusLbl.setText(unitMainDescription);
        nameLabel.setText(squad.getFullName());
        show();
    }

}
