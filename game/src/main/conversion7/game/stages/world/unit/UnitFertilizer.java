package conversion7.game.stages.world.unit;

import conversion7.engine.utils.Utils;
import org.slf4j.Logger;

@Deprecated
public class UnitFertilizer {

    private static final Logger LOG = Utils.getLoggerForClass();

    @Deprecated
    public static void completeFertilization(Unit male, Unit female) {
//        if (LOG.isDebugEnabled())
//            LOG.info(String.format("successful fertilization male/female: %s ### %s", male, female));
//
//        // get primary/secondary parent
//        Unit parametersParent;
//        Unit parametersParentSecondary;
//        if (MathUtils.RANDOM.nextBoolean()) {
//            parametersParent = male;
//            parametersParentSecondary = female;
//        } else {
//            parametersParent = female;
//            parametersParentSecondary = male;
//        }
//        if (LOG.isDebugEnabled()) LOG.debug(" parametersParent = " + parametersParent);
//        if (LOG.isDebugEnabled()) LOG.debug(" parametersParentSecondary = " + parametersParentSecondary);
//
//        // get final class
//        Map<Class<? extends Unit>, UnitParameters> possibleChildClasses =
//                UnitClassConstants.getChildrenClassesWithParameterDiffs(parametersParent.getClass());
//
//        Class<? extends Unit> finalChildClass = null;
//        UnitParameters finalChildDiff = null;
//
//        int randomIndex = MathUtils.RANDOM.nextInt(possibleChildClasses.size() + 1);
//
//        if (randomIndex == possibleChildClasses.size()) { // no evolution  to another class
//            finalChildClass = parametersParent.getClass();
//            finalChildDiff = new UnitParameters();
//        } else { // evolution to another class
//            Iterator<Map.Entry<Class<? extends Unit>, UnitParameters>> it = possibleChildClasses.entrySet().iterator();
//            int i = 0;
//            while (it.hasNext()) {
//                if (i == randomIndex) {
//                    Map.Entry<Class<? extends Unit>, UnitParameters> finalChildClassWithDiff = it.next();
//                    finalChildClass = finalChildClassWithDiff.getKey();
//                    finalChildDiff = finalChildClassWithDiff.getValue();
//                    break;
//                }
//                i++;
//            }
//        }
//        Assert.assertNotNull(finalChildDiff);
//
//        LOG.debug(" finalChildClass = " + finalChildClass);
//        LOG.debug(" finalChildDiff = " + finalChildDiff);
//        Assert.assertNotNull(finalChildClass);
//
//        // calculate secondary affect
//        Map<Class<? extends Unit>, UnitParameters> possibleChildClassesSecondary =
//                UnitClassConstants.getChildrenClassesWithParameterDiffs(parametersParentSecondary.getClass());
//
//        UnitParameters secondaryDiff = null;
//        if (possibleChildClassesSecondary.containsKey(finalChildClass)) {
//            if (LOG.isDebugEnabled()) LOG.debug(" possibleChildClassesSecondary contains finalChildClass");
//            secondaryDiff = possibleChildClassesSecondary.get(finalChildClass);
//        }
//        // in case both parent could lead to finalChildClass: mix diffs
//        if (secondaryDiff != null) {
//            // mix diffs
//            finalChildDiff.mixWith(secondaryDiff);
//        }
//
//        // calculate finalChildParams by mixedParentParams + diffs
//        UnitParameters finalChildParams = new UnitParameters()
//                .copyFrom(parametersParent.getBaseParams())
//                .mixWith(parametersParentSecondary.getBaseParams())
//                .plus(finalChildDiff)
//                .mutate();
//        LOG.info("finalChildParams {}", finalChildParams);
//
//        boolean finalChildGender = false;
//        if (MathUtils.RANDOM.nextBoolean()) {
//            finalChildGender = true;
//        }
//
//        Unit childUnit = UnitFertilizer2.createUnit(finalChildClass, finalChildGender, finalChildParams);
//        female.getEffectManager().addEffect(
//                new ChildbearingEffect(female, childUnit));
//        if (!male.getEffectManager().containsEffect(PostFertilizationMaleEffect.class)) {
//            male.getEffectManager().addEffect(new PostFertilizationMaleEffect(50, 5));
//        }
//        if (LOG.isDebugEnabled()) LOG.debug("> fertilized.");
    }

}
