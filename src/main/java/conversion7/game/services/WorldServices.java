package conversion7.game.services;

import com.badlogic.gdx.utils.Array;
import conversion7.engine.ClientCore;
import conversion7.engine.Gdxg;
import conversion7.engine.pools.system.PoolManager;
import conversion7.engine.utils.PropertiesLoader;
import conversion7.engine.utils.Utils;
import conversion7.game.PackageReflectedConstants;
import conversion7.game.classes.animals.AbstractAnimalUnit;
import conversion7.game.classes.animals.oligocene.Amphicyonidae;
import conversion7.game.classes.theOldest.Propliopithecus;
import conversion7.game.classes.theOldest.SahelanthropusTchadensis;
import conversion7.game.stages.world.Area;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.creator.WorldCreator;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.AnimalHerd;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.objects.HumanSquad;
import conversion7.game.stages.world.objects.TownFragment;
import conversion7.game.stages.world.objects.effects.AbstractObjectEffect;
import conversion7.game.stages.world.team.Team;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.stages.world.unit.UnitFertilizer;
import org.slf4j.Logger;

import static org.fest.assertions.api.Assertions.assertThat;

public class WorldServices {

    private static final Logger LOG = Utils.getLoggerForClass();

    private static boolean ANIMAL_HERD_RANDOM_CLASSES =
            PropertiesLoader.getIntProperty("WorldDirector.ANIMAL_HERD_RANDOM_CLASSES") == 1;
    private static final Array<Class<?>> ANIMALS_OLIGOCENE_CLASSES = PackageReflectedConstants.REFLECTED_PACKAGES.get(
            PackageReflectedConstants.PackageReflected.ANIMALS_OLIGOCENE);

    public static Unit.UnitSpecialization nextUnitSpecialization;

    public static void addEffectToAreaObject(Class<? extends AbstractObjectEffect> effectClass, AreaObject areaObject) {
        areaObject.addEffectIfAbsentOtherwiseProlong(effectClass);
    }

    public static void createHumanSquadWithInitialUnits(Team team, Cell cell) {
        HumanSquad army = team.createHumanSquad(cell);

        WorldCreator.START_UNITS_WIP.clear();
        for (int i = 0; i < 8 + Utils.RANDOM.nextInt(3); i++) {
            Unit unit = UnitFertilizer.createStandardUnit(Propliopithecus.class, i % 2 == 0);
            WorldCreator.START_UNITS_WIP.add(unit);
        }
        army.getUnitsController().addUnitsAndValidate(WorldCreator.START_UNITS_WIP);
    }

    public static AnimalHerd createAnimalHerdWithRandomUnits(Cell cell) {
        AnimalHerd animalHerd = World.ANIMAL_TEAM.createAnimalHerd(cell);

        Class<? extends Unit> animalClass;
        if (ANIMAL_HERD_RANDOM_CLASSES) {
            animalClass = getRandomAnimalClass();
        } else {
            animalClass = (Class<? extends Unit>) ANIMALS_OLIGOCENE_CLASSES.get(0);
        }

        Array<Unit> units = PoolManager.ARRAYS_POOL.obtain();
        int amount = Utils.RANDOM.nextInt(10) + 10;
        for (int i = 0; i < amount; i++) {
            units.add(UnitFertilizer.createStandardUnit(animalClass, Utils.RANDOM.nextBoolean()));
        }
        animalHerd.getUnitsController().addUnitsAndValidate(units);
        PoolManager.ARRAYS_POOL.free(units);
        return animalHerd;
    }

    public static Class<? extends AbstractAnimalUnit> getRandomAnimalClass() {
        int classIndex = Utils.RANDOM.nextInt(ANIMALS_OLIGOCENE_CLASSES.size);
        return (Class<? extends AbstractAnimalUnit>)
                ANIMALS_OLIGOCENE_CLASSES.get(classIndex);
    }

    public static HumanSquad createHumanSquadWithSomeUnit(Team forTeam, Cell onCell) {
        return createHumanSquadWithSomeUnit(forTeam, onCell, createSomeHumanUnit());
    }

    public static HumanSquad createHumanSquadWithSomeUnit(Team forTeam, Cell onCell, Unit... units) {
        HumanSquad humanSquad = forTeam.createHumanSquad(onCell);
        LOG.info("createHumanSquadWithSomeUnit: " + humanSquad);
        // do not create more than 1 unit in this certain step, because some tests wait for 1 unit exactly
        humanSquad.getUnitsController().addUnitsAndValidate(new Array<>(units));
        return humanSquad;
    }

    public static AnimalHerd createAnimalHerd(Cell onCell) {
        AnimalHerd animalHerdWithRandomUnits = createAnimalHerdWithRandomUnits(onCell);
        LOG.info("createAnimalHerd: " + animalHerdWithRandomUnits);
        return animalHerdWithRandomUnits;
    }

    public static TownFragment createTownFragment(Team forTeam, Cell onCell) {
        TownFragment townFragment = forTeam.createTown(onCell);
        LOG.info("createTownFragment: " + townFragment);
        return townFragment;
    }

    public static HumanSquad createHumanSquadOutOfWorld() {
        Area area = new Area(0, 0);
        Cell cell = area.getCell(1, 1);
        cell.getLandscapeController().setDefaultDirtCell();
        assertThat(cell.getLandscape()).isNotNull();
        assertThat(cell.getLandscape().type).isNotNull();
        Team team = new Team(true, "testTeam");
        return team.createHumanSquad(cell);
    }

    public static Unit createHumanUnit(Class<? extends Unit> unitClass) {
        Unit.UnitSpecialization specialization = nextUnitSpecialization == null ? Unit.UnitSpecialization.getRandom() : nextUnitSpecialization;
        Unit standardUnit = UnitFertilizer.createStandardUnit(unitClass, Utils.RANDOM.nextBoolean(), specialization);
        LOG.info("createHumanUnit: " + standardUnit);
        nextUnitSpecialization = null;
        return standardUnit;
    }

    public static Unit createSomeHumanUnit() {
        return createHumanUnit(SahelanthropusTchadensis.class);
    }

    public static Array<Unit> createSomeHumanUnits(int amount) {
        Array<Unit> units = new Array<>();
        for (int i = 0; i < amount; i++) {
            units.add(createSomeHumanUnit());
        }
        return units;
    }

    public static AbstractAnimalUnit createSomeAnimalUnit() {
        return (AbstractAnimalUnit) UnitFertilizer.createStandardUnit(Amphicyonidae.class, Utils.RANDOM.nextBoolean());
    }

    public static void showAreaViewer() {
        ClientCore.core.activateStage(World.getAreaViewer());
    }

    public static void showAreaViewerWithWelcome() {
        showAreaViewer();
        Gdxg.clientUi.showWelcomeHint();
    }

    public static void disableFaunaGeneration() {
        WorldCreator.TEAMS_COUNT_LIMIT = 1;
        WorldCreator.MAX_HUMAN_SQUAD_CHANCE = 0;
        WorldCreator.MAX_ANIMAL_HERDS_AMOUNT_LIMIT = 0;
        WorldCreator.MAX_ANIMAL_HERD_CHANCE = 0;
    }
}
