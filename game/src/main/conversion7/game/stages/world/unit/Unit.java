package conversion7.game.stages.world.unit;

import conversion7.engine.utils.Utils;
import conversion7.game.GdxgConstants;
import conversion7.game.stages.world.climate.Climate;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.unit.effects.items.ConcealmentEffect;
import conversion7.game.unit_classes.ClassStandard;
import conversion7.game.unit_classes.UnitClassConstants;
import conversion7.game.unit_classes.animals.BaseAnimalClass;
import org.slf4j.Logger;

/** Base world unit class */
public abstract class Unit {

    public static final int EVOLUTION_EXP_PER_UNIT_EXP = 1;
    public static final int BASE_EXP_FOR_LEVEL = 1500;
    public static final int INSPIRATION_POINTS_MAX = BASE_EXP_FOR_LEVEL / 2;
    public static final int DIES_AT_AGE_STEP = UnitAge.OLD.getEndsAtAgeStep();
    public static final float PARAM_MLT_PER_LEVEL = 1f / UnitClassConstants.BASE_POWER;
    public static final int MANA_MAX = 100;
    private static final Logger LOG = Utils.getLoggerForClass();

    /** If unit gets this amount of exp - he will get x2 to level steps */
    public static final int MAX_EXP_PER_WORLD_STEP_TO_MATURITY_EXP = 100;
    public static final int MATURITY_EXP_AT_END_WORLD_STEP = 100;

    public static final int HEALTHY_TEMPERATURE_MIN = Climate.TEMPERATURE_CENTER + 5;
    public static final int HEALTHY_CELL_FOOD_MIN = 10;
    public static final int HEALTHY_CELL_WATER_MIN = 10;
    public static final int HEALTH_AFTER_BACK_TO_LIFE = 1;
    public AbstractSquad squad;
    public ClassStandard classStandard;
    private Class<? extends BaseAnimalClass> gameClass;
    public int id;
    public boolean gender;
    private String name;
    private UnitParameters startingParams;

    public Unit() {
        gameClass = (Class<? extends BaseAnimalClass>) getClass();
        classStandard = UnitClassConstants.CLASS_STANDARDS.get(gameClass);
    }

    public AbstractSquad getSquad() {
        return squad;
    }

    public String getGameClassShortName() {
       return classStandard.classShortName;
    }
    public String getGameClassName() {
        return getClass().getSimpleName();
    }

    public Class getGameClass() {
        // or this.getClass()
        return gameClass;
    }

    public UnitParameters getStartingParams() {
        return startingParams;
    }

    public Unit setStartingParams(UnitParameters startingParams) {
        this.startingParams = startingParams;
        return this;
    }

    public boolean isAnimal() {
        return UnitClassConstants.ANIMAL_CLASSES.contains(gameClass, true);
    }

    public void revealIfConcealed() {
        squad.batchFloatingStatusLines.addLine("Reveal");
        squad.getEffectManager().removeEffectIfExist(ConcealmentEffect.class);
    }

    public Unit init(boolean gender) {
        this.id = Utils.getNextId();
        this.gender = gender;
        this.name = this.getClass().getSimpleName() + id;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getGameClassName()).append(" ")
                .append("id = ").append(id).append(GdxgConstants.HINT_SPLITTER)
                .append("male? = ").append(gender).append(GdxgConstants.HINT_SPLITTER)
                .append("TEAM = ").append(squad == null ? null : squad.team).append(GdxgConstants.HINT_SPLITTER)
                .append("SQUAD = ").append(squad).append(GdxgConstants.HINT_SPLITTER);
        return sb.toString();
    }

    public void assignToSquad(AbstractSquad object) {
        this.squad = object;
    }
}
