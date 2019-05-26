package conversion7.game.ai.global.tasks;

import com.badlogic.gdx.utils.Array;
import conversion7.engine.utils.Utils;
import conversion7.game.GameError;
import conversion7.game.ai.global.AiTaskType;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.landscape.CellDistanceToComparator;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.unit.actions.ActionEvaluation;
import conversion7.game.stages.world.view.AreaViewerAnimationsHelper;
import conversion7.game.stages.world.view.InWorldActionListener;
import org.slf4j.Logger;

public class MoveTask extends AbstractUnitTask<AbstractSquad> {

    private static final Logger LOG = Utils.getLoggerForClass();
    public final Cell createdAt;
    public Cell moveTarget;

    public MoveTask(AbstractSquad owner, Cell moveTarget, AiTaskType taskType) {
        super(owner);
        this.moveTarget = moveTarget;
        createdAt = owner.getLastCell();
        expiresInSteps = 1;
        setPriority(taskType.priority);
    }

    public MoveTask(AbstractSquad squad, Array<Cell> cells, AiTaskType aiTaskType) {
        this(squad, findBestTarget(squad.getLastCell(), cells), aiTaskType);
    }

    private static Cell findBestTarget(Cell from, Array<Cell> cells) {
        CellDistanceToComparator.instance().sort(cells, from);
        return cells.get(0);
    }

    public static void move(AbstractSquad owner, Cell moveTarget, InWorldActionListener listener, boolean globalStrategy) {
        AreaViewerAnimationsHelper.subscribeOnAnimationCompleted(owner, listener);
        if (globalStrategy) {
            owner.moveStepsTo(moveTarget, owner.getMoveAp());
        } else {
            owner.moveOneStepTo(moveTarget);
        }
        if (!AreaViewerAnimationsHelper.hasAnimationStarted(owner)) {
            listener.onEvent();
        }
    }

    @Override
    public boolean isValid() {
        return ActionEvaluation.MOVE.evaluateOwner(owner);
    }

    @Override
    public void run() {
        if (moveTarget == owner.getLastCell()) {
            throw new GameError("");
        }
        if (moveTarget == owner.getPreviousCell()) {
            owner.batchFloatingStatusLines.addLine("Enough of that place..");
            complete();
        } else {
            move(owner, moveTarget, this, globalStrategy);
        }
    }


}
