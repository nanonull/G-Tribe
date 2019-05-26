package conversion7.game.stages.world.view;

import conversion7.engine.Gdxg;
import conversion7.engine.geometry.Point2s;
import conversion7.engine.utils.Utils;
import org.slf4j.Logger;

public class ViewHelper {

    private static final Logger LOG = Utils.getLoggerForClass();

    private AreaViewer areaViewer;
    boolean shiftInProgress = false;

    public ViewHelper(AreaViewer areaViewer) {
        this.areaViewer = areaViewer;
    }

    /**
     * focusedArea = focusedArea + shiftBy
     * load focusedArea
     * move camera by -shiftBy
     */
    protected void shiftView(Point2s shiftBy) {
        shiftInProgress = true;
        Point2s focusedAreaWorldPosInAreas = new Point2s(areaViewer.getFocusedArea().worldPosInAreas);
        shiftAreaLines(shiftBy, focusedAreaWorldPosInAreas);
//        Gdxg.graphic.getCameraController().translateCamera(-shiftBy.x * Area.WIDTH_IN_CELLS, -shiftBy.y * Area.HEIGHT_IN_CELLS);
        shiftInProgress = false;
    }

    /**
     * Do not try repeat this...
     */
    private void shiftAreaLines(Point2s shiftBy, Point2s focusedAreaWorldPosInAreas) {
        if (shiftBy.x < 0) {
            shiftLeft(focusedAreaWorldPosInAreas);
            focusedAreaWorldPosInAreas.x--;
        } else if (shiftBy.x > 0) {
            shiftRight(focusedAreaWorldPosInAreas);
            focusedAreaWorldPosInAreas.x++;
        }

        if (shiftBy.y < 0) {
            shiftDown(focusedAreaWorldPosInAreas);
            focusedAreaWorldPosInAreas.y--;
        } else if (shiftBy.y > 0) {
            shiftUp(focusedAreaWorldPosInAreas);
            focusedAreaWorldPosInAreas.y++;
        }

        // apply new positions and load new areas
        for (int x = 0; x < AreaViewer.WIDTH_IN_AREAS; x++) {
            for (int y = 0; y < AreaViewer.HEIGHT_IN_AREAS; y++) {
                AreaView areaView = areaViewer.views[x][y];
                areaView.applyPositions();
                if (areaView.newAssignedArea != null) {
                    areaView.loadArea();
                }
            }
        }
    }

    private void shiftUp(Point2s focusedAreaWorldPosInAreas) {
        AreaView[] hiddenRow = new AreaView[AreaViewer.WIDTH_IN_AREAS];
        // translate & relink hidden views (line contra to shift direction)
        for (int x = 0; x < AreaViewer.WIDTH_IN_AREAS; x++) {
            hiddenRow[x] = areaViewer.views[x][0];
            hiddenRow[x].translateInViews(0, AreaViewer.HEIGHT_IN_AREAS);
        }

        // relink active view-lines
        for (int row = 0; row < AreaViewer.HEIGHT_IN_AREAS - 1; row++) {
            for (int view = 0; view < AreaViewer.WIDTH_IN_AREAS; view++) {
                areaViewer.views[view][row] = areaViewer.views[view][row + 1];
            }
        }

        // assign new area to relinked views
        for (int x = 0; x < AreaViewer.WIDTH_IN_AREAS; x++) {
            areaViewer.views[x][AreaViewer.HEIGHT_IN_AREAS - 1] = hiddenRow[x];
            hiddenRow[x].assignArea(Gdxg.core.world.getArea(
                    focusedAreaWorldPosInAreas.x - AreaViewer.X_RADIUS + x,
                    focusedAreaWorldPosInAreas.y + (AreaViewer.Y_RADIUS + 1)));
        }
    }

