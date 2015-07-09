package conversion7.game.stages.world.objects.controllers;

import conversion7.game.interfaces.Validatable;
import conversion7.game.stages.world.objects.AreaObject;

public class AreaObjectEffectsController extends AbstractObjectController implements Validatable {
    public AreaObjectEffectsController(AreaObject areaObject) {
        super(areaObject);
    }

    @Override
    public void invalidate() {
        valid = false;
    }

    @Override
    public void validate() {
        if (!valid) {
            valid = true;
            areaObject.validateEffects();
        }
    }
}
