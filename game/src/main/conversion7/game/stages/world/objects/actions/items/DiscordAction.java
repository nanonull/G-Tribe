package conversion7.game.stages.world.objects.actions.items;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import conversion7.game.Assets;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.actions.AbstractWorldTargetableAction;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.team.TribeRelationType;
import conversion7.game.stages.world.unit.UnitAge;
import conversion7.game.stages.world.unit.effects.items.DisableHealingEffect;
import conversion7.game.stages.world.unit.effects.items.DiscordEffect;

public class DiscordAction extends AbstractWorldTargetableAction {

    public static final String DESC = "Add effects to unit:" +
            "\n \n * " + DisableHealingEffect.class.getSimpleName()+
            "\n" + DisableHealingEffect.DESC +
            "\n \n * " + DiscordEffect.class.getSimpleName() +
            "\n " + DiscordEffect.DESC;
    public static final int EFFECT_LENGTH = 3;

    public DiscordAction() {
        super(Group.ATTACK);
    }

    public int getDistance() {
        return 1;
    }

    @Override
    public TextureRegion getIconTexture() {
        return Assets.getTextureReg("disab_heal");
    }

    @Override
    public String getActionWorldHint() {
        return "disable heal";
    }

    @Override
    public String getShortName() {
        return "DisHeal";
    }

    public static boolean testAge(UnitAge age) {
        return age.getLevel() >= UnitAge.ADULT.getLevel();
    }

    @Override
    protected Color getTargetCellSelectionColor(Cell cellAround) {
        return Color.SCARLET;
    }

    @Override
    public String buildDescription() {
        return getName() + "\n \n" + DESC;
    }

    @Override
    public void handleAcceptedInput(Cell input) {
        AbstractSquad targetSquad = input.squad;
        targetSquad.getEffectManager().getOrCreate(DisableHealingEffect.class).resetTickCounter();
        targetSquad.getEffectManager().getOrCreate(DiscordEffect.class).resetTickCounter();
        targetSquad.team.addRelation(TribeRelationType.ATTACK, getObject().team);
    }
}
