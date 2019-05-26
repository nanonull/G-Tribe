package conversion7.game.stages.world.objects.controllers;

import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.unit.effects.items.UnitInCampEffect;

public class UnitCellValidator extends AbstractSquadValidator {

    public UnitCellValidator(AbstractSquad squad) {
        super(squad);
    }

    @Override
    public void validate() {
        squad.calculateTemperatureHungerThirstEffects();
        if (squad.getLastCell().camp != null && squad.getLastCell().camp.isConstructionCompleted()) {
            if (!squad.getEffectManager().containsEffect(UnitInCampEffect.class)) {
                squad.getEffectManager().getOrCreate(UnitInCampEffect.class);
            }
        } else {
            squad.getEffectManager().removeEffectIfExist(UnitInCampEffect.class);
        }
    }
}
