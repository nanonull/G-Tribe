package conversion7.game.stages.world.objects.actions.items;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import conversion7.game.Assets;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.actions.AbstractHumanSquadAction;
import conversion7.game.stages.world.objects.unit.AttackCalculation;
import conversion7.game.stages.world.objects.unit.WorldSquad;

public class LightningAction extends AbstractHumanSquadAction {

    public static final int RADIUS = 1;
    public static final int BASE_DMG = (int) (FireballAction.BASE_DMG * 0.6f);
    public static final int MANA_COST = 10;
    public static final String DESC = "Throw lightning on cell. " +
            "\nSplash radius: " + RADIUS +
            "\nCentral unit is stunned." +
            "\nDamage: " + BASE_DMG;

    public LightningAction() {
        super(Group.ATTACK);
    }

    @Override
    public TextureRegion getIconTexture() {
        return Assets.getTextureReg("lightning");
    }

    @Override
    public String getActionWorldHint() {
        return "throw lightning";
    }

    @Override
    public int getDistance() {
        return getSquad().getViewRadius(false);
    }

    @Override
    public String getShortName() {
        return "LGHTN";
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
        lightOn(input);
        stunOn(input);
        for (Cell adjCell : input.getCellsAroundToRadiusInclusively(RADIUS)) {
            lightOn(adjCell);
        }
    }

    private void stunOn(Cell cell) {
        if (cell.hasSquad()) {
            StunningAction.addStunOn(cell.squad);
        }
    }

    private void lightOn(Cell cell) {
        WorldSquad attacker = getSquad();
        cell.addFloatLabel("Lightning", Color.ORANGE);
        if (attacker.canAttack(cell)) {
            attacker.power.freeNextAttack = true;
            new AttackCalculation(attacker, cell).setCustomDamage(BASE_DMG).start();
        }
    }
}
