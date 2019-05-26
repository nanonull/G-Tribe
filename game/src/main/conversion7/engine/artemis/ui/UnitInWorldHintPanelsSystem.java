package conversion7.engine.artemis.ui;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Color;
import conversion7.engine.Gdxg;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.team.Team;
import conversion7.game.ui.utils.UiUtils;
import org.slf4j.Logger;
import org.testng.Assert;

public class UnitInWorldHintPanelsSystem extends IteratingSystem {
    private static final Logger LOG = Utils.getLoggerForClass();

    private static ComponentMapper<UnitInWorldHintPanelsComp> components;
    private static final float ALPHA = 0.55f;
    private static final Color GREEN = UiUtils.alpha(ALPHA, Color.GREEN);
    private static final Color RED = UiUtils.alpha(ALPHA, Color.RED);
    private static final Color ORANGE = UiUtils.alpha(ALPHA, Color.ORANGE);
    private static final Color ALLY = UiUtils.alpha(ALPHA, Color.CYAN);
    private static final Color BLUE = UiUtils.alpha(ALPHA, Color.BLUE);

    public UnitInWorldHintPanelsSystem() {
        super(Aspect.all(UnitInWorldHintPanelsComp.class));
    }

    public static void refresh(Team teamToRefreshOn) {
        for (AbstractSquad squad : teamToRefreshOn.getSquads()) {
            squad.refreshUiPanelInWorld();
        }
    }

    public static UnitInWorldHintPanelsComp getOrCreate(AreaObject squad) {
        UnitInWorldHintPanelsComp comp = components.getSafe(squad.entityId);
        if (comp == null) {
            comp = components.create(squad.entityId);
        }
        comp.squad = (AbstractSquad) squad;
        return comp;
    }

    @Override
    protected void process(int entityId) {
        UnitInWorldHintPanelsComp comp = components.get(entityId);
        AbstractSquad owner = comp.squad;

        Team team = owner.getTeam();
        Assert.assertNotNull(team);

        if (comp.updateMainPanelData) {
            Color teamColor;
            Team playerTeam;
            try {
                playerTeam = Gdxg.core.world.lastActivePlayerTeam;
            } catch (NullPointerException e) {
                return;
            }
            if (team == playerTeam) {
                teamColor = GREEN;
            } else if (team.isAnimalTeam()) {
                teamColor = ORANGE;
            } else if (playerTeam != null && playerTeam.isEnemyOf(team)) {
                teamColor = RED;
            } else if (playerTeam != null && playerTeam.isAllyOf(team)) {
                teamColor = ALLY;
            } else {
                teamColor = BLUE;
            }

            owner.getUnitInWorldHintPanel().setPanelColor(teamColor);
            owner.getUnitInWorldHintPanel().getHealthWidgetBar().load(owner);
            owner.getUnitInWorldHintPanel().invalidate();

            if (team == playerTeam) {
                owner.getUnitInWorldHintPanel().updateAgeAndLvl();
                owner.getUnitInWorldHintPanel().getUnitIconWithInfoPanel().updateAP();
            }
        }

        if (comp.updateIconBorderColor) {
            owner.getUnitInWorldHintPanel().getUnitIconWithInfoPanel().setBorderColor(comp.iconBorderColor);
        }
        components.remove(entityId);
    }
}