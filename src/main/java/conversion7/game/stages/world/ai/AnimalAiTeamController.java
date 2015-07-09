package conversion7.game.stages.world.ai;

import conversion7.engine.utils.Utils;
import conversion7.game.GdxgConstants;
import conversion7.game.stages.world.team.Team;
import org.slf4j.Logger;

public class AnimalAiTeamController extends AiTeamController {

    private static final Logger LOG = Utils.getLoggerForClass();


    public AnimalAiTeamController(Team team) {
        super(team);
    }

    @Override
    public void ai() {
        if (LOG.isDebugEnabled()) LOG.debug("start Animal AI team " + team.getTeamId());
        if (GdxgConstants.AI_AREA_OBJECT_ENABLED) {
            stepObjectsGoals();
        }
    }

}
