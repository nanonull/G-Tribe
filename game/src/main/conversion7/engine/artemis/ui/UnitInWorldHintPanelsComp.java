package conversion7.engine.artemis.ui;

import com.artemis.Component;
import com.badlogic.gdx.graphics.Color;
import conversion7.game.stages.world.objects.unit.AbstractSquad;

public class UnitInWorldHintPanelsComp extends Component {
    public AbstractSquad squad;
    public boolean updateMainPanelData = true;
    public boolean updateIconBorderColor = false;
    public Color iconBorderColor;

    public void init(AbstractSquad squad, boolean updateMainPanelData, boolean updateIconBorderColor, Color iconBorderColor) {
        this.squad = squad;
        this.updateMainPanelData = updateMainPanelData;
        this.updateIconBorderColor = updateIconBorderColor;
        this.iconBorderColor = iconBorderColor;
    }
}
