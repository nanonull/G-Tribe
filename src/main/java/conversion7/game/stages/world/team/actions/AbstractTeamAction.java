package conversion7.game.stages.world.team.actions;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import conversion7.game.Assets;
import conversion7.game.interfaces.HintProvider;
import conversion7.game.interfaces.IconTextureProvider;
import conversion7.game.stages.world.team.Team;

public abstract class AbstractTeamAction implements HintProvider, IconTextureProvider {

    protected Team team;

    public AbstractTeamAction(Team team) {
        this.team = team;
    }

    public Team getTeam() {
        return team;
    }

    public String getHint() {
        return getClass().getSimpleName();
    }

    @Override
    public TextureRegion getIconTexture() {
        return Assets.armyIcon;
    }

    public abstract void action();

}
