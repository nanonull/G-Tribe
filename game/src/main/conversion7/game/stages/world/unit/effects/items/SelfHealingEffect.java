package conversion7.game.stages.world.unit.effects.items;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import conversion7.game.Assets;
import conversion7.game.stages.world.unit.effects.AbstractUnitEffect;

public class SelfHealingEffect extends AbstractUnitEffect {

    public static final int HEALING_LENGTH_STEPS = 1;
    private static final int HEAL_BASE = 2;
    public static final int CAMP_HEAL_BOOST = SelfHealingEffect.HEAL_BASE;
    private static final int FEMALE_HEAL_BOOST = 1;
    private static final int FEMALE_HEAL = HEAL_BASE + FEMALE_HEAL_BOOST;

    public SelfHealingEffect() {
        super(SelfHealingEffect.class.getSimpleName(), Type.POSITIVE, null);
    }

    @Override
    public String getHint() {
        return super.getHint() + " " + tickCounter + "/" + HEALING_LENGTH_STEPS
                + "\n \nHeals unit when effect counter expires.\n " +
                "\nBase heal: male " + HEAL_BASE + "hp , female " + FEMALE_HEAL + "hp." +
                "\nHealing in camp +" + CAMP_HEAL_BOOST + "hp to Base heal";

    }

    @Override
    public Image getIcon() {
        Image image = new Image(Assets.getTextureReg("heal"));
        return image;
    }

    @Override
    public void tick() {
        super.tick();
        if (tickCounter >= HEALING_LENGTH_STEPS) {
            int healOn = HEAL_BASE;
            boolean onCamp = (getOwner().getLastCell().getCamp() != null);
            if (getOwner().isFemale()) {
                healOn += FEMALE_HEAL_BOOST;
            }
            if (onCamp) {
                healOn += CAMP_HEAL_BOOST;
            }
            getOwner().heal(healOn);
            resetTickCounter();
        }
    }
}
