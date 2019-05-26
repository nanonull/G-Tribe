package conversion7.game.stages.world.landscape;

import com.badlogic.gdx.utils.Array;
import conversion7.engine.utils.MathUtils;
import conversion7.engine.utils.Utils;
import org.slf4j.Logger;
import org.testng.Assert;

public class LandscapeController {

    private static final Logger LOG = Utils.getLoggerForClass();
    public static final int HILL_CHANCE = 20;
    public static final float LITTLE_MORE_CHANCE_FOR_CHANGE_LEVEL = HILL_CHANCE / 100f;

    private Cell cell;
    private Array<Integer> soilTypesIndexForRandom = new Array<>();
    public boolean hasBeenGotForGenerating = false;
    public AverageCellParams averageCellParams;


    public LandscapeController(Cell cell) {
        this.cell = cell;
        soilTypesIndexForRandom.add(0);
        soilTypesIndexForRandom.add(1);
        soilTypesIndexForRandom.add(2);
    }

    private static Soil getRandomSoil() {
        int dirt = MathUtils.RANDOM.nextInt(101);
        int sand = MathUtils.RANDOM.nextInt(101 - dirt);
        int stone = (100 - dirt - sand);
        return new Soil(dirt, sand, stone);
    }

    public static int getLevelByAverageHeight(float averageHeight) {
        int absAverageHeight = (int) averageHeight;
        float mod;
        if (absAverageHeight == 0) {
            mod = averageHeight - absAverageHeight;
        } else {
            mod = averageHeight % absAverageHeight;
        }

        if (mod == 0) {
            mod = MathUtils.RANDOM.nextBoolean() ? LITTLE_MORE_CHANCE_FOR_CHANGE_LEVEL : -LITTLE_MORE_CHANCE_FOR_CHANGE_LEVEL;
        }
        float absMod = Math.abs(mod);

        float changeLevelThrow = MathUtils.RANDOM.nextFloat() - LITTLE_MORE_CHANCE_FOR_CHANGE_LEVEL;
//        0.1f difference = 10% chance to change level
//        0.5f difference = 50% chance...
        if (absMod >= changeLevelThrow) {
            int nextLevel = (int) (mod / mod);
            if (Float.compare(mod, 0) == -1) {
                nextLevel *= -1;
            }
            return nextLevel;
        }
        return absAverageHeight;
    }

    /**
     * Temporary used for creating cell which could be used for moving.<BR></BR>
     * for cases when object created from testing flow
     */
    public void regenerateLandscapeToBeAvailableForMove() {
        LOG.info("regenerateLandscapeToBeAvailableForMove");
        cell.getLandscape().setType(Landscape.Type.COMMON);
    }

    public void setDefaultDirtCell() {
        Soil soil = new Soil(85, 10, 5);
        cell.setLandscape(new Landscape(Landscape.Type.COMMON, 0, Landscape.Addition.NONE, soil));
    }

    public void setDefaultSandCell() {
        Soil soil = new Soil(5, 85, 10);
        cell.setLandscape(new Landscape(Landscape.Type.COMMON, 0, Landscape.Addition.NONE, soil));
    }

    public void setDefaultStoneCell() {
        Soil soil = new Soil(5, 10, 85);
        cell.setLandscape(new Landscape(Landscape.Type.COMMON, 0, Landscape.Addition.NONE, soil));
    }

    public void setWaterCell() {
        cell.setLandscape(new Landscape(Landscape.Type.WATER, Landscape.WATER_LEVEL, Landscape.Addition.NONE, getRandomSoil()));
    }

    public void setMountainCell() {
        cell.setLandscape(new Landscape(Landscape.Type.MOUNTAIN, 0, Landscape.Addition.NONE, getRandomSoil()));
    }

    public void setRandomLandscape() {
        cell.setLandscape(new Landscape(Landscape.Type.COMMON, 0, Landscape.Addition.NONE, getRandomSoil()));
    }

