package conversion7.game.stages.world.objects.actions.items;

import conversion7.game.stages.world.objects.actions.AbstractSquadAction;
import conversion7.game.stages.world.unit.effects.items.CooldownEffect;

public class SprintAction extends AbstractSquadAction {

    private static final int BOOST = 2;
    public static final String DESC = "Move AP this turn +" + BOOST;

    public SprintAction() {
        super(Group.COMMON);
    }

    @Override
    public String getShortName() {
        return "Sprint";
    }

    @Override
    public String buildDescription() {
        return getName() + "\n \n" + DESC;
    }

    @Override
    public void begin() {
        CooldownEffect cooldownEffect = getSquad().effectManager.getOrCreate(CooldownEffect.class);
        cooldownEffect.addCooldown(CooldownEffect.Type.SPRINT);
        getSquad().updateMoveAp(+BOOST);
    }

}
