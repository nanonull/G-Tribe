package conversion7.debug_tests;

import conversion7.AAATest;
import conversion7.AbstractTests;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.battle.Battle;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.creator.WorldCreator;
import conversion7.game.stages.world.objects.AbstractSquad;
import conversion7.game.stages.world.objects.AnimalHerd;
import conversion7.game.stages.world.team.Team;
import conversion7.test_steps.WorldSteps;
import org.slf4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class Bug16Test extends AbstractTests {

    private static final Logger LOG = Utils.getLoggerForClass();


    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        AnimalHerd.SEARCH_ATTACK_TARGETS_IN_RADIUS = 1;
        WorldCreator.MAX_ANIMAL_HERDS_AMOUNT_LIMIT = 0;
        WorldCreator.MAX_ANIMAL_HERD_CHANCE = 0;

        super.beforeClass();
    }

    @Test(invocationCount = 1)
    public void test_TwoAnimalHerdsWillAttackHumanArmy() {
        new AAATest() {
            @Override
            public void body() {

                Team playerTeam = World.getPlayerTeam();
                AbstractSquad playerSquad = playerTeam.getArmies().get(0);
                WorldSteps.placeAnimalHerdsAround(playerSquad, 2);

                Utils.infinitySleepThread();
            }
        }.run();
    }

    @Test(invocationCount = 1)
    public void test_HumanArmyWinsTwoBattlesInOneTurn() {
        new AAATest() {
            @Override
            public void body() {
                Battle._HUMAN_PLAYER_COULD_NOT_LOST = true;

                Team playerTeam = World.getPlayerTeam();
                AbstractSquad playerSquad = playerTeam.getArmies().get(0);
                WorldSteps.placeAnimalHerdsAround(playerSquad, 2);

                Utils.infinitySleepThread();
            }
        }.run();
    }

}
