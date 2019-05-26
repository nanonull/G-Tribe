package conversion7.game.stages.world.objects.actions.items;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import conversion7.game.stages.world.inventory.InventoryItemStaticParams;
import conversion7.game.stages.world.landscape.BrezenhamLine;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.actions.AbstractHumanSquadAction;
import conversion7.game.stages.world.objects.buildings.ResourceCosts;
import conversion7.game.stages.world.team.skills.SkillType;

public class BallistaJavelinShotAction extends AbstractHumanSquadAction {

    public static final int BASE_DMG = BallistaShotAction.BASE_DMG +
            InventoryItemStaticParams.ATLATL.getRangedDamage();
    public static final Array<SkillType> SKILLS_REQ = new Array<>();

    static {
        SKILLS_REQ.add(SkillType.ARMS);
        SKILLS_REQ.add(SkillType.BRAIN);
    }

    public BallistaJavelinShotAction() {
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
        return "BJavelin";
    }


    @Override
    protected Color getTargetCellSelectionColor(Cell cellAround) {
        return Color.SCARLET;
    }

    @Override
    public String buildDescription() {
        return getName() + "\nIt damages all units on path during fly" +
                "\nRange dmg " + BASE_DMG +
                "\nSkills required: " + SKILLS_REQ +
                "\nResource cost: " + ResourceCosts.getCostAsString(BallistaJavelinShotAction.class);
    }

    @Override
    public void handleAcceptedInput(Cell input) {
        throwOn(input);
        getSquad().getInventory().remove(ResourceCosts.getCost(BallistaJavelinShotAction.class));
    }

    private void throwOn(Cell targCell) {
        Array<Cell> cellsLine = BrezenhamLine.getCellsLine(targCell, getSquad().cell);
        cellsLine.removeIndex(cellsLine.size - 1);
        for (Cell cell : cellsLine) {
            cell.addFloatLabel("Javelin shot", Color.ORANGE);
            if (cell.hasSquad()) {
                if (cell.squad.hurtBy(BASE_DMG, getSquad())) {
                }
            }
        }
    }
}
