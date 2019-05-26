package conversion7.game.stages.world.objects.actions.items;

import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.objects.ScorpionObject;
import conversion7.game.stages.world.objects.actions.AbstractSquadAction;
import conversion7.game.stages.world.objects.buildings.ResourceCosts;

public class BuildScorpionAction extends AbstractSquadAction {
    public BuildScorpionAction() {
        super(Group.DEFENCE);
    }

    @Override
    public String getShortName() {
        return "BldScorp";
    }

    @Override
    protected String buildDescription() {
        return getName() +
                "\n \nResources cost: " + ResourceCosts.getCostAsString(ScorpionObject.class);
    }

    @Override
    public void begin() {
        AreaObject.create(getSquad().getLastCell(), getSquad().team, ScorpionObject.class);
        getSquad().getInventory().remove(ResourceCosts.getCost(ScorpionObject.class));
    }
}
