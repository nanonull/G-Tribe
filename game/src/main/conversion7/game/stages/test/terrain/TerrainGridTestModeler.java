package conversion7.game.stages.test.terrain;

import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import conversion7.engine.geometry.Point2s;
import conversion7.engine.geometry.grid.TriangleGrid;
import conversion7.engine.geometry.terrain.TerrainAttribute;
import conversion7.engine.geometry.terrain.TerrainChunk;
import conversion7.engine.geometry.terrain.TerrainGrid;
import conversion7.engine.geometry.terrain.VertexHeightMap;
import conversion7.engine.utils.MathUtils;
import conversion7.engine.utils.Utils;
import conversion7.game.BattleConstants;
import conversion7.game.stages.world.area.Area;
import conversion7.game.stages.world.landscape.Cell;
import org.slf4j.Logger;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Deprecated
public class TerrainGridTestModeler {

    private static final Logger LOG = Utils.getLoggerForClass();

    public static Point2s[][] cellOriginsPositionsInSegments;
    public static VertexHeightMap cellsHeightMap;

    private static int cellSegmentation = Cell.CELL_TERRAIN_SEGMENTATION;
    private static float cellSegmentSize = 1f / cellSegmentation;
    public static int cellCubicRadiusInSegments = cellSegmentation / 2;
    public static int gridWidthInCells = Area.WIDTH_IN_CELLS;
    public static int gridHeightInCells = Area.HEIGHT_IN_CELLS;
    private static int segmentsWidth = gridWidthInCells * cellSegmentation;
    private static int segmentsHeight = gridHeightInCells * cellSegmentation;
    private static float maxDistanceFromMainOrigin = Vector2.dst(cellCubicRadiusInSegments, cellCubicRadiusInSegments, 0, 0);
    public static int _importantNeighbors;

    public static void initModeler() {
        cellsHeightMap = new VertexHeightMap(gridWidthInCells, gridHeightInCells);
        cellOriginsPositionsInSegments = new Point2s[gridWidthInCells][gridHeightInCells];
        for (int x = 0; x < cellsHeightMap.verticesAmountX; x++) {
            for (int y = 0; y < cellsHeightMap.verticesAmountY; y++) {
                cellsHeightMap.heights[x][y] = (MathUtils.RANDOM.nextInt(3) - 1) / 3f;
            }
        }

        for (int x = 0; x < gridWidthInCells; x++) {
            for (int y = 0; y < gridHeightInCells; y++) {
                cellOriginsPositionsInSegments[x][y] = new Point2s(x * cellSegmentation + cellCubicRadiusInSegments,
                        y * cellSegmentation + cellCubicRadiusInSegments);
            }
        }
    }

