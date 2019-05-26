package tests.acceptance.world

import conversion7.engine.Gdxg
import conversion7.engine.utils.Utils
import conversion7.game.stages.world.team.Team
import org.slf4j.Logger
import org.testng.Assert
import shared.BaseGdxgSpec
import shared.steps.TeamActivationSnapshot

public class WorldTurnsTest extends BaseGdxgSpec {

    private static final Logger LOG = Utils.getLoggerForClass(WorldTurnsTest);

    def 'test dummy world without teams'() {
        given:
        assert Gdxg.core.world.step == 0
        assert Gdxg.core.world.teams.size == 0
        assert Gdxg.core.world.activeTeam == null
        for (int i = 0; i < 20; i++) {
            Utils.sleepThread(50)
            assert Gdxg.core.world.step == 0
        }
    }

    def 'test rewindTeamsToStartNewWorldStep'() {
        given:
        commonCoreStep {
            worldSteps.createUnit(
                    worldSteps.createHumanTeam(),
                    worldSteps.getNextNeighborCell());
        }
        assert Gdxg.core.world.teams.size == 1
        assert Gdxg.core.world.activeTeam != null

        when:
        def step = Gdxg.core.world.step
        LOG.info("Gdxg.core.world.step $step")
        worldSteps.rewindTeamsToStartNewWorldStep();

        then:
        assert Gdxg.core.world.step == step + 1
        assert Gdxg.core.world.activeTeam != null
        assert Gdxg.core.world.activeTeam == Gdxg.core.world.teams.first()
    }

    public void 'test player + ai human teams'() {
        given:
        lockCore()
        def squad = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextNeighborCell());
        def team1 = squad.team

