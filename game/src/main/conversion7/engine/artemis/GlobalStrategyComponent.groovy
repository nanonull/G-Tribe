package conversion7.engine.artemis

import com.artemis.Component
import conversion7.game.stages.world.team.Team
import groovy.transform.ToString

@ToString(includeFields = true, includeNames = true, includePackage = false)
public class GlobalStrategyComponent extends Component {
    public long onActiveFrame;
    public Team onActiveTeam;
    public boolean completeAiTeams
}
