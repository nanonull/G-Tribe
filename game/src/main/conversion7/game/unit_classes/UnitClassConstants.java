package conversion7.game.unit_classes;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import conversion7.engine.utils.ExcelFile;
import conversion7.engine.utils.MathUtils;
import conversion7.engine.utils.Normalizer;
import conversion7.engine.utils.ResourceLoader;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.climate.Climate;
import conversion7.game.stages.world.unit.Power2;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.stages.world.unit.UnitParameters;
import conversion7.game.ui.world.team_classes.UnitClassTreeNode;
import conversion7.game.unit_classes.animals.BaseAnimalClass;
import conversion7.game.unit_classes.humans.BaseHumanClass;
import conversion7.game.unit_classes.humans.australopitecus.Australopithecus;
import conversion7.game.unit_classes.humans.australopitecus.Paranthropus;
import conversion7.game.unit_classes.humans.theOldest.Ardipithecus;
import conversion7.game.unit_classes.humans.theOldest.ArdipithecusKadabba;
import conversion7.game.unit_classes.humans.theOldest.ArdipithecusRamidus;
import conversion7.game.unit_classes.humans.theOldest.Chororapithecus;
import conversion7.game.unit_classes.humans.theOldest.Dryopithecus;
import conversion7.game.unit_classes.humans.theOldest.Gorilla;
import conversion7.game.unit_classes.humans.theOldest.Orrorin;
import conversion7.game.unit_classes.humans.theOldest.OrrorinTugenensis;
import conversion7.game.unit_classes.humans.theOldest.Pan;
import conversion7.game.unit_classes.humans.theOldest.Pliopithecus;
import conversion7.game.unit_classes.humans.theOldest.Propliopithecus;
import conversion7.game.unit_classes.humans.theOldest.SahelanthropusTchadensis;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.testng.Assert;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Map.Entry;

public class UnitClassConstants {

    private static final Logger LOG = Utils.getLoggerForClass();

    public static Class BASE_HUMAN_CLASS = Propliopithecus.class;
    public static final int BASE_POWER = 14;
    public static final int BASE_DMG = calcBaseDmg();
    public static final int AGE_UP_POWER_ADD = Math.max(1, BASE_POWER / 8) ;
    public static final int LVL_UP_POWER_ADD = AGE_UP_POWER_ADD * 2 ;
    public static final int LVL_UP_POWER_ADD_TANK = LVL_UP_POWER_ADD * 2;
    public static int maxClassLvl = 1;
    public static final int MAX_PARAM_VALUE = BASE_POWER * 5;
    public static final UnitParameters BASE_PARAMS = new UnitParameters(60, BASE_POWER, BASE_POWER, BASE_POWER);
    public static final Map<Class<? extends Unit>, ClassStandard> CLASS_STANDARDS = new LinkedHashMap<>();
    public static final Array<Class<? extends BaseHumanClass>> HUMAN_CLASSES = new Array<>();
    public static final Array<Class<? extends BaseAnimalClass>> ANIMAL_CLASSES = new Array<>();
    public static final Array<Class<? extends BaseAnimalClass>> RND_ANIMAL_CLASSES = new Array<>();
    public static Map<Class<? extends Unit>, UnitClassTreeNode> DESIGN_TABLE_THE_OLDEST_CLASSES = new HashMap<>();
    /** Stores parameter diffs from Parent class to Child class */
    public static final Map<Class, Map<Class<? extends Unit>, UnitParameters>> DIFFS_FROM_PARENT_TO_CHILD =
            new HashMap<>();
    private static final Comparator<? super Class<? extends BaseAnimalClass>> RANDOM_SORT = (o1, o2) -> MathUtils.random(-1, 1);

    private static int getMaxAnimalLvl() {
        return RND_ANIMAL_CLASSES.size - 1;
    }

    private static int calcBaseDmg() {
        int v = (int) (BASE_POWER * Power2.BASE_DAMAGE_MLT);
        if (v < 1) {
            v = 1;
        }
        return v;
    }

    public static void init() {
        Assert.assertTrue(BASE_POWER > 0);
        try {
            initClassStandards();
            calculateDiffs();
            calculateHumanClassesTree();
        } catch (Throwable e) {
            throw new GdxRuntimeException(e);
        }
    }

