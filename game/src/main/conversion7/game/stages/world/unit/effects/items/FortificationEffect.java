package conversion7.game.stages.world.unit.effects.items;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import conversion7.game.Assets;
import conversion7.game.stages.world.unit.effects.AbstractUnitEffect;

public class FortificationEffect extends AbstractUnitEffect {

    private static final int MAX_ARMOR = 3;
    public static final String SHARED_DESCRIPTION = "";
    public static final int ARMOR_PER_STEP = 1;
    private int armor;

    public FortificationEffect() {
        super(FortificationEffect.class.getSimpleName(), Type.POSITIVE);
        setEnabled(false);
    }

    @Override
    public Image getIcon() {
        return new Image(Assets.getTextureReg("shield_48px"));
    }

    @Override
    public String getHint() {
        return "Fortification armor [" + armor + "]\n \n" +
                SHARED_DESCRIPTION;
    }

    public int getArmor() {
        return armor;
    }

    public void updateArmor(int on) {
        armor += on;
        if (armor <= 0) {
            remove();
        } else if (armor >= MAX_ARMOR) {
            armor = MAX_ARMOR;
        }

        getOwner().refreshUiPanelInWorld();
    }
}
