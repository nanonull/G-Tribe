package conversion7.game.stages.world.objects.actions;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import conversion7.engine.Gdxg;
import conversion7.game.Assets;
import conversion7.game.interfaces.TargetableOnObject;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.objects.actions.subactions.InputFoodForShareSubaction;
import conversion7.game.stages.world.objects.actions.subactions.SelectHumanSquadOrTownSubAction;

public class ShareFoodAction extends AbstractAreaObjectAction implements TargetableOnObject {

    private SelectHumanSquadOrTownSubAction selectHumanSquadOrTownSubAction = new SelectHumanSquadOrTownSubAction(this);
    private AreaObject target;
    private InputFoodForShareSubaction inputFoodForShareSubaction = new InputFoodForShareSubaction(this);

    public ShareFoodAction(AreaObject object) {
        super(object);
    }

    /** Action chain: select object > select food amount > press OK */
    @Override
    public void execute() {
        Gdxg.clientUi.hideBarsForSelectedObject();
        selectHumanSquadOrTownSubAction.execute();
    }

    @Override
    public TextureRegion getIconTexture() {
        return Assets.homeIcon;
    }

    @Override
    public void setTarget(AreaObject target) {
        this.target = target;
        inputFoodForShareSubaction.execute();
    }

    public void complete(int food) {
        getObject().getFoodStorage().updateFoodOnValueAndValidate(-food);
        target.getFoodStorage().updateFoodOnValueAndValidate(+food);
        target.getTeam().updatedAttitude(getObject().getTeam(), +1);
        World.getAreaViewer().unhideSelection();
    }

    @Override
    public void cancel() {
        World.getAreaViewer().unhideSelection();
    }
}