    private void shiftDown(Point2s focusedAreaWorldPosInAreas) {
        AreaView[] hiddenRow = new AreaView[AreaViewer.WIDTH_IN_AREAS];
        // translate & relink hidden views (line contra to shift direction)
        for (int x = 0; x < AreaViewer.WIDTH_IN_AREAS; x++) {
            hiddenRow[x] = areaViewer.views[x][AreaViewer.HEIGHT_IN_AREAS - 1];
            hiddenRow[x].translateInViews(0, -AreaViewer.HEIGHT_IN_AREAS);
        }

        // relink active view-lines
        for (int row = AreaViewer.HEIGHT_IN_AREAS - 1; row > 0; row--) {
            for (int view = 0; view < AreaViewer.WIDTH_IN_AREAS; view++) {
                areaViewer.views[view][row] = areaViewer.views[view][row - 1];
            }
        }

        // assign new area to relinked views
        for (int x = 0; x < AreaViewer.WIDTH_IN_AREAS; x++) {
            areaViewer.views[x][0] = hiddenRow[x];
            hiddenRow[x].assignArea(Gdxg.core.world.getArea(
                    focusedAreaWorldPosInAreas.x - AreaViewer.X_RADIUS + x,
                    focusedAreaWorldPosInAreas.y - (AreaViewer.Y_RADIUS + 1)));
        }
    }

    private void shiftRight(Point2s focusedAreaWorldPosInAreas) {
        AreaView[] hiddenColumn = new AreaView[AreaViewer.HEIGHT_IN_AREAS];
        // translate & relink hidden views (line contra to shift direction)
        for (int y = 0; y < AreaViewer.HEIGHT_IN_AREAS; y++) {
            hiddenColumn[y] = areaViewer.views[0][y];
            hiddenColumn[y].translateInViews(AreaViewer.WIDTH_IN_AREAS, 0);
        }

        // relink active view-lines
        for (int column = 0; column < AreaViewer.WIDTH_IN_AREAS - 1; column++) {
            for (int view = 0; view < AreaViewer.HEIGHT_IN_AREAS; view++) {
                areaViewer.views[column][view] = areaViewer.views[column + 1][view];
            }
        }

        // assign new area to relinked views
        for (int y = 0; y < AreaViewer.HEIGHT_IN_AREAS; y++) {
            areaViewer.views[AreaViewer.WIDTH_IN_AREAS - 1][y] = hiddenColumn[y];
            hiddenColumn[y].assignArea(Gdxg.core.world.getArea(
                    focusedAreaWorldPosInAreas.x + (AreaViewer.X_RADIUS + 1),
                    focusedAreaWorldPosInAreas.y - AreaViewer.Y_RADIUS + y));
        }
    }

    private void shiftLeft(Point2s focusedAreaWorldPosInAreas) {
        AreaView[] hiddenColumn = new AreaView[AreaViewer.HEIGHT_IN_AREAS];
        //Reset hidden views (row contra to shift direction)
        for (int y = 0; y < AreaViewer.HEIGHT_IN_AREAS; y++) {
            hiddenColumn[y] = areaViewer.views[AreaViewer.WIDTH_IN_AREAS - 1][y];
            hiddenColumn[y].translateInViews(-AreaViewer.WIDTH_IN_AREAS, 0);
        }

        // relink active view-lines
        for (int column = AreaViewer.WIDTH_IN_AREAS - 1; column > 0; column--) {
            for (int view = 0; view < AreaViewer.HEIGHT_IN_AREAS; view++) {
                areaViewer.views[column][view] = areaViewer.views[column - 1][view];
            }
        }

        // assign new area to relinked views
        for (int y = 0; y < AreaViewer.HEIGHT_IN_AREAS; y++) {
            areaViewer.views[0][y] = hiddenColumn[y];
            hiddenColumn[y].assignArea(Gdxg.core.world.getArea(
                    focusedAreaWorldPosInAreas.x - (AreaViewer.X_RADIUS + 1),
                    focusedAreaWorldPosInAreas.y - AreaViewer.Y_RADIUS + y));
        }
    }

}
