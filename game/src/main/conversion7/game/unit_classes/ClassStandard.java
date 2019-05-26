package conversion7.game.unit_classes;

import com.badlogic.gdx.utils.Array;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.stages.world.unit.UnitParameterType;
import conversion7.game.stages.world.unit.UnitParameters;

public class ClassStandard {

    public Array<Class<? extends Unit>> childClasses = new Array<>();
    public Class<? extends Unit> unitClass;
    /**Evolution level*/
    public int level;
    private UnitParameters params;
    private TimeFrame birthTimeFrame;
    public Integer migrationTemperature;
    public boolean aggrAnimal;
    public boolean scaringAnimal;
    public String classShortName = "";
    public Integer skin;
    public Integer tooth;
    public Integer fang;
    public Integer tusk;

    public ClassStandard(Class<? extends Unit> unitClass, int level, UnitParameters params
            , Array<Class<? extends Unit>> childClasses) {
        this.unitClass = unitClass;
        this.level = level;
        UnitClassConstants.maxClassLvl = Math.max(UnitClassConstants.maxClassLvl, level);
        this.params = params;
        this.childClasses.addAll(childClasses);
        // TODO define birth timeLine for different Animal classes
        birthTimeFrame = new TimeFrame(-50000000, 2000);
    }

    public Array<Class<? extends Unit>> getChildClasses() {
        return childClasses;
    }

    public UnitParameters getParams() {
        return params;
    }

    public TimeFrame getBirthTimeFrame() {
        return birthTimeFrame;
    }

    public String describe() {
        StringBuilder stringBuilder = new StringBuilder(unitClass.getSimpleName()).append(" - ")
                .append("Class evolution Level ").append(level).append("\n \n")
                .append("Base power ").append(getBasePower()).append("\n \n");
        if (childClasses.size > 0) {
            stringBuilder.append("Child classes:\n");
            for (Class<? extends Unit> childClass : childClasses) {
                stringBuilder.append(" * ").append(childClass.getSimpleName()).append("\n");
            }
        }
        return stringBuilder.toString();

    }

    @Override
    public String toString() {
        return describe();
    }

    public int getBasePower() {
        return params.get(UnitParameterType.STRENGTH);
    }
}
