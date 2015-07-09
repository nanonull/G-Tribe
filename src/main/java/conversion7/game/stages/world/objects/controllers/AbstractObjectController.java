package conversion7.game.stages.world.objects.controllers;

import conversion7.game.stages.world.objects.AreaObject;

public abstract class AbstractObjectController {

    protected boolean valid;
    protected AreaObject areaObject;

    public AbstractObjectController(AreaObject areaObject) {
        this.areaObject = areaObject;
    }

}
