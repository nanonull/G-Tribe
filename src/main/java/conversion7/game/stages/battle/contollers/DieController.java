package conversion7.game.stages.battle.contollers;

import conversion7.engine.utils.Utils;
import conversion7.game.stages.battle.calculation.FigureStepParams;

public class DieController extends AbstractActionController {

    public DieController(FigureStepParams owner) {
        super(owner);
    }

    @Override
    public void cancel() {
        Utils.error("It is not possible to cancel death!");
    }

    @Override
    public void act(float delta) {

    }

    @Override
    public void start() {

    }

    @Override
    public void complete() {

    }

    @Override
    public void completeHalf() {

    }

}
