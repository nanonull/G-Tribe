package conversion7.game.stages.battle_deprecated.contollers;

import conversion7.game.stages.battle_deprecated.BattleFigure;
import conversion7.game.stages.battle_deprecated.calculation.Cell;
import conversion7.game.stages.battle_deprecated.calculation.FigureStepParams;

/**
 * Created by MP on 16.09.2014.
 */
public class IdleController extends AbstractActionController {

    private Cell target;

    /**
     * Target means direction during idle, could be null
     */
    public IdleController(FigureStepParams owner, Cell target) {
        super(owner);
        this.target = target;
    }

    @Override
    public void cancel() {

    }

    @Override
    public void act(float delta) {

    }

    @Override
    public void start() {
        owner.battleFigure.modelActor.getAsActor3d()
                .getAnimation().setAnimation(BattleFigure.AnimationMode.IDLE.toString(), -1, 0.9f, null);

        if (target != null) {
            owner.battleFigure.rotateOnTarget(target);
        }
    }

    @Override
    public void completeHalf() {

    }

    @Override
    public void complete() {

    }
}
