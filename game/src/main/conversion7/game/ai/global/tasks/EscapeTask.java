package conversion7.game.ai.global.tasks;

import conversion7.engine.geometry.Point2s;
import conversion7.game.ai.global.AiTaskType;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.unit.Unit;

public class EscapeTask extends MoveTask {
    public EscapeTask(Unit owner, Cell escapeTo) {
        super(owner.squad, escapeTo, AiTaskType.ESCAPE);
    }

    public EscapeTask(Unit owner, Unit escapeFrom) {
        this(owner, getCellToEscape(owner, escapeFrom.squad));
    }

    public EscapeTask(Unit owner, AreaObject escapeFrom) {
        this(owner, getCellToEscape(owner, escapeFrom));
    }

    public static Cell getCellToEscape(Unit owner, AreaObject escapeFrom) {
        Cell ownerCell = owner.squad.getLastCell();
        Point2s escapeDirection = ownerCell.getDiffWithCell(escapeFrom.getLastCell());
        escapeDirection.multiply(-escapeFrom.getViewRadius());
        return ownerCell.getCell(escapeDirection.x, escapeDirection.y);
    }
}
