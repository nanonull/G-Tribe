package conversion7.game.stages.world.objects.actions.items;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.actions.AbstractHumanSquadAction;
import conversion7.game.stages.world.objects.buildings.ResourceCosts;
import conversion7.game.stages.world.team.skills.SkillType;


public class BallistaVolleyShotAction extends AbstractHumanSquadAction {

    public static final Array<SkillType> SKILLS_REQ = new Array<>();
    public static final int SHOTS = 5;
    private static final int BASE_DMG = BallistaShotAction.BASE_DMG;

    static {
        SKILLS_REQ.add(SkillType.ARMS);
        SKILLS_REQ.add(SkillType.BRAIN);
    }

    public BallistaVolleyShotAction() {
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
        return "BVolley";
    }


    @Override
    protected Color getTargetCellSelectionColor(Cell cellAround) {
        return Color.SCARLET;
    }

    @Override
    public String buildDescription() {
        return getName() + "\n " +
                "\nMake " + SHOTS + " shots: 1st on target cell, other on random cells around" +
                "\nRange dmg " + BASE_DMG + "x" + SHOTS +
                "\nSkills required: " + SKILLS_REQ +
                "\nResource cost: " + ResourceCosts.getCostAsString(BallistaVolleyShotAction.class);
    }

    @Override
    public void handleAcceptedInput(Cell input) {
        startOn(input);
        getSquad().getInventory().remove(ResourceCosts.getCost(BallistaVolleyShotAction.class));
    }

    private void startOn(Cell cell) {
        shotOn(cell);

        Array<Cell> cells = cell.getCellsAroundOnRadius(1, new Array<>());
        cells.add(cell);

        for (int i = 0; i < 4; i++) {
            shotOn(cells.random());
        }
    }

    private void shotOn(Cell cell) {
        cell.addFloatLabel("Volley shot", Color.ORANGE);
        if (cell.hasSquad()) {
            if (cell.squad.hurtBy(BASE_DMG, getSquad())) {
            }
        }
    }
}
