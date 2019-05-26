package conversion7.game.stages.world.unit;

import conversion7.engine.validators.NodeValidator;

// TODO r
public class UnitParametersController extends NodeValidator {

    private Unit unit;

    public UnitParametersController(Unit unit) {
        this.unit = unit;
    }

    @Override
    public void validate() {
        unit.getSquad().getUnitParametersValidator().invalidate();
    }
}
