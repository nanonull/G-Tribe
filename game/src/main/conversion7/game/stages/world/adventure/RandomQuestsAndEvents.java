package conversion7.game.stages.world.adventure;

import com.badlogic.gdx.utils.Array;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.quest.BaseQuest;
import conversion7.game.stages.world.quest.items.DestroyAnimalsSpawnQuest;

public class RandomQuestsAndEvents {

    public static final Array<Class<? extends BaseQuest>> ALL_QUESTS_FOR_RND = new Array<>();
    private World world;
    private int activeEvents;

    static {
        ALL_QUESTS_FOR_RND.add(DestroyAnimalsSpawnQuest.class);
    }

    public RandomQuestsAndEvents(World world) {
        this.world = world;
    }

    public void newStep() {
        if (activeEvents == 0 && world.lastActivePlayerTeam != null && world.step > 0) {
            Class<? extends BaseQuest> aClass = ALL_QUESTS_FOR_RND.get(0);
            BaseQuest quest = world.lastActivePlayerTeam.journal.get(aClass);
            if (quest == null) {
                BaseQuest baseQuest = BaseQuest.startQuest(world.lastActivePlayerTeam, aClass);
            }
        }
    }
}
