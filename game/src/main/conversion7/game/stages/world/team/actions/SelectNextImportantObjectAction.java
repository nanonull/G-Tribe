package conversion7.game.stages.world.team.actions;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import conversion7.engine.Gdxg;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.team.Team;
import conversion7.game.ui.UiLogger;
import org.slf4j.Logger;

public class SelectNextImportantObjectAction extends AbstractTeamAction {

    private static final Logger LOG = Utils.getLoggerForClass();

    private int objectsIndex = -1;

    public SelectNextImportantObjectAction(Team team) {
        super(team);
    }

    @Override
    public String getUiName() {
        return "Show goal object";
    }

    @Override
    public void action() {
        if (team.isDefeated()) {
            LOG.info("team.isDefeated");
            return;
        }

        Array<AreaObject> importantObjects = team.world.getImportantObjects();
        if (importantObjects.size == 0) {
            UiLogger.addGameInfoLabel("Nothing special in world");
            return;
        }

        objectsIndex++;
        if (objectsIndex >= importantObjects.size) {
            objectsIndex = 0;
        }

        AreaObject areaObject = importantObjects.get(objectsIndex);

        focusOn(areaObject);
    }

    public static void focusOn(AreaObject areaObject) {
        Cell cell = areaObject.getLastCell();
        cell.addFloatLabel("Goal object: " + areaObject.getClass().getSimpleName(), Color.ORANGE);
        Gdxg.graphic.getCameraController().moveCameraToLookAtWorldCell(cell);
        Gdxg.core.areaViewer.selectCell(cell);
    }

    public static void focusOn(Cell cell) {
        cell.addFloatLabel("Goal cell", Color.ORANGE);
        Gdxg.graphic.getCameraController().moveCameraToLookAtWorldCell(cell);
        Gdxg.core.areaViewer.selectCell(cell);
    }

}
