package conversion7.game.stages.world.team.goals;

import conversion7.game.ai.global.AiTaskType;
import conversion7.game.ai.global.tasks.MoveTask;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.team.Team;

public class FindAndAttackTribeGoal extends AbstractTribeGoal {
    private Team attacker;
    private Team targetTeam;
    private Cell targetCell;

    public FindAndAttackTribeGoal(Team attacker, Team targetTeam) {
        this.attacker = attacker;
        this.targetTeam = targetTeam;
        calcTargetCell();
    }

    public boolean isValid() {
        return targetTeam != null && !targetTeam.isDefeated();
    }

    private void calcTargetCell() {
        for (AbstractSquad squad : attacker.getSquads()) {
            for (Cell cell : squad.getVisibleCellsAround()) {
                if (cell.hasSquad() && cell.squad.team == targetTeam) {
                    targetCell = cell;
                    return;
                }
            }
        }

        // set approximately
        targetCell = targetTeam.getSquads().get(0).getLastCell();
    }

    @Override
    public void execute(Team executorTeam) {
        for (AbstractSquad squad : attacker.getSquads()) {
            squad.addAiTask(new MoveTask(squad, targetCell, AiTaskType.MOVE_FOR_TRIBE_GOAL));
        }
    }

}
