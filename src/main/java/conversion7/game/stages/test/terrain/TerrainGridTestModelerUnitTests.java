package conversion7.game.stages.test.terrain;

import com.badlogic.gdx.math.Interpolation;
import conversion7.engine.geometry.Point2s;
import conversion7.engine.utils.MathUtils;
import conversion7.game.stages.world.landscape.Cell;
import org.fest.assertions.api.Assertions;
import org.fest.assertions.api.Fail;

import java.util.ArrayList;
import java.util.List;

public class TerrainGridTestModelerUnitTests {

    public static void main(String[] args) {
        testImportantNeighbours();
    }

    //

    private static void testImportantNeighbours() {
        TerrainGridTestModeler.initModeler();
        testImportantNeighbours_leftCenter_and_nearestToOrigin_corner();
        testImportantNeighbours_leftBottom_and_nearestToOrigin_corner();
        testImportantNeighbours_leftBottomCorner();
        testImportantNeighbours_leftCenterCorner();
    }

    private static void testImportantNeighbours_leftCenter_and_nearestToOrigin_corner() {
        List<Point2s> cellsAroundMainCurCell = new ArrayList<>();

        Point2s mainCell = new Point2s(1, 1);
        Point2s mainCellOriginInSegm = TerrainGridTestModeler.cellOriginsPositionsInSegments[mainCell.x][mainCell.y];
        Point2s leftBottomCorner = new Point2s(mainCellOriginInSegm).minus(1, 0);

        TerrainGridTestModeler.getCellsAround(mainCell.x, mainCell.y,
                TerrainGridTestModeler.gridWidthInCells, TerrainGridTestModeler.gridHeightInCells,
                cellsAroundMainCurCell);

        TerrainGridTestModeler.getNeighboursAverageHeight(leftBottomCorner.x, leftBottomCorner.y, cellsAroundMainCurCell);

        if (TerrainGridTestModeler._importantNeighbors != 3) {
            Fail.fail("testImportantNeighbours_leftCenter_and_nearestToOrigin_corner");
        }
    }

    private static void testImportantNeighbours_leftBottom_and_nearestToOrigin_corner() {
        List<Point2s> cellsAroundMainCurCell = new ArrayList<>();

        Point2s mainCell = new Point2s(1, 1);
        Point2s mainCellOriginInSegm = TerrainGridTestModeler.cellOriginsPositionsInSegments[mainCell.x][mainCell.y];
        Point2s leftBottomCorner = new Point2s(mainCellOriginInSegm).minus(1, 1);

        TerrainGridTestModeler.getCellsAround(mainCell.x, mainCell.y,
                TerrainGridTestModeler.gridWidthInCells, TerrainGridTestModeler.gridHeightInCells,
                cellsAroundMainCurCell);

        TerrainGridTestModeler.getNeighboursAverageHeight(leftBottomCorner.x, leftBottomCorner.y, cellsAroundMainCurCell);

        if (TerrainGridTestModeler._importantNeighbors != 3) {
            Fail.fail("testImportantNeighbours_leftBottom_and_nearestToOrigin_corner");
        }
    }

    private static void testImportantNeighbours_leftBottomCorner() {
        List<Point2s> cellsAroundMainCurCell = new ArrayList<>();

        Point2s mainCell = new Point2s(1, 1);
        Point2s mainCellOriginInSegm = TerrainGridTestModeler.cellOriginsPositionsInSegments[mainCell.x][mainCell.y];
        Point2s leftBottomCorner = new Point2s(mainCellOriginInSegm).minus(
                TerrainGridTestModeler.cellCubicRadiusInSegments, TerrainGridTestModeler.cellCubicRadiusInSegments);

        TerrainGridTestModeler.getCellsAround(mainCell.x, mainCell.y,
                TerrainGridTestModeler.gridWidthInCells, TerrainGridTestModeler.gridHeightInCells,
                cellsAroundMainCurCell);

        TerrainGridTestModeler.getNeighboursAverageHeight(leftBottomCorner.x, leftBottomCorner.y, cellsAroundMainCurCell);

        if (TerrainGridTestModeler._importantNeighbors != 3) {
            Fail.fail("testImportantNeighbours_leftBottomCorner");
        }
    }

    private static void testImportantNeighbours_leftCenterCorner() {
        List<Point2s> cellsAroundMainCurCell = new ArrayList<>();

        Point2s mainCell = new Point2s(1, 1);
        Point2s mainCellOriginInSegm = TerrainGridTestModeler.cellOriginsPositionsInSegments[mainCell.x][mainCell.y];
        Point2s leftBottomCorner = new Point2s(mainCellOriginInSegm).minus(
                TerrainGridTestModeler.cellCubicRadiusInSegments, 0);

        TerrainGridTestModeler.getCellsAround(mainCell.x, mainCell.y,
                TerrainGridTestModeler.gridWidthInCells, TerrainGridTestModeler.gridHeightInCells,
                cellsAroundMainCurCell);

        TerrainGridTestModeler.getNeighboursAverageHeight(leftBottomCorner.x, leftBottomCorner.y, cellsAroundMainCurCell);

        if (TerrainGridTestModeler._importantNeighbors != 5) {
            Fail.fail("testImportantNeighbours_leftCenterCorner");
        }
    }

    //

    private static void testLibgdxInterpolation() {
        float value = Interpolation.linear.apply(0, 1, 0.5f);
        Assertions.assertThat(value).isEqualTo(0.5f);
    }

    private static void testInterpolatedHeight() {
        int cellSegmentation = Cell.CELL_TERRAIN_SEGMENTATION;
        float cellSegmentSize = 1f / cellSegmentation;
        int centralSegmentIndexInCell = cellSegmentation / 2;
        float maxDistanceFromMainOrigin = MathUtils.getCircleRadiusAroundSquare(centralSegmentIndexInCell * cellSegmentSize);

        float interpolatedHeight;
        float distanceToMainOrigin;
        float currentCellOriginHeight;

        // 1 - original vertex
        currentCellOriginHeight = 1;
        distanceToMainOrigin = 0;
        interpolatedHeight = TerrainGridTestModeler.getInterpolatedHeight(0, distanceToMainOrigin,
                currentCellOriginHeight, maxDistanceFromMainOrigin);
        Assertions.assertThat(interpolatedHeight).isEqualTo(currentCellOriginHeight);

        // 2 - farthest segment vertex in cell
        currentCellOriginHeight = 1;
        float neighboursAverageHeight = 0;
        distanceToMainOrigin = maxDistanceFromMainOrigin;
        interpolatedHeight = TerrainGridTestModeler.getInterpolatedHeight(neighboursAverageHeight, distanceToMainOrigin,
                currentCellOriginHeight, maxDistanceFromMainOrigin);
        Assertions.assertThat(interpolatedHeight).isEqualTo(neighboursAverageHeight);
    }
}
