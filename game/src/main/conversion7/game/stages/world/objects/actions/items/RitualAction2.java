package conversion7.game.stages.world.objects.actions.items;

import conversion7.game.stages.world.objects.PrimalExperienceJewel;
import conversion7.game.stages.world.objects.actions.AbstractSquadAction;

public class RitualAction2 extends AbstractSquadAction {


    public RitualAction2() {
        super(Group.TRIBE);
    }

    @Override
    public String getShortName() {
        return "Ritual";
    }

    @Override
    public String buildDescription() {
        return getName() + "\n \nStart ritual";
    }

    @Override
    public void begin() {
        PrimalExperienceJewel.create(getSquad().getLastCell());
    }

}
