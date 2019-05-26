package conversion7.game.stages.world.unit.effects.items;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import conversion7.game.Assets;
import conversion7.game.stages.world.unit.UnitParameterType;
import conversion7.game.stages.world.unit.effects.AbstractUnitEffect;

public class ThirstEffect extends AbstractUnitEffect {
    public ThirstEffect() {
        super(ThirstEffect.class.getSimpleName(), Type.NEGATIVE);
        effectParameters.put(UnitParameterType.HEALTH_DAMAGE_PER_STEP, -ColdEffect.NEGATIVE_CELL_COND_HURT);
    }

    @Override
    public Image getIcon() {
        return new Image(Assets.getTextureReg("thirst"));
    }
}
