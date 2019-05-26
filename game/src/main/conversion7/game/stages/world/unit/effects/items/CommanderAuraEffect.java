package conversion7.game.stages.world.unit.effects.items;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import conversion7.game.Assets;
import conversion7.game.stages.world.unit.UnitAge;
import conversion7.game.stages.world.unit.effects.AbstractUnitEffect;
import conversion7.game.ui.utils.UiUtils;

public class CommanderAuraEffect extends AbstractUnitEffect {

    private static final Color A_COLOR = UiUtils.alpha(0.95f, Color.ORANGE);
    public static int EXP_ADD_PERCENT = 100;
    public static final UnitAge STARTS_FROM = UnitAge.OLD;
    public static final String DESC = "Allies around get +" + EXP_ADD_PERCENT + "% more experience" +
            "\n \nMale has it from age: " + STARTS_FROM;

    public CommanderAuraEffect() {
        super(CommanderAuraEffect.class.getSimpleName(), Type.POSITIVE, null);
    }

    @Override
    public String getHint() {
        return super.getHint() + "\n \n" + DESC;

    }

    @Override
    public Image getIcon() {
        Image image = new Image(Assets.plus);
        image.setColor(A_COLOR);
        return image;
    }

}
