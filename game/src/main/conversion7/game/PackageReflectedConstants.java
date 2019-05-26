package conversion7.game;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import conversion7.engine.utils.Timer;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.ai_deprecated.tasks.AbstractSquadTask;
import conversion7.game.stages.world.gods.AbstractGod;
import conversion7.game.stages.world.inventory.items.types.AbstractInventoryItem;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.unit_classes.animals.BaseAnimalClass;
import conversion7.game.unit_classes.humans.BaseHumanClass;
import org.slf4j.Logger;

import java.io.IOException;
import java.lang.reflect.Modifier;

public class PackageReflectedConstants {

    private static final Logger LOG = Utils.getLoggerForClass();

    public static final ObjectMap<PackageForScan, Array<Class<?>>> REFLECTED_PACKAGES = new ObjectMap<>();
    public static final Array<Class<? extends AbstractSquadTask>> ABSTRACT_AREA_OBJECT_TASKS = new Array<>();
    public static final Array<Class<? extends Unit>> ALL_UNIT_CLASSES = new Array<>();
    public static final Array<Class<? extends BaseHumanClass>> HUMAN_UNIT_CLASSES = new Array<>();
    public static final Array<Class<? extends BaseAnimalClass>> WORLD_ANIMAL_CLASSES = new Array<>();
    public static final Array<Class<? extends AbstractInventoryItem>> INVENTORY_ITEM_CLASSES = new Array<>();
    public static final Array<Class<? extends AbstractGod>> GOD_CLASSES = new Array<>();

    static {
        LOG.info("Collect classes for defined packages");
        Timer timer = new Timer();
        try {
            collectAllClassesInRequestedPackages();
            filterAndProcess();
        } catch (IOException e) {
            throw new GdxRuntimeException(e);
        } finally {
            timer.stop("Classes were collected.");
        }
    }

    private static void filterAndProcess() {
        LOG.info("filterAndProcess");
        for (Class<?> clazz : REFLECTED_PACKAGES.get(PackageForScan.WORLD_UNIT_PACKAGE)) {
            if (!clazz.equals(Unit.class) && Unit.class.isAssignableFrom(clazz) &&
                    !Modifier.isAbstract(clazz.getModifiers())) {
                ALL_UNIT_CLASSES.add((Class<? extends Unit>) clazz);
                if (BaseAnimalClass.class.isAssignableFrom(clazz)) {
                    WORLD_ANIMAL_CLASSES.add((Class<? extends BaseAnimalClass>) clazz);
                }
                if (BaseHumanClass.class.isAssignableFrom(clazz)) {
                    HUMAN_UNIT_CLASSES.add((Class<? extends BaseHumanClass>) clazz);
                }
            }
        }

        for (Class<?> clazz : REFLECTED_PACKAGES.get(PackageForScan.AREA_OBJECT_TASKS_PACKAGE)) {
            if (/*AbstractAreaObjectTask.class.isAssignableFrom(clazz) &&*/ !Modifier.isAbstract(clazz.getModifiers())
                    && !Modifier.isInterface(clazz.getModifiers())) {
                ABSTRACT_AREA_OBJECT_TASKS.add((Class<? extends AbstractSquadTask>) clazz);
            }
        }

        for (Class<?> clazz : REFLECTED_PACKAGES.get(PackageForScan.INVENTORY_ITEMS_PACKAGE)) {
            if (!Modifier.isAbstract(clazz.getModifiers())) {
                INVENTORY_ITEM_CLASSES.add((Class<? extends AbstractInventoryItem>) clazz);
            }
        }

        for (Class<?> clazz : REFLECTED_PACKAGES.get(PackageForScan.WORLD_GODS_PACKAGE)) {
            if (!Modifier.isAbstract(clazz.getModifiers()) && AbstractGod.class.isAssignableFrom(clazz)) {
                GOD_CLASSES.add((Class<? extends AbstractGod>) clazz);
            }
        }
    }

    private static void collectAllClassesInRequestedPackages() throws IOException {
        LOG.info("collectAllClassesInRequestedPackages");
        final ClassLoader loader = Thread.currentThread().getContextClassLoader();

        for (PackageForScan packageForScan : PackageForScan.values()) {
            Array<Class<?>> classes = new Array<>();
            REFLECTED_PACKAGES.put(packageForScan, classes);

            ImmutableSet<ClassPath.ClassInfo> levelClasses;
            if (packageForScan.isRecursive()) {
                levelClasses = ClassPath.from(loader).getTopLevelClassesRecursive(packageForScan.toString());
            } else {
                levelClasses = ClassPath.from(loader).getTopLevelClasses(packageForScan.toString());
            }
            for (final ClassPath.ClassInfo info : levelClasses) {
                Class<?> clazz = info.load();
                classes.add(clazz);
            }
        }
    }

    public static Class<? extends Unit> getWorldUnitClass(String classString) {
        for (Class<? extends Unit> worldUnitClass : ALL_UNIT_CLASSES) {
            if (worldUnitClass.getSimpleName().equals(classString)) {
                return worldUnitClass;
            }
        }
        return null;
    }

    public enum PackageForScan {
        WORLD_UNIT_PACKAGE("conversion7.game.unit_classes", true),
        ANIMALS_OLIGOCENE_PACKAGE("conversion7.game.unit_classes.animals.oligocene", false),
        AREA_OBJECT_EFFECTS_PACKAGE("conversion7.game.stages.world.objects.effects", false),
        AREA_OBJECT_TASKS_PACKAGE("conversion7.game.stages.world.ai.tasks", true),
        INVENTORY_ITEMS_PACKAGE("conversion7.game.stages.world.inventory.items", true),
        WORLD_GODS_PACKAGE("conversion7.game.stages.world.gods", true);

        private final String value;
        private boolean recursive;

        PackageForScan(String value, boolean recursive) {
            this.value = value;
            this.recursive = recursive;
        }

        public boolean isRecursive() {
            return recursive;
        }

        @Override
        public String toString() {
            return value;
        }

    }


}
