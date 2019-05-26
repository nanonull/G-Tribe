package conversion7.game.stages.test.terrain;

import com.badlogic.gdx.graphics.VertexAttribute;
import conversion7.engine.geometry.grid.AbstractTriangleGrid;
import conversion7.engine.geometry.grid.TriangleGrid;
import conversion7.engine.geometry.grid.TriangleVertex;
import conversion7.engine.utils.MathUtils;
import conversion7.game.stages.world.area.Area;
import conversion7.game.stages.world.landscape.Cell;

public class GridsTestModeler {

    public static TriangleGrid createGridWithSharedVertices() {

        float cellSegmentSize = 1;
        int width = 3;
        int height = 2;

        TriangleGridWithSharedVertices triangleGrid = new TriangleGridWithSharedVertices(width, height, false,
                new VertexAttribute[]{VertexAttribute.Position(), VertexAttribute.TexCoords(0)});

        short n1, n2, n3, n4;
        float xPos;
        float yPos;
        float cellHeight;

        TriangleVertex[][] vertices = new TriangleVertex[width + 1][height + 1];

        for (int cx = 0; cx < width; cx++) {
            for (int cy = 0; cy < height; cy++) {
                xPos = cx + cellSegmentSize;
                yPos = cy + cellSegmentSize;
                cellHeight = 0;

                /* top Right Vertex */
                n1 = triangleGrid.addVertex(xPos + cellSegmentSize, cellHeight, yPos);
                triangleGrid.addUVMap(1, 0);
                vertices[cx + 1][cy] = triangleGrid.currentVertex;
                /* top left Vertex */
                if (cx > 0) {
                    TriangleVertex prevColumnVertex = vertices[cx][cy];
                    n2 = triangleGrid.addVertex(prevColumnVertex);
                } else {
                    n2 = triangleGrid.addVertex(xPos, cellHeight, yPos);
                    triangleGrid.addUVMap(0, 1);
                    vertices[cx][cy] = triangleGrid.currentVertex;
                }
                /* bottom Right Vertex */
                n3 = triangleGrid.addVertex(xPos + cellSegmentSize, cellHeight, yPos + cellSegmentSize);
                triangleGrid.addUVMap(1, 1);
                vertices[cx + 1][cy + 1] = triangleGrid.currentVertex;

                triangleGrid.addIndices(n1, n2, n3);

                /* Bottom left Vertex */
                if (cx > 0) {
                    TriangleVertex prevColumnVertex = vertices[cx][cy + 1];
                    n4 = triangleGrid.addVertex(prevColumnVertex);
                } else {
                    n4 = triangleGrid.addVertex(xPos, cellHeight, yPos + cellSegmentSize);
                    triangleGrid.addUVMap(0, 0);
                    vertices[cx][cy + 1] = triangleGrid.currentVertex;
                }

                triangleGrid.addIndices(n3, n2, n4);

            }
        }

        return triangleGrid;
    }

    public static AbstractTriangleGrid createTriangleGridWithCellSegmentation() {

        int cellSegmentation = Cell.CELL_TERRAIN_SEGMENTATION;
        float cellSegmentSize = 1f / cellSegmentation;
        int width = Area.WIDTH_IN_CELLS * cellSegmentation;
        int height = Area.HEIGHT_IN_CELLS * cellSegmentation;

        TriangleGrid triangleGrid = new TriangleGrid(width, height, false,
                new VertexAttribute[]{VertexAttribute.Position(), VertexAttribute.ColorPacked(), VertexAttribute.Normal()});

        short n1, n2, n3;
        float xPos;
        float yPos;
        float cellHeight;

        for (int cx = 0; cx < Area.WIDTH_IN_CELLS; cx++) {
            for (int cy = 0; cy < Area.HEIGHT_IN_CELLS; cy++) {

                for (int x = 0; x < cellSegmentation; x++) {
                    for (int y = 0; y < cellSegmentation; y++) {
                        xPos = cx + cellSegmentSize * x;
                        yPos = cy + cellSegmentSize * y;
                        cellHeight = MathUtils.RANDOM.nextBoolean() ? 0.4f : 0;
                        if (x == 1 && y == 1) {
                            cellHeight = 0.2f;
                        }


                        n1 = triangleGrid.addVertex(xPos + cellSegmentSize, cellHeight, yPos);
                        triangleGrid.addColorToVertex(1, 0, 0, 1);
                        triangleGrid.addNormal();
                        /* top left Vertex */
                        n2 = triangleGrid.addVertex(xPos, cellHeight, yPos);
                        triangleGrid.addColorToVertex(0, 1, 0, 1);
                        triangleGrid.addNormal();
                        /* bottom Right Vertex */
                        n3 = triangleGrid.addVertex(xPos + cellSegmentSize, cellHeight, yPos + cellSegmentSize);
                        triangleGrid.addColorToVertex(0, 0, 1, 1);
                        triangleGrid.addNormal();

                        triangleGrid.addIndices(n1, n2, n3);
                        /* Bottom left Vertex */
                        n1 = triangleGrid.addVertex(xPos, cellHeight, yPos + cellSegmentSize);
                        triangleGrid.addColorToVertex(0, 0, 0, 0);
                        triangleGrid.addNormal();

                        triangleGrid.addIndices(n3, n2, n1);
                    }
                }
            }
        }

        return triangleGrid;
    }

    public static AbstractTriangleGrid createSimpleTriangleGrid() {

        int size = 1;
        int width = 1;
        int height = 1;

        TriangleGrid triangleGrid = new TriangleGrid(width, height, false,
                new VertexAttribute[]{VertexAttribute.Position(), VertexAttribute.TexCoords(0)});

        short n1, n2, n3;
        int xPos;
        int yPos;


        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                xPos = size * x;
                yPos = size * y;

                  /* Top right Vertex */
                n1 = triangleGrid.addVertex(xPos + size, 0, yPos);
                triangleGrid.addUVMap(1, 0);
                /* top left Vertex */
                n2 = triangleGrid.addVertex(xPos, 0, yPos);
                triangleGrid.addUVMap(0, 1);
                /* bottom Right Vertex */
                n3 = triangleGrid.addVertex(xPos + size, 0, yPos + size);
                triangleGrid.addUVMap(1, 1);

                triangleGrid.addIndices(n1, n2, n3);
                /* Bottom left Vertex */
                n1 = triangleGrid.addVertex(xPos, 0, yPos + size);
                triangleGrid.addUVMap(0, 0);

                triangleGrid.addIndices(n3, n2, n1);
            }
        }

        return triangleGrid;
    }


}
