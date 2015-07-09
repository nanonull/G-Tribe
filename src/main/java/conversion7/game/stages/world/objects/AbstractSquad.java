package conversion7.game.stages.world.objects;

import com.badlogic.gdx.utils.Array;
import conversion7.engine.pools.system.PoolManager;
import conversion7.engine.utils.Utils;
import conversion7.game.GdxgConstants;
import conversion7.game.stages.world.WorldPath;
import conversion7.game.stages.world.inventory.InventoryItemStaticParams;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.landscape.PathData;
import conversion7.game.stages.world.objects.actions.FireAction;
import conversion7.game.stages.world.objects.actions.RangeAttackAction;
import conversion7.game.stages.world.team.Team;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.ui.UiLogger;
import conversion7.game.utils.collections.IterationRegistrators;
import org.slf4j.Logger;
import org.testng.Assert;

public abstract class AbstractSquad extends AreaObject {

    private static final Logger LOG = Utils.getLoggerForClass();

    private Array<Unit> readyRangeUnits = new Array<>();

    public AbstractSquad(Cell cell, Team team) {
        super(cell, team);
    }

    /** Returns true if target reached or further path is unavailable */
    public boolean moveOneStepTo(Cell target) {
        if (LOG.isDebugEnabled()) LOG.debug(this + " moveOneStepTo " + target);
        Array<PathData> path = WorldPath.getPath(this.getCell(), target);
        if (path == null) {
            return true;
        }

        boolean lastStep = false;
        if (path.size == 1) {
            lastStep = true;
        }
        this.moveOn(path.get(0).cell);
        PoolManager.ARRAYS_POOL.free(path);

        return lastStep;
    }

    @Override
    protected void initActions() {
        super.initActions();
    }

    @Override
    public void validateActions() {
        super.validateActions();
        if (getTeam().getTeamSkillsManager().getFireSkill().isLearned()) {
            addActionIfAbsent(FireAction.class);
        }
    }

    @Override
    public void validateReadyRangeUnits() {
        readyRangeUnits.clear();
        IterationRegistrators.UNITS_ITERATION_REGISTRATOR.start();
        for (int i = 0; i < units.size; i++) {
            Unit unit = units.get(i);
            if (unit.isRangeAttackPossible()) {
                readyRangeUnits.add(unit);
            }
        }
        IterationRegistrators.UNITS_ITERATION_REGISTRATOR.end();

        if (isRangeAttackPossible()) {
            addActionIfAbsent(RangeAttackAction.class);
        } else {
            removeActionIfExist(RangeAttackAction.class);
        }
    }

    public boolean isRangeAttackPossible() {
        return readyRangeUnits.size > 0;
    }

    public void executeRangeAttack(AreaObject targetSquad) {
        LOG.info(String.format("executeRangeAttack [who: %s] > [target: %s]", this, targetSquad));
        Assert.assertTrue(isRangeAttackPossible(), "readyRangeUnits were not validated before!");

        for (Unit readyRangeUnit : readyRangeUnits) {
            LOG.info("Unit tries range attack: " + readyRangeUnit);

            int unitRangeAttackChance = readyRangeUnit.getRangeAttackChance();
            int thrownAttackChance = Utils.RANDOM.nextInt(InventoryItemStaticParams.MAX_ATTACK_CHANCE);

            int rangeDamage = readyRangeUnit.getRangeDamage();
            readyRangeUnit.getEquipment().spendRangeBullet(targetSquad.getCell().getInventory());
            if (unitRangeAttackChance > thrownAttackChance || GdxgConstants.NO_ATTACK_MISS) {
                Unit targetUnit = targetSquad.getUnits().get(Utils.RANDOM.nextInt(targetSquad.getUnits().size));
                readyRangeUnit.hit(targetUnit, rangeDamage);
            } else {
                UiLogger.addInfoLabel("Range attack miss!");
            }

            if (targetSquad.getUnits().size == 0) {
                break;
            }
        }

        validate();
        targetSquad.validateAndDefeat();

        readyRangeUnits.clear();
    }
}
