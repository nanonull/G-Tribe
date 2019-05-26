package conversion7.game.stages.world.objects.actions.items;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import conversion7.game.Assets;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.actions.AbstractWorldTargetableAction;

public class MeleeAttackAction extends AbstractWorldTargetableAction {

    public MeleeAttackAction() {
        super(Group.ATTACK);
    }

    @Override
    public TextureRegion getIconTexture() {
        return Assets.swordIcon;
    }

    public int getDistance() {
        return 1;
    }

    @Override
    public String getActionWorldHint() {
        return "melee hit";
    }

    @Override
    protected Color getTargetCellSelectionColor(Cell cellAround) {
        return Color.FIREBRICK;
    }

    @Override
    public String buildDescription() {
        return getName();
    }

    @Override
    public void handleAcceptedInput(Cell input) {
        getSquad().initAttack(input).setMeleeAttack(true).start();
    }

}
