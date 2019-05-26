package conversion7.engine.artemis.ui;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import conversion7.engine.Gdxg;
import conversion7.game.stages.world.team.Team;

public class ShowTeamUiSystem extends IteratingSystem {
    public static ComponentMapper<ShowTeamUiComponent> components;

    public ShowTeamUiSystem() {
        super(Aspect.all(ShowTeamUiComponent.class));
    }

    @Override
    protected void process(int entityId) {
        Team activeTeam = Gdxg.core.world.activeTeam;
        if (activeTeam != null && activeTeam.isHumanPlayer()) {
            if (!activeTeam.isDefeated()) {
                Gdxg.clientUi.getEventsBar().showFor(activeTeam);
            }
            Gdxg.clientUi.getTeamBar().showFor(activeTeam);
            components.remove(entityId);
        }
    }
}
