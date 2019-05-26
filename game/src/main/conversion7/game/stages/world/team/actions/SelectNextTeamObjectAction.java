package conversion7.game.stages.world.team.actions;

import com.badlogic.gdx.utils.Array;
import conversion7.engine.Gdxg;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.team.Team;
import org.slf4j.Logger;

public class SelectNextTeamObjectAction extends AbstractTeamAction {

    private static final Logger LOG = Utils.getLoggerForClass();

    private int objectsIndex = -1;

    public SelectNextTeamObjectAction(Team team) {
        super(team);
    }

    public static void focusOn(AbstractSquad squad) {
        Cell cell = squad.getLastCell();
        Gdxg.graphic.getCameraController().moveCameraToLookAtWorldCell(cell);
        Gdxg.core.areaViewer.selectCell(cell);
    }

    @Override
    public String getUiName() {
        return "Select next unit";
    }

    @Override
    public void action() {
        if (team.isDefeated() || team.world.isBattleActive()) {
            return;
        }

        Array<AbstractSquad> teamArmies = team.getSquads();
        objectsIndex++;
        if (objectsIndex >= teamArmies.size) {
            objectsIndex = 0;
        }

        focusOn(teamArmies.get(objectsIndex));
    }

}
