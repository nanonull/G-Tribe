package conversion7.game.stages.battle_deprecated2;

import conversion7.engine.utils.Utils;
import conversion7.game.stages.battle_deprecated.BattleUnitsSpeedComparator;
import conversion7.game.stages.world.unit.Unit;
import org.slf4j.Logger;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RoundStep {

    private static final Logger LOG = Utils.getLoggerForClass();

    private BattleField battleField;
    private Type type;
    private List<BattleUnit> activeAliveUnits = new ArrayList<>();

    public RoundStep(BattleField battleField, Type type) {
        this.battleField = battleField;
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    /** Returns unit of winner team */
    public BattleUnit execute() {
        collectActiveAliveUnits();

        activeAliveUnits.sort(BattleUnitsSpeedComparator.INSTANCE);
        for (BattleUnit battleUnit : activeAliveUnits) {
            if (!battleUnit.isAlive()) {
                LOG.info("Unit killed during round: {}", battleUnit.getUnit());
                continue;
            }

            BattleUnit enemyBattleUnit = battleUnit.getCell().getRandomEnemyUnit();
            if (enemyBattleUnit == null) {
                return battleUnit;
            }

            Unit enemyUnit = enemyBattleUnit.getUnit();
            Assert.assertTrue(enemyUnit.squad.isAlive());

            enemyUnit.squad.setDeathValidationForNextHurtActive(false);
            Unit unit = battleUnit.getUnit();
            switch (type) {
                case RANGE:
                    if (unit.squad.canRangeAttack()) {
//                        unit.rangeHit(enemyUnit);
                    }
                    break;
                case MIXED:
//                    unit.attackByBestWeapon(enemyUnit);
                    break;
                default:
                    throw new RuntimeException("unsupported");
            }
            enemyUnit.squad.setDeathValidationForNextHurtActive(true);

            if (enemyBattleUnit.validateUnitDeathDuringRound()) {
                if (battleUnit.getCell().getRandomEnemyUnit() == null) {
                    return battleUnit;
                }
            }
        }

        return null;
    }

    private void collectActiveAliveUnits() {
        activeAliveUnits.clear();
        for (BattleCell activeCell : battleField.getActiveCells()) {
            for (Map.Entry<Unit, BattleUnit> unitEntry : activeCell.getActiveUnitsMap().entrySet()) {
                Unit worldUnit = unitEntry.getKey();
                if (worldUnit.squad.isAlive()) {
                    activeAliveUnits.add(unitEntry.getValue());
                }
            }
        }
    }

    enum Type {
        RANGE, MIXED
    }
}
