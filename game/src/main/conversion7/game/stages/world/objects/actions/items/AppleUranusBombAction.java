package conversion7.game.stages.world.objects.actions.items;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import conversion7.engine.artemis.ui.float_lbl.FloatingStatusOnCellSystem;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.actions.AbstractHumanSquadAction;
import conversion7.game.stages.world.objects.buildings.ResourceCosts;
import conversion7.game.stages.world.objects.unit.AttackCalculation;
import conversion7.game.stages.world.objects.unit.WorldSquad;
import conversion7.game.stages.world.team.skills.SkillType;
import conversion7.game.unit_classes.UnitClassConstants;

public class AppleUranusBombAction extends AbstractHumanSquadAction {

    public static final int RADIUS = 1;
    public static final Array<SkillType> SKILLS_REQ = new Array<>();
    private static final int BASE_DMG = (int) (UnitClassConstants.BASE_POWER * 0.95f);

    static {
        SKILLS_REQ.add(SkillType.BRAIN);
        SKILLS_REQ.add(SkillType.ARMS);
        SKILLS_REQ.add(SkillType.WEAPON_MASTERY);
        SKILLS_REQ.add(SkillType.UFO_WEAPON);
    }

    public AppleUranusBombAction() {
        super(Group.ATTACK);
    }

    @Override
    public String getActionWorldHint() {
        return "to bomb";
    }

    @Override
    public int getDistance() {
        return getSquad().getViewRadius(false);
    }

    @Override
    public String getShortName() {
        return "Bomb";
    }

    @Override
    protected Color getTargetCellSelectionColor(Cell cellAround) {
        return Color.SCARLET;
    }

    @Override
    public String buildDescription() {
        return getName() + "\n " +
                "\nStuns units and deal " + BASE_DMG + " dmg" +
                "\nRadius: " + RADIUS +
                "\nSkills required: " + SKILLS_REQ +
                "\nResource cost: " + ResourceCosts.getCostAsString(AppleUranusBombAction.class);
    }

    @Override
    public void handleAcceptedInput(Cell input) {
        bombOn(input);
        for (Cell adjCell : input.getCellsAroundToRadiusInclusively(RADIUS)) {
            bombOn(adjCell);
        }
        getSquad().getInventory().remove(ResourceCosts.getCost(AppleUranusBombAction.class));
    }

    private void bombOn(Cell cell) {
        WorldSquad caster = getSquad();
        FloatingStatusOnCellSystem.scheduleMessage(cell, caster.team, "! BOOOM !", Color.ORANGE);

        if (getSquad().canAttack(cell)) {
            getSquad().power.freeNextAttack = true;
            new AttackCalculation(getSquad(), cell).setCustomDamage(BASE_DMG).start();
        }

    }
}
