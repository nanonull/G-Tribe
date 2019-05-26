package conversion7.game.stages.world.objects.actions.items;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import conversion7.game.Assets;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.actions.AbstractHumanSquadAction;
import conversion7.game.stages.world.objects.unit.AttackCalculation;
import conversion7.game.stages.world.objects.unit.WorldSquad;
import conversion7.game.unit_classes.UnitClassConstants;

public class FireballAction extends AbstractHumanSquadAction {

    public static final int RADIUS = 1;
    public static final int BASE_DMG = (int) (UnitClassConstants.BASE_DMG * 2.5f);
    public static final int MANA_COST = 10;
    public static final String DESC = "Throw fireball on cell." +
            "\nSplash radius: " + RADIUS +
            "\nDamage: " + BASE_DMG;

    public FireballAction() {
        super(Group.ATTACK);
    }

    @Override
    public TextureRegion getIconTexture() {
        return Assets.getTextureReg("fireball");
    }

    @Override
    public String getActionWorldHint() {
        return "fireball";
    }

    @Override
    public int getDistance() {
        return getSquad().getViewRadius(false);
    }

    @Override
    public String getShortName() {
        return "FRBL";
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
        fireOn(input);
        for (Cell adjCell : input.getCellsAroundToRadiusInclusively(RADIUS)) {
            fireOn(adjCell);
        }
    }

    private void fireOn(Cell cell) {
        WorldSquad attacker = getSquad();
        cell.addFloatLabel("Fireball", Color.ORANGE);
        if (attacker.canAttack(cell)) {
            attacker.power.freeNextAttack = true;
            new AttackCalculation(attacker, cell).setCustomDamage(BASE_DMG).start();
        }

        if (cell.getLandscape().hasForest() && cell.canBeFired()) {
            getSquad().fireForest(cell);
        }
    }
}
