package conversion7.game.stages.world.objects.actions.items;

import conversion7.game.dialogs.SelectSkillDialog;
import conversion7.game.stages.world.objects.actions.AbstractSquadAction;

public class SelectSkillAction extends AbstractSquadAction {
    public SelectSkillAction() {
        super(Group.COMMON);
    }

    @Override
    public String getShortName() {
        return "SKILL";
    }

    @Override
    public void begin() {
        new SelectSkillDialog(getSquad()).start();
    }

    @Override
    protected String buildDescription() {
        return getName();
    }
}
