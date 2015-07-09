package conversion7.game.classes;

import conversion7.engine.utils.Utils;
import conversion7.game.classes.animals.oligocene.Amphicyonidae;
import conversion7.game.classes.animals.oligocene.Ekaltadeta;
import conversion7.game.classes.animals.oligocene.Eusmilus;
import conversion7.game.classes.animals.oligocene.Hyaenodon;
import conversion7.game.classes.animals.oligocene.Indricotherium;
import conversion7.game.classes.animals.oligocene.Mastodon;
import conversion7.game.classes.animals.oligocene.Mesohippus;
import conversion7.game.classes.animals.oligocene.Oreodontidae;
import conversion7.game.classes.animals.oligocene.Phorusrhacidae;
import conversion7.game.classes.animals.oligocene.Protoceras;
import conversion7.game.classes.animals.oligocene.Pyrotherium;
import conversion7.game.classes.animals.oligocene.Thylacoleo;
import conversion7.game.classes.australopitecus.Australopithecus;
import conversion7.game.classes.australopitecus.Paranthropus;
import conversion7.game.classes.test.TestClass1;
import conversion7.game.classes.test.TestClass2;
import conversion7.game.classes.theOldest.Ardipithecus;
import conversion7.game.classes.theOldest.ArdipithecusKadabba;
import conversion7.game.classes.theOldest.ArdipithecusRamidus;
import conversion7.game.classes.theOldest.Chororapithecus;
import conversion7.game.classes.theOldest.Dryopithecus;
import conversion7.game.classes.theOldest.Gorilla;
import conversion7.game.classes.theOldest.Orrorin;
import conversion7.game.classes.theOldest.OrrorinTugenensis;
import conversion7.game.classes.theOldest.Pan;
import conversion7.game.classes.theOldest.Pliopithecus;
import conversion7.game.classes.theOldest.Propliopithecus;
import conversion7.game.classes.theOldest.SahelanthropusTchadensis;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.stages.world.unit.UnitParameters;
import conversion7.game.ui.world.team_classes.UnitClassTreeNode;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.Map.Entry;

public class UnitClassConstants {

    private static final Logger LOG = Utils.getLoggerForClass();

    public static final Map<Class<? extends Unit>, ClassStandard> CLASS_STANDARDS = new LinkedHashMap<>();

    @SuppressWarnings("unchecked")
    public static void init() {
        initClassStandards();
        calculateDiffs();
        calculateHumanClassesTree();
    }

    public static Map<Class<? extends Unit>, UnitClassTreeNode> DESIGN_TABLE_THE_OLDEST_CLASSES = new HashMap<>();

