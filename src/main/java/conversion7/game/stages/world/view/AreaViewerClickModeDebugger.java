package conversion7.game.stages.world.view;

import com.badlogic.gdx.utils.Array;
import conversion7.engine.Gdxg;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.WorldPath;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.landscape.PathData;
import org.slf4j.Logger;

public class AreaViewerClickModeDebugger {

    private static final Logger LOG = Utils.getLoggerForClass();

    static Cell from;
    static Cell to;

    public static void addCell(Cell newCell) {
        LOG.info("mouseOnStage:   " + World.getAreaViewer().getStage().mouseOnStage);
        if (from == null) {
            from = newCell;
        } else if (to == null) {
            to = newCell;
            switch (AreaViewer.DEVELOPER_AREA_VIEWER_CLICK_MODE) {
                case 1:
                    showDistanceTo();
                    break;
                case 2:
                    calculatePath();
                    break;
                case 3:
                    moveCamera();
                    break;
            }
        }
    }

    private static void showDistanceTo() {
        LOG.info("\n\nTestWorldDistanceTo.showDistanceTo");
        LOG.info("from: " + from);
        LOG.info("to:   " + to);
        LOG.info("         distance: " + from.distanceTo(to));

        clearData();
    }

    private static void calculatePath() {
        LOG.info("\n\nTestWorldDistanceTo.calculatePath");
        LOG.info("from: " + from);
        LOG.info("to:   " + to);

        Array<PathData> pathDatas = WorldPath.getPath(from, to);
        if (pathDatas == null) {
            LOG.info("path is null");
        } else {
            LOG.info("path:");
            int i = 0;
            for (PathData pathData : pathDatas) {
                LOG.info(i + " " + pathData.cell);
            }
        }

        clearData();
    }

    private static void moveCamera() {
        LOG.info("\n\nTestWorldDistanceTo.moveCamera");
        LOG.info("from: " + from);
        LOG.info("to:   " + to);
        Gdxg.graphic.getCameraController().moveCameraToLookAtWorldCell(to);
        clearData();
    }

    private static void clearData() {
        LOG.info("\n");
        from = null;
        to = null;
    }
}