        def squad2 = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextStandaloneCell());
        def team2 = squad2.team
        releaseCore()

        when:
        def step = Gdxg.core.world.step
        worldSteps.rewindTeamsToStartNewWorldStep();

        then:
        assert Gdxg.core.world.step == step + 1
        assert team1 == Gdxg.core.world.teams.first()
        assert Gdxg.core.world.activeTeam != null
        assert Gdxg.core.world.activeTeam == team1
    }

    public void 'test TeamsCompleteTheirTurnAtCurrentStep'() {
        given:
        lockCore()
        worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextNeighborCell());
        worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextStandaloneCell());
        assert Gdxg.core.world.teams.size == 2
        releaseCore()

        when:
        int inWorldStep = Gdxg.core.world.getStep();
        worldSteps.rewindTeamsToStartNewWorldStep();

        then:
        Assert.assertEquals(Gdxg.core.world.getStep(), inWorldStep + 1);
        int i = 0;
        for (Team team : Gdxg.core.world.teams) {
            LOG.info("i {}, team: {}", i, team);
            Assert.assertEquals(team.getLastActAtWorldStep(), inWorldStep);
            i++;
        }
    }

    public void 'test PlayerTeam'() {
        Team playerTeam

        when:
        lockCore()
        worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextNeighborCell());
        releaseCore()
        waitForNextCoreStep()
        playerTeam = Gdxg.core.world.getLastActivePlayerTeam();

        then:
        Assert.assertNotNull(playerTeam);
        Assert.assertEquals(Gdxg.core.world.teams.get(0), playerTeam);
        Assert.assertEquals(Gdxg.core.world.activeTeam, playerTeam);

        when:
        worldSteps.rewindTeamsToStartNewWorldStep();
        playerTeam = Gdxg.core.world.getLastActivePlayerTeam();

        then:
        Assert.assertNotNull(playerTeam);
        Assert.assertEquals(Gdxg.core.world.teams.get(0), playerTeam);
        Assert.assertEquals(Gdxg.core.world.activeTeam, playerTeam);
    }

    public void 'test FirstTeamShouldStartNewWorldStep'() {
        given:
        lockCore()
        def squad = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextNeighborCell());
        def team1 = squad.team
        def squad2 = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextNeighborCell());
        def team2 = squad2.team
        assert Gdxg.core.world.teams.size == 2
        assert Gdxg.core.world.teams.first() == team1
        releaseCore()

        when:
        worldSteps.rewindTeamsToStartNewWorldStep();

        then:
        assert team1 != null
        assert Gdxg.core.world.activeTeam == team1
    }

    public void 'test teams order sorting (when created in random order)'() {
        when:
        def world = Gdxg.core.world

        lockCore()

        def team1 = worldSteps.createHumanTeam(true)
        worldSteps.createUnit(
                team1,
                worldSteps.getNextNeighborCell());

        def team2_1 = worldSteps.createHumanTeam(false)
        worldSteps.createUnit(
                team2_1,
                worldSteps.getNextNeighborCell());

        def team3 = worldSteps.createAnimalTeam()
        worldSteps.createUnit(
                team3,
                worldSteps.getNextNeighborCell());

        def team2_2 = worldSteps.createHumanTeam(false)
        worldSteps.createUnit(
                team2_2,
                worldSteps.getNextNeighborCell());

        releaseCore()
        waitForNextCoreStep()

        then: "create teams"
        assert world.teams.size == 4
        assert world.activeTeam == team1
        assert world.teams.get(0) == team1
        assert world.teams.get(1) == team2_1
        assert world.teams.get(2) == team2_2
        assert world.teams.get(3) == team3
        assert world.animalTeam == team3
    }

    public void 'test whole teams turns cycle (without auto-completeAiTeams) + teams order sorting (when created in already correct order)'() {
        when:
        def world = Gdxg.core.world

        lockCore()

        def team1 = worldSteps.createHumanTeam()
        worldSteps.createUnit(
                team1,
                worldSteps.getNextStandaloneCell());

        def team2_1 = worldSteps.createHumanTeam()
        worldSteps.createUnit(
                team2_1,
                worldSteps.getNextStandaloneCell());

        def team2_2 = worldSteps.createHumanTeam()
        worldSteps.createUnit(
                team2_2,
                worldSteps.getNextStandaloneCell());

        def team3 = worldSteps.createAnimalTeam()
        def herd3 = worldSteps.createUnit(
                team3,
                worldSteps.getNextStandaloneCell());

        releaseCore()
        waitForNextCoreStep()

        then: "create teams"
        assert world.teams.size == 4
        assert world.activeTeam == team1
        assert world.teams.get(0) == team1
        assert world.teams.get(1) == team2_1
        assert world.teams.get(2) == team2_2
        assert world.teams.get(3) == team3
        assert world.animalTeam == team3

        when:
        Gdxg.core.world.requestNextTeamTurn(false);
        waitForNextCoreStep()
        then: "2nd team turn"
        assert world.teams.size == 4
        assert world.activeTeam == team2_1
        assert world.teams.get(0) == team1
        assert world.teams.get(1) == team2_1
        assert world.teams.get(2) == team2_2
        assert world.teams.get(3) == team3
        assert world.animalTeam == team3

        when:
        Gdxg.core.world.requestNextTeamTurn(false);
        waitForNextCoreStep()
        then: "3rd team turn"
        assert world.teams.size == 4
        assert world.activeTeam == team2_2
        assert world.teams.get(0) == team1
        assert world.teams.get(1) == team2_1
        assert world.teams.get(2) == team2_2
        assert world.teams.get(3) == team3
        assert world.animalTeam == team3

        when:
        Gdxg.core.world.requestNextTeamTurn(false);
        waitForNextCoreStep()
        then: "4th team turn"
        assert world.teams.size == 4
        assert world.activeTeam == team3
        assert world.teams.get(0) == team1
        assert world.teams.get(1) == team2_1
        assert world.teams.get(2) == team2_2
        assert world.teams.get(3) == team3
        assert world.animalTeam == team3

        when:
        Gdxg.core.world.requestNextTeamTurn(false);
        waitForNextCoreStep()
        then: "1st team turn, new world step"
        assert world.teams.size == 4
        assert world.activeTeam == team1
        assert world.teams.get(0) == team1
        assert world.teams.get(1) == team2_1
        assert world.teams.get(2) == team2_2
        assert world.teams.get(3) == team3
        assert world.animalTeam == team3
    }

    def 'test AI team doesnt require Next turn button'() {
        given: "human and ai players in game"
        def world = Gdxg.core.world

        Team team1 = null
        Team team2 = null
        commonCoreStep {
            team1 = worldSteps.createHumanTeam(true)
            team2 = worldSteps.createHumanTeam(false)
            worldSteps.createUnit(team1, worldSteps.getNextStandaloneCell())
            worldSteps.createUnit(team2, worldSteps.getNextStandaloneCell())
        }
        assert world.activeTeam == team1

        when: "player ends turn"
        def team2lastActAtWorldStep = team2.lastActAtWorldStep
        def activationData = new TeamActivationSnapshot()
        commonCoreStep {
            world.requestNextTeamTurn()
        }

        then: "player team is again active"
        worldSteps.waitNewPlayerTeamWasActivated(activationData)
        assert world.activeTeam == team1

        and: "ai team also made a turn"
        assert team2.lastActAtWorldStep == team2lastActAtWorldStep + 1
    }

    def 'test only ai team in world'() {
        given:
        lockCore()
        def world = Gdxg.core.world
        def humanTeam = worldSteps.createHumanTeam()
        def animalTeam = worldSteps.createAnimalTeam()
        humanTeam.defeat()
        releaseCoreAndWaitNextCoreStep()
        assert world.step == 0

        for (int i = 0; i < 10; i++) {
            assert world.step == 0
            assert world.teams.size == 2
            assert world.lastActivePlayerTeam == null || world.lastActivePlayerTeam.defeated
            assert world.activeTeam == animalTeam
            Utils.sleepThread(50)
        }

        def actWorldStep = world.step
        println "world.step ${actWorldStep}"

        when:
        worldSteps.rewindTeamsToStartNewWorldStep();
        waitForNextCoreStep()

        then:
        for (int i = 0; i < 10; i++) {
            assert world.step == 1
            assert world.teams.size == 1
            assert world.lastActivePlayerTeam == null
            assert world.activeTeam == animalTeam
            Utils.sleepThread(50)
        }

        when:
        worldSteps.rewindTeamsToStartNewWorldStep();
        waitForNextCoreStep()

        then:
        for (int i = 0; i < 10; i++) {
            assert world.step == 2
            assert world.teams.size == 1
            assert world.lastActivePlayerTeam == null
            assert world.activeTeam == animalTeam
            Utils.sleepThread(5)
        }
    }

}
