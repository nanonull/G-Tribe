package conversion7.game.stages.world.unit;

import com.badlogic.gdx.utils.GdxRuntimeException;
import conversion7.engine.utils.Utils;
import conversion7.game.classes.ClassStandard;
import conversion7.game.classes.UnitClassConstants;
import conversion7.game.stages.world.unit.effects.items.Childbearing;
import org.slf4j.Logger;
import org.testng.Assert;

import java.util.Iterator;
import java.util.Map;

public class UnitFertilizer {

    private static final Logger LOG = Utils.getLoggerForClass();

    /** Returns true if fertilized */
    public static boolean fertilize(Unit male, Unit female, int fertilizeChance) {
        if (LOG.isDebugEnabled()) LOG.debug("< attempt to fertilize " + female);

        if (Utils.RANDOM.nextInt(100) >= fertilizeChance) {
            return false;
        }

        if (female.hasNegativeCondition()) {
            if (LOG.isDebugEnabled()) LOG.debug("> no fertilization - female hasNegativeCondition");
            return false;
        }

        if (LOG.isDebugEnabled())
            LOG.info(String.format("successful fertilization male/female: %s ### %s", male, female));

        // get primary/secondary parent
        Unit parametersParent;
        Unit parametersParentSecondary;
        if (Utils.RANDOM.nextBoolean()) {
            parametersParent = male;
            parametersParentSecondary = female;
        } else {
            parametersParent = female;
            parametersParentSecondary = male;
        }
        if (LOG.isDebugEnabled()) LOG.debug(" parametersParent = " + parametersParent);
        if (LOG.isDebugEnabled()) LOG.debug(" parametersParentSecondary = " + parametersParentSecondary);

        // get final class
        Map<Class<? extends Unit>, UnitParameters> possibleChildClasses =
                UnitClassConstants.getChildrenClassesWithParameterDiffs(parametersParent.getClass());

        Class<? extends Unit> finalChildClass = null;
        UnitParameters finalChildDiff = null;

        int randomIndex = Utils.RANDOM.nextInt(possibleChildClasses.size() + 1);

        if (randomIndex == possibleChildClasses.size()) { // no evolution  to another class
            finalChildClass = parametersParent.getClass();
            finalChildDiff = new UnitParameters();
        } else { // evolution to another class
            Iterator<Map.Entry<Class<? extends Unit>, UnitParameters>> it = possibleChildClasses.entrySet().iterator();
            int i = 0;
            while (it.hasNext()) {
                if (i == randomIndex) {
                    Map.Entry<Class<? extends Unit>, UnitParameters> finalChildClassWithDiff = it.next();
                    finalChildClass = finalChildClassWithDiff.getKey();
                    finalChildDiff = finalChildClassWithDiff.getValue();
                    break;
                }
                i++;
            }
        }
        Assert.assertNotNull(finalChildDiff);

        LOG.debug(" finalChildClass = " + finalChildClass);
        LOG.debug(" finalChildDiff = " + finalChildDiff);

        if (finalChildClass == null) {
            Utils.error("finalChildClass is null");
        }

        // calculate secondary affect
        Map<Class<? extends Unit>, UnitParameters> possibleChildClassesSecondary =
                UnitClassConstants.getChildrenClassesWithParameterDiffs(parametersParentSecondary.getClass());

        UnitParameters secondaryDiff = null;
        if (possibleChildClassesSecondary.containsKey(finalChildClass)) {
            if (LOG.isDebugEnabled()) LOG.debug(" possibleChildClassesSecondary contains finalChildClass");
            secondaryDiff = possibleChildClassesSecondary.get(finalChildClass);
        }
        // in case both parent could lead to finalChildClass: mix diffs
        if (secondaryDiff != null) {
            // mix diffs
            finalChildDiff.mixWith(secondaryDiff);
        }

        // calculate finalChildParams by mixedParentParams + diffs
        UnitParameters finalChildParams = new UnitParameters()
                .copyFrom(parametersParent.getParams())
                .mixWith(parametersParentSecondary.getParams())
                .applyDiff(finalChildDiff)
                .mutate();

        boolean finalChildGender = false;
        if (Utils.RANDOM.nextBoolean()) {
            finalChildGender = true;
        }

        female.getEffectManager().addEffect(
                new Childbearing(female, createUnit(finalChildClass, finalChildGender,
                        Unit.UnitSpecialization.getRandom()
                        , finalChildParams)));
        if (LOG.isDebugEnabled()) LOG.debug("> fertilized.");
        return true;
    }

    public static Unit createUnit(Class<? extends Unit> unitClass, boolean gender, Unit.UnitSpecialization specialization, UnitParameters params) {
        Unit unit;
        try {
            unit = unitClass.newInstance()
                    .create(gender, specialization)
                    .assignParams(params.updateHealthToVitality());
        } catch (InstantiationException | IllegalAccessException e) {
            throw new GdxRuntimeException(e.getMessage(), e);
        }
        return unit;
    }

    public static Unit createStandardUnit(Class<? extends Unit> unitClass, boolean gender) {
        return createStandardUnit(unitClass, gender, Unit.UnitSpecialization.getRandom());
    }

    public static Unit createStandardUnit(Class<? extends Unit> unitClass, boolean gender, Unit.UnitSpecialization specialization) {
        ClassStandard classStandard = UnitClassConstants.CLASS_STANDARDS.get(unitClass);
        Assert.assertNotNull(classStandard);
        UnitParameters unitParameters = new UnitParameters().copyFrom(classStandard.getParams());
        return createUnit(unitClass, gender, specialization, unitParameters);
    }

}
