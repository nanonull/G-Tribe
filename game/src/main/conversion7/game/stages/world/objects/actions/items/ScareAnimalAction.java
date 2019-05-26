package conversion7.game.stages.world.objects.actions.items;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import conversion7.engine.utils.MathUtils;
import conversion7.game.Assets;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.actions.AbstractWorldTargetableAction;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.stages.world.unit.effects.items.ScaredEffect;

public class ScareAnimalAction extends AbstractWorldTargetableAction {


    public static final float ANIMAL_POWER_MOD_WHEN_ANIMAL_ACTS = 2f;
    public static final float HUMAN_POWER_MOD_FOR_SUCCES_SCARING = 1.5f;

    public ScareAnimalAction() {
        super(Group.ATTACK);
    }

    @Override
    public TextureRegion getIconTexture() {
        return Assets.getTextureReg("scare");
    }

    public int getDistance() {
        return 1;
    }

    @Override
    public String getActionWorldHint() {
        return "scare animal";
    }

    public static void tryScare(AbstractSquad squad, Unit target) {
        int chance = (int) (CaptureUnitAction.getCaptureChance(squad, target.squad) * 3.9f);
        if (MathUtils.testPercentChance(chance)) {
            target.squad.getEffectManager().getOrCreate(ScaredEffect.class).resetTickCounter();
            squad.cell.addFloatLabel("Scared at % " + chance, Color.ORANGE);
        } else {
            squad.cell.addFloatLabel("Scaring failed at % " + chance, Color.ORANGE);
        }
    }

    @Override
    protected String buildDescription() {
        return getName() + "\n \n" +
                ScaredEffect.SCARE_FULL_HINT;
    }

    @Override
    protected Color getTargetCellSelectionColor(Cell cellAround) {
        return Color.SCARLET;
    }

    @Override
    public void handleAcceptedInput(Cell input) {
        updateExecutorParameters();
        tryScare(getSquad(), input.squad.unit);
    }

}
