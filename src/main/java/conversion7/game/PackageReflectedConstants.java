package conversion7.game;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.objects.effects.AbstractObjectEffect;
import conversion7.game.stages.world.unit.Unit;

import java.io.IOException;
import java.lang.reflect.Modifier;

public class PackageReflectedConstants {

    public static final ObjectMap<PackageReflected, Array<Class<?>>> REFLECTED_PACKAGES = new ObjectMap<>();
    public static final Array<Class<? extends AbstractObjectEffect>> AREA_OBJECT_EFFECT_CLASSES = new Array<>();
    public static final Array<Class<? extends Unit>> WORLD_UNIT_CLASSES = new Array<>();

    public enum PackageReflected {
        WORLD_UNIT_CLASSES("conversion7.game.classes", true),
        ANIMALS_OLIGOCENE("conversion7.game.classes.animals.oligocene", false),
        AREA_OBJECT_EFFECTS("conversion7.game.stages.world.objects.effects", false),;

        private final String value;
        private boolean recursive;

        PackageReflected(String value, boolean recursive) {
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

    static {
        // collect classes for each defined package
        try {
            final ClassLoader loader = Thread.currentThread().getContextClassLoader();

            for (PackageReflected packageReflected : PackageReflected.values()) {
                Array<Class<?>> classes = new Array<>();
                REFLECTED_PACKAGES.put(packageReflected, classes);

                ImmutableSet<ClassPath.ClassInfo> levelClasses;
                if (packageReflected.isRecursive()) {
                    levelClasses = ClassPath.from(loader).getTopLevelClassesRecursive(packageReflected.toString());
                } else {
                    levelClasses = ClassPath.from(loader).getTopLevelClasses(packageReflected.toString());
                }
                for (final ClassPath.ClassInfo info : levelClasses) {
                    Class<?> clazz = info.load();
                    classes.add(clazz);
                }
            }

        } catch (IOException e) {
            Utils.error(e);
        }
    }

    public static Class<? extends AbstractObjectEffect> getAreaObjectEffectClass(String classString) {
        for (Class<? extends AbstractObjectEffect> effectClass : AREA_OBJECT_EFFECT_CLASSES) {
            if (effectClass.getSimpleName().equals(classString)) {
                return effectClass;
            }
        }
        return null;
    }

    static {
        for (Class<?> clazz : REFLECTED_PACKAGES.get(PackageReflected.AREA_OBJECT_EFFECTS)) {
            if (!clazz.equals(AbstractObjectEffect.class) && AbstractObjectEffect.class.isAssignableFrom(clazz)) {
                AREA_OBJECT_EFFECT_CLASSES.add((Class<? extends AbstractObjectEffect>) clazz);
            }
        }
    }

    public static Class<? extends Unit> getWorldUnitClass(String classString) {
        for (Class<? extends Unit> worldUnitClass : WORLD_UNIT_CLASSES) {
            if (worldUnitClass.getSimpleName().equals(classString)) {
                return worldUnitClass;
            }
        }
        return null;
    }

    static {
        for (Class<?> clazz : REFLECTED_PACKAGES.get(PackageReflected.WORLD_UNIT_CLASSES)) {
            if (!clazz.equals(Unit.class) && Unit.class.isAssignableFrom(clazz) && !Modifier.isAbstract(clazz.getModifiers())) {
                WORLD_UNIT_CLASSES.add((Class<? extends Unit>) clazz);
            }
        }
    }


}
