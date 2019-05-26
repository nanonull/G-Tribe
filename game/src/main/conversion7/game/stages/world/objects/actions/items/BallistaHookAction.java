package conversion7.game.stages.world.objects.actions.items;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.actions.AbstractHumanSquadAction;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.objects.unit.WorldSquad;
import conversion7.game.stages.world.team.skills.SkillType;

public class BallistaHookAction extends AbstractHumanSquadAction {

    public static final int BASE_DMG = HookAction.BASE_DMG * 2;
    public static final Array<SkillType> SKILLS_REQ = new Array<>();

    static {
        SKILLS_REQ.add(SkillType.ARMS);
        SKILLS_REQ.add(SkillType.BRAIN);
    }

    public BallistaHookAction() {
        super(Group.ATTACK);
    }

    @Override
    public String getActionWorldHint() {
        return "to shot";
    }


    @Override
    public int getDistance() {
        return getSquad().getViewRadius(false) * 2;
    }

    @Override
    public String getShortName() {
        return "BHook";
    }


    @Override
    protected Color getTargetCellSelectionColor(Cell cellAround) {
        return Color.SCARLET;
    }

    @Override
    public String buildDescription() {
        return getName() + "\n" +
                "\nRange dmg " + BASE_DMG +
                "\nSkills required: " + SKILLS_REQ;
    }

    @Override
    public void handleAcceptedInput(Cell input) {
        throwOn(input);
    }

    private void throwOn(Cell targCell) {
        WorldSquad squad = getSquad();
        AbstractSquad targSq = targCell.squad;
        targSq.hurtBy(BASE_DMG, squad);
        if (targSq.isAlive()) {
            HookAction.hook(squad, squad.cell, targCell);
        }
    }
}
