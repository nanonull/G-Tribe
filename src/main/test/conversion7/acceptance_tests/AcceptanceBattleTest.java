package conversion7.acceptance_tests;

import conversion7.AAATest;
import conversion7.AbstractTests;
import conversion7.game.WaitLibrary;
import conversion7.game.services.WorldServices;
import conversion7.game.stages.battle.Battle;
import conversion7.game.stages.battle.BattleFigure;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.objects.HumanSquad;
import conversion7.game.stages.world.team.Team;
import conversion7.game.stages.world.unit.Unit;
import conversion7.test_steps.BattleSteps;
import conversion7.test_steps.WorldSteps;
import conversion7.test_steps.asserts.BattleAsserts;
import conversion7.test_steps.asserts.WorldAsserts;
import org.testng.annotations.Test;

public class AcceptanceBattleTest extends AbstractTests {

    @Test
    public void test_BattleFigureStartParametersDependOnUnitParameters() {
        new AAATest() {
            Battle battle;

            @Override
            public void body() {
                Team humanTeam1 = World.getPlayerTeam();
                Team team2 = World.createHumanTeam(false);
                HumanSquad army1 = humanTeam1.createHumanSquad(WorldSteps.getNextStandaloneCell());
                HumanSquad army2 = team2.createHumanSquad(WorldSteps.getNextNeighborCell());

                Unit unit1 = WorldServices.createSomeHumanUnit();
                WorldSteps.addUnitToAreaObject(unit1, army1);
                Unit unit2 = WorldServices.createSomeHumanUnit();
                WorldSteps.addUnitToAreaObject(unit2, army2);

                BattleSteps.setAutoBattle(false);
                army1.attack(army2);
                battle = BattleSteps.getActiveBattle();
                BattleFigure figure1 = battle.getFigure(unit1);

                BattleAsserts.assertFigureLifeIs(figure1, unit1.getParams().getHealth());
            }

            @Override
            public void tearDown() {
                battle.finish();
                WorldServices.showAreaViewer();
            }
        }.run();
    }

    @Test
    public void test_UnitDiesIfHasZeroLifeAfterRound() {
        new AAATest() {
            Battle battle;

            @Override
            public void body() {
                Team humanTeam1 = World.getPlayerTeam();
                Team team2 = World.createHumanTeam(false);
                HumanSquad army1 = humanTeam1.createHumanSquad(WorldSteps.getNextStandaloneCell());
                HumanSquad army2 = team2.createHumanSquad(WorldSteps.getNextNeighborCell());

                Unit unit1 = WorldServices.createSomeHumanUnit();
                WorldSteps.addUnitToAreaObject(unit1, army1);

                Unit unit2 = WorldServices.createSomeHumanUnit();
                WorldSteps.addUnitToAreaObject(unit2, army2);

                BattleSteps.setAutoBattle(false);
                WorldSteps.makeUnitInvincible(unit1);
                army1.attack(army2);

                battle = BattleSteps.getActiveBattle();
                battle.calculateRound();
                battle.playRound();
                WaitLibrary.waitBattleRoundCompleted(battle);
                WaitLibrary.waitBattleCompleted(battle);
                WorldServices.showAreaViewer();

                LOG.info("Asserts");
                WorldAsserts.assertWorldIsActiveStage();

                WorldAsserts.assertUnitAlive(unit1);
                WorldAsserts.assertAreaObjectAlive(army1);
                WorldAsserts.assertTeamAlive(humanTeam1);

                WorldAsserts.assertUnitDead(unit2);
                WorldAsserts.assertAreaObjectDefeated(army2);
                WorldAsserts.assertTeamDefeated(team2);
            }

        }.run();
    }

    @Test
    public void test_UnitAndArmyAndTeamDefeatedInBattle() {
        new AAATest() {
            @Override
            public void body() {
                Team humanTeam1 = World.getPlayerTeam();
                Team team2 = World.createHumanTeam(false);
                HumanSquad army1 = humanTeam1.createHumanSquad(WorldSteps.getNextStandaloneCell());
                HumanSquad army2 = team2.createHumanSquad(WorldSteps.getNextNeighborCell());

                Unit unit1 = WorldServices.createSomeHumanUnit();
                WorldSteps.addUnitToAreaObject(unit1, army1);
                Unit unit2 = WorldServices.createSomeHumanUnit();
                WorldSteps.addUnitToAreaObject(unit2, army2);

                WorldSteps.makeUnitInvincible(unit1);
                BattleSteps.setAutoBattle(true);
                army1.attack(army2);

                LOG.info("Asserts");
                WorldAsserts.assertWorldIsActiveStage();

                WorldAsserts.assertUnitAlive(unit1);
                WorldAsserts.assertAreaObjectAlive(army1);
                WorldAsserts.assertTeamAlive(humanTeam1);

                WorldAsserts.assertUnitDead(unit2);
                WorldAsserts.assertAreaObjectDefeated(army2);
                WorldAsserts.assertTeamDefeated(team2);
            }

        }.run();
    }
}

