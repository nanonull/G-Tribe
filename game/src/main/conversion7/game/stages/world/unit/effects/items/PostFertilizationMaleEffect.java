package conversion7.game.stages.world.unit.effects.items;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import conversion7.game.Assets;
import conversion7.game.stages.world.unit.UnitParameters;
import conversion7.game.stages.world.unit.effects.AbstractUnitEffect;

public class PostFertilizationMaleEffect extends AbstractUnitEffect implements Comparable {

    public int critBoostPercent;
    public int expiresIn;

    public PostFertilizationMaleEffect(int critBoostPercent, int expiresIn) {
        super(PostFertilizationMaleEffect.class.getSimpleName(), Type.POSITIVE, new UnitParameters());
        this.critBoostPercent = critBoostPercent;
        this.expiresIn = expiresIn;
    }

    @Override
    public Image getIcon() {
        return new Image(Assets.getTextureReg("male"));
    }

    @Override
    public String getHint() {
        return super.getHint() + " " + tickCounter + "/" + expiresIn
                + "\n \nEffect is removed when effect counter expires\n \n" +
                "Effect adds following attributes to unit and his neighbor allies: \n" +
                " * chance of critical damage: " + critBoostPercent + "%";
    }

    @Override
    public void tick() {
        super.tick();
        if (tickCounter == expiresIn) {
            remove();
        }
    }

    @Override
    public int compareTo(Object o) {
        int thisCrit = this.critBoostPercent;
        int anotherCrit = ((PostFertilizationMaleEffect) o).critBoostPercent;
        return Integer.compare(anotherCrit, thisCrit);
    }
}