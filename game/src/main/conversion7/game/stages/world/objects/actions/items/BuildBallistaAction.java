package conversion7.game.stages.world.objects.actions.items;

import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.objects.BallistaObject;
import conversion7.game.stages.world.objects.actions.AbstractSquadAction;
import conversion7.game.stages.world.objects.buildings.ResourceCosts;

public class BuildBallistaAction extends AbstractSquadAction {
    public BuildBallistaAction() {
        super(Group.DEFENCE);
    }

    @Override
    public String getShortName() {
        return "BldBallista";
    }

    @Override
    protected String buildDescription() {
        return getName() +
                "\n \nResources cost: " + ResourceCosts.getCostAsString(BallistaObject.class);
    }

    @Override
    public void begin() {
        AreaObject.create(getSquad().getLastCell(), getSquad().team, BallistaObject.class);
        getSquad().getInventory().remove(ResourceCosts.getCost(BallistaObject.class));
    }
}
