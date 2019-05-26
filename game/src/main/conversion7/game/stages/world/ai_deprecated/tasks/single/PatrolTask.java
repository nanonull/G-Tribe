package conversion7.game.stages.world.ai_deprecated.tasks.single;

import conversion7.engine.geometry.Point2s;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import org.slf4j.Logger;

public class PatrolTask extends AbstractSquadTaskSingle {

    private static final Logger LOG = Utils.getLoggerForClass();

    Cell start;
    Point2s direction = new Point2s(0, /*Utils.RANDOM.nextBoolean() ? -1 :*/ 1);
    int curStep = 0;

    public PatrolTask(AbstractSquad owner) {
        super(owner);
        this.start = owner.getLastCell();
    }


    @Override
    public boolean execute() {
        if (curStep < 5) {
            curStep++;
            Cell nextCell = owner.getArea().getCell(owner.getLastCell().x + direction.x, owner.getLastCell().y + direction.y);
            if (nextCell.canBeSeized()) {
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
