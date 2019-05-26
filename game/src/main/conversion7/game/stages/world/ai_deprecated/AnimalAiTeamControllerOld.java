package conversion7.game.stages.world.ai_deprecated;

import conversion7.engine.utils.Utils;
import conversion7.game.GdxgConstants;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.team.Team;
import org.slf4j.Logger;

import java.util.Comparator;

public class AnimalAiTeamControllerOld extends AiTeamControllerOld {

    public static final Comparator<? super AbstractSquad> AI_ACT_ORDER_COMPARATOR = (o1, o2) -> {
        int res = Integer.compare(o1.getHadAiActAtStep(), o2.getHadAiActAtStep());
        if (res == 0) {
            res = Integer.compare(o1.getId(), o2.getId());
        }
        return res;
    };
    private static final Logger LOG = Utils.getLoggerForClass();

    @Deprecated
    public AnimalAiTeamControllerOld(Team team) {
        super(team);
    }

    @Override
    public void ai() {
        if (LOG.isDebugEnabled()) LOG.debug("start Animal AI team " + team.getTeamId());
        if (GdxgConstants.AREA_OBJECT_AI) {
            stepObjectsGoals();
        }
    }

}
