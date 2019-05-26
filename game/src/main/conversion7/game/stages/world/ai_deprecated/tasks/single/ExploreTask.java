package conversion7.game.stages.world.ai_deprecated.tasks.single;

import conversion7.engine.Gdxg;
import conversion7.engine.geometry.Point2s;
import conversion7.game.interfaces.AreaViewerInputResolver;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.unit.AbstractSquad;

@Deprecated
public class ExploreTask extends AbstractSquadTaskSingle implements AreaViewerInputResolver {

    private static final int EXPLORE_DURATION = 8;

    Cell start;
    Point2s direction;
    int curStep = 0;

    public ExploreTask(AbstractSquad owner) {
        super(owner);
        this.start = owner.getLastCell();
    }

    public void setDirection(Point2s direction) {
        this.direction = direction;
    }

    @Override
    public boolean execute() {
        if (curStep < EXPLORE_DURATION) {
            curStep++;
            Cell nextCell = owner.getArea().getCell(owner.getLastCell().x + direction.x, owner.getLastCell().y + direction.y);
            if (nextCell.canBeSeized()) {
                owner.moveOn(nextCell);
                return false;
            }
        }
        complete();
        return true;
    }

    /** Need cell not equal to selected */
    @Override
    public boolean couldAcceptInput(Cell input) {
        return !Gdxg.getAreaViewer().getSelectedCell().equals(input);
    }

    @Override
    public void beforeInputHandle() {

    }

    @Override
    public void handleAcceptedInput(Cell input) {
        Point2s direction = Gdxg.getAreaViewer().getSelectedCell().getDiffWithCell(input);
        setDirection(direction.trim(1));
    }

    @Override
    public void afterInputHandled() {
    }

    @Override
    public boolean hasAcceptableDistanceTo(Cell mouseOverCell) {
        return false;
    }
}
