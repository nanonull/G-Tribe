package conversion7.game.stages.world.objects.unit;

import com.badlogic.gdx.utils.Array;
import conversion7.engine.utils.MathUtils;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.AnimalSpawn;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.objects.actions.items.ScareAnimalAction;
import conversion7.game.stages.world.team.Team;
import conversion7.game.stages.world.unit.effects.AbstractUnitEffect;
import conversion7.game.stages.world.unit.effects.items.PanicEffect;
import conversion7.game.stages.world.unit.effects.items.PoisonEffect;
import conversion7.game.stages.world.unit.effects.items.ScaredEffect;
import conversion7.game.stages.world.unit.effects.items.WeakeningEffect;

@Deprecated
public class AnimalHerd extends WorldSquad {

    public static final int HUNT_EXP = AnimalSpawn.CAPTURE_EXP / 8;
    public static final Array<Class<? extends AbstractUnitEffect>> ATTACK_EFFECTS = new Array<>();

    static {
        //  2019-04-26  return effects after battle redesign
//        ATTACK_EFFECTS.add(PanicEffect.class);
        ATTACK_EFFECTS.add(WeakeningEffect.class);
//        ATTACK_EFFECTS.add(ScaredEffect.class);
        ATTACK_EFFECTS.add(PoisonEffect.class);
    }

    public AbstractSquad rider;

    AnimalHerd(Cell cell, Team team) {
        super(cell, team);
    }

    public static void applyAnimalScaring(AbstractSquad animal, AreaObject targetObj) {
        if (animal.isAnimal()) {
            boolean canScare = animal.unit.classStandard.scaringAnimal;
            if (canScare) {
                if (targetObj.isSquad() && MathUtils.testPercentChance(20)) {
                    targetObj.toSquad().effectManager.getOrCreate(PanicEffect.class).resetTickCounter();
                }
            }
        }
    }

    @Override
    public String getFullName() {
        return getName();
    }

    @Override
    public String toString() {
        return super.toString() + " id=" + getId();
    }

    public float getMyRelativePowerRatioWith(AbstractSquad otherObject) {
        float powerRatio = super.getMyRelativePowerRatioWith(otherObject);
        if (this.getEffectManager().containsEffect(ScaredEffect.class)) {
            powerRatio /= ScareAnimalAction.ANIMAL_POWER_MOD_WHEN_ANIMAL_ACTS;
        }
        return powerRatio;
    }

    @Override
    public boolean hasMoreActualPowerThan(AbstractSquad otherObject) {
        if (otherObject.getEffectManager().containsEffect(ScaredEffect.class)) {
            return getPowerValue() > otherObject.getPowerValue() * ScareAnimalAction.ANIMAL_POWER_MOD_WHEN_ANIMAL_ACTS;
        } else {
            return super.hasMoreActualPowerThan(otherObject);
        }
    }

    @Override
    public boolean couldJoinToTeam(AreaObject targetToBeJoined) {
        return false;
    }


}
