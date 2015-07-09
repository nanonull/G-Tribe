package conversion7.game.stages.world.ai.tasks.single;

import conversion7.engine.geometry.Point2s;
import conversion7.engine.utils.PropertiesLoader;
import conversion7.engine.utils.Utils;
import conversion7.game.interfaces.AreaViewerInputResolver;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.AreaObject;
import org.slf4j.Logger;

public class ExploreTask extends AbstractAreaObjectTaskSingle implements AreaViewerInputResolver {

    private static final Logger LOG = Utils.getLoggerForClass();
    public static final int DEFAULT_PRIORITY =
            PropertiesLoader.getIntProperty("AreaObjectTask.Priority.ExploreTask");
    private static final int EXPLORE_DURATION = 8;

    Cell start;
    Point2s direction;
    int curStep = 0;

    public ExploreTask(AreaObject owner) {
        super(owner, DEFAULT_PRIORITY);
        this.start = owner.getCell();
    }

    @Override
    public boolean execute() {
        if (curStep < EXPLORE_DURATION) {
            curStep++;
            Cell nextCell = owner.getArea().getCell(owner.getCell().x + direction.x, owner.getCell().y + direction.y);
            if (nextCell.couldBeSeized()) {
                owner.moveOn(nextCell);
                return false;
            }
        }
        complete();
        return true;
    }

    public void setDirection(Point2s direction) {
        this.direction = direction;
    }

    /** Need cell not equal to selected */
    @Override
    public boolean couldAcceptInput(Cell input) {
        return !World.getAreaViewer().selectedObject.getCell().equals(input);
    }

    @Override
    public void handleInput(Cell input) {
        Point2s direction = World.getAreaViewer().selectedObject.getCell().diffWithCell(input);
        setDirection(direction.trim(1));
    }

    @Override
    public void onInputHandled() {
        World.getAreaViewer().unhideSelection();
    }
}
