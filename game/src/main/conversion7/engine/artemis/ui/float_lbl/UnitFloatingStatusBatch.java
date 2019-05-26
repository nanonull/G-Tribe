package conversion7.engine.artemis.ui.float_lbl;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ObjectSet;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.team.Team;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class UnitFloatingStatusBatch {
    public static final boolean SHOW_DETAILED_DAMAGE_LABELS = false;
    public static final boolean SHOW_EFFECT_LABELS = false;
    public static final boolean SHOW_TASK_LABELS = false;
    public static final boolean ADD_TO_UNIT_LOG = false;
    private static final Logger LOG = Utils.getLoggerForClass();
    private List<String> lines = new ArrayList<>();
    private boolean started;
    private AreaObject areaObject;

    public UnitFloatingStatusBatch(AreaObject areaObject) {
        this.areaObject = areaObject;
    }

    public void addDebugLine(String line) {
        if (SHOW_DETAILED_DAMAGE_LABELS) {
            addLine(line);
        }
        LOG.info(line);
    }

    public void addImportantLine(String line) {
        addLine(" ! " + StringUtils.capitalize(line));
    }

    public void addLine(String line) {
        if (ADD_TO_UNIT_LOG) {
            areaObject.addSnapshotLog(line, "");
        }
        lines.add(line);
        if (!started) {
            flush(Color.WHITE);
        }
    }

    public void start() {
        if (started) {
            flush(Color.WHITE);
        }
        started = true;
    }

    public void flush(Color color) {
        if (lines.size() > 0) {
            ObjectSet<Team> teams = new ObjectSet<>();
            for (AbstractSquad visibleBySquad : areaObject.getLastCell().visibleBySquads) {
                teams.add(visibleBySquad.team);
            }

            for (Team team : teams) {
                FloatingStatusOnCellSystem.scheduleMessage(areaObject.getLastCell(), team,
                        Utils.joinToString(lines, "\n"), color);
            }

            lines.clear();
        }
        started = false;
    }
}
