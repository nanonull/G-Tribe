package conversion7.game.stages.battle.contollers;

import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.battle.calculation.FigureStepParams;
import org.slf4j.Logger;

import static conversion7.game.stages.battle.Battle.ANIM_SPEED;
import static conversion7.game.stages.battle.Battle.ANIM_TRANSITION;
import static conversion7.game.stages.battle.BattleFigure.AnimationMode.ATTACK;
import static conversion7.game.stages.battle.BattleFigure.AnimationMode.IDLE;

public class AttackController extends AbstractActionController {

    private static final Logger LOG = Utils.getLoggerForClass();

    FigureStepParams target;

    public AttackController(FigureStepParams target, FigureStepParams owner) {
        super(owner);
        this.target = target;

        if (!owner.killed) {
            owner.hit(target);
            if (LOG.isDebugEnabled()) LOG.debug("created " + this);
        } else {
            if (LOG.isDebugEnabled()) LOG.debug("created as dummy " + this);
        }

    }

    @Override
    public String toString() {
        return "ATTACK_CONTROLLER [" +
                "target = " + target + "; " +
                "owner = " + owner + "; " +
                "]";
    }

    @Override
    public void act(float delta) {

    }

    @Override
    public void start() {
        LOG.debug(" start AttackController on owner " + owner);
        AnimationController animationController = owner.battleFigure.modelActor.getAsActor3d().getAnimation();
        animationController.animate(ATTACK.toString(), 1, ANIM_SPEED * 2.5f, null, ANIM_TRANSITION);

        owner.battleFigure.rotateOnTarget(target.cell);
        animationController.queue(IDLE.toString(), -1, ANIM_SPEED, null, ANIM_TRANSITION);
    }

    @Override
    public void complete() {

    }

    @Override
    public void completeHalf() {

    }

    @Override
    public void cancel() {
        LOG.debug(" cancel " + this);
        complete();
    }
}
