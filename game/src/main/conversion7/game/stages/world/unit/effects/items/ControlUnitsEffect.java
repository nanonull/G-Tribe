package conversion7.game.stages.world.unit.effects.items;

import com.badlogic.gdx.utils.Array;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.stages.world.unit.effects.AbstractUnitEffect;

public class ControlUnitsEffect extends AbstractUnitEffect {

    public Array<Unit> underControl = new Array<>();

    public ControlUnitsEffect() {
        super(ControlUnitsEffect.class.getSimpleName(), Type.POSITIVE, null);
    }

    @Override
    public String getHint() {
        return super.getHint() + "\n \n" +
                "Unit controls animal unit(s):\n" + getAnimalsNamesList()
                + "\n \nUnits get out of control if they don't see controller for " +
                UnderControlEffect.STEPS_FOR_GET_OUT_FROM_CONTROL + " steps.";
    }

    private String getAnimalsNamesList() {
        StringBuilder builder = new StringBuilder();
        for (Unit unit : underControl) {
            builder.append(unit.getGameClassName()).append(" ").append(unit.squad.team.getName()).append("\n");
        }
        return builder.toString();
    }

    public void validate() {
        if (underControl.size == 0) {
            remove();
        }
    }
}
