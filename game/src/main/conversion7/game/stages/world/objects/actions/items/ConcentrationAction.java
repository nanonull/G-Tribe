package conversion7.game.stages.world.objects.actions.items;

import conversion7.game.stages.world.objects.actions.AbstractSquadAction;
import conversion7.game.stages.world.unit.effects.items.ConcentrationEffect;

@Deprecated
public class ConcentrationAction extends AbstractSquadAction {
    public ConcentrationAction() {
        super(Group.DEFENCE);
    }

    @Override
    public String getShortName() {
        return "Conc";
    }

    @Override
    public void begin() {
//        getSquad().effectManager.getOrCreate(ConcentrationEffect.class);
    }

    @Override
    protected String buildDescription() {
        return toString() + "\n \n" + ConcentrationEffect.HINT;
    }
}
