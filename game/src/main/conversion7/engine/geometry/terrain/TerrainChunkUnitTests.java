package conversion7.engine.geometry.terrain;

import conversion7.engine.ClientApplication;
import conversion7.engine.ClientCore;
import conversion7.engine.Gdxg;
import conversion7.engine.geometry.Point2s;
import conversion7.engine.utils.Utils;
import conversion7.game.GdxgConstants;
import conversion7.game.WaitLibrary;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.area.Area;
import conversion7.game.stages.world.landscape.Cell;
import org.slf4j.Logger;
import org.testng.annotations.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class TerrainChunkUnitTests {

    private static final Logger LOG = Utils.getLoggerForClass();

    @Test
    public void run() {
        ClientApplication.startLibgdxCoreApp(new Run());
        Gdxg.core.waitCreated();
        World currWorld = Gdxg.core.world;
        WaitLibrary.waitTillNewWorldInitialized(currWorld, 10);

        testImportantNeighbours_leftBottomCell_leftCenter_and_nearestToOrigin_segment();
        testImportantNeighbours_leftBottomCell_leftBottom_and_nearestToOrigin_corner();
        testImportantNeighbours_leftBottomCell_leftBottomCorner();
        testImportantNeighbours_leftBottomCell_leftCenterCorner();

        testImportantNeighbours_leftTopCell_leftCenter_and_nearestToOrigin_segment();
        testImportantNeighbours_leftTopCell_leftTop_and_nearestToOrigin_corner();
        testImportantNeighbours_leftTopCell_leftTopCorner();
        testImportantNeighbours_leftTopCell_leftCenterCorner();

        testImportantNeighbours_rightTopCell_rightCenter_and_nearestToOrigin_segment();
        testImportantNeighbours_rightTopCell_rightTop_and_nearestToOrigin_corner();
        testImportantNeighbours_rightTopCell_rightTopCorner();
        testImportantNeighbours_rightTopCell_rightCenterCorner();

        testImportantNeighbours_rightBottomCell_rightCenter_and_nearestToOrigin_segment();
        testImportantNeighbours_rightBottomCell_rightBottom_and_nearestToOrigin_corner();
        testImportantNeighbours_rightBottomCell_rightBottomCorner();
        testImportantNeighbours_rightBottomCell_rightCenterCorner();

    }

    private void testImportantNeighbours_leftBottomCell_leftCenter_and_nearestToOrigin_segment() {
        LOG.info("testImportantNeighbours_leftBottomCell_leftCenter_and_nearestToOrigin_segment");
        Area area = Gdxg.core.world.areas[0][0];
        Cell cell = area.getCell(0, 0);
        Point2s segment = new Point2s(Cell.CELL_SEGMENTS_ORIGIN).minus(1, 0);

        TerrainChunk terrainChunk = new TerrainChunk(area);
        terrainChunk.getAverageInterpolatedVertex(cell, segment.x, segment.y);

        assertThat(terrainChunk._importantNeighbors).isEqualTo(3);
    }

    // leftBottomCell

    private void testImportantNeighbours_leftBottomCell_leftBottom_and_nearestToOrigin_corner() {
        LOG.info("testImportantNeighbours_leftBottomCell_leftBottom_and_nearestToOrigin_corner");

        Area area = Gdxg.core.world.areas[0][0];
        Cell cell = area.getCell(0, 0);
        Point2s segment = new Point2s(Cell.CELL_SEGMENTS_ORIGIN).minus(1, 1);

        TerrainChunk terrainChunk = new TerrainChunk(area);
        terrainChunk.getAverageInterpolatedVertex(cell, segment.x, segment.y);

        assertThat(terrainChunk._importantNeighbors).isEqualTo(3);
    }

    private void testImportantNeighbours_leftBottomCell_leftBottomCorner() {
        LOG.info("testImportantNeighbours_leftBottomCell_leftBottomCorner");

        Area area = Gdxg.core.world.areas[0][0];
        Cell cell = area.getCell(0, 0);
        Point2s segment = new Point2s(Cell.CELL_SEGMENTS_ORIGIN).minus(Cell.CELL_ORIGIN_IN_SEGMENTS, Cell.CELL_ORIGIN_IN_SEGMENTS);

        TerrainChunk terrainChunk = new TerrainChunk(area);
        terrainChunk.getAverageInterpolatedVertex(cell, segment.x, segment.y);

        assertThat(terrainChunk._importantNeighbors).isEqualTo(3);
    }

    private void testImportantNeighbours_leftBottomCell_leftCenterCorner() {
        LOG.info("testImportantNeighbours_leftBottomCell_leftCenterCorner");

        Area area = Gdxg.core.world.areas[0][0];
        Cell cell = area.getCell(0, 0);
        Point2s segment = new Point2s(Cell.CELL_SEGMENTS_ORIGIN).minus(Cell.CELL_ORIGIN_IN_SEGMENTS, 0);


        TerrainChunk terrainChunk = new TerrainChunk(area);
        terrainChunk.getAverageInterpolatedVertex(cell, segment.x, segment.y);

        assertThat(terrainChunk._importantNeighbors).isEqualTo(5);
    }

    private void testImportantNeighbours_leftTopCell_leftCenter_and_nearestToOrigin_segment() {
        LOG.info("testImportantNeighbours_leftTopCell_leftCenter_and_nearestToOrigin_segment");
        Area area = Gdxg.core.world.areas[0][GdxgConstants.HEIGHT_IN_AREAS - 1];
        Cell cell = area.getCell(0, Area.HEIGHT_IN_CELLS - 1);
        Point2s segment = new Point2s(Cell.CELL_SEGMENTS_ORIGIN).minus(1, 0);


        TerrainChunk terrainChunk = new TerrainChunk(area);
        terrainChunk.getAverageInterpolatedVertex(cell, segment.x, segment.y);

        assertThat(terrainChunk._importantNeighbors).isEqualTo(3);
    }

    // leftTopCell

    private void testImportantNeighbours_leftTopCell_leftTop_and_nearestToOrigin_corner() {
        LOG.info("testImportantNeighbours_leftTopCell_leftTop_and_nearestToOrigin_corner");

        Area area = Gdxg.core.world.areas[0][GdxgConstants.HEIGHT_IN_AREAS - 1];
        Cell cell = area.getCell(0, Area.HEIGHT_IN_CELLS - 1);
        Point2s segment = new Point2s(Cell.CELL_SEGMENTS_ORIGIN);
        segment.x -= 1;
        segment.y += 1;


        TerrainChunk terrainChunk = new TerrainChunk(area);
        terrainChunk.getAverageInterpolatedVertex(cell, segment.x, segment.y);

        assertThat(terrainChunk._importantNeighbors).isEqualTo(3);
    }

    private void testImportantNeighbours_leftTopCell_leftTopCorner() {
        LOG.info("testImportantNeighbours_leftTopCell_leftTopCorner");

        Area area = Gdxg.core.world.areas[0][GdxgConstants.HEIGHT_IN_AREAS - 1];
        Cell cell = area.getCell(0, Area.HEIGHT_IN_CELLS - 1);
        Point2s segment = new Point2s(Cell.CELL_SEGMENTS_ORIGIN);
        segment.x -= Cell.CELL_ORIGIN_IN_SEGMENTS;
        segment.y += Cell.CELL_ORIGIN_IN_SEGMENTS;


        TerrainChunk terrainChunk = new TerrainChunk(area);
        terrainChunk.getAverageInterpolatedVertex(cell, segment.x, segment.y);

        assertThat(terrainChunk._importantNeighbors).isEqualTo(3);
    }

    private void testImportantNeighbours_leftTopCell_leftCenterCorner() {
        LOG.info("testImportantNeighbours_leftTopCell_leftCenterCorner");

        Area area = Gdxg.core.world.areas[0][GdxgConstants.HEIGHT_IN_AREAS - 1];
        Cell cell = area.getCell(0, Area.HEIGHT_IN_CELLS - 1);
        Point2s segment = new Point2s(Cell.CELL_SEGMENTS_ORIGIN);
        segment.x -= Cell.CELL_ORIGIN_IN_SEGMENTS;


        TerrainChunk terrainChunk = new TerrainChunk(area);
        terrainChunk.getAverageInterpolatedVertex(cell, segment.x, segment.y);

        assertThat(terrainChunk._importantNeighbors).isEqualTo(5);
    }

    private void testImportantNeighbours_rightTopCell_rightCenter_and_nearestToOrigin_segment() {
        LOG.info("testImportantNeighbours_rightTopCell_rightCenter_and_nearestToOrigin_segment");
        Area area = Gdxg.core.world.areas[GdxgConstants.WIDTH_IN_AREAS - 1][GdxgConstants.HEIGHT_IN_AREAS - 1];
        Cell cell = area.getCell(Area.WIDTH_IN_CELLS - 1, Area.HEIGHT_IN_CELLS - 1);
        Point2s segment = new Point2s(Cell.CELL_SEGMENTS_ORIGIN);
        segment.x += 1;


        TerrainChunk terrainChunk = new TerrainChunk(area);
        terrainChunk.getAverageInterpolatedVertex(cell, segment.x, segment.y);

        assertThat(terrainChunk._importantNeighbors).isEqualTo(3);
    }

    // rightTopCell

    private void testImportantNeighbours_rightTopCell_rightTop_and_nearestToOrigin_corner() {
        LOG.info("testImportantNeighbours_rightTopCell_rightTop_and_nearestToOrigin_corner");

        Area area = Gdxg.core.world.areas[GdxgConstants.WIDTH_IN_AREAS - 1][GdxgConstants.HEIGHT_IN_AREAS - 1];
        Cell cell = area.getCell(Area.WIDTH_IN_CELLS - 1, Area.HEIGHT_IN_CELLS - 1);
        Point2s segment = new Point2s(Cell.CELL_SEGMENTS_ORIGIN);
        segment.x += 1;
        segment.y += 1;


        TerrainChunk terrainChunk = new TerrainChunk(area);
        terrainChunk.getAverageInterpolatedVertex(cell, segment.x, segment.y);

        assertThat(terrainChunk._importantNeighbors).isEqualTo(3);
    }

    private void testImportantNeighbours_rightTopCell_rightTopCorner() {
        LOG.info("testImportantNeighbours_rightTopCell_rightTopCorner");

        Area area = Gdxg.core.world.areas[GdxgConstants.WIDTH_IN_AREAS - 1][GdxgConstants.HEIGHT_IN_AREAS - 1];
        Cell cell = area.getCell(Area.WIDTH_IN_CELLS - 1, Area.HEIGHT_IN_CELLS - 1);
        Point2s segment = new Point2s(Cell.CELL_SEGMENTS_ORIGIN);
        segment.x += Cell.CELL_ORIGIN_IN_SEGMENTS;
        segment.y += Cell.CELL_ORIGIN_IN_SEGMENTS;


        TerrainChunk terrainChunk = new TerrainChunk(area);
        terrainChunk.getAverageInterpolatedVertex(cell, segment.x, segment.y);

        assertThat(terrainChunk._importantNeighbors).isEqualTo(3);
    }

    private void testImportantNeighbours_rightTopCell_rightCenterCorner() {
        LOG.info("testImportantNeighbours_rightTopCell_rightCenterCorner");

        Area area = Gdxg.core.world.areas[GdxgConstants.WIDTH_IN_AREAS - 1][GdxgConstants.HEIGHT_IN_AREAS - 1];
        Cell cell = area.getCell(Area.WIDTH_IN_CELLS - 1, Area.HEIGHT_IN_CELLS - 1);
        Point2s segment = new Point2s(Cell.CELL_SEGMENTS_ORIGIN);
        segment.x += Cell.CELL_ORIGIN_IN_SEGMENTS;


        TerrainChunk terrainChunk = new TerrainChunk(area);
        terrainChunk.getAverageInterpolatedVertex(cell, segment.x, segment.y);

        assertThat(terrainChunk._importantNeighbors).isEqualTo(5);
    }

    private void testImportantNeighbours_rightBottomCell_rightCenter_and_nearestToOrigin_segment() {
        LOG.info("testImportantNeighbours_rightBottomCell_rightCenter_and_nearestToOrigin_segment");
        Area area = Gdxg.core.world.areas[GdxgConstants.WIDTH_IN_AREAS - 1][0];
        Cell cell = area.getCell(Area.WIDTH_IN_CELLS - 1, 0);
        Point2s segment = new Point2s(Cell.CELL_SEGMENTS_ORIGIN).plus(1, 0);


        TerrainChunk terrainChunk = new TerrainChunk(area);
        terrainChunk.getAverageInterpolatedVertex(cell, segment.x, segment.y);

        assertThat(terrainChunk._importantNeighbors).isEqualTo(3);
    }

    // rightBottomCell

    private void testImportantNeighbours_rightBottomCell_rightBottom_and_nearestToOrigin_corner() {
        LOG.info("testImportantNeighbours_rightBottomCell_rightBottom_and_nearestToOrigin_corner");

        Area area = Gdxg.core.world.areas[GdxgConstants.WIDTH_IN_AREAS - 1][0];
        Cell cell = area.getCell(Area.WIDTH_IN_CELLS - 1, 0);
        Point2s segment = new Point2s(Cell.CELL_SEGMENTS_ORIGIN).plus(1, 1);


        TerrainChunk terrainChunk = new TerrainChunk(area);
        terrainChunk.getAverageInterpolatedVertex(cell, segment.x, segment.y);

        assertThat(terrainChunk._importantNeighbors).isEqualTo(3);
    }

    private void testImportantNeighbours_rightBottomCell_rightBottomCorner() {
        LOG.info("testImportantNeighbours_rightBottomCell_rightBottomCorner");

        Area area = Gdxg.core.world.areas[GdxgConstants.WIDTH_IN_AREAS - 1][0];
        Cell cell = area.getCell(Area.WIDTH_IN_CELLS - 1, 0);
        Point2s segment = new Point2s(Cell.CELL_SEGMENTS_ORIGIN).plus(Cell.CELL_ORIGIN_IN_SEGMENTS, Cell.CELL_ORIGIN_IN_SEGMENTS);


        TerrainChunk terrainChunk = new TerrainChunk(area);
        terrainChunk.getAverageInterpolatedVertex(cell, segment.x, segment.y);

        assertThat(terrainChunk._importantNeighbors).isEqualTo(3);
    }

    private void testImportantNeighbours_rightBottomCell_rightCenterCorner() {
        LOG.info("testImportantNeighbours_rightBottomCell_rightCenterCorner");

        Area area = Gdxg.core.world.areas[GdxgConstants.WIDTH_IN_AREAS - 1][0];
        Cell cell = area.getCell(Area.WIDTH_IN_CELLS - 1, 0);
        Point2s segment = new Point2s(Cell.CELL_SEGMENTS_ORIGIN).plus(Cell.CELL_ORIGIN_IN_SEGMENTS, 0);


        TerrainChunk terrainChunk = new TerrainChunk(area);
        terrainChunk.getAverageInterpolatedVertex(cell, segment.x, segment.y);

        assertThat(terrainChunk._importantNeighbors).isEqualTo(5);
    }

    class Run extends ClientCore {
    }

}
