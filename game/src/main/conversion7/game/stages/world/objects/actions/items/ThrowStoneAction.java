package conversion7.game.stages.world.objects.actions.items;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.inventory.InventoryItemStaticParams;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.actions.AbstractHumanSquadAction;
import conversion7.game.stages.world.objects.buildings.ResourceCosts;
import conversion7.game.stages.world.objects.unit.AttackCalculation;
import conversion7.game.stages.world.objects.unit.WorldSquad;
import conversion7.game.stages.world.team.skills.SkillType;

public class ThrowStoneAction extends AbstractHumanSquadAction {

    public static final int BASE_DMG = InventoryItemStaticParams.STICK.getMeleeDamage();
    public static final Array<SkillType> SKILLS_REQ = new Array<>();
    private static final int RADIUS = World.BASE_VIEW_RADIUS - 1;

    static {
        SKILLS_REQ.add(SkillType.ARMS);
    }

    public ThrowStoneAction() {
        super(Group.ATTACK);
    }

    @Override
    public String getActionWorldHint() {
        return "to throw stone";
    }

    @Override
    public int getDistance() {
        return getSquad().getViewRadius(false);
    }

    @Override
    public String getShortName() {
        return "Throw";
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
                "\nResource cost: " + ResourceCosts.getCostAsString(ThrowStoneAction.class);
    }

    @Override
    public void handleAcceptedInput(Cell input) {
        getSquad().getInventory().remove(ResourceCosts.getCost(ThrowStoneAction.class));
        throwOn(input);
    }

    private void throwOn(Cell cell) {
        WorldSquad owner = getSquad();
        cell.addFloatLabel("Throw stone", Color.ORANGE);

        if (owner.canAttack(cell)) {
            owner.power.freeNextAttack = true;
            new AttackCalculation(owner, cell).setCustomDamage(BASE_DMG).start();
        }
    }
}
