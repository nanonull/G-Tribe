package conversion7.game.stages.world.team.actions;

import conversion7.engine.Gdxg;
import conversion7.game.stages.world.team.Team;

public class OpenJournalAction extends AbstractTeamAction {

    public OpenJournalAction(Team team) {
        super(team);
    }

    @Override
    public String getUiName() {
        return "Journal";
    }

    @Override
    public void action() {
        Gdxg.clientUi.journalPanel.showFor(team);
    }

}
