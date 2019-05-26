package conversion7.game.stages.world.objects.controllers;

import conversion7.game.stages.world.objects.unit.AbstractSquad;

public abstract class AbstractSquadValidator extends AbstractObjectController {

    protected AbstractSquad squad;

    public AbstractSquadValidator(AbstractSquad squad) {
        super(squad);
        this.squad = squad;
    }

}
