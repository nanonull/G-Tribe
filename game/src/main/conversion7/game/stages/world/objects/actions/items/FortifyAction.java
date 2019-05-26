package conversion7.game.stages.world.objects.actions.items;

import conversion7.game.stages.world.objects.actions.AbstractSquadAction;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.stages.world.unit.effects.items.FortificationEffect;
import org.testng.Assert;

@Deprecated
public class FortifyAction extends AbstractSquadAction {

    public FortifyAction() {
        super(/*ActionEvaluation.FORTIFY*/Group.DEFENCE);
    }

    @Override
    public String buildDescription() {
        return getName()
                + "\n \nStart fortification.\n" + FortificationEffect.SHARED_DESCRIPTION;
    }

    @Override
    public void begin() {
        Unit unit = getSquad().unit;
        Assert.assertTrue(unit.squad.canFortify());
        unit.squad.getEffectManager().addEffect(new FortificationEffect());
    }

}
