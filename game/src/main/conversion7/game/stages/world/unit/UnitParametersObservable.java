package conversion7.game.stages.world.unit;

import conversion7.game.stages.world.objects.unit.AbstractSquad;

public class UnitParametersObservable extends UnitParameters {
    private Unit unit;

    public UnitParametersObservable(Unit unit) {
        this.unit = unit;
    }

    public void validate() {
        AbstractSquad squad = unit.getSquad();
        if (squad == null) {
            return;
        }
        squad.getUnitParametersValidator().invalidate();
        squad.validate();
    }

    @Override
    public void put(UnitParameterType key, Integer value) {
        super.put(key, value);
        validate();
    }

    @Override
    public UnitParameters copyFrom(UnitParameters copy) {
        UnitParameters unitParameters = super.copyFrom(copy);
        validate();
        return unitParameters;
    }

    @Override
    public UnitParameters mixWith(UnitParameters mixWith) {
        UnitParameters unitParameters = super.mixWith(mixWith);
        validate();
        return unitParameters;
    }
}
