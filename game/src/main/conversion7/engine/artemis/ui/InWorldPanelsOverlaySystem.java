package conversion7.engine.artemis.ui;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import conversion7.engine.ClientCore;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.StageObject;
import conversion7.game.stages.world.objects.buildings.Camp;
import org.slf4j.Logger;

public class InWorldPanelsOverlaySystem extends IteratingSystem {
    private static final Logger LOG = Utils.getLoggerForClass();
    private static ComponentMapper<InWorldPanelsOverlayComp> components;

    ClientCore core;

    public InWorldPanelsOverlaySystem() {
        super(Aspect.all(InWorldPanelsOverlayComp.class));
    }

    public static void createFor(StageObject object) {
        components.create(object.entityId).updatedObj = object;
    }

    @Override
    protected void process(int entityId) {
        InWorldPanelsOverlayComp comp = components.get(entityId);
        components.remove(entityId);
        if (comp.updatedObj instanceof Camp) {
            core.getClientUi().getCellDetailsRootPanel().load((Camp) comp.updatedObj, true);
        } else {
            LOG.warn("Unknown {} for InWorldPanelsOverlaySystem", comp.updatedObj);
        }
    }
}