package conversion7.game.stages.world.objects.controllers;

import conversion7.engine.validators.NodeValidator;
import conversion7.game.stages.world.objects.AreaObject;

public abstract class AbstractObjectController extends NodeValidator {

    protected AreaObject areaObject;

    public AbstractObjectController(AreaObject areaObject) {
        this.areaObject = areaObject;
    }

}