    public static void initClassStandards() throws ClassNotFoundException, InvocationTargetException, IllegalAccessException {
        try {
            List<Map<String, String>> readClassTables =
                    ExcelFile.readXlsx(ResourceLoader.getResourceAsStream("unit_classes.xlsx"));
            for (Map<String, String> table : readClassTables) {
                UnitExcelDto unitExcelDto = new UnitExcelDto();
                BeanUtils.populate(unitExcelDto, table);
                unitExcelDto.calcChildClassesList();

                UnitParameters paramsDiffFromBase = new UnitParameters(unitExcelDto.getHeight()
                        , unitExcelDto.getStr()
                        , unitExcelDto.getAgi()
                        , unitExcelDto.getVit());

                ClassStandard classStandard = new ClassStandard(
                        unitExcelDto.getClazz()
                        , unitExcelDto.getLvl()
                        , paramsDiffFromBase.plus(BASE_PARAMS)
                        , unitExcelDto.getChildClassesList()
                );
                CLASS_STANDARDS.put(classStandard.unitClass, classStandard);
                classStandard.migrationTemperature = getHealthyTemperatureByPercent(unitExcelDto.getMigrationTemperaturePercent());
                classStandard.migrationTemperature = roundTemperatureToClosestExistingTemperature(classStandard.migrationTemperature);
                classStandard.aggrAnimal = unitExcelDto.getAggrAnimal() == null ? false : unitExcelDto.getAggrAnimal();
                classStandard.scaringAnimal = unitExcelDto.getScaringAnimal() == null ? false : unitExcelDto.getScaringAnimal();
                classStandard.classShortName = unitExcelDto.getClassShortName();
                classStandard.skin = unitExcelDto.getSkin();
                classStandard.tooth = unitExcelDto.getTooth();
                classStandard.fang = unitExcelDto.getFang();
                classStandard.tusk = unitExcelDto.getTusk();

                if (BaseHumanClass.class.isAssignableFrom(classStandard.unitClass)) {
                    HUMAN_CLASSES.add((Class<? extends BaseHumanClass>) classStandard.unitClass);
                } else if (BaseAnimalClass.class.isAssignableFrom(classStandard.unitClass)) {
                    ANIMAL_CLASSES.add((Class<? extends BaseAnimalClass>) classStandard.unitClass);
                }
            }

            RND_ANIMAL_CLASSES.addAll(ANIMAL_CLASSES);
            RND_ANIMAL_CLASSES.shuffle();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        LOG.debug("{}", CLASS_STANDARDS);
    }

    public static Integer roundTemperatureToClosestExistingTemperature(Integer inputT) {
        if (Climate.EXISTING_TEMPERATURES.contains(inputT, true)) {
            return inputT;
        }

        for (Integer temperature : Climate.EXISTING_TEMPERATURES) {
            if (temperature >= inputT) {
                return temperature;
            }
        }
        throw new GdxRuntimeException("!");
    }

    private static Integer getHealthyTemperatureByPercent(Integer migrationTemperaturePercent) {
        double normalized = Normalizer.normalize(migrationTemperaturePercent
                , 100, 0
                , Climate.TEMPERATURE_MAX, Unit.HEALTHY_TEMPERATURE_MIN);
        return (int) normalized;
    }

    private static void calculateDiffs() {

        UnitParameters paramDiffs;
        HashMap<Class<? extends Unit>, UnitParameters> children;

        for (Entry<Class<? extends Unit>, ClassStandard> entry : CLASS_STANDARDS.entrySet()) {
            Class<? extends Unit> parentClass = entry.getKey();
            ClassStandard parentStandard = CLASS_STANDARDS.get(parentClass);

            children = new HashMap<>();
            DIFFS_FROM_PARENT_TO_CHILD.put(parentClass, children);

            for (Class<? extends Unit> childClass : entry.getValue().getChildClasses()) {
                ClassStandard childStandard = CLASS_STANDARDS.get(childClass);
                if (childStandard == null) {
                    LOG.warn("calculateDiffs > childStandard == null: " +
                            "standard was not defined for " + childClass.getSimpleName());
                    continue;
                }

                paramDiffs = new UnitParameters()
                        .copyFrom(childStandard.getParams())
                        .minus(parentStandard.getParams());

                children.put(childClass, paramDiffs);
            }
        }
    }

    private static void calculateHumanClassesTree() {
        DESIGN_TABLE_THE_OLDEST_CLASSES.put(Propliopithecus.class, new UnitClassTreeNode(0, 1, Propliopithecus.class));
        DESIGN_TABLE_THE_OLDEST_CLASSES.put(Pliopithecus.class, new UnitClassTreeNode(1, 1, Pliopithecus.class));
        DESIGN_TABLE_THE_OLDEST_CLASSES.put(Dryopithecus.class, new UnitClassTreeNode(2, 1, Dryopithecus.class));
        DESIGN_TABLE_THE_OLDEST_CLASSES.put(Chororapithecus.class, new UnitClassTreeNode(3, 2, Chororapithecus.class));
        DESIGN_TABLE_THE_OLDEST_CLASSES.put(SahelanthropusTchadensis.class, new UnitClassTreeNode(4, 1, SahelanthropusTchadensis.class));
        DESIGN_TABLE_THE_OLDEST_CLASSES.put(Orrorin.class, new UnitClassTreeNode(4, 2, Orrorin.class));
        DESIGN_TABLE_THE_OLDEST_CLASSES.put(OrrorinTugenensis.class, new UnitClassTreeNode(4, 3, OrrorinTugenensis.class));
        DESIGN_TABLE_THE_OLDEST_CLASSES.put(Ardipithecus.class, new UnitClassTreeNode(5, 1, Ardipithecus.class));
        DESIGN_TABLE_THE_OLDEST_CLASSES.put(Pan.class, new UnitClassTreeNode(5, 2, Pan.class));
        DESIGN_TABLE_THE_OLDEST_CLASSES.put(Gorilla.class, new UnitClassTreeNode(5, 3, Gorilla.class));
        DESIGN_TABLE_THE_OLDEST_CLASSES.put(ArdipithecusRamidus.class, new UnitClassTreeNode(6, 0, ArdipithecusRamidus.class));
        DESIGN_TABLE_THE_OLDEST_CLASSES.put(ArdipithecusKadabba.class, new UnitClassTreeNode(6, 1, ArdipithecusKadabba.class));
        DESIGN_TABLE_THE_OLDEST_CLASSES.put(Paranthropus.class, new UnitClassTreeNode(7, 0, Paranthropus.class));
        DESIGN_TABLE_THE_OLDEST_CLASSES.put(Australopithecus.class, new UnitClassTreeNode(7, 1, Australopithecus.class));

        // build tree
        for (Entry<Class<? extends Unit>, UnitClassTreeNode> treeObjectEntry : DESIGN_TABLE_THE_OLDEST_CLASSES.entrySet()) {
            ClassStandard classStandard = CLASS_STANDARDS.get(treeObjectEntry.getKey());
            if (classStandard == null) {
                continue;
            }

            UnitClassTreeNode treeNode = treeObjectEntry.getValue();
            for (Class<? extends Unit> childClass : classStandard.getChildClasses()) {
                UnitClassTreeNode newChildNode = DESIGN_TABLE_THE_OLDEST_CLASSES.get(childClass);
                if (newChildNode == null) {
                    Utils.error("DESIGN_TABLE_THE_OLDEST_CLASSES > Tree node was not defined for: " + childClass);
                } else {
                    treeNode.addChildNode(newChildNode);
                }
            }
        }
    }

    public static Map<Class<? extends Unit>, UnitParameters> getChildrenClassesWithParameterDiffs(Class parentClass) {
        return DIFFS_FROM_PARENT_TO_CHILD.get(parentClass);
    }

    public static Class<? extends BaseAnimalClass> getAnimalClassByLevel(int lvl) {
        int maxAnimalLvl = getMaxAnimalLvl();
        if (lvl > maxAnimalLvl) {
            lvl = maxAnimalLvl;
        }
        RND_ANIMAL_CLASSES.shuffle();
        for (Class<? extends BaseAnimalClass> aClass : RND_ANIMAL_CLASSES) {
            if (CLASS_STANDARDS.get(aClass).level == lvl) {
                return aClass;
            }
        }
        return null;
    }
}
