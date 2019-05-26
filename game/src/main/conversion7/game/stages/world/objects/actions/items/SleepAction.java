package conversion7.game.stages.world.objects.actions.items;

import com.badlogic.gdx.graphics.Color;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.actions.AbstractWorldTargetableAction;
import conversion7.game.stages.world.unit.effects.items.SleptEffect;

public class SleepAction extends AbstractWorldTargetableAction {

    public static final String DESC = "Make unit sleep.\n \n" + SleptEffect.DESC;

    public SleepAction() {
        super(Group.ATTACK);
    }

    public int getDistance() {
        return 1;
    }

    @Override
    public String getShortName() {
        return "Sleep";
    }

    @Override
    public String getActionWorldHint() {
        return "to sleep";
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
        input.getSquad().effectManager.getOrCreate(SleptEffect.class).resetTickCounter();
    }

}
