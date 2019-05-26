package conversion7.game.stages.world.objects.actions.items;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import conversion7.game.Assets;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.actions.AbstractHumanSquadAction;
import conversion7.game.stages.world.objects.unit.AttackCalculation;
import conversion7.game.stages.world.objects.unit.WorldSquad;
import conversion7.game.stages.world.unit.effects.items.StunnedEffect;

public class EarthShakeAction extends AbstractHumanSquadAction {

    public static final int RADIUS = 1;
    public static final int MANA_COST = 10;
    private static final int BASE_DMG = LightningAction.BASE_DMG;
    public static final String DESC = "Stun and deal " + BASE_DMG + " dmg"
            + "\nRadius: " + RADIUS;

    public EarthShakeAction() {
        super(Group.ATTACK);
    }

    @Override
    public TextureRegion getIconTexture() {
        return Assets.getTextureReg("earthshake");
    }

    @Override
    public String getActionWorldHint() {
        return "to earth shake";
    }

    @Override
    public int getDistance() {
        return getSquad().getViewRadius(false);
    }

    @Override
    public String getShortName() {
        return "SHAKE";
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
        shakeOn(input);
        for (Cell adjCell : input.getCellsAroundToRadiusInclusively(RADIUS)) {
            shakeOn(adjCell);
        }
    }

    private void shakeOn(Cell cell) {
        WorldSquad caster = getSquad();
        cell.addFloatLabel("Earth Shake", Color.ORANGE);

        if (caster.canAttack(cell)) {
            // stun
            StunnedEffect stunnedEffect = cell.squad.getEffectManager().getOrCreate(StunnedEffect.class);
            stunnedEffect.resetTickCounter();
            // dmg
            caster.power.freeNextAttack = true;
            new AttackCalculation(caster, cell).setCustomDamage(BASE_DMG).start();
        }

    }
}
