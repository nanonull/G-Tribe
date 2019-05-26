package conversion7.engine.artemis.ui.float_lbl;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ObjectSet;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.team.Team;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class CellFloatingStatusBatch {
    private static final Logger LOG = Utils.getLoggerForClass();
    private List<String> lines = new ArrayList<>();
    private boolean started;
    private Cell cell;

    public CellFloatingStatusBatch(Cell cell) {
        this.cell = cell;
    }

    public void addImportantLine(String line) {
        addLine(" ! " + StringUtils.capitalize(line));
    }

    public void addLine(String line) {
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
        ObjectSet<Team> teams = new ObjectSet<>();
        for (AbstractSquad visibleBySquad : cell.visibleBySquads) {
            teams.add(visibleBySquad.team);
        }

        String joinToString = Utils.joinToString(lines, "\n");
        for (Team team : teams) {
            if (team.isHumanPlayer()) {
                FloatingStatusOnCellSystem.scheduleMessage(cell, team,
                        joinToString, color);
            }
        }

        lines.clear();
        started = false;
    }
}
