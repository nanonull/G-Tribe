package conversion7.game.ai.global.branches;

import com.badlogic.gdx.utils.Array;
import conversion7.game.ai.battle.BattleAiBranch;
import conversion7.game.ai.global.AiTaskType;
import conversion7.game.ai.global.tasks.MoveTask;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.utils.collections.Comparators;

public class EnemiesAiBranch {
    public static void eval(AbstractSquad squad) {
        Array<AbstractSquad> possibleTargets = new Array<>();
        for (AreaObject visibleObject : squad.visibleObjects) {
            if (visibleObject.isSquad()) {
                AbstractSquad targetSquad = (AbstractSquad) visibleObject;
                if (squad.team.canAttack(targetSquad.team)) {
                    possibleTargets.add(targetSquad);
                }
            }
        }
        if (possibleTargets.size == 0) {
            return;
        }

        for (AbstractSquad visibleSquad : possibleTargets) {
            visibleSquad.calculateTargetValueRelativeTo(squad);
        }
        possibleTargets.sort(Comparators.SQUAD_TARGET_VALUE_COMPARATOR);
        AbstractSquad bestTarget = possibleTargets.first();

        squad.team.potentialBattleTargets.add(bestTarget);
        boolean waitOnRange = squad.power.getRangeDamage() > 0;
        if (!waitOnRange && BattleAiBranch.isRelativelyStrong(squad, bestTarget)) {
            squad.addAiTask(new MoveTask(squad, bestTarget.cell, AiTaskType.MOVE_FOR_ATTACK));
        }
//            // flee?
//            squad.addAiTask(new EscapeTask(squad.unit, visibleObject));
    }
}
