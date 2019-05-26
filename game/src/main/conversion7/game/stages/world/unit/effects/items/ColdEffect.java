package conversion7.game.stages.world.unit.effects.items;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import conversion7.game.Assets;
import conversion7.game.stages.world.unit.UnitParameterType;
import conversion7.game.stages.world.unit.effects.AbstractUnitEffect;
import conversion7.game.unit_classes.UnitClassConstants;

public class ColdEffect extends AbstractUnitEffect {

    public static final int NEGATIVE_CELL_COND_HURT = calcNeg();

    public ColdEffect() {
        super(ColdEffect.class.getSimpleName(), Type.NEGATIVE);
        effectParameters.put(UnitParameterType.HEALTH_DAMAGE_PER_STEP, -ColdEffect.NEGATIVE_CELL_COND_HURT);
    }

    @Override
    public Image getIcon() {
        return new Image(Assets.getTextureReg("cold"));
    }

    @Override
    public String getShortIconName() {
        return "Cold";
    }

    private static int calcNeg() {
        int v = (int) Math.ceil(UnitClassConstants.BASE_POWER * 0.1f);
        if (v < 1) {
            v = 1;
        }
        return v;
    }
}
