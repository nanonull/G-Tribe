package conversion7.game.stages.battle_deprecated.contollers;

import aurelienribon.tweenengine.Tween;
import conversion7.engine.Gdxg;
import conversion7.engine.tween.LinearEquation;
import conversion7.engine.tween.Node3dAccessor;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.battle_deprecated.BattleFigure;
import conversion7.game.stages.battle_deprecated.calculation.Cell;
import conversion7.game.stages.battle_deprecated.calculation.FigureStepParams;
import conversion7.game.stages.battle_deprecated.calculation.Round;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import org.slf4j.Logger;

import static conversion7.game.stages.battle_deprecated.Battle.ANIM_SPEED;
import static conversion7.game.stages.battle_deprecated.Battle.ANIM_TRANSITION;

public class MoveController extends AbstractActionController {

    private static final Logger LOG = Utils.getLoggerForClass();

    public Cell target;

    Tween tween;

    public MoveController(FigureStepParams owner, Cell target) {
        super(owner);
        this.target = target;
        target.registeredMovementOnMe = this;
        if (LOG.isDebugEnabled()) LOG.debug("created " + this);
    }

    @Override
    public String toString() {
        return "MOVE_CONTROLLER [" +
                "target = " + target + "; " +
                "owner = " + owner + "; " +
                "]";
    }

    @Override
    public void act(float delta) {
    }

    @Override
    public void start() {
        owner.battleFigure.modelActor.getAsActor3d().getAnimation()
                .animate(BattleFigure.AnimationMode.WALK.toString(), -1, ANIM_SPEED, null, ANIM_TRANSITION);

        tween = Tween.to(owner.battleFigure.figureVisualGroup, Node3dAccessor.POSITION_XYZ, Round.STEP_LENGTH.getFloatSeconds())
                .target(target.x + 0.5f - owner.battleFigure.battle.getHalfOfTotalWidth(), AbstractSquad.ACTOR_Z,
                        -target.y - 0.5f + owner.battleFigure.battle.getHalfOfTotalHeight())
                .ease(new LinearEquation())
                .start(Gdxg.tweenManager);

        owner.battleFigure.rotateOnTarget(target);
    }

    @Override
    public void completeHalf() {

    }

    @Override
    public void complete() {
        tween.kill();
        owner.battleFigure.updateBody(target.x, target.y);
    }


    @Override
    public void cancel() {
        LOG.debug(" cancel " + this);
    }
}
