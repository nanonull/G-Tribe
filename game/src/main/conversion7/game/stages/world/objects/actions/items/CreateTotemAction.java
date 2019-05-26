package conversion7.game.stages.world.objects.actions.items;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.GdxRuntimeException;
import conversion7.engine.utils.MathUtils;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.actions.AbstractWorldTargetableAction;
import conversion7.game.stages.world.objects.totem.AttackTotem;
import conversion7.game.stages.world.objects.totem.DefenceTotem;
import conversion7.game.stages.world.objects.totem.HealingTotem;
import conversion7.game.stages.world.objects.totem.ViewDistanceTotem;
import org.slf4j.Logger;

public class CreateTotemAction extends AbstractWorldTargetableAction {

    private static final Logger LOG = Utils.getLoggerForClass();

    public CreateTotemAction() {
        super(Group.COMMON);
    }

    public int getDistance() {
        return 1;
    }

    @Override
    public String getShortName() {
        return "Totem";
    }

    @Override
    public String getActionWorldHint() {
        return "create totem";
    }

    @Override
    public String buildDescription() {
        return getName() + "\n \n";
    }

    @Override
    public void handleAcceptedInput(Cell input) {
        switch (MathUtils.RANDOM.nextInt(4)) {
            case 0:
                new AttackTotem(input, getSquad().team);
                break;
            case 1:
                new DefenceTotem(input, getSquad().team);
                break;
            case 2:
                new HealingTotem(input, getSquad().team);
                break;
            case 3:
                new ViewDistanceTotem(input, getSquad().team);
                break;
            default:
                throw new GdxRuntimeException("");
        }

    }

    @Override
    protected Color getTargetCellSelectionColor(Cell cellAround) {
        return Color.CYAN;
    }
}
