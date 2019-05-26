package conversion7.game.stages.world.unit.effects.items;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import conversion7.game.Assets;
import conversion7.game.stages.world.objects.actions.items.DiscordAction;
import conversion7.game.stages.world.unit.effects.AbstractUnitEffect;
import conversion7.game.ui.utils.UiUtils;

public class DisableHealingEffect extends AbstractUnitEffect {

    public static final int STEPS_LENGTH = DiscordAction.EFFECT_LENGTH;
    public static final Color A_COLOR = UiUtils.alpha(0.95f, Color.ORANGE);
    public static final String DESC = "Unit can't be healed";

    public DisableHealingEffect() {
        super(DisableHealingEffect.class.getSimpleName(), Type.NEGATIVE, null);
    }


    @Override
    public Image getIcon() {
        return new Image(Assets.getTextureReg("disab_heal"));
    }

    @Override
    public String getHint() {
        return super.getHint() + " " + tickCounter + "/" + STEPS_LENGTH
                + "\n \n" + DESC;

    }


    @Override
    public void tick() {
        super.tick();
        if (tickCounter == STEPS_LENGTH) {
            remove();
        }
    }
}
