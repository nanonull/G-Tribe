package conversion7.game.stages.world.landscape;

import com.badlogic.gdx.utils.Array;
import conversion7.engine.geometry.Point2s;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.World;
import org.slf4j.Logger;

public class BrezenhamLine {
    private static final Logger LOG = Utils.getLoggerForClass();
    private static Array<Cell> cellsWip = new Array<>();

    /** Order of cells: last item = from */
    public static Array<Cell> getCellsLine(Cell to, Cell from) {
        World world = to.getArea().world;
        cellsWip.clear();
        int x0 = to.getWorldPosInCells().x;
        int y0 = to.getWorldPosInCells().y;
        Point2s diffWithFrom = to.getDiffWithCell(from);
        int x1 = x0 + diffWithFrom.x;
        int y1 = y0 + diffWithFrom.y;

        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);

        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int err = dx - dy;

        while (true) {
            cellsWip.add(world.getCell(x0, y0));

            if (x0 == x1 && y0 == y1)
                break;

            int e2 = err * 2;
            if (e2 > -dx) {
                err -= dy;
                x0 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y0 += sy;
            }
        }

        return cellsWip;
    }

}
