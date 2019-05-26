package conversion7.game.stages.world.team.actions;

import conversion7.engine.Gdxg;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.team.Team;
import org.slf4j.Logger;

public class OpenGodsPanelAction extends AbstractTeamAction {

    private static final Logger LOG = Utils.getLoggerForClass();

    public OpenGodsPanelAction(Team team) {
        super(team);
    }

    @Override
    public String getUiName() {
        return "Gods";
    }

    @Override
    public void action() {
        Gdxg.clientUi.getGodsPanel().showFor(team);
    }

}
