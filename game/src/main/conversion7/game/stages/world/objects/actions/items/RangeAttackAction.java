package conversion7.game.stages.world.objects.actions.items;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import conversion7.game.Assets;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.actions.AbstractHumanSquadAction;

public class RangeAttackAction extends AbstractHumanSquadAction {


    public RangeAttackAction() {
        super(Group.ATTACK);
    }

    @Override
    public String getActionWorldHint() {
        return "range hit";
    }

    @Override
    public String getShortName() {
        return "Range";
    }

    @Override
    public int getDistance() {
        return getSquad().getViewRadius(false);
    }

    @Override
    public TextureRegion getIconTexture() {
        return Assets.getTextureReg("bow");
    }

    @Override
    protected Color getTargetCellSelectionColor(Cell cellAround) {
        return Color.SCARLET;
    }

    @Override
    public String buildDescription() {
        return getName() + "\n \nAttack using equipped range weapon.\n" +
                (getSquad().equipment.hasRangeWeap()
                        ? getSquad().equipment.getBulletCostAndActualAmount() : "");
    }

    @Override
    public void handleAcceptedInput(Cell input) {
        getSquad().initAttack(input).setMeleeAttack(false).start();

    }
}
