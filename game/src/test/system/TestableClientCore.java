package system;

import com.artemis.BaseSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import conversion7.engine.ClientCore;
import conversion7.engine.Gdxg;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.area.Area;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.team.Team;
import org.slf4j.Logger;
import system.break_point_steps_core.TestTargetApplication;

import java.util.ArrayList;
import java.util.List;

public class TestableClientCore extends ClientCore implements TestTargetApplication {

    private static final Logger LOG = Utils.getLoggerForClass();
    static final boolean RELEASE_REFERENCES_ON_WORLD = "y".equals(System.getProperty("RELEASE_REFERENCES_ON_WORLD"));
    private volatile boolean inRender = true;
    private int framesLocked;
    private volatile Runnable coreStepBody;
    public final Array<Throwable> applicationErrorsTotal = new Array<>();
    List<World> worlds = new ArrayList<>();

    @Override
    public int getFramesInLock() {
        return framesLocked;
    }

    @Override
    public boolean isInRender() {
        return inRender;
    }

    @Override
    public long getCoreFrameId() {
        return Gdx.graphics.getFrameId();
    }

    @Override
    public void create() {
        super.create();

    }

    @Override
    public void scheduleCoreStep(Runnable coreStepBody) {
        this.coreStepBody = coreStepBody;
    }

    @Override
    public boolean hasCoreStepScheduled() {
        return coreStepBody != null;
    }

    @Override
    public boolean flushErrors() {
        boolean flushErrors;
        synchronized (applicationErrorsOnCurrentTick) {
            applicationErrorsTotal.addAll(applicationErrorsOnCurrentTick);
            flushErrors = super.flushErrors();
        }

        return flushErrors;
    }

    public void reset() {
        acquireCoreLock();
        // reset before world release
        for (BaseSystem system : artemis.getSystems()) {
            system.setEnabled(false);
        }
        releaseReferencesOnPreviousWorlds();
        areaViewer.reset();
        releaseCoreLock();
    }

    // somewhat world references are alive when run junit tests...
    // when create worlds in a loop - that's not reproduced
    private void releaseReferencesOnPreviousWorlds() {
        World activeWorld = Gdxg.core.world;
        if (activeWorld != null) {
            worlds.add(activeWorld);
        }

        // release from end, left last world ref
        if (worlds.size() > 1) {
            World removedWorld = worlds.remove(0);
            for (Area area : removedWorld.areasList) {
                for (Cell[] row : area.cells) {
                    for (Cell cell : row) {
                        cell.resourcesGenerator.world = null;
                    }

                }

                area.world = null;
            }
            for (Team team : removedWorld.teams) {
                team.world = null;
            }
        }
    }
}
