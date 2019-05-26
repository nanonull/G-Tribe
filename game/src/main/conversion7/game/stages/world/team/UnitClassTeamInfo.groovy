package conversion7.game.stages.world.team;

import conversion7.game.stages.world.unit.Unit
import conversion7.game.unit_classes.ClassStandard
import groovy.transform.ToString;


@ToString(includeFields = true, includeNames = true, includePackage = false, excludes = ['metaClass'])
public class UnitClassTeamInfo {

    private Class<? extends Unit> unitClass;
    private int amount;
    ClassStandard classStandard

    public UnitClassTeamInfo(Class<? extends Unit> unitClass, int amount, ClassStandard classStandard) {
        this.classStandard = classStandard
        this.unitClass = unitClass;
        this.amount = amount;
    }

    public Class<? extends Unit> getUnitClass() {
        return unitClass;
    }

    public String getUnitClassName() {
        return unitClass.getSimpleName();
    }

    public String getUnitClassNameShort() {
        return unitClass.getSimpleName().substring(0 , 4);
    }

    public int getAmount() {
        return amount;
    }

    public void updateClassAmount(int onValue) {
        amount += onValue;
    }
}
