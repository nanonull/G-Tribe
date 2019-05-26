package conversion7.game.stages.world.objects.controllers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.GdxRuntimeException;
import conversion7.engine.artemis.AnimationSystem;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.team.events.AbstractEventNotification;
import conversion7.game.stages.world.team.events.NotEnoughResourcesDeathEvent;
import conversion7.game.stages.world.team.events.NotEnoughResourcesHurtEvent;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.stages.world.unit.UnitFertilizer2;
import conversion7.game.stages.world.unit.UnitParameterType;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.stream.Stream;

public class UnitEndStepValidator extends AbstractSquadValidator {

    private static final Logger LOG = Utils.getLoggerForClass();

    public UnitEndStepValidator(AbstractSquad obj) {
        super(obj);
    }

    @Override
    public void validate() {
        throw new GdxRuntimeException("Not supported!");
    }

    public void execute() throws UnitDefeatedThrowable {
        checkSquadDefeated();

        squad.calculateTemperatureHungerThirstEffects();
        stepUnitsEffects();
        checkSquadDefeated();
        squad.setAgeStep(squad.getAgeStep() + 1);

        squad.resetActionPoints();
        squad.setMadeRitualOnStep(false);
        if (squad.canFertilize()) {
            fertilizeAround();
        }
        squad.getActionsController().invalidate();
        squad.getInventoryController().invalidate();
        squad.updateExperience(squad.getLastCell().getGatheringValue(), "Gathering exp");
        squad.team.getGatheringStatistic().food += squad.getLastCell().getFood();
        squad.team.getGatheringStatistic().water += squad.getLastCell().getWater();
        squad.validate();

        squad.experienceOnStep = 0;
    }

    private void checkSquadDefeated() throws UnitDefeatedThrowable {
        if (squad.hasMortalWound()) {
            throw new UnitDefeatedThrowable();
        }
    }

    private void fertilizeAround() {
        Optional<AbstractSquad> foundFemale = Stream.of(squad.getSquadsAround().toArray())
                .filter(s -> s.canBeFertilized()
                        && s.getTeam() == squad.getTeam())
                .sorted((o1, o2) -> Float.compare(o1.getBaseMaxPower(), o2.getBaseMaxPower()))
                .findFirst();

        if (foundFemale.isPresent()) {
            AbstractSquad female = foundFemale.get();
            AnimationSystem.vectorAnimation("Fertilize", this.squad.getLastCell(), female.getLastCell());
            UnitFertilizer2.initFertilization(this.squad, female);
        }
    }


    private void stepUnitsEffects() throws UnitDefeatedThrowable {
        squad.getRootValidator().setTreeValidationEnabled(false);

        AbstractEventNotification theMostCriticalEventForObject = null;
        Unit unit = squad.getUnit();

        squad.getEffectManager().effectsTick();

        // damage
        int healthChange = squad.getEffectManager().get(UnitParameterType.HEALTH_DAMAGE_PER_STEP).value;
        if (healthChange < 0) {
            unit.squad.batchFloatingStatusLines.start();
            unit.squad.batchFloatingStatusLines.addLine("Effects:");
            if (squad.hurtBy(Math.abs(healthChange), null)) {
                if (theMostCriticalEventForObject == null
                        || !theMostCriticalEventForObject.getClass().equals(NotEnoughResourcesDeathEvent.class)) {
                    theMostCriticalEventForObject = new NotEnoughResourcesDeathEvent((AbstractSquad) squad);
                }
                checkSquadDefeated();
            } else {
                if (theMostCriticalEventForObject == null) {
                    theMostCriticalEventForObject = new NotEnoughResourcesHurtEvent((AbstractSquad) squad);
                }
            }
            unit.squad.batchFloatingStatusLines.flush(Color.ORANGE);
        } else if (healthChange > 0) {
            LOG.error("Implement case when HEALTH_DAMAGE_PER_STEP > 0!");
        }
        checkSquadDefeated();

        squad.getRootValidator().setTreeValidationEnabled(true);
        squad.validate();

        // event notification
        if (squad.getTeam().isHumanPlayer() && theMostCriticalEventForObject != null) {
            squad.getTeam().getNextStepEvents().add(theMostCriticalEventForObject);
        }
    }

}
