package conversion7.game.stages.world.quest;

import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import conversion7.engine.Gdxg;
import conversion7.engine.utils.Utils;
import conversion7.game.QuestStartError;
import conversion7.game.stages.world.team.Team;
import conversion7.game.ui.UiLogger;
import org.slf4j.Logger;

public class Journal {
    private static final Logger LOG = Utils.getLoggerForClass();
    public OrderedMap<Class, BaseQuest> quests = new OrderedMap<>();
    public Team team;

    public Journal(Team team) {
        this.team = team;
    }


    public <T extends BaseQuest> T get(Class<T> aClass) {
        BaseQuest quest = quests.get(aClass);
        return (T) quest;
    }

    public <T extends BaseQuest> T getOrCreate(Class<T> aClass) {
        BaseQuest quest = quests.get(aClass);
        if (quest == null) {
            try {
                quest = aClass.newInstance();
                quest.setTeam(team);
                quests.put(aClass, quest);
                quest.prepareAndStart();
            } catch (QuestStartError e) {
                UiLogger.addGameInfoLabel("Quest cant be start: " + aClass.getSimpleName());
                LOG.error(e.getMessage(), e);
            } catch (Exception e) {
                Gdxg.core.addError(e);
            }
        }
        return (T) quest;
    }
}
