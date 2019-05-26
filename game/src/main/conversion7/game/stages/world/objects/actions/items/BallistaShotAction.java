package conversion7.game.stages.world.objects.actions.items;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.actions.AbstractHumanSquadAction;
import conversion7.game.stages.world.objects.buildings.ResourceCosts;
import conversion7.game.stages.world.objects.unit.WorldSquad;
import conversion7.game.stages.world.team.skills.SkillType;

import java.util.Iterator;

public class BallistaShotAction extends AbstractHumanSquadAction {

    public static final int BASE_DMG = ScorpionShotAction.BASE_DMG + 1;
    public static final Array<SkillType> SKILLS_REQ = new Array<>();

    static {
        SKILLS_REQ.add(SkillType.ARMS);
        SKILLS_REQ.add(SkillType.BRAIN);
    }

    public BallistaShotAction() {
        super(Group.ATTACK);
    }

    public static Array<Cell> getTeamSightCells(WorldSquad squad, int radius) {
        int viewRadius = radius;
        Array<Cell> visibleCellsOnSight = squad.getLastCell().getCellsAroundToRadiusInclusively(viewRadius);
        Iterator<Cell> iterator = visibleCellsOnSight.iterator();
        while (iterator.hasNext()) {
            Cell cell = iterator.next();
            if (!squad.team.canSeeCell(cell)) {
                iterator.remove();
            }
        }
        return visibleCellsOnSight;
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
        return "BShot";
    }

    @Override
    protected Color getTargetCellSelectionColor(Cell cellAround) {
        return Color.SCARLET;
    }

    @Override
    public String buildDescription() {
        return getName() + "\n " +
                "\nRange dmg " + BASE_DMG +
                "\nSkills required: " + SKILLS_REQ +
                "\nResource cost: " + ResourceCosts.getCostAsString(BallistaShotAction.class);
    }

    @Override
    public void handleAcceptedInput(Cell input) {
        throwOn(input);
        getSquad().getInventory().remove(ResourceCosts.getCost(BallistaShotAction.class));
    }

    private void throwOn(Cell cell) {
        cell.addFloatLabel("Ballista shot", Color.ORANGE);

        if (cell.hasSquad()) {
            if (cell.squad.hurtBy(BASE_DMG, getSquad())) {
            }
        }

    }
}
