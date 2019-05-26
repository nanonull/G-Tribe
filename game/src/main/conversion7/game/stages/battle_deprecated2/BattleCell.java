package conversion7.game.stages.battle_deprecated2;

import conversion7.engine.utils.MathUtils;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.unit.Unit;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BattleCell {


    private static final List<BattleUnit> battleUnitsWip = new ArrayList<>();
    private Map<Unit, BattleUnit> activeUnitsMap = new HashMap<>();
    private Map<Unit, BattleUnit> allActiveUnitsMap = new HashMap<>();
    private List<BattleCell> enemyCells = new ArrayList<>();
    private Cell worldCell;
    private Type type;
    private AbstractSquad squad;
    private BattleField battleField;

    public BattleCell(BattleField battleField, Cell worldCell, List<Unit> activeUnits, Type type) {
        this.battleField = battleField;
        AbstractSquad seizedBySquad = worldCell.getSquad();
        if (seizedBySquad == null) {
            this.type = Type.EMPTY;
            return;
        }

        this.worldCell = worldCell;
        this.squad = seizedBySquad;
        this.type = type;

        Unit unit = seizedBySquad.unit;
        BattleUnit battleUnit = new BattleUnit(unit);
        battleUnit.setCell(this);
        activeUnitsMap.put(unit, battleUnit);
        allActiveUnitsMap.put(unit, battleUnit);
        unit.squad.setMadeAttacks(unit.squad.getMadeAttacks() + 1);

    }

    public Type getType() {
        return type;
    }

    public AbstractSquad getSquad() {
        return squad;
    }

    public Map<Unit, BattleUnit> getActiveUnitsMap() {
        return activeUnitsMap;
    }

    public Cell getWorldCell() {
        return worldCell;
    }

    public Map<Unit, BattleUnit> getAllActiveUnitsMap() {
        return allActiveUnitsMap;
    }

    public BattleField getBattleField() {
        return battleField;
    }

    public BattleUnit getRandomEnemyUnit() {
        Assert.assertFalse(enemyCells.isEmpty());
        battleUnitsWip.clear();
        for (BattleCell enemyCell : enemyCells) {
            for (Map.Entry<Unit, BattleUnit> battleUnitEntry : enemyCell.activeUnitsMap.entrySet()) {
                battleUnitsWip.add(battleUnitEntry.getValue());
            }
        }
        if (battleUnitsWip.isEmpty()) {
            return null;
        }
        Collections.shuffle(battleUnitsWip, MathUtils.RANDOM);
        return battleUnitsWip.get(0);
    }

    public boolean isAlive() {
        return !squad.isRemovedFromWorld();
    }

    public void addEnemy(BattleCell otherCell) {
        enemyCells.add(otherCell);
    }

    public void validateUnits() {
        for (Map.Entry<Unit, BattleUnit> battleUnitEntry : getAllActiveUnitsMap().entrySet()) {
            battleUnitEntry.getValue().validateUnitDeathAfterBattle();
        }
    }

    enum Type {
        ATTACKER, DEFENDER, NEIGHBOR, EMPTY
    }
}