    /**
     * Segments are built from left to right, from top to bottom.<br>
     * And this differs from AreaViewer draw order: left to right, bottom to top!
     */
    public static TriangleGrid createDetailedTerrainGridWithSegmentation() {

        // terrain > cell > segment

        TriangleGrid triangleGrid = new TriangleGrid(segmentsWidth, segmentsHeight, false,
                new VertexAttribute[]{VertexAttribute.Position(), VertexAttribute.TexCoords(0)
                        , TerrainAttribute.getVertexAttribute()
                });

        TerrainGrid terrainGrid = new TerrainGrid(segmentsWidth, segmentsHeight);
        initModeler();

        short n1, n2, n3, n4;
        float xPos;
        float yPos;
        float cellHeight;
        int segmentX, segmentY;
        List<Point2s> cellsAroundMainCurCell = new ArrayList<>();
        float soilY = 0.2f;

        for (int cellX = 0; cellX < gridWidthInCells; cellX++) {
            for (int cellY = 0; cellY < gridHeightInCells; cellY++) {

                cellHeight = 0;
                Point2s currentCellOriginPosSegment = cellOriginsPositionsInSegments[cellX][cellY];
                cellsAroundMainCurCell.clear();
                getCellsAround(cellX, cellY, gridWidthInCells, gridHeightInCells, cellsAroundMainCurCell);

                for (int sx = 0; sx < cellSegmentation; sx++) {
                    for (int sy = 0; sy < cellSegmentation; sy++) {
                        xPos = cellX + cellSegmentSize * sx;
                        yPos = cellY + cellSegmentSize * sy;
                        segmentX = cellX * cellSegmentation + sx;
                        segmentY = cellY * cellSegmentation + sy;

                        /* top Right Vertex */
                        n1 = triangleGrid.addVertex(xPos + cellSegmentSize, cellHeight, yPos);
                        triangleGrid.addUVMap(1, 0);
                        triangleGrid.addSoil(0.5f, MathUtils.RANDOM.nextFloat(), 0.5f, 0.5f);
                        terrainGrid.addVertex(triangleGrid.currentVertex, segmentX + 1, segmentY);
                        /* top left Vertex */
                        n2 = triangleGrid.addVertex(xPos, cellHeight, yPos);
                        triangleGrid.addUVMap(0, 1);
                        triangleGrid.addSoil(0.5f, MathUtils.RANDOM.nextFloat(), 0.5f, 0.5f);
                        terrainGrid.addVertex(triangleGrid.currentVertex, segmentX, segmentY);

                        /* bottom Right Vertex */
                        n3 = triangleGrid.addVertex(xPos + cellSegmentSize, cellHeight, yPos + cellSegmentSize);
                        triangleGrid.addUVMap(1, 1);
                        triangleGrid.addSoil(0.5f, MathUtils.RANDOM.nextFloat(), 0.5f, 0.5f);
                        terrainGrid.addVertex(triangleGrid.currentVertex, segmentX + 1, segmentY + 1);

                        triangleGrid.addIndices(n1, n2, n3);

                        /* Bottom left Vertex */
                        n4 = triangleGrid.addVertex(xPos, cellHeight, yPos + cellSegmentSize);
                        triangleGrid.addUVMap(0, 0);
                        triangleGrid.addSoil(0.5f, MathUtils.RANDOM.nextFloat(), 0.5f, 0.5f);
                        terrainGrid.addVertex(triangleGrid.currentVertex, segmentX, segmentY + 1);

                        triangleGrid.addIndices(n3, n2, n4);

                        // segment height:

                        float currentCellOriginHeight = cellsHeightMap.heights[cellX][cellY]; // OC.height
                        float distanceToMainOrigin = (float) currentCellOriginPosSegment.distance(segmentX, segmentY); // dO
                        float neighboursAverageHeight = getNeighboursAverageHeight(segmentX, segmentY, cellsAroundMainCurCell);

                        float segmentVertexHeight = // vertex.height
                                getInterpolatedHeight(neighboursAverageHeight, distanceToMainOrigin, currentCellOriginHeight, maxDistanceFromMainOrigin);
                        terrainGrid.vertices[segmentX][segmentY].setY(segmentVertexHeight);
                    }
                }
            }
        }

        return triangleGrid;
    }

    /** With filter out-of-bounds */
    public static void getCellsAround(int cellX, int cellY, int worldWidth, int worldHeight, List<Point2s> targetArray) {
        for (Point point : BattleConstants.CELLS_AROUND) {
            int newX = cellX + point.x;
            int newY = cellY + point.y;

            if (newX < 0 || newX >= worldWidth
                    || newY < 0 || newY >= worldHeight) {
                // skip out of world bounds
                continue;
            }

            targetArray.add(new Point2s(newX, newY));
        }
    }

    public static float getInterpolatedHeight(float neighboursAverageHeight, float distanceToMainOrigin,
                                              float currentCellOriginHeight, float maxDistanceFromMainOrigin) {
        float interpolatedDst = Interpolation.linear.apply(0, maxDistanceFromMainOrigin / TerrainChunk.NEIGHBOR_AFFECT_DECREASE_MODIFIER,
                distanceToMainOrigin / maxDistanceFromMainOrigin);
        return Interpolation.linear.apply(currentCellOriginHeight, neighboursAverageHeight, interpolatedDst / maxDistanceFromMainOrigin);
    }

    public static float getNeighboursAverageHeight(int segmentX, int segmentY, List<Point2s> cellsAroundMainCurCell) {
//        LOG.info("getNeighboursAverageHeight");
        float neighboursAverageHeight = 0;
        _importantNeighbors = 0;

        for (Point2s cellAround : cellsAroundMainCurCell) {
            Point2s cellPosInSegments = cellOriginsPositionsInSegments[cellAround.x][cellAround.y];
            double distanceToNeighborInSegm = cellPosInSegments.distance(segmentX, segmentY);
            if (distanceToNeighborInSegm < TerrainChunk.IGNORE_NEIGHBOR_CELL_ON_SEGMENT_DISTANCE) {
                neighboursAverageHeight += cellsHeightMap.heights[cellAround.x][cellAround.y];
                _importantNeighbors++;
            }
        }

        if (_importantNeighbors == 0) {
            return 0;
        } else {
            return neighboursAverageHeight / _importantNeighbors;
        }
    }
}
