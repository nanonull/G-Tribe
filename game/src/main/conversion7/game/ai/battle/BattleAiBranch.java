package conversion7.game.ai.battle;

import com.badlogic.gdx.utils.Array;
import conversion7.game.ai.global.AiTaskType;
import conversion7.game.ai.global.tasks.EscapeTask;
import conversion7.game.ai.global.tasks.MeleeAttackTask;
import conversion7.game.ai.global.tasks.MoveTask;
import conversion7.game.ai.global.tasks.RangeAttackTask;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.objects.buildings.SpaceShip;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.unit.actions.ActionEvaluation;

public class BattleAiBranch {

    private static final float ATTACK_POWER_RATIO_MIN = 0.8f;
    private static final float FLEE_POWER_RATIO = ATTACK_POWER_RATIO_MIN / 2;

    public static void eval(AbstractSquad squad) {
        Array<AreaObject> visibleObjects = squad.visibleObjects;
        for (AreaObject visibleObject : visibleObjects) {
            boolean isAdjacent = false;
            if (visibleObject.isNeighborOf(squad)) {
                isAdjacent = true;
            }

            if (visibleObject.hasPower()) {
                if (visibleObject.getClass().equals(SpaceShip.class)) {
                    continue;
                }

                if (squad.team.canAttack(visibleObject.team) && visibleObject.cell != null) {
                    boolean waitOnRange = true;
                    boolean shouldAttackNotFlee = true;

                    if (visibleObject.isSquad()) {
                        shouldAttackNotFlee = squad.isAnimal()
                                || isRelativelyStrong(squad, visibleObject.toSquad())
                                || hasGoodHealth(squad);
                    }

                    if (shouldAttackNotFlee) {
                        if (ActionEvaluation.MELEE_ATTACK.testMeVsTargetFull(squad, visibleObject.cell)) {
                            squad.addAiTask(new MeleeAttackTask(squad, visibleObject.cell)
                                    .setPriority(AiTaskType.MELEE_ATTACK.priority));
                        } else if (ActionEvaluation.RANGE_ATTACK.testMeVsTargetFull(squad, visibleObject.cell)) {
                            squad.addAiTask(new RangeAttackTask(squad, visibleObject.cell)
                                    .setPriority(AiTaskType.RANGE_ATTACK.priority));
                        } else {
                            waitOnRange = squad.power.getRangeDamage() > 0;
                            if (!waitOnRange) {
                                squad.addAiTask(new MoveTask(squad, visibleObject.getLastCell(), AiTaskType.MOVE_FOR_ATTACK));
                            }
                        }

                    } else {
                        squad.addAiTask(new EscapeTask(squad.unit, visibleObject));
                    }
                }
            }
        }
    }

    public static boolean canRangeAttack(AbstractSquad squad, AbstractSquad anotherSquad) {
        return ActionEvaluation.RANGE_ATTACK.testMeVsTargetFull(squad, anotherSquad.cell);
    }

    public static boolean isRelativelyStrong(AbstractSquad squad, AbstractSquad anotherSquad) {
        return squad.getMyRelativePowerRatioWith(anotherSquad) > ATTACK_POWER_RATIO_MIN;
    }

    public static boolean hasGoodHealth(AbstractSquad squad) {
        return squad.power.getValueRatio() > BattleAiBranch.FLEE_POWER_RATIO;
    }
}
