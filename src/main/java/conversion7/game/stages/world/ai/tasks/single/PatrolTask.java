package conversion7.game.stages.world.ai.tasks.single;

import conversion7.engine.geometry.Point2s;
import conversion7.engine.utils.PropertiesLoader;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.AreaObject;
import org.slf4j.Logger;

public class PatrolTask extends AbstractAreaObjectTaskSingle {

    private static final Logger LOG = Utils.getLoggerForClass();
    public static final int DEFAULT_PRIORITY =
            PropertiesLoader.getIntProperty("AreaObjectTask.Priority.PatrolTask");

    Cell start;
    Point2s direction = new Point2s(0, /*Utils.RANDOM.nextBoolean() ? -1 :*/ 1);
    int curStep = 0;

    public PatrolTask(AreaObject owner) {
        super(owner, DEFAULT_PRIORITY);
        this.start = owner.getCell();
    }


    @Override
    public boolean execute() {
        if (curStep < 5) {
            curStep++;
            Cell nextCell = owner.getArea().getCell(owner.getCell().x + direction.x, owner.getCell().y + direction.y);
            if (nextCell.couldBeSeized()) {
                owner.moveOn(nextCell);
            } else {
                changeDirection();
            }
        } else {
            changeDirection();
        }
        return false;
    }


    private void changeDirection() {
        curStep = 0;
        direction.y *= -1;
    }

    @Override
    public void complete() {

    }
}
