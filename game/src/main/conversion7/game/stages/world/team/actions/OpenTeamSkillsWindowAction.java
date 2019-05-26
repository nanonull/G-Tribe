package conversion7.game.stages.world.team.actions;

import conversion7.engine.Gdxg;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.team.Team;
import org.slf4j.Logger;

public class OpenTeamSkillsWindowAction extends AbstractTeamAction {

    private static final Logger LOG = Utils.getLoggerForClass();

    public OpenTeamSkillsWindowAction(Team team) {
        super(team);
    }

    @Override
    public String getUiName() {
        return "Tribe Skills";
    }

    @Override
    public void action() {
        LOG.info("action " + getHint());
        Gdxg.clientUi.getTeamSkillsWindow().showFor(getTeam());
    }
}
