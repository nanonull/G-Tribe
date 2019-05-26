package conversion7.game.stages.world.unit.effects.items;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import conversion7.engine.utils.MathUtils;
import conversion7.engine.utils.Utils;
import conversion7.game.Assets;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.objects.unit.WorldSquad;
import conversion7.game.stages.world.team.Team;
import conversion7.game.stages.world.team.events.NoFreeCellForChildEvent;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.stages.world.unit.UnitFertilizer2;
import conversion7.game.stages.world.unit.UnitParameterType;
import conversion7.game.stages.world.unit.UnitParameters;
import conversion7.game.stages.world.unit.effects.AbstractUnitEffect;
import conversion7.game.unit_classes.ClassStandard;
import conversion7.game.unit_classes.UnitClassConstants;
import org.slf4j.Logger;

public class ChildbearingEffect extends AbstractUnitEffect {
    private static final Logger LOG = Utils.getLoggerForClass();

    public static final int PREGNANCY_DURATION = 1;
    private Unit mother;
    private Unit child;
    private int motherExpCollected;
    public Unit mainParent;
    private boolean evolution;
    public Unit father;
    private int fatherInitExp;
    private int motherInitExp;

    @Deprecated
    public ChildbearingEffect(Unit mother, Unit child) {
        super(ChildbearingEffect.class.getSimpleName(), Type.POSITIVE, null);
        this.mother = mother;
        this.child = child;
        child.squad.setMother(mother);
    }

    public ChildbearingEffect(Unit mother) {
        super(ChildbearingEffect.class.getSimpleName(), Type.POSITIVE, null);
        this.mother = mother;
    }

    public Unit getChild() {
        return child;
    }

    @Override
    public String getHint() {
        return super.getHint() + " " + tickCounter + "/" + PREGNANCY_DURATION
                + "\n \nNew unit is born when effect counter expires";
    }

    private int getInitialExp() {
        return (fatherInitExp + motherInitExp) / 4;
    }

    @Override
    public Image getIcon() {
        return new Image(Assets.getTextureReg("child"));
    }

    public void setFatherInitExp(int fatherInitExp) {
        this.fatherInitExp = fatherInitExp;
    }

    public void setMotherInitExp(int motherInitExp) {
        this.motherInitExp = motherInitExp;
    }

    @Override
    public void tick() {
        super.tick();
        AbstractSquad mother = getOwner();
        motherExpCollected += mother.experienceOnStep;
        if (tickCounter >= PREGNANCY_DURATION) {
            evolution = true;
            tryToCreateAndPlaceChildUnitInWorld();
            remove();
        }
    }

    private void tryToCreateAndPlaceChildUnitInWorld() {
        Team team = mother.squad.getTeam();
        mother.squad.batchFloatingStatusLines.start();

        Array<Cell> freeCells = new Array<>();
        for (Cell cell : mother.squad.getLastCell().getCellsAround()) {
            if (cell.canBeSeized()) {
                freeCells.add(cell);
            }
        }
        freeCells.shuffle();


        int maxUnitsToCreate = 1;
        if (MathUtils.testPercentChance(15)) {
            maxUnitsToCreate++;
        }

        if (mother.squad.getLastCell().hasCamp()) {
            maxUnitsToCreate += MathUtils.random(1, 3);
            mother.squad.batchFloatingStatusLines.addLine("Camp effect +");
        }

        // vary rare x6...
        maxUnitsToCreate = MathUtils.random(1, maxUnitsToCreate);
        maxUnitsToCreate = MathUtils.random(1, maxUnitsToCreate);
        maxUnitsToCreate = MathUtils.random(1, maxUnitsToCreate);

        if (freeCells.size == 0) {
            LOG.info(" was NOT born: {}", this);
            team.getNextStepEvents().add(new NoFreeCellForChildEvent(mother.squad));
        } else {
            int unitsCreated = 0;
            for (Cell freeCell : freeCells) {
                if (unitsCreated < maxUnitsToCreate) {
                    WorldSquad child = WorldSquad.create(createChild(), mother.squad.team, freeCell);
                    LOG.info(" was born: {}", child);
                    child.setMother(mother);
                    child.setBloodlineByParents(mother, father);
                    team.world.validateNewUnitBirth(child.unit);
                    child.updateExperience(getInitialExp(), "Born exp");
                    unitsCreated++;
                } else {
                    break;
                }
            }
            mother.squad.validate();
        }
    }

    private Unit createChild() {
        Class<? extends Unit> mainParentClass = mainParent.getClass();
        ClassStandard mainParentStandard = UnitClassConstants.CLASS_STANDARDS.get(mainParentClass);

        // get final class
//        ClassStandard nextClassStandard = null;
//        if (evolution) {
//            for (Class<? extends Unit> childClass : mainParentStandard.childClasses) {
//                ClassStandard classStandard = UnitClassConstants.CLASS_STANDARDS.get(childClass);
//                if (nextClassStandard == null) {
//                    nextClassStandard = classStandard;
//                } else {
//                    if (classStandard.level == nextClassStandard.level) {
//                        if (MathUtils.RANDOM.nextBoolean()) {
//                            nextClassStandard = classStandard;
//                        }
//                    } else if (classStandard.level > nextClassStandard.level) {
//                        nextClassStandard = classStandard;
//                    }
//                }
//            }
//        } else {
//            // evol. possible
//            Array<Class<? extends Unit>> allTargetClasses = new Array<>(mainParentStandard.childClasses);
//            allTargetClasses.add(mainParentClass);
//
//            int rndIdx = MathUtils.random(0, allTargetClasses.size - 1);
//            nextClassStandard = UnitClassConstants.CLASS_STANDARDS.get(allTargetClasses.get(rndIdx));
//        }

        // evol always
        Array<Class<? extends Unit>> allTargetClasses = new Array<>(mainParentStandard.childClasses);
        int rndIdx = MathUtils.random(0, allTargetClasses.size - 1);
        ClassStandard nextClassStandard = UnitClassConstants.CLASS_STANDARDS.get(allTargetClasses.get(rndIdx));

        if (nextClassStandard == null) {
            nextClassStandard = mainParentStandard;
        }
        if (mainParentStandard.childClasses.contains(nextClassStandard.unitClass, true)) {
            evolution = true;
        }

        int newPower = Math.round((father.classStandard.getBasePower() + mother.classStandard.getBasePower()) / 2f);
//        if (evolution) {
//            newPower++;
//        }

        // createUnit with params
        UnitParameters params = new UnitParameters();
        params.setDefault();
        params.put(UnitParameterType.STRENGTH, newPower);
        return UnitFertilizer2.createUnit(nextClassStandard.unitClass,
                MathUtils.RANDOM.nextBoolean()
                , params);
    }
}
