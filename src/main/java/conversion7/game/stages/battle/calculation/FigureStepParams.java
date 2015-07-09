package conversion7.game.stages.battle.calculation;

import conversion7.engine.utils.Utils;
import conversion7.game.stages.battle.Battle;
import conversion7.game.stages.battle.BattleFigure;
import conversion7.game.stages.battle.contollers.AbstractActionController;
import conversion7.game.stages.battle.contollers.MoveController;
import org.slf4j.Logger;

import static java.lang.String.format;

/**
 * Version of figure's parameters in battle step
 */
public class FigureStepParams {

    private static final Logger LOG = Utils.getLoggerForClass();

    public final int id;
    private final int version;
    public Step step;
    public BattleFigure battleFigure;


    public Cell cell;
    public AbstractActionController action = null;
    public boolean killed = false;

    // fluent parameters; based on UnitParameters
    public int speed;
    public int life;

    private float valueForEnemy = -1;
    public double distanceToMeFromLastAttacker;

    /**
     * Before round starts
     */
    public FigureStepParams(BattleFigure owner) {
        step = owner.battle.round.startStep;
        battleFigure = owner;
        id = battleFigure.getId();
        version = battleFigure.paramsNumber++;

        speed = 1;
    }

    /**
     * During round
     */
    public FigureStepParams(FigureStepParams previousParams, Step newStep) {
        step = newStep;
        battleFigure = previousParams.battleFigure;
        id = battleFigure.getId();
        version = battleFigure.paramsNumber++;

        if (previousParams.action != null && previousParams.action.getClass().equals(MoveController.class)) {
            MoveController mc = (MoveController) previousParams.action;
            step.getCell(mc.target.x, mc.target.y).seize(this);
        } else {
            step.getCell(previousParams.cell.x, previousParams.cell.y).seize(this);
        }

        speed = previousParams.speed;
        life = previousParams.life;
    }

    @Override
    public String toString() {
        return new StringBuilder(getClass().getSimpleName()).append(": ")
                .append("id=").append(id)
                .append(" life=").append(life)
                .append(" speed=").append(speed)
                .append(" ").append(cell)
                .append(" version=").append(version)
                .append(" ").append(battleFigure)
                .append(" ").append(step)
                .toString();
    }

    /** Returns true if target will be killed, otherwise returns false */
    public boolean hit(FigureStepParams target) {
        if (Battle._HUMAN_PLAYER_COULD_NOT_LOST && target.battleFigure.getTeamSide().getTeam().isHumanPlayer()) {
            if (LOG.isDebugEnabled()) LOG.debug("no damage due to _HUMAN_PLAYER_COULD_NOT_LOST");
            return false;
        }
        int damage = getDamage();
        if (LOG.isDebugEnabled())
            LOG.debug(format("hit with damage %d, [@attacker %s], [@target %s]", damage, this, target));
        target.life -= damage;
        if (LOG.isDebugEnabled()) LOG.debug(" target life left = " + target.life);

        if (target.life <= 0) {
            if (LOG.isDebugEnabled()) LOG.debug(" last hit, target figure killed: " + target);
            battleFigure.howManyKilled++;
            target.killed = true;
            return true;
        }

        return false;
    }


    public int getDamage() {
        return battleFigure.representsUnit.getMeleeDamage();
    }

    // TODO reset valueForEnemy each battle step
    public float calculateValueForEnemy() {
        if (valueForEnemy == -1) {
            valueForEnemy = speed * getDamage();
            if (life <= 0) {
                valueForEnemy = 0;
            } else {
                valueForEnemy /= life;
            }
        }
        return valueForEnemy;
    }

}
