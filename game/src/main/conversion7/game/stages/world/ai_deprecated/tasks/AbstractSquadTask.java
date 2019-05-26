package conversion7.game.stages.world.ai_deprecated.tasks;

import com.badlogic.gdx.utils.ObjectMap;
import conversion7.engine.utils.PropertiesLoader;
import conversion7.engine.utils.Utils;
import conversion7.game.PackageReflectedConstants;
import conversion7.game.interfaces.Cancelable;
import conversion7.game.interfaces.Completable;
import org.testng.Assert;

/**
 * Deprecated. Use new conversion7.engine.ai_new.base.AiTask
 * New AI evaluation: conversion7.engine.ai_new.base.AiEvaluator
 */
@Deprecated
public abstract class AbstractSquadTask implements Completable, Cancelable {

    public static final ObjectMap<Class<? extends AbstractSquadTask>, Integer> PRIORITIES = new ObjectMap<>();

    /** Use conversion7.engine.ai_new.base.AiTask#priority */
    @Deprecated
    public Integer priority;
    protected int id;

    static {
        for (Class<? extends AbstractSquadTask> taskClass : PackageReflectedConstants.ABSTRACT_AREA_OBJECT_TASKS) {
            int intProperty = PropertiesLoader.getIntProperty("AreaObjectTask.Priority." + taskClass.getSimpleName());
            Assert.assertNotNull(intProperty, "No priority defined for task " + taskClass);
            PRIORITIES.put(taskClass, intProperty);
        }
    }

    public AbstractSquadTask() {
        this.priority = PRIORITIES.get(getClass());
        if (priority == null) {
            priority = 0;
        }
        this.id = Utils.getNextId();
    }

    public String getDescription() {
        return toString();
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
