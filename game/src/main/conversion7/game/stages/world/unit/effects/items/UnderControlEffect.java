package conversion7.game.stages.world.unit.effects.items;

import conversion7.game.stages.world.objects.actions.items.ControlUnitAction;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.team.Team;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.stages.world.unit.effects.AbstractUnitEffect;

@Deprecated
public class UnderControlEffect extends AbstractUnitEffect {

    public static final int STEPS_FOR_GET_OUT_FROM_CONTROL = 5;
    public Unit controller;
    public Team previousTeam;

    public UnderControlEffect() {
        super(UnderControlEffect.class.getSimpleName(), Type.NEGATIVE, null);
    }

    @Override
    public String getHint() {
        return super.getHint() + "\nUnit is under control of another unit:\n"
                + controller.squad.getFullName() + " of team " + controller.squad.team.getName()
                + "\n \nUnit gets out from control if doesn't see controller for " + (tickCounter) + "/" +
                UnderControlEffect.STEPS_FOR_GET_OUT_FROM_CONTROL + " steps";
    }

    public static boolean isUnderControl(AbstractSquad squad) {
        return squad.getEffectManager().getEffect(UnderControlEffect.class) != null;
    }

    @Override
    public void tick() {
        super.tick();
        validate();
    }

    public void validate() {
        if (tickCounter >= STEPS_FOR_GET_OUT_FROM_CONTROL) {
            getOwner().batchFloatingStatusLines.addImportantLine("Control released");
            ControlUnitAction.releaseControlByAnotherUnit(this);
        }
    }
}
