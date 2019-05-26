package conversion7.game.stages.world.objects.actions.items;

import conversion7.game.stages.world.objects.actions.AbstractSquadAction;
import conversion7.game.stages.world.objects.buildings.CommunicationSatellite;
import conversion7.game.stages.world.objects.buildings.ResourceCosts;

public class BuildSatelliteAction extends AbstractSquadAction {
    public BuildSatelliteAction() {
        super(Group.TRIBE);
    }

    @Override
    public String getShortName() {
        return "BldSat";
    }

    @Override
    protected String buildDescription() {
        return getName() +
                "\n \nResources cost: " + ResourceCosts.getCostAsString(CommunicationSatellite.class);
    }

    @Override
    public void begin() {
        new CommunicationSatellite(getSquad().getLastCell(), getSquad().team).validateView();
        getSquad().getInventory().remove(ResourceCosts.getCost(CommunicationSatellite.class));
    }
}
