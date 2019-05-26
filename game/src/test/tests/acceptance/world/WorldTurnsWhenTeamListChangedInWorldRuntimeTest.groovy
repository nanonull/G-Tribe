package tests.acceptance.world

import conversion7.engine.Gdxg
import conversion7.engine.utils.Utils
import conversion7.game.stages.world.team.Team
import org.slf4j.Logger
import shared.BaseGdxgSpec
import shared.steps.TeamActivationSnapshot

public class WorldTurnsWhenTeamListChangedInWorldRuntimeTest extends BaseGdxgSpec {

    private static final Logger LOG = Utils.getLoggerForClass(WorldTurnsWhenTeamListChangedInWorldRuntimeTest);

    public void 'test team added after active team'() {
        given: "1 active Team T"
        def world = Gdxg.core.world

        lockCore()
        def team1 = worldSteps.createHumanTeam()
        worldSteps.createUnit(
                team1,
                worldSteps.getNextNeighborCell());

        def activationData
        activationData = new TeamActivationSnapshot()
        releaseCore()
        worldSteps.waitNewTeamWasActivated(activationData)
        waitForNextCoreStep()
        assert world.activeTeam == team1

        when: "new Team T2 added"
        Team team2 = null
        commonCoreStep({
            team2 = worldSteps.createHumanTeam()
            worldSteps.createUnit(
                    team2,
                    worldSteps.getNextStandaloneCell());
        })

        then: "Team T is still active"
        assert world.activeTeam == team1

        when: "Team T ends turn"
        commonCoreStep { world.requestNextTeamTurn(false) }

        then: "Team T2 is active team"
        assert team2
        assert world.activeTeam == team2

        when: "Team T2 ends turn"
        def step0 = world.step
        commonCoreStep { world.requestNextTeamTurn(false) }

        then: "Team T is active team again"
        assert world.activeTeam == team1

        and: "next step started in world"
        assert world.step == step0 + 1
    }

    public void 'test team removed after active team'() {
        given: "1 active Team T"
        def world = Gdxg.core.world

        lockCore()
        def team1 = worldSteps.createHumanTeam()
        worldSteps.createUnit(
                team1,
                worldSteps.getNextNeighborCell());

        def activationData
        activationData = new TeamActivationSnapshot()
        releaseCore()
        worldSteps.waitNewTeamWasActivated(activationData)
        waitForNextCoreStep()

        and: "new Team T2 added"
        Team team2
        commonCoreStep({
            team2 = worldSteps.createHumanTeam()
            worldSteps.createUnit(
                    team2,
                    worldSteps.getNextStandaloneCell());
        })

        when: "Team T2 defeated"
        commonCoreStep({ worldSteps.defeatTeam(team2) })

        then: "Team T is still active"
        assert world.activeTeam == team1

        when: "Team T ends turn"
        def step0 = world.step
        commonCoreStep { world.requestNextTeamTurn(false) }

        then: "Team T is active team again"
        assert world.activeTeam == team1

        and: "next step started in world"
        assert world.step == step0 + 1
    }

    //team added before active
    //test teams order; when team added before active team - add human-ai when animals is active
    public void 'test team added before active'() {
        given:
        def world = Gdxg.core.world

        lockCore()
        def team1 = worldSteps.createHumanTeam()
        worldSteps.createUnit(
                team1,
                worldSteps.getNextNeighborCell());

        def teamAnimals = worldSteps.createAnimalTeam()
        worldSteps.createUnit(
                teamAnimals,
                worldSteps.getNextStandaloneCell());
        def activationData
        activationData = new TeamActivationSnapshot()
        releaseCore()
        worldSteps.waitNewTeamWasActivated(activationData)
        waitForNextCoreStep()

        when:
        activationData = new TeamActivationSnapshot()
        world.requestNextTeamTurn(false);
        worldSteps.waitNewTeamWasActivated(activationData)
        waitForNextCoreStep()

        then: "animals is active"
        assert world.activeTeam == teamAnimals

        when: "team inserted before active team"
        lockCore()
        def team2 = worldSteps.createHumanTeam()
        worldSteps.createUnit(
                team2,
                worldSteps.getNextStandaloneCell());
        releaseCore()
        waitForNextCoreStep()
        then: "animals is active"
        assert world.activeTeam == teamAnimals

        when: "animals completes its turn"
        activationData = new TeamActivationSnapshot()
        world.requestNextTeamTurn(false);
        worldSteps.waitNewTeamWasActivated(activationData)
        waitForNextCoreStep()
        then: "1st team is active"
        assert world.activeTeam == team1

        when: "1st team completes its turn"
        activationData = new TeamActivationSnapshot()
        world.requestNextTeamTurn(false);
        worldSteps.waitNewTeamWasActivated(activationData)
        waitForNextCoreStep()
        then: "2nd team is active"
        assert world.activeTeam == team2

        when: "2nd team completes its turn"
        activationData = new TeamActivationSnapshot()
        world.requestNextTeamTurn(false);
        worldSteps.waitNewTeamWasActivated(activationData)
        waitForNextCoreStep()
        then: "animals is active"
        assert world.activeTeam == teamAnimals
    }

    //test teams order; when team removed before active team - remove human-ai when animals is active
    public void 'test team removed before active team'() {
        given:
        def world = Gdxg.core.world
        def activationData

        lockCore()
        def team1 = worldSteps.createHumanTeam()
        worldSteps.createUnit(
                team1,
                worldSteps.getNextNeighborCell());

        def team2 = worldSteps.createHumanTeam()
        worldSteps.createUnit(
                team2,
                worldSteps.getNextStandaloneCell());

        def animalsTeam = worldSteps.createAnimalTeam()
        worldSteps.createUnit(animalsTeam
                , worldSteps.getNextStandaloneCell())

        activationData = new TeamActivationSnapshot()
        releaseCore()
        worldSteps.waitNewTeamWasActivated(activationData)
        waitForNextCoreStep()

        when: "rewind 1st ai team"
        activationData = new TeamActivationSnapshot()
        world.requestNextTeamTurn(false);
        worldSteps.waitNewTeamWasActivated(activationData)
        waitForNextCoreStep()

        and: "...rewind 2nd ai team"
        activationData = new TeamActivationSnapshot()
        world.requestNextTeamTurn(false);
        worldSteps.waitNewTeamWasActivated(activationData)
        waitForNextCoreStep()

        then: "animals is active"
        assert world.activeTeam == animalsTeam

        when: "team defeated before active team"
        commonCoreStep({ worldSteps.defeatTeam(team2) })

        then: "animals is active"
        assert world.activeTeam == animalsTeam

        when: "animals completes its turn"
        activationData = new TeamActivationSnapshot()
        world.requestNextTeamTurn(false);
        worldSteps.waitNewTeamWasActivated(activationData)
        waitForNextCoreStep()

        then: "1st team is active"
        assert world.activeTeam == team1

        when: "1st team completes its turn"
        activationData = new TeamActivationSnapshot()
        world.requestNextTeamTurn(false);
        worldSteps.waitNewTeamWasActivated(activationData)
        waitForNextCoreStep()
        then: "animalsTeam is active"
        assert world.activeTeam == animalsTeam

    }
}
