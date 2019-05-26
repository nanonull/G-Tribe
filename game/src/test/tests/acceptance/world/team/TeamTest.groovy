package tests.acceptance.world.team

import conversion7.game.stages.world.objects.unit.AbstractSquad
import conversion7.game.stages.world.team.Team
import org.testng.Assert
import shared.BaseGdxgSpec
import shared.asserts.WorldAsserts
import spock.lang.Ignore

public class TeamTest extends BaseGdxgSpec {

    public void 'test defeated team removed on next world step'() {
        given:
        lockCore()

        AbstractSquad squad = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextNeighborCell());
        def team = squad.team

        worldSteps.createTeamTempGarantNoZeroTeamsInWorld()

        releaseCoreAndWaitNextCoreStep()

        when:
        commonCoreStep({ worldSteps.defeatTeam(team) })

        then: "not removed"
        WorldAsserts.assertTeamDefeated(team, true, false)

        when:
        worldSteps.rewindTeamsToStartNewWorldStep()

        then: "removed"
        WorldAsserts.assertTeamDefeated(team, true, true);
    }

    @Ignore
    public void 'test TeamDefeatedWhenLastSquadWasJoinedToAnotherTeam'() {
        given:
        def humanSquad1 = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextNeighborCell());

        def humanSquad2ToJoin = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextStandaloneCell());

        Team humanSquad1Team = humanSquad1.getTeam();
        Team humanSquad2TeamWillBeDefeated = humanSquad2ToJoin.getTeam();

        when:
        humanSquad1Team.joinSquad(humanSquad2ToJoin);

        then:
        Assert.assertEquals(humanSquad1Team, humanSquad2ToJoin.getTeam());
        WorldAsserts.assertTeamDefeated(humanSquad2TeamWillBeDefeated, true, false);

        when:
        waitForNextCoreStep()

        then: "not affected"
        WorldAsserts.assertTeamDefeated(humanSquad2TeamWillBeDefeated, true, false)

    }

}
