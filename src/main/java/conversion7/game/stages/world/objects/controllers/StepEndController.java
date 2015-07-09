package conversion7.game.stages.world.objects.controllers;

import com.badlogic.gdx.utils.Array;
import conversion7.engine.pools.system.PoolManager;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.WorldThreadLocalSort;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.objects.FoodStorage;
import conversion7.game.stages.world.objects.effects.AbstractObjectEffect;
import conversion7.game.stages.world.objects.effects.IncreaseFertilizingChanceEffect;
import conversion7.game.stages.world.team.events.AbstractEventNotification;
import conversion7.game.stages.world.team.events.NotEnoughResourcesDeathEvent;
import conversion7.game.stages.world.team.events.NotEnoughResourcesHurtEvent;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.stages.world.unit.UnitFertilizer;
import conversion7.game.stages.world.unit.effects.items.Childbearing;
import conversion7.game.utils.collections.Comparators;
import conversion7.game.utils.collections.IterationRegistrators;
import org.slf4j.Logger;

import java.util.Iterator;

import static java.lang.String.format;

public class StepEndController extends AbstractObjectController {

    private static final Logger LOG = Utils.getLoggerForClass();
    private static final int FERTILIZATION_TAKES_FOOD = 5;
    private static final int FEED_CYCLE_LIMIT = 2;

    private Array<Unit> men = PoolManager.ARRAYS_POOL.obtain();
    private Array<Unit> women = PoolManager.ARRAYS_POOL.obtain();

    public StepEndController(AreaObject areaObject) {
        super(areaObject);
    }

    public void execute() {
        applyObjectEffectsOnStepEnd();
        temperatureFoodWaterUpdateOnStepEnd();
        applyUnitsEffectsOnStepEnd();

        if (!areaObject.validateAndDefeat()) {
            fertilize();
            areaObject.validateEffects();
            areaObject.resetActionPoints();
        }
    }

    private void fertilize() {
        men.clear();
        women.clear();
        IterationRegistrators.UNITS_ITERATION_REGISTRATOR.start();
        for (int i = 0; i < areaObject.getUnits().size; i++) {
            Unit unit = areaObject.getUnits().get(i);
            if (unit.getGender()) {
                men.add(unit);
            } else {
                if (!unit.getEffectManager().containsEffect(Childbearing.class)) {
                    women.add(unit);
                }
            }
        }
        IterationRegistrators.UNITS_ITERATION_REGISTRATOR.end();

        // fertilize
        int fertilizeChance = areaObject.isTownFragment() ? 20 : 5;
        if (areaObject.hasEffect(IncreaseFertilizingChanceEffect.class)) {
            fertilizeChance *= 3;
        }
        while (areaObject.getFoodStorage().getFood() >= FERTILIZATION_TAKES_FOOD && women.size > 0 && men.size > 0) {
            Unit man = men.get(Utils.RANDOM.nextInt(men.size));
            men.removeValue(man, false);

            Unit woman = women.get(Utils.RANDOM.nextInt(women.size));
            women.removeValue(woman, false);

            if (UnitFertilizer.fertilize(man, woman, fertilizeChance)) {
                areaObject.getFoodStorage().updateFoodOnValueAndValidate(-FERTILIZATION_TAKES_FOOD);
            }
        }
    }

    private void temperatureFoodWaterUpdateOnStepEnd() {
        IterationRegistrators.UNITS_ITERATION_REGISTRATOR.start();
        for (int i = 0; i < areaObject.getUnits().size; i++) {
            Unit unit = areaObject.getUnits().get(i);
            unit.updateTemperature(unit.getTemperatureStepBalance());
            if (unit.getFood() > 0) {
                unit.updateFood(-Unit.EAT_FOOD_QUANTITY);
            }
            if (unit.getWater() > 0) {
                unit.updateWater(-1);
            }
        }
        IterationRegistrators.UNITS_ITERATION_REGISTRATOR.end();
        feeding();
    }

