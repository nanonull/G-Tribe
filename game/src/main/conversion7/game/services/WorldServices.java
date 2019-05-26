package conversion7.game.services;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import conversion7.engine.CameraController;
import conversion7.engine.ClientCore;
import conversion7.engine.Gdxg;
import conversion7.engine.artemis.engine.time.SchedulingSystem;
import conversion7.engine.utils.MathUtils;
import conversion7.engine.utils.PropertiesLoader;
import conversion7.engine.utils.Utils;
import conversion7.engine.utils.Waiting;
import conversion7.game.PackageReflectedConstants;
import conversion7.game.WaitLibrary;
import conversion7.game.run.RunAndScheduleLibrary;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.WorldSettings;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.objects.buildings.Camp;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.objects.unit.WorldSquad;
import conversion7.game.stages.world.team.Team;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.unit_classes.animals.BaseAnimalClass;
import conversion7.game.unit_classes.animals.oligocene.Protoceras;
import conversion7.game.unit_classes.humans.theOldest.Propliopithecus;
import org.slf4j.Logger;

import java.util.UUID;

public class WorldServices {

    private static final Logger LOG = Utils.getLoggerForClass();

    private static boolean ANIMAL_HERD_RANDOM_CLASSES =
            PropertiesLoader.getIntProperty("WorldDirector.ANIMAL_HERD_RANDOM_CLASSES") == 1;
    private static final Array<Class<?>> ANIMALS_OLIGOCENE_CLASSES = PackageReflectedConstants.REFLECTED_PACKAGES.get(
            PackageReflectedConstants.PackageForScan.ANIMALS_OLIGOCENE_PACKAGE);

    // test only
    @Deprecated
    private static AreaObject.UnitSpecialization nextUnitSpecialization;
    public static Class<? extends BaseAnimalClass> ANIMAL_FIRST_CLASS = Protoceras.class;
    public static Class<? extends Unit> HUMAN_FIRST_CLASS = Propliopithecus.class;
    public static Boolean nextUnitGender;

    public static Class<? extends BaseAnimalClass> getRandomAnimalClass() {
        int classIndex = MathUtils.RANDOM.nextInt(ANIMALS_OLIGOCENE_CLASSES.size);
        return (Class<? extends BaseAnimalClass>)
                ANIMALS_OLIGOCENE_CLASSES.get(classIndex);
    }

    public static AreaObject.UnitSpecialization getNextUnitSpecialization() {
        return nextUnitSpecialization;
    }

    public static void setNextUnitSpecialization(AreaObject.UnitSpecialization nextUnitSpecialization) {
        if (true) throw new RuntimeException("Review test");
        WorldServices.nextUnitSpecialization = nextUnitSpecialization;
    }

    public static AbstractSquad createWorldInitialClassUnit(Team team, Cell cell) {
        WorldSquad worldSquad = WorldSquad.create(team.isAnimalTeam() ? ANIMAL_FIRST_CLASS : HUMAN_FIRST_CLASS, team, cell);
        return worldSquad;
    }

//    @Deprecated
//    public static Camp createCamp(Team forTeam, Cell cell) {
//        Camp camp = forTeam.createCamp(cell);
//        LOG.info("createCamp: " + camp);
//        return camp;
//    }

    public static void areaViewerFirstLook() {
        Gdxg.core.activateStage(Gdxg.core.areaViewer);
        CameraController.scheduleCameraFocusOnPlayerSquad();
        RunAndScheduleLibrary.scheduleSingleExecution(500, () -> {
            Gdxg.clientUi.showWorldManagementInterface();
            Gdxg.clientUi.showWelcomeHint();
        });
    }

    public static World scheduleNewWorld(ClientCore core, WorldSettings settings) {
        World currWorld = Gdxg.core.world;
        core.acquireCoreLock();
        UUID scheduledEnt = SchedulingSystem.schedule("scheduleNewWorld", 0, () -> {
            LOG.info("scheduleNewWorld exec");
            Gdxg.core.createNewWorld(settings);
            areaViewerFirstLook();
        });
        core.releaseCoreLock();
        LOG.info("scheduleNewWorld scheduledEnt = {}, artemis {}", scheduledEnt, core.artemis);
        WaitLibrary.waitTillNewWorldInitialized(currWorld, settings.worldCreationTimeout);
        return Gdxg.core.world;
    }

    public static void waitForNextCoreSteps(int steps) {
        for (int i = 0; i < steps; i++) {
            waitForNextCoreStep(null);
        }
    }

    public static void waitForNextCoreStep(Long initialFrame) {
        ClientCore core = Gdxg.core;
        LOG.info("waitForNextCoreStep, step-in, core.frameId {}", core.frameId);
        long inFrameId;
        if (initialFrame == null) {
            // this way may have some issues...
            inFrameId = core.frameId;
        } else {
            inFrameId = initialFrame;
        }
        new Waiting("waitForNextCoreStep") {
            @Override
            public boolean until() {
                return core.frameId > inFrameId;
            }
        }.waitUntil();
        LOG.info("waitForNextCoreStep, step-out, core.frameId {}", core.frameId);
    }

    public static void killTown(Camp camp) {
        throw new GdxRuntimeException("");
    }
}
