package conversion7.game.stages.battle.contollers;


import conversion7.game.interfaces.Cancelable;
import conversion7.game.interfaces.Progressive;
import conversion7.game.stages.battle.calculation.FigureStepParams;

public abstract class AbstractActionController implements Progressive, Cancelable {

    public FigureStepParams owner = null;

    public AbstractActionController(FigureStepParams owner) {
        this.owner = owner;
    }

}