    private void feeding() {
        IterationRegistrators.UNITS_ITERATION_REGISTRATOR.assertNotStarted();
        WorldThreadLocalSort.instance().sort(areaObject.getUnits(), Comparators.UNIT_FOOD_WATER_PRIORITY_COMPARATOR);
        int availableFoodOnCellWithStorage = areaObject.getAvailableFood();
        int availableWaterOnCell = areaObject.getCell().getWater();
        int feedCycle = 0;
        boolean haveSomeFoodOrWater = true;
        while (haveSomeFoodOrWater && feedCycle < FEED_CYCLE_LIMIT) {
            feedCycle++;
            if (LOG.isDebugEnabled()) LOG.debug("feed cycle: " + feedCycle);
            IterationRegistrators.UNITS_ITERATION_REGISTRATOR.safeIteration();
            for (int i = 0; i < areaObject.getUnits().size; i++) {
                Unit unit = areaObject.getUnits().get(i);
                if (availableFoodOnCellWithStorage == 0 && availableWaterOnCell == 0) {
                    haveSomeFoodOrWater = false;
                    break;
                }
                if (availableFoodOnCellWithStorage > 0 && Unit.FOOD_LIMIT > unit.getFood()) {
                    unit.updateFood(+1);
                    if (LOG.isDebugEnabled()) LOG.debug(format("unit.food: %d, feed unit: %s", unit.getFood(), unit));
                    availableFoodOnCellWithStorage--;
                }

                if (availableWaterOnCell > 0 && Unit.WATER_LIMIT > unit.getWater()) {
                    unit.updateWater(+1);
                    if (LOG.isDebugEnabled()) LOG.debug(format("unit.water: %d, feed unit: %s", unit.getWater(), unit));
                    availableWaterOnCell--;
                }
            }
        }

        // storage losses
        if (availableFoodOnCellWithStorage > 0) {
            availableFoodOnCellWithStorage -= FoodStorage.FOOD_LOSSES_PER_STEP;
        }

        // send extra food to storage
        areaObject.getFoodStorage().setFoodAndValidate(availableFoodOnCellWithStorage);
    }

    private void applyObjectEffectsOnStepEnd() {
        Iterator<AbstractObjectEffect> effectsIterator = areaObject.getEffects().iterator();
        while (effectsIterator.hasNext()) {
            AbstractObjectEffect effect = effectsIterator.next();
            effect.tick(effectsIterator);
        }
    }

    private void applyUnitsEffectsOnStepEnd() {
        Iterator<Unit> unitIterator = areaObject.getUnits().iterator();
        AbstractEventNotification theMostCriticalEventForObject = null;

        areaObject.setValidateEnabled(false);
        IterationRegistrators.UNITS_ITERATION_REGISTRATOR.start();
        while (unitIterator.hasNext()) {
            Unit unit = unitIterator.next();

            unit.getEffectManager().effectsTick();

            // damage
            int effectsDamage = unit.getEffectManager().getEffectsDamage();
            if (effectsDamage > 0) {
                if (unit.hurt(effectsDamage)) {
                    if (theMostCriticalEventForObject == null
                            || !theMostCriticalEventForObject.getClass().equals(NotEnoughResourcesDeathEvent.class)) {
                        theMostCriticalEventForObject = new NotEnoughResourcesDeathEvent(areaObject);
                    }
                    unit.dies(unitIterator);
                } else {
                    if (theMostCriticalEventForObject == null) {
                        theMostCriticalEventForObject = new NotEnoughResourcesHurtEvent(areaObject);
                    }
                }
            }
        }
        IterationRegistrators.UNITS_ITERATION_REGISTRATOR.end();
        areaObject.setValidateEnabled(true);
        areaObject.validate();

        // event notification
        if (areaObject.getTeam().isHumanPlayer() && theMostCriticalEventForObject != null) {
            areaObject.getTeam().getEvents().add(theMostCriticalEventForObject);
        }
    }
}
