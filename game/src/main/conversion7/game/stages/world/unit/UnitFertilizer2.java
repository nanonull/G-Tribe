package conversion7.game.stages.world.unit;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.GdxRuntimeException;
import conversion7.engine.utils.MathUtils;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.SoulQueue;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.unit.effects.items.ChildbearingEffect;
import conversion7.game.stages.world.unit.effects.items.ColdEffect;
import conversion7.game.stages.world.unit.effects.items.HungerEffect;
import conversion7.game.stages.world.unit.effects.items.IncreaseFertilizingChanceEffect;
import conversion7.game.stages.world.unit.effects.items.PostFertilizationMaleEffect;
import conversion7.game.stages.world.unit.effects.items.ThirstEffect;
import conversion7.game.unit_classes.ClassStandard;
import conversion7.game.unit_classes.UnitClassConstants;
import org.slf4j.Logger;
import org.testng.Assert;

public class UnitFertilizer2 {
    private static final Logger LOG = Utils.getLoggerForClass();
    public static Integer overrideNextFertilizationChance = null;
    public static Boolean ignoreConditionsOnNextFertilization = null;
    public static final int BASE_PERC = 75;
    public static final int CAMP_FERTILIZE_PERC = 95;
    public static final int AGE_FROM = UnitAge.ADULT.getLevel();
    public static SoulQueue soulQueue;

    public static void completeFertilization(Unit male, Unit female) {
        ChildbearingEffect childbearingEffect = new ChildbearingEffect(female);
        female.squad.getEffectManager().addEffect(childbearingEffect);
        if (!male.squad.getEffectManager().containsEffect(PostFertilizationMaleEffect.class)) {
            male.squad.getEffectManager().addEffect(new PostFertilizationMaleEffect(50, 5));
        }
        childbearingEffect.father = male;
        if (male.classStandard.level > female.classStandard.level) {
            female.squad.batchFloatingStatusLines.addLine("Dominant male");
            childbearingEffect.mainParent = male;
        } else {
            female.squad.batchFloatingStatusLines.addLine("Dominant female");
            childbearingEffect.mainParent = female;
        }
        childbearingEffect.setFatherInitExp(male.squad.experience);
        childbearingEffect.setMotherInitExp(female.squad.experience);
        if (LOG.isDebugEnabled()) LOG.debug(" mainParent = " + childbearingEffect.mainParent);

    }

    /** Returns true if fertilized */
    public static boolean tryFertilize(Unit male, Unit female, int fertilizeChance) {
        female.squad.batchFloatingStatusLines.start();
        if (LOG.isDebugEnabled()) LOG.debug("< attempt to fertilize " + female);
        Integer overrideChance = UnitFertilizer2.overrideNextFertilizationChance;
        Boolean ignoreConditions = UnitFertilizer2.ignoreConditionsOnNextFertilization;
        UnitFertilizer2.overrideNextFertilizationChance = null;
        UnitFertilizer2.ignoreConditionsOnNextFertilization = null;

        boolean fertilized;
        if (overrideChance == null) {
            fertilized = MathUtils.testPercentChance(fertilizeChance);
        } else {
            fertilized = MathUtils.testPercentChance(overrideChance);
        }

        if (!fertilized) {
            female.squad.batchFloatingStatusLines.addLine("Fertilization failed % " + fertilizeChance);
        }

        if (fertilized && (ignoreConditions == null || !ignoreConditions)) {
            if (hasNegativeEffects(female)) {
                female.squad.cell.addFloatLabel("Fertilization failed: negative effects", Color.ORANGE);
                fertilized = false;
            }

//            if (!female.getSquad().getCell().hasEnoughUnitFood()) {
//                female.squad.cell.addFloatLabel("Fertilization failed: not enough food", Color.ORANGE);
//                fertilized = false;
//            }
        }

        if (fertilized) {
            female.squad.batchFloatingStatusLines.addImportantLine("Fertilized % " + fertilizeChance);
            completeFertilization(male, female);
            female.squad.batchFloatingStatusLines.flush(Color.CYAN);
            male.squad.updateExperience(Power2.EXPERIENCE_PER_FERTILIZE);
        } else {
            female.squad.batchFloatingStatusLines.flush(Color.ORANGE);
        }
        return fertilized;
    }

    private static boolean hasNegativeEffects(Unit female) {
        return female.squad.getEffectManager().containsEffect(ColdEffect.class)
                || female.squad.getEffectManager().containsEffect(HungerEffect.class)
                || female.squad.getEffectManager().containsEffect(ThirstEffect.class)
                ;
    }

    public static void initFertilization(AbstractSquad maleSquad, AbstractSquad femaleSquad) {
        int cellChance = (maleSquad.getLastCell().getCamp() != null || femaleSquad.getLastCell().getCamp() != null) ?
                CAMP_FERTILIZE_PERC : BASE_PERC;

        int effectChance = 0;
        if (maleSquad.getEffectManager().containsEffect(IncreaseFertilizingChanceEffect.class)) {
            effectChance = IncreaseFertilizingChanceEffect.CHANCE;
        }

        int finalChance = Math.max(cellChance, effectChance);
        tryFertilize(maleSquad.unit, femaleSquad.unit, finalChance);
    }

    public static Unit createUnit(Class<? extends Unit> unitClass, boolean gender, UnitParameters params) {
        Unit unit;
        try {
            unit = unitClass.newInstance()
                    .init(gender)
                    .setStartingParams(params);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new GdxRuntimeException(e.getMessage(), e);
        }

        return unit;
    }

    public static Unit createStandardUnit(Class<? extends Unit> unitClass, boolean gender) {
        ClassStandard classStandard = UnitClassConstants.CLASS_STANDARDS.get(unitClass);
        Assert.assertNotNull(classStandard);
        UnitParameters unitParameters = new UnitParameters().copyFrom(classStandard.getParams());
        Unit unit = createUnit(unitClass, gender, unitParameters);
        return unit;
    }

}
