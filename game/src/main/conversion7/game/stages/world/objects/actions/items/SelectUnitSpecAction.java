package conversion7.game.stages.world.objects.actions.items;

import conversion7.game.dialogs.SelectSpecDialog;
import conversion7.game.stages.world.objects.actions.AbstractSquadAction;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.unit.hero_classes.SpecClass;

public class SelectUnitSpecAction extends AbstractSquadAction {

    public SelectUnitSpecAction() {
        super(Group.COMMON);
    }

    @Override
    public String getShortName() {
        return "SPEC";
    }


    public static void makeSpec(AbstractSquad squad, SpecClass specClass) {
        squad.setSpec(specClass);
    }

    @Override
    public String buildDescription() {
        return getName() + "\n \n";
    }

    @Override
    public void begin() {
        new SelectSpecDialog(getSquad()).start();
    }

}
