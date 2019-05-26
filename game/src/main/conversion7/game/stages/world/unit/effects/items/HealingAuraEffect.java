package conversion7.game.stages.world.unit.effects.items;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import conversion7.game.Assets;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.stages.world.unit.UnitAge;
import conversion7.game.stages.world.unit.effects.AbstractUnitEffect;
import conversion7.game.ui.utils.UiUtils;

public class HealingAuraEffect extends AbstractUnitEffect {

    public static final int HEALING_LENGTH_STEPS = 1;
    private static final Color A_COLOR = UiUtils.alpha(0.95f, Color.PURPLE);
    public static int HEAL_AMOUNT = 1;
    public static final UnitAge STARTS_FROM = UnitAge.MATURE;
    public static final int STARTS_FROM_LEVEL = STARTS_FROM.getLevel();
    public static final int ENDS_FROM_LEVEL = UnitAge.OLD.getLevel();
    private static final String DESC = "Heal neighbour allies: HP +" + HEAL_AMOUNT +
            "\n \nFemale has it from age: " + STARTS_FROM.name();

    public HealingAuraEffect() {
        super(HealingAuraEffect.class.getSimpleName(), Type.POSITIVE, null);
    }

    @Override
    public String getHint() {
        return super.getHint() + " " + tickCounter + "/" + HEALING_LENGTH_STEPS
                + "\n \n" + DESC;

    }

    @Override
    public Image getIcon() {
        Image image = new Image(Assets.getTextureReg("heal"));
        image.setColor(A_COLOR);
        return image;
    }

    private static void healUnitsAround(Unit aroundUnit) {
        for (Cell cell : aroundUnit.squad.getLastCell().getCellsAround()) {
            if (cell.hasSquad() && cell.squad.team == aroundUnit.squad.team) {
                cell.squad.heal(HEAL_AMOUNT);
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (tickCounter == HEALING_LENGTH_STEPS) {
            healUnitsAround(this.getOwner().unit);
            resetTickCounter();
        }
    }
}
