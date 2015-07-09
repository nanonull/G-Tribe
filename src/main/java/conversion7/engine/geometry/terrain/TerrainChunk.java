package conversion7.engine.geometry.terrain;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.utils.GdxRuntimeException;
import conversion7.engine.geometry.Point2s;
import conversion7.engine.geometry.grid.TriangleGrid;
import conversion7.engine.utils.FastAsserts;
import conversion7.engine.utils.NormalizerUtil;
import conversion7.game.stages.world.Area;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.landscape.Cell;

/** It will build triangleGrid using pre-calculated vertexDataGrid for whole world */
public class TerrainChunk {

    public final static float NEIGHBOR_AFFECT_DECREASE_MODIFIER = 1.5f;
    static int _importantNeighbors;
    private final int gridWidthInCells;
    private final int gridHeightInCells;
    private final int cellSegmentation;
    private final float segmentSize;

    private int segmentsWidth;
    private int segmentsHeight;
    private TriangleGrid triangleGrid;
    private Area area;
    public static final float IGNORE_NEIGHBOR_CELL_ON_SEGMENT_DISTANCE = Cell.CELL_TERRAIN_SEGMENTATION +
            Cell.CELL_TERRAIN_SEGMENTATION / 4f;
    private static final NormalizerUtil NEIGHBOR_CELL_AFFECT_NORMALIZER =
            new NormalizerUtil(IGNORE_NEIGHBOR_CELL_ON_SEGMENT_DISTANCE
                    - Cell.CELL_ORIGIN_IN_SEGMENTS,
                    0, 1, 0);

    public TerrainChunk(Area area) {
        this.area = area;
        this.gridWidthInCells = Area.WIDTH_IN_CELLS;
        this.gridHeightInCells = Area.HEIGHT_IN_CELLS;
        this.cellSegmentation = Cell.CELL_TERRAIN_SEGMENTATION;
        this.segmentSize = Cell.CELL_SEGMENT_SIZE;

        segmentsWidth = gridWidthInCells * cellSegmentation;
        segmentsHeight = gridHeightInCells * cellSegmentation;

        FastAsserts.assertMoreThan(cellSegmentation, 1);

        build();
    }

    public static TerrainVertexData getAverageInterpolatedVertex(Cell curCell, int segmentX, int segmentY) {
        float distanceToMainOrigin = (float) curCell.getSegmentDistanceToOrigin(segmentX, segmentY);
        Point2s curSegmentWorldPos = curCell.getSegmentWorldPos(segmentX, segmentY);
        return getAverageInterpolatedVertex(curCell, curSegmentWorldPos, distanceToMainOrigin);
    }

    public static TerrainVertexData getAverageInterpolatedVertex(Cell curCell, Point2s curSegmentWorldPos, float distanceToMainOrigin) {
        _importantNeighbors = 0;
        TerrainVertexData averageNeighborVertexData = new TerrainVertexData();
        for (Cell cellAround : curCell.getNeighborCells()) {
            Point2s cellAroundPosInSegments = cellAround.getOriginWorldPosInSegments();
            float distanceToNeighborInSegm = Cell.getDistanceBtwSegments(curSegmentWorldPos, cellAroundPosInSegments);
            if (distanceToNeighborInSegm < IGNORE_NEIGHBOR_CELL_ON_SEGMENT_DISTANCE) {
                averageNeighborVertexData.append(cellAround.getLandscape().getTerrainVertexData());
                _importantNeighbors++;
            }
        }

        // TODO clear on iteration end
        if (_importantNeighbors < 3) {
            throw new GdxRuntimeException("_importantNeighbors: " + _importantNeighbors);
        }

        // origin will be affected by zero, farthest cell will be affected almost fully by average neighbors
        averageNeighborVertexData.divide(_importantNeighbors);
        TerrainVertexData copyOriginVertexData = new TerrainVertexData(curCell.getLandscape().getTerrainVertexData());
        copyOriginVertexData.interpolateWithNeighbors(averageNeighborVertexData,
                distanceToMainOrigin, Cell.DISTANCE_FROM_CORNER_TO_ORIGIN_IN_SEGMENTS);
        return copyOriginVertexData;
    }

    private void build() {

        triangleGrid = new TriangleGrid(segmentsWidth, segmentsHeight, false,
                new VertexAttribute[]{VertexAttribute.Position(),
                        VertexAttribute.Normal(),
                        VertexAttribute.TexCoords(0),
                        TerrainAttribute.getVertexAttribute()
                });

        int startSegmX = area.worldPosInCells.x * Cell.CELL_TERRAIN_SEGMENTATION;
        int startSegmY = area.worldPosInCells.y * Cell.CELL_TERRAIN_SEGMENTATION;
        TerrainDataGrid terrainDataGrid = World.worldTerrainDataGrid;

        short n1, n2, n3, n4;
        float xPos;
        float yPos;
        int segmWorldX, segmWorldY;
        for (int sx = 0; sx < Area.WIDTH_IN_SEGMENTS; sx++) {
            for (int sy = 0; sy < Area.HEIGHT_IN_SEGMENTS; sy++) {
                xPos = segmentSize * sx;
                yPos = -(segmentSize * sy);
                segmWorldX = sx + startSegmX;
                segmWorldY = sy + startSegmY;
                TerrainVertexData vertexData00 = terrainDataGrid.vertices[segmWorldX][segmWorldY];
                TerrainVertexData vertexData01 = terrainDataGrid.vertices[segmWorldX][segmWorldY + 1];
                TerrainVertexData vertexData10 = terrainDataGrid.vertices[segmWorldX + 1][segmWorldY];
                TerrainVertexData vertexData11 = terrainDataGrid.vertices[segmWorldX + 1][segmWorldY + 1];

                        /* top Right Vertex */
                n1 = triangleGrid.addVertex(xPos + segmentSize, vertexData10.getHeight(), yPos);
                triangleGrid.addUVMap(1, 0);
                triangleGrid.addSoil(vertexData10.getSoil());
                triangleGrid.addNormal();
                        /* top left Vertex */
                n2 = triangleGrid.addVertex(xPos, vertexData00.getHeight(), yPos);
                triangleGrid.addUVMap(0, 1);
                triangleGrid.addSoil(vertexData00.getSoil());
                triangleGrid.addNormal();

                        /* bottom Right Vertex */
                n3 = triangleGrid.addVertex(xPos + segmentSize, vertexData11.getHeight(), yPos - segmentSize);
                triangleGrid.addUVMap(1, 1);
                triangleGrid.addSoil(vertexData11.getSoil());
                triangleGrid.addNormal();

                triangleGrid.addIndices(n1, n2, n3);

                // comment to see half quads!
                        /* Bottom left Vertex */
                n4 = triangleGrid.addVertex(xPos, vertexData01.getHeight(), yPos - segmentSize);
                triangleGrid.addUVMap(0, 0);
                triangleGrid.addSoil(vertexData01.getSoil());
                triangleGrid.addNormal();

                triangleGrid.addIndices(n3, n2, n4);
            }
        }
    }


    public Mesh getMesh() {
        return triangleGrid.getMesh();
    }

    public int getVertexUnitsAmount() {
        return triangleGrid.getVertexUnitsTotalAmount();
    }
}
