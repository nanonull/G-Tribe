package conversion7.game.stages.world.team.actions;

import com.badlogic.gdx.utils.Array;
import conversion7.engine.Gdxg;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.objects.AbstractSquad;
import conversion7.game.stages.world.team.Team;
import org.slf4j.Logger;

public class FocusNextTeamObjectAction extends AbstractTeamAction {

    private static final Logger LOG = Utils.getLoggerForClass();

    private int objectsIndex = -1;

    public FocusNextTeamObjectAction(Team team) {
        super(team);
    }

    @Override
    public void action() {
        if (team.isDefeated()) {
            LOG.info("team.isDefeated");
            return;
        }

        Array<AbstractSquad> teamArmies = team.getArmies();
        objectsIndex++;
        if (objectsIndex == teamArmies.size) {
            objectsIndex = 0;
        }
        Gdxg.graphic.getCameraController().moveCameraToLookAtWorldCell(teamArmies.get(objectsIndex).getCell());
    }

}
