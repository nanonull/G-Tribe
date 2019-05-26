package conversion7.game.stages.world.objects.actions.items;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import conversion7.game.stages.world.inventory.InventoryItemStaticParams;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.actions.AbstractHumanSquadAction;
import conversion7.game.stages.world.objects.buildings.ResourceCosts;
import conversion7.game.stages.world.objects.unit.AttackCalculation;
import conversion7.game.stages.world.objects.unit.WorldSquad;
import conversion7.game.stages.world.team.skills.SkillType;

public class ScorpionShotAction extends AbstractHumanSquadAction {

    public static final int BASE_DMG = InventoryItemStaticParams.BOW.getRangedDamage() + 1;
    public static final Array<SkillType> SKILLS_REQ = new Array<>();

    static {
        SKILLS_REQ.add(SkillType.ARMS);
        SKILLS_REQ.add(SkillType.BRAIN);
    }

    public ScorpionShotAction() {
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
        return "Scorp";
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
                "\nResource cost: " + ResourceCosts.getCostAsString(ScorpionShotAction.class);
    }

    @Override
    public void handleAcceptedInput(Cell input) {
        throwOn(input);
        getSquad().getInventory().remove(ResourceCosts.getCost(ScorpionShotAction.class));
    }

    private void throwOn(Cell cell) {
        WorldSquad attacker = getSquad();
        cell.addFloatLabel("Scorpion shot", Color.ORANGE);

        if (attacker.canAttack(cell)) {
            attacker.power.freeNextAttack = true;
            new AttackCalculation(attacker, cell).setCustomDamage(BASE_DMG).start();
        }

    }
}
