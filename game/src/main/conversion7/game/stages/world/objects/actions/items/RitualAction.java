package conversion7.game.stages.world.objects.actions.items;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import conversion7.engine.utils.MathUtils;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.Ritual;
import conversion7.game.stages.world.objects.actions.AbstractWorldTargetableAction;
import conversion7.game.stages.world.objects.buildings.Camp;
import conversion7.game.stages.world.team.Team;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.stages.world.unit.effects.AbstractUnitEffect;
import conversion7.game.stages.world.unit.effects.items.IncreaseBattleParamsEffect;
import conversion7.game.stages.world.unit.effects.items.IncreaseViewRadiusEffect;
import conversion7.game.stages.world.unit.effects.items.ScaredEffect;
import org.testng.Assert;

@Deprecated
public class RitualAction extends AbstractWorldTargetableAction {

    public static final int EVOLUTION_EXP_PER_FOOD = 2;
    public static final Array<Class<? extends AbstractUnitEffect>> POSSIBLE_EFFECT_CLASSES = new Array<>();

    static {
        Assert.assertTrue(EVOLUTION_EXP_PER_FOOD % 2 == 0, "EVOLUTION_EXP_PER_FOOD should be odd!");
        POSSIBLE_EFFECT_CLASSES.add(IncreaseBattleParamsEffect.class);
        POSSIBLE_EFFECT_CLASSES.add(IncreaseViewRadiusEffect.class);
        POSSIBLE_EFFECT_CLASSES.add(ScaredEffect.class);
    }

    public RitualAction() {
        super(Group.TRIBE);
    }

    public int getDistance() {
        return 1;
    }

    private static AbstractUnitEffect getRandomEffect() {
        int nextInt = MathUtils.RANDOM.nextInt(POSSIBLE_EFFECT_CLASSES.size);
        try {
            return POSSIBLE_EFFECT_CLASSES.get(nextInt).newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getActionWorldHint() {
        return "execute ritual";
    }

    public static void doRitual(Unit unitExecutor, Cell onCell) {
        if (onCell.ritual == null) {
            onCell.ritual = new Ritual(onCell, unitExecutor.squad.team);
        }
        onCell.ritual.makeProgress();
    }

    public static boolean canDoOnCell(Unit unit, Cell cell) {
        Camp camp = cell.camp;
        Team myTeam = unit.squad.team;
        if (camp != null && camp.isConstructionCompleted() && camp.getLastCell().hasSquad()
                && camp.getLastCell().squad.team == myTeam) {
            Ritual ritual = camp.getLastCell().ritual;
            if (ritual == null || (ritual.team == myTeam
                    && ritual.getLastCell().squad == ritual.getRitualFor()
                    && !ritual.canBeFinished())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String buildDescription() {
        return getName() + "\n \n" +
                "\n" + Ritual.STEPS + " units should execute ritual action to complete ritual." +
                "\nTarget unit should be in camp.";
    }

    @Override
    public void handleAcceptedInput(Cell input) {
        updateExecutorParameters();
        doRitual(getSquad().unit, input);
    }

    @Override
    public void cancel() {
    }

    @Override
    protected Color getTargetCellSelectionColor(Cell cellAround) {
        return Color.CYAN;
    }
}
