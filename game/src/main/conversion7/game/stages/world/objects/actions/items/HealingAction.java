package conversion7.game.stages.world.objects.actions.items;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import conversion7.game.Assets;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.actions.AbstractWorldTargetableAction;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.unit.UnitAge;
import conversion7.game.stages.world.unit.effects.items.HealActionCooldownEffect;
import conversion7.game.unit_classes.UnitClassConstants;

public class HealingAction extends AbstractWorldTargetableAction {
    public static final int HEALTH_AMOUNT = (int) (UnitClassConstants.BASE_DMG * 2.5f);
    public static final String DESC = "Heal ally's HP on: " + HEALTH_AMOUNT;

    public HealingAction() {
        super(Group.DEFENCE);
    }

    public int getDistance() {
        return 1;
    }

    @Override
    public TextureRegion getIconTexture() {
        return Assets.getTextureReg("heal");
    }

    @Override
    public String getActionWorldHint() {
        return "heal";
    }

    public static boolean testAge(UnitAge age) {
        return age.getLevel() >= UnitAge.ADULT.getLevel();
    }

    @Override
    protected Color getTargetCellSelectionColor(Cell cellAround) {
        return Color.GREEN;
    }

    @Override
    public String buildDescription() {
        return getName() + "\n \n" + DESC;
    }

    @Override
    public void handleAcceptedInput(Cell input) {
        AbstractSquad targetSquad = input.squad;
        targetSquad.heal(HEALTH_AMOUNT);
        getSquad().getEffectManager().addEffect(new HealActionCooldownEffect());
    }
}
