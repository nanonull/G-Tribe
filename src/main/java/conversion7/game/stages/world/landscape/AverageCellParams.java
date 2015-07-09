package conversion7.game.stages.world.landscape;

/** Used in landscape generation */
public class AverageCellParams {

    int dirtSum;
    int sandSum;
    int stoneSum;
    int numberWaters;
    int numberMountains;
    int numberCommonCells;

    private float heightSum;
    private float averageHeight;

    public AverageCellParams(Cell cellOrigin) {
        for (Cell neighborCell : cellOrigin.getNeighborCells()) {
            if (neighborCell.getLandscape() != null) {
                Soil soil = neighborCell.getLandscape().soil;
                dirtSum += soil.getSoilTypeValue(Soil.TypeId.DIRT);
                sandSum += soil.getSoilTypeValue(Soil.TypeId.SAND);
                stoneSum += soil.getSoilTypeValue(Soil.TypeId.STONE);
                if (neighborCell.getLandscape().type.equals(Landscape.TYPE.WATER)) {
                    numberWaters++;
                } else if (neighborCell.getLandscape().type.equals(Landscape.TYPE.MOUNTAINS)) {
                    numberMountains++;
                } else if (neighborCell.getLandscape().type.equals(Landscape.TYPE.COMMON)) {
                    numberCommonCells++;
                }

                heightSum += neighborCell.getLandscape().getTerrainVertexData().getHeight();
            }
        }

        // get average soils
        float allPoints = dirtSum + sandSum + stoneSum;
        float multi = allPoints / 100;

        dirtSum = Math.round(dirtSum / multi);
        sandSum = Math.round(sandSum / multi);
        stoneSum = Math.round(stoneSum / multi);

        averageHeight = heightSum / 8;
    }

    public float getAverageHeight() {
        return averageHeight;
    }

    public int getRefreshedSoilSum() {
        return dirtSum + sandSum + stoneSum;
    }
}
