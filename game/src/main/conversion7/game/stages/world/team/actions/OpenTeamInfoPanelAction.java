package conversion7.game.stages.world.team.actions;

import conversion7.engine.Gdxg;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.team.Team;
import org.slf4j.Logger;

public class OpenTeamInfoPanelAction extends AbstractTeamAction {

    private static final Logger LOG = Utils.getLoggerForClass();

    public OpenTeamInfoPanelAction(Team team) {
        super(team);
    }

    @Override
    public String getUiName() {
        return "Tribe Info";
    }

    @Override
    public void action() {
        Gdxg.clientUi.getTribeInfoPanel().showFor(team);
    }

}
