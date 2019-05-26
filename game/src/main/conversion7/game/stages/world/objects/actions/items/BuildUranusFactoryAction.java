package conversion7.game.stages.world.objects.actions.items;

import conversion7.game.stages.world.objects.actions.AbstractSquadAction;
import conversion7.game.stages.world.objects.buildings.ResourceCosts;
import conversion7.game.stages.world.objects.buildings.UranusFactory;

public class BuildUranusFactoryAction extends AbstractSquadAction {
    public BuildUranusFactoryAction() {
        super(Group.TRIBE);
    }

    @Override
    public String getShortName() {
        return "BldUran";
    }

    @Override
    protected String buildDescription() {
        return getName() +
                "\n \nResources cost: " + ResourceCosts.getCostAsString(UranusFactory.class);
    }

    @Override
    public void begin() {
        new UranusFactory(getSquad().getLastCell(), getSquad().team).validateView();
        getSquad().getInventory().remove(ResourceCosts.getCost(UranusFactory.class));
    }
}
