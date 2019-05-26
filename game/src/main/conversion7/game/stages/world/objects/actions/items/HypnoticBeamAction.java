package conversion7.game.stages.world.objects.actions.items;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.actions.AbstractWorldTargetableAction;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.team.Team;

public class HypnoticBeamAction extends AbstractWorldTargetableAction {
    public HypnoticBeamAction() {
        super(Group.ATTACK);
    }

    @Override
    public String getActionWorldHint() {
        return "to capture tribe";
    }

    @Override
    public int getDistance() {
        return getSquad().getViewRadius(false);
    }

    @Override
    public String getShortName() {
        return "Hypno";
    }

    @Override
    protected Color getTargetCellSelectionColor(Cell cellAround) {
        return Color.SCARLET;
    }

    @Override
    public void handleAcceptedInput(Cell input) {
        Team team = input.squad.team;
        for (AbstractSquad squad : new Array<>(team.getSquads())) {
//            squad.cell.addFloatLabel("Unit captured", Color.CYAN);
//            getSquad().team.joinSquad(squad);
            CaptureUnitAction.tryCapture(getSquad(), squad);
        }
        getSquad().archonHypnoCharges--;
    }

    @Override
    protected String buildDescription() {
        return getName() + "\n \nTry to capture whole tribe of human-like creatures."/* +
                "\nOne charge per " + Archon.class.getSimpleName() + " unit"*/;
    }
}
