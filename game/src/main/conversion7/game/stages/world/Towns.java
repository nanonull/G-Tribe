package conversion7.game.stages.world;

import com.badlogic.gdx.utils.Array;
import conversion7.engine.pools.system.PoolManager;
import conversion7.engine.utils.Utils;
import conversion7.game.GdxgConstants;
import conversion7.game.stages.world.objects.buildings.Camp;
import conversion7.game.stages.world.team.Team;
import org.fest.assertions.api.Assertions;
import org.slf4j.Logger;

// future
@Deprecated
public class Towns {

    private static final Logger LOG = Utils.getLoggerForClass();

    Team team;
    int id;
    public Array<Camp> fragments = PoolManager.ARRAYS_POOL.obtain();


    public Towns(Camp... fragments) {
        LOG.info("< create Camp");

        Assertions.assertThat(fragments).as("attempt to create Camp without at least 1 fragment!")
                .isNotNull().isNotEmpty();

        id = Utils.getNextId();
        team = fragments[0].getTeam();

        for (Camp tf : fragments) {
            addFragment(tf);
        }

        LOG.info("> created " + toString());

    }

    public void addFragment(Camp fragment) {
        fragments.add(fragment);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("TOWN ")
                .append("side = ").append(team.getTeamId()).append(GdxgConstants.HINT_SPLITTER)
                .append("id = ").append(id).append(GdxgConstants.HINT_SPLITTER);
        return sb.toString();
    }

}
