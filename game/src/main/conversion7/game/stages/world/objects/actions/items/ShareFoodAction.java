package conversion7.game.stages.world.objects.actions.items;

import conversion7.engine.Gdxg;
import conversion7.game.stages.world.actions.AbstractAction;
import conversion7.game.stages.world.objects.actions.items.subactions.InputFoodForShareSubaction;
import conversion7.game.stages.world.objects.unit.AbstractSquad;

// TODO make ShareFoodAction using trade/barter inventory
@Deprecated
public class ShareFoodAction extends AbstractAction {

    private final AbstractSquad initiator;
    private AbstractSquad target;
    private InputFoodForShareSubaction inputFoodForShareSubaction;

    public ShareFoodAction(AbstractSquad initiator, AbstractSquad target) {
        this.initiator = initiator;
        this.target = target;
        inputFoodForShareSubaction = new InputFoodForShareSubaction(this);
    }

    public AbstractSquad getInitiator() {
        return initiator;
    }

    /** Action chain:  select food amount > press OK */
    public void execute() {
        inputFoodForShareSubaction.execute();
    }

    public void complete(int food) {
//        initiator.getFoodStorage().updateFoodOnValueAndValidate(-food);
//        target.getFoodStorage().updateFoodOnValueAndValidate(+food);
        target.getTeam().updatedAttitude(initiator.getTeam(), +1);
        Gdxg.getAreaViewer().unhideSelection();
    }

    @Override
    public void cancel() {
        Gdxg.getAreaViewer().unhideSelection();
    }
}
