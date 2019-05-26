package conversion7.game.stages.world.objects.actions.items;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import conversion7.engine.geometry.Point2s;
import conversion7.game.Assets;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.actions.AbstractWorldTargetableAction;
import conversion7.game.stages.world.objects.unit.AbstractSquad;


public class ChargeAction extends AbstractWorldTargetableAction {

    private static final int BASE_DMG = (int) (ThrowStoneAction.BASE_DMG * 2f);
    public static final String DESC = "Run into enemy to knock him and deal " + BASE_DMG + " dmg";

    public ChargeAction() {
        super(Group.ATTACK);
    }

    @Override
    public TextureRegion getIconTexture() {
        return Assets.getTextureReg("charge");
    }

    public int getDistance() {
        return 1;
    }

    @Override
    public String getActionWorldHint() {
        return "charge";
    }

    public static void tryToPush(AbstractSquad attacker, AbstractSquad targSq) {
        Cell attackerCell = attacker.getLastCell();
        Cell targCell = targSq.getLastCell();
        Cell nextCellAfterTarget = getNextCellOnDir(attackerCell, targCell);

        if (nextCellAfterTarget.canBeSeized()) {
            targSq.power.freeNextMove = true;
            targSq.moveOn(nextCellAfterTarget);

            attacker.power.freeNextMove = true;
            attacker.moveOn(targCell);
        }
    }

    public static Cell getNextCellOnDir(Cell from, Cell targCell) {
        Point2s dir = from.getDiffWithCell(targCell).trim(1);
        return targCell.getCell(dir.x, dir.y);
    }

    @Override
    protected Color getTargetCellSelectionColor(Cell cellAround) {
        return Color.SCARLET;
    }

    @Override
    public String buildDescription() {
        return getName()
                + "\n \n" + DESC;
    }

    @Override
    public void handleAcceptedInput(Cell input) {
        AbstractSquad attacker = getSquad();
        input.addFloatLabel("Charge", Color.ORANGE);
        attacker.initAttack(input).setCustomDamage(BASE_DMG).start();

        tryToPush(attacker, input.squad);
    }

}
