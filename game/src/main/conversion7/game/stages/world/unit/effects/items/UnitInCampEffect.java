package conversion7.game.stages.world.unit.effects.items;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import conversion7.game.Assets;
import conversion7.game.stages.world.unit.effects.AbstractUnitEffect;

public class UnitInCampEffect extends AbstractUnitEffect {


    public UnitInCampEffect() {
        super(UnitInCampEffect.class.getSimpleName(), Type.POSITIVE);
    }

    @Override
    public Image getIcon() {
        return new Image(Assets.getTextureReg("icons/home"));
    }

    @Override
    public String getShortIconName() {
        return "Camp";
    }

    @Override
    public String getHint() {
        return super.getHint() + "\n \nIn camp: " +
                "\n * any healing effect gets bonus +" + SelfHealingEffect.CAMP_HEAL_BOOST +
                "\n * unit can't be hit " +
                "\n * no cold ";
    }
}