    private static void calculateHumanClassesTree() {
        // init objects
        DESIGN_TABLE_THE_OLDEST_CLASSES.put(Propliopithecus.class, new UnitClassTreeNode(0, 1, Propliopithecus.class));
        DESIGN_TABLE_THE_OLDEST_CLASSES.put(Pliopithecus.class, new UnitClassTreeNode(1, 1, Pliopithecus.class));
        DESIGN_TABLE_THE_OLDEST_CLASSES.put(Dryopithecus.class, new UnitClassTreeNode(2, 1, Dryopithecus.class));
        DESIGN_TABLE_THE_OLDEST_CLASSES.put(Chororapithecus.class, new UnitClassTreeNode(3, 3, Chororapithecus.class));
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

    private static void initClassStandards() {
        CLASS_STANDARDS.put(Propliopithecus.class, new ClassStandard(
                new UnitParameters(60, 15, 20, 15),
                Pliopithecus.class
        ));

        CLASS_STANDARDS.put(Pliopithecus.class, new ClassStandard(
                new UnitParameters(65, 16, 25, 16),
                Dryopithecus.class
        ));

        CLASS_STANDARDS.put(Dryopithecus.class, new ClassStandard(
                new UnitParameters(70, 18, 30, 18),
                SahelanthropusTchadensis.class, Orrorin.class, Chororapithecus.class
        ));

        CLASS_STANDARDS.put(Chororapithecus.class, new ClassStandard(
                new UnitParameters(120, 30, 25, 30),
                Orrorin.class
        ));

        CLASS_STANDARDS.put(SahelanthropusTchadensis.class, new ClassStandard(
                new UnitParameters(120, 30, 40, 30),
                Ardipithecus.class, Pan.class
        ));

        CLASS_STANDARDS.put(Orrorin.class, new ClassStandard(
                new UnitParameters(120, 35, 35, 30),
                Pan.class, Gorilla.class, OrrorinTugenensis.class
        ));

        CLASS_STANDARDS.put(OrrorinTugenensis.class, new ClassStandard(
                new UnitParameters(120, 35, 30, 35),
                Pan.class, Gorilla.class, Orrorin.class
        ));

        CLASS_STANDARDS.put(Ardipithecus.class, new ClassStandard(
                new UnitParameters(120, 35, 45, 35),
                ArdipithecusRamidus.class, ArdipithecusKadabba.class
        ));

        CLASS_STANDARDS.put(Pan.class, new ClassStandard(
                new UnitParameters(120, 40, 40, 35)
        ));

        CLASS_STANDARDS.put(Gorilla.class, new ClassStandard(
                new UnitParameters(150, 50, 30, 50)
        ));

        CLASS_STANDARDS.put(ArdipithecusRamidus.class, new ClassStandard(
                new UnitParameters(120, 40, 50, 40),
                ArdipithecusKadabba.class, Paranthropus.class, Australopithecus.class
        ));

        CLASS_STANDARDS.put(ArdipithecusKadabba.class, new ClassStandard(
                new UnitParameters(120, 45, 45, 40),
                ArdipithecusRamidus.class, Paranthropus.class, Australopithecus.class
        ));

        // Animals
        CLASS_STANDARDS.put(Amphicyonidae.class, new ClassStandard(
                new UnitParameters(0, 40, 40, 40),
                null
        ));

        CLASS_STANDARDS.put(Ekaltadeta.class, new ClassStandard(
                new UnitParameters(0, 40, 40, 40),
                Ekaltadeta.class
        ));

        CLASS_STANDARDS.put(Eusmilus.class, new ClassStandard(
                new UnitParameters(0, 40, 40, 40),
                Eusmilus.class
        ));

        CLASS_STANDARDS.put(Hyaenodon.class, new ClassStandard(
                new UnitParameters(0, 40, 40, 40),
                Hyaenodon.class
        ));

        CLASS_STANDARDS.put(Indricotherium.class, new ClassStandard(
                new UnitParameters(0, 40, 40, 40),
                Indricotherium.class
        ));

        CLASS_STANDARDS.put(Mastodon.class, new ClassStandard(
                new UnitParameters(0, 40, 40, 40),
                Mastodon.class
        ));

        CLASS_STANDARDS.put(Mesohippus.class, new ClassStandard(
                new UnitParameters(0, 40, 40, 40),
                Mesohippus.class
        ));

        CLASS_STANDARDS.put(Oreodontidae.class, new ClassStandard(
                new UnitParameters(0, 40, 40, 40),
                Oreodontidae.class
        ));

        CLASS_STANDARDS.put(Phorusrhacidae.class, new ClassStandard(
                new UnitParameters(0, 40, 40, 40),
                Phorusrhacidae.class
        ));

        CLASS_STANDARDS.put(Protoceras.class, new ClassStandard(
                new UnitParameters(0, 40, 40, 40),
                Protoceras.class
        ));

        CLASS_STANDARDS.put(Pyrotherium.class, new ClassStandard(
                new UnitParameters(0, 40, 40, 40),
                Pyrotherium.class
        ));

        CLASS_STANDARDS.put(Thylacoleo.class, new ClassStandard(
                new UnitParameters(0, 40, 40, 40),
                Thylacoleo.class
        ));

        // TEST CLASSES
        CLASS_STANDARDS.put(TestClass1.class, new ClassStandard(
                new UnitParameters(50, 20, 20, 20),
                TestClass2.class
        ));

        CLASS_STANDARDS.put(TestClass2.class, new ClassStandard(
                new UnitParameters(50, 20, 20, 20),
                TestClass1.class
        ));

        LOG.info("Class standards: " + CLASS_STANDARDS.size());
    }


    /** Stores parameter diffs from Parent class to Child class */
    public static final Map<Class, Map<Class<? extends Unit>, UnitParameters>> DIFFS_FROM_PARENT_TO_CHILD =
            new HashMap<>();


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
                        .createDiffFrom(parentStandard.getParams());

                children.put(childClass, paramDiffs);
            }
        }
    }


    public static Map<Class<? extends Unit>, UnitParameters> getChildrenClassesWithParameterDiffs(Class parentClass) {
        return DIFFS_FROM_PARENT_TO_CHILD.get(parentClass);
    }
}