    public void generateLandscape(Biom biom) {

        if (cell.getLandscape() != null || hasBeenGotForGenerating) {
            return;
        }

        hasBeenGotForGenerating = true;
        averageCellParams = new AverageCellParams(cell);

//        conversion7.engine.utils.Timer timer6 = new conversion7.engine.utils.Timer();
        // get landscapeType
        int rndLandType = MathUtils.RANDOM.nextInt((averageCellParams.numberCommonCells +
                averageCellParams.numberMountains + averageCellParams.numberWaters) * 10
                + 1); // add small chance to generate COMMON cells in case only Waters and Mountains
        Landscape.Type landscapeType;
        if (rndLandType < averageCellParams.numberWaters * 10) {
            landscapeType = Landscape.Type.WATER;
        } else if (rndLandType < averageCellParams.numberWaters * 10 + averageCellParams.numberMountains * 7) {
            landscapeType = Landscape.Type.MOUNTAIN;
        } else {
            landscapeType = Landscape.Type.COMMON;
        }

        // add random SMALL mutations
        // -1/0/+1 + Multiplier
        // -3/-2/-1/0/1/2/3
        averageCellParams.dirtSum += MathUtils.RANDOM.nextInt(11) - 5 + biom.dirtMultiplier;
        averageCellParams.sandSum += MathUtils.RANDOM.nextInt(11) - 5 + biom.sandMultiplier;
        averageCellParams.stoneSum += MathUtils.RANDOM.nextInt(11) - 5 + biom.stoneMultiplier;

        // add rare random BIG mutation
        if (MathUtils.RANDOM.nextInt(10) < 3) {
            byte rndType = (byte) MathUtils.RANDOM.nextInt(3);
            byte rndValue = (byte) (MathUtils.RANDOM.nextInt(20) + 10);
            switch (rndType) {
                case 0:
                    averageCellParams.dirtSum += rndValue;
                    break;
                case 1:
                    averageCellParams.sandSum += rndValue;
                    break;
                case 2:
                    averageCellParams.stoneSum += rndValue;
                    break;

            }
        }


        // limit
        if (averageCellParams.dirtSum < 0) {
            averageCellParams.dirtSum = 0;
        }
        if (averageCellParams.dirtSum > 100) {
            averageCellParams.dirtSum = 100;
        }

        if (averageCellParams.sandSum < 0) {
            averageCellParams.sandSum = 0;
        }
        if (averageCellParams.sandSum > 100) {
            averageCellParams.sandSum = 100;
        }
        if (averageCellParams.stoneSum < 0) {
            averageCellParams.stoneSum = 0;
        }
        if (averageCellParams.stoneSum > 100) {
            averageCellParams.stoneSum = 100;
        }
//        TestLandGenerator.increaseStatAfterAverageCell(timer6.stop());


//        conversion7.engine.utils.Timer timer4 = new conversion7.engine.utils.Timer();
        // distribute extra points until sum of params reaches 100
//        int newSum = averageCellParams.dirtSum + averageCellParams.sandSum + averageCellParams.stoneSum; // 100*3 = 300 | 0*3 = 0
        int newSum = averageCellParams.getRefreshedSoilSum();
        if (averageCellParams.getRefreshedSoilSum() != 100) {
            int toAdd = 100 - newSum; // -200 | 100
            int typeIndex;
            int cycleIndex = 0;
            soilTypesIndexForRandom.shuffle();

            while (toAdd != 0) {
                typeIndex = soilTypesIndexForRandom.get(cycleIndex);
                switch (typeIndex) {
                    case 0:
                        averageCellParams.dirtSum += toAdd; //100 -200 = -100 | 0 +100 = 100
                        if (averageCellParams.dirtSum < 0) {
                            averageCellParams.dirtSum = 0; // 0
                        }
                        if (averageCellParams.dirtSum > 100) {
                            averageCellParams.dirtSum = 100;
                        }
                        toAdd = 100 - (averageCellParams.getRefreshedSoilSum());
                        break;
                    case 1:
                        averageCellParams.sandSum += toAdd; // 100 -100 = 0
                        if (averageCellParams.sandSum < 0) {
                            averageCellParams.sandSum = 0;
                        }
                        if (averageCellParams.sandSum > 100) {
                            averageCellParams.sandSum = 100;
                        }
                        toAdd = 100 - (averageCellParams.getRefreshedSoilSum());
                        break;
                    case 2:
                        averageCellParams.stoneSum += toAdd;
                        if (averageCellParams.stoneSum < 0) {
                            averageCellParams.stoneSum = 0;
                        }
                        if (averageCellParams.stoneSum > 100) {
                            averageCellParams.stoneSum = 100;
                        }
                        toAdd = 100 - (averageCellParams.getRefreshedSoilSum());
                        break;

                }

                cycleIndex++;
                if (cycleIndex == 3) {
                    cycleIndex = 0;
                }
            }
        }
//        TestLandGenerator.increaseStatDistributeExtraPoints(timer4.stop());

        // update Multipliers
        if (averageCellParams.dirtSum < 15) biom.setDirtMultiplier(biom.dirtMultiplier + 1);
        if (averageCellParams.sandSum < 3) biom.setSandMultiplier(biom.sandMultiplier + 1);
        if (averageCellParams.stoneSum < 1) biom.setStoneMultiplier(biom.stoneMultiplier + 1);

        if (averageCellParams.dirtSum > 90) biom.setDirtMultiplier(biom.dirtMultiplier - 1);
        if (averageCellParams.sandSum > 60) biom.setSandMultiplier(biom.sandMultiplier - 1);
        if (averageCellParams.stoneSum > 40) biom.setStoneMultiplier(biom.stoneMultiplier - 1);

//        conversion7.engine.utils.Timer timer2 = new conversion7.engine.utils.Timer();
        Soil soil = new Soil(averageCellParams);
//        TestLandGenerator.increaseStatNewSoil(timer2.stop());


//        conversion7.engine.utils.Timer timer3 = new conversion7.engine.utils.Timer();

        int level;
        if (landscapeType.equals(Landscape.Type.WATER)) {
            level = Landscape.WATER_LEVEL;
        } else {
            level = getLevelByAverageHeight(averageCellParams.getAverageHeight());
            if (level == Landscape.WATER_LEVEL) {
                level = 0;
                // OR use this for islands biom
//                landscapeType = Landscape.Type.WATER;
            }
        }

        Landscape.Addition addition = Landscape.Addition.NONE;
        if (landscapeType.equals(Landscape.Type.COMMON)) {
            int dirt = soil.getSoilTypeValue(Soil.TypeId.DIRT);
            if (dirt > 50 && MathUtils.testPercentChance(Math.round(dirt / 3f))) {
                addition = Landscape.Addition.FOREST;
            } else if (dirt > 50 && MathUtils.testPercentChance(Math.round(dirt / 10f))) {
                addition = Landscape.Addition.BOG;
            }
        }

        Assert.assertNull(cell.getLandscape(), "Landscape has been already generated while this method works");
        Landscape landscape = new Landscape(landscapeType, level, addition, soil);
        cell.setLandscape(landscape);

//        TestLandGenerator.increaseStatNewLandscape(timer3.stop());

    }

}
