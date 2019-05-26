package conversion7.engine.artemis.ui;

import com.artemis.Component;
import conversion7.game.stages.world.objects.unit.AbstractSquad;

public class SwitchSquadHighlightComp extends Component {
    public boolean toHighlight;
    public AbstractSquad squad;

    public void init(AbstractSquad squad, boolean toHighlight) {
        this.squad = squad;
        this.toHighlight = toHighlight;
    }
}
