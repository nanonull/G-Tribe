package conversion7.game.stages.world.team;

import conversion7.game.stages.world.unit.Unit;

public class UnitClassTeamInfo {

    private Class<? extends Unit> unitClass;
    private int amount;

    public UnitClassTeamInfo(Class<? extends Unit> unitClass, int amount) {
        this.unitClass = unitClass;
        this.amount = amount;
    }

    public Class<? extends Unit> getUnitClass() {
        return unitClass;
    }

    public int getAmount() {
        return amount;
    }

    public void updateClassAmount(int onValue) {
        amount += onValue;
    }
}
