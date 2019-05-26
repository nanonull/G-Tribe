package conversion7.game.stages.world.objects.actions.items;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import conversion7.game.Assets;
import conversion7.game.stages.world.ai_deprecated.tasks.single.AttackTaskOldOld;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.actions.AbstractWorldTargetableAction;
import conversion7.game.stages.world.unit.UnitAge;

public class ProvokeAction extends AbstractWorldTargetableAction {

    public static final String DESC = "Target unit will try to chase and attack me";

    public ProvokeAction() {
        super(Group.ATTACK);
    }

    public static boolean testAge(UnitAge age) {
        return age.getLevel() >= UnitAge.ADULT.getLevel();
    }

    @Override
    public TextureRegion getIconTexture() {
        return Assets.getTextureReg("provoke");
    }

    @Override
    public String getActionWorldHint() {
        return "provoke for attack";
    }

    @Override
    public int getDistance() {
        return getSquad().getViewRadius(false);
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
        input.squad.addTaskToWipSet(new AttackTaskOldOld(input.squad, getSquad()));
    }
}
