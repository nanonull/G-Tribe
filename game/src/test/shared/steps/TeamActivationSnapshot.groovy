package shared.steps

import conversion7.engine.Gdxg
import conversion7.engine.artemis.GlobalStrategySystem
import conversion7.game.stages.world.team.Team

class TeamActivationSnapshot {
    long frame
    Team team

    /**Create before conversion7.game.stages.world.World#requestNextTeamTurn() as example*/
    public TeamActivationSnapshot() {
        def teamTurnSystem = Gdxg.core.artemis.getSystem(GlobalStrategySystem)
        frame = teamTurnSystem.teamActivatedOnCoreFrame
        team = Gdxg.core.world.activeTeam
    }
}
