package conversion7.game.ai.global.branches;

import com.badlogic.gdx.utils.Array;
import conversion7.game.ai.global.AiTaskType;
import conversion7.game.ai.global.tasks.BuildCampTask;
import conversion7.game.ai.global.tasks.MoveTask;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.unit.actions.ActionEvaluation;

public class ExploreAndExpandAiBranch {
    static Array<Cell> cellsWip = new Array<>();

    public static void eval(AbstractSquad squad) {
        cellsWip.clear();
        cellsWip.addAll(squad.getVisibleCellsAround());
        if (cellsWip.size == 0) {
            return;
        }

        Cell.sortByValue(cellsWip);
        Cell bestCell = cellsWip.get(0);
        if (ActionEvaluation.BUILD_CAMP.evaluateOwner(squad)) {
            if (bestCell == squad.getLastCell() || bestCell.getTotalValue() == squad.getLastCell().getTotalValue()) {
                squad.addAiTask(new BuildCampTask(squad).setPriority(AiTaskType.BUILD_CAMP.priority));
            } else {
                squad.addAiTask(new MoveTask(squad, bestCell, AiTaskType.MOVE_FOR_BUILD_CAMP));
            }
        } else {
            squad.addAiTask(new MoveTask(squad, bestCell, AiTaskType.MOVE_TO_BETTER_CELL));
        }

        for (Cell cell : cellsWip) {
            if (cell.camp != null) {
                if (squad.team == cell.camp.team && !cell.camp.isConstructionCompleted()) {
                    squad.addAiTask(new MoveTask(squad, cell, AiTaskType.MOVE_TO_COMPLETE_CAMP_BUILDING));
                }

                if (squad.team.canCaptureCamp(cell)) {
                    squad.addAiTask(new MoveTask(squad, cell, AiTaskType.MOVE_TO_CAPTURE_CAMP));
                }
            }

            if (cell.hasSquad() && squad.team.canAskToJoinAtWorldStep()) {
                tryTalkToJoinOtherAi(squad, cell.squad);
            }
        }
    }

    private static void tryTalkToJoinOtherAi(AbstractSquad me, AbstractSquad other) {
        if (me.team != other.team
                && me.team.isHumanAiTribe()
                && other.team.isHumanAiTribe()
                && !me.team.isEnemyOf(other.team)) {
            boolean tryToJoin = me.team.tryToJoin(other);
        }
    }

}
