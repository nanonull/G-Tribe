package conversion7.tests_standalone.misc.world;

import conversion7.engine.utils.Utils;
import org.slf4j.Logger;

public class TestLandGenerator {

    private static final Logger LOG = Utils.getLoggerForClass();

    private static int cellGenerated;
    private static long cellGeneratedTime;
    private static int cellApplyLandTexture;
    private static long cellApplyLandTextureTime;
    private static int biomSteps;
    private static long biomStepsTime;
    private static int newSoil;
    private static long newSoilTime;
    private static int newLandscape;
    private static long newLandscapeTime;
    private static int distributeExtraPoints;
    private static long distributeExtraPointsTime;
    private static int getAverageCell;
    private static long getAverageCellTime;
    private static int afterAverageCell;
    private static long afterAverageCellTime;
    private static int updateBiomMultis;
    private static long updateBiomMultisTime;

    synchronized public static void increaseStatForCellGenerating(long stop) {
        cellGenerated++;
        cellGeneratedTime += stop;
    }

    synchronized public static void increaseStatForCellApplyLandTexture(long stop) {
        cellApplyLandTexture++;
        cellApplyLandTextureTime += stop;
    }

    public static void increaseStatForBiomStep(long stop) {
        biomSteps++;
        biomStepsTime += stop;
    }

    public static void increaseStatNewSoil(long stop) {
        newSoil++;
        newSoilTime += stop;
    }

    public static void increaseStatNewLandscape(long stop) {
        newLandscape++;
        newLandscapeTime += stop;
    }

    public static void showStatistic() {
        LOG.info("\n##### Generator statistic: ");


        LOG.info("cellGenerated: " + cellGenerated);
        LOG.info("cellGeneratedTime: " + cellGeneratedTime);
        LOG.info("AV: " + cellGeneratedTime / cellGenerated);
        LOG.info("");

        LOG.info("cellApplyLandTexture: " + cellApplyLandTexture);
        LOG.info("cellApplyLandTextureTime: " + cellApplyLandTextureTime);
        LOG.info("AV: " + cellApplyLandTextureTime / cellApplyLandTexture);
        LOG.info("");

        LOG.info("biomSteps: " + biomSteps);
        LOG.info("biomStepsTime: " + biomStepsTime);
        LOG.info("AV: " + biomStepsTime / biomSteps);
        LOG.info("");

        LOG.info("newSoil: " + newSoil);
        LOG.info("newSoilTime: " + newSoilTime);
        LOG.info("AV: " + newSoilTime / newSoil);
        LOG.info("");

        LOG.info("newLandscape: " + newLandscape);
        LOG.info("newLandscapeTime: " + newLandscapeTime);
        LOG.info("AV: " + newLandscapeTime / newLandscape);
        LOG.info("");

        LOG.info("getAverageCell: " + getAverageCell);
        LOG.info("getAverageCellTime: " + getAverageCellTime);
        LOG.info("AV: " + getAverageCellTime / getAverageCell);
        LOG.info("");

        LOG.info("distributeExtraPoints: " + distributeExtraPoints);
        LOG.info("distributeExtraPointsTime: " + distributeExtraPointsTime);
        LOG.info("AV: " + distributeExtraPointsTime / distributeExtraPoints);
        LOG.info("");

        LOG.info("afterAverageCell: " + afterAverageCell);
        LOG.info("afterAverageCellTime: " + afterAverageCellTime);
        LOG.info("AV: " + afterAverageCellTime / afterAverageCell);
        LOG.info("");

        LOG.info("updateBiomMultis: " + updateBiomMultis);
        LOG.info("updateBiomMultisTime: " + updateBiomMultisTime);
        LOG.info("AV: " + updateBiomMultisTime / updateBiomMultis);
        LOG.info("");
    }

    public static void increaseStatDistributeExtraPoints(long stop) {
        distributeExtraPoints++;
        distributeExtraPointsTime += stop;
    }

    public static void increaseStatGetAverageCell(long stop) {
        getAverageCell++;
        getAverageCellTime += stop;
    }

    public static void increaseStatAfterAverageCell(long stop) {
        afterAverageCell++;
        afterAverageCellTime += stop;
    }

    public static void increaseStatUpdateBiomMultis(long stop) {
        updateBiomMultis++;
        updateBiomMultisTime += stop;
    }
}
