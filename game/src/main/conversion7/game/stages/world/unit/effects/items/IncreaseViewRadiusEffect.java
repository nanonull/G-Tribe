package conversion7.game.stages.world.unit.effects.items;

import conversion7.game.stages.world.unit.effects.AbstractUnitEffect;

public class IncreaseViewRadiusEffect extends AbstractUnitEffect {


    public static final int INCREASE_RADIUS = 1;
    public static final String DESC = "Add +" + INCREASE_RADIUS + " to view radius";

    public IncreaseViewRadiusEffect() {
        super(IncreaseViewRadiusEffect.class.getSimpleName(), Type.POSITIVE);
    }


    @Override
    public String getShortIconName() {
        return "View";
    }

    @Override
    public String getHint() {
        return super.getHint() + "\n \n" + DESC;
    }
}
