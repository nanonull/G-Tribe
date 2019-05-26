package conversion7.game.stages.world.objects.actions.items;

import conversion7.game.stages.world.objects.actions.AbstractSquadAction;
import conversion7.game.stages.world.objects.buildings.IronFactory;
import conversion7.game.stages.world.objects.buildings.ResourceCosts;

public class BuildIronFactoryAction extends AbstractSquadAction {
    public BuildIronFactoryAction() {
        super(Group.TRIBE);
    }

    @Override
    public String getShortName() {
        return "BldIron";
    }

    @Override
    protected String buildDescription() {
        return getName() +
                "\n \nResources cost: " + ResourceCosts.getCostAsString(IronFactory.class);
    }

    @Override
    public void begin() {
        new IronFactory(getSquad().getLastCell(), getSquad().team).validateView();
        getSquad().getInventory().remove(ResourceCosts.getCost(IronFactory.class));
    }
}
