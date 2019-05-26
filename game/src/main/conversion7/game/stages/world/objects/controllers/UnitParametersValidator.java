package conversion7.game.stages.world.objects.controllers;

import conversion7.engine.artemis.ui.UnitInWorldHintPanelsSystem;
import conversion7.game.stages.world.objects.AreaObject;

public class UnitParametersValidator extends AbstractObjectController {

    private AreaObject squad;

    public UnitParametersValidator(AreaObject squad) {
        super(squad);
        this.squad = squad;
    }

    @Override
    public void validate() {
        squad.refreshUiPanelInWorld();
    }

}
