package conversion7.game.stages.world.objects.controllers;

import conversion7.game.interfaces.Validatable;
import conversion7.game.stages.world.objects.AreaObject;

public class ReadyRangeUnitsController extends AbstractObjectController implements Validatable {

    public ReadyRangeUnitsController(AreaObject areaObject) {
        super(areaObject);
    }

    @Override
    public void invalidate() {
        valid = false;
    }

    @Override
    public void validate() {
        if (!valid) {
            areaObject.validateReadyRangeUnits();
        }
    }
}
