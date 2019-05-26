package tests.debug;

import conversion7.engine.utils.Utils;
import org.slf4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import shared.steps.WorldSteps;
import shared.tests.BaseTests;

public class Bug16Test extends BaseTests {

    private static final Logger LOG = Utils.getLoggerForClass();
    WorldSteps worldSteps = getSteps(WorldSteps.class);

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        if (true) throw new RuntimeException("fix beforeClass");

//        AnimalHerd.SEARCH_ATTACK_TARGETS_IN_RADIUS = 1;
//        WorldSettings.ANIMAL_HERDS_AMOUNT_LIMIT = 0;
//        WorldSettings.ANIMAL_HERD_CHANCE_MAX = 0;

        super.beforeClass();
    }

    @Test(invocationCount = 1)
    public void test_HumanArmyWinsTwoBattlesInOneTurn() {
        throw new RuntimeException("TODO make after new battle design");
    }

}
