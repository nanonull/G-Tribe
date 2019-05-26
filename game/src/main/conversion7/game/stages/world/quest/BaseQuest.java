package conversion7.game.stages.world.quest;

import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.team.Team;
import conversion7.game.ui.UiLogger;

public abstract class BaseQuest {

    public OrderedMap<Object, State> states = new OrderedMap<>();
    public OrderedMap<Object, String> stateMessages = new OrderedMap<>();
    public OrderedMap<Object, Cell> stateCellTargets = new OrderedMap<>();
    public Team team;

    public BaseQuest() {
    }

    public String getName() {
        return getClass().getSimpleName();
    }

    public String getAddInfo() {
        return "";
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public static <T extends BaseQuest> T startQuest(Team team, Class<T> cls) {
        return team.journal.getOrCreate(cls);
    }

    public void failAllOpen() {
        for (Object key : states.keys()) {
            if (states.get(key) == State.OPEN) {
                fail(key);
            }
        }
    }

    public void initEntry(Object key, String msg) {
        stateMessages.put(key, msg);
        startQuestEntry(key);
        stateUpdated();
    }

    public abstract void initEntries();

    public void prepareAndStart() {
        initEntries();
        onStart();
        stateUpdated();
    }

    public abstract void onStart();

    public void startQuestEntry(Object entry) {
        states.put(entry, State.OPEN);
        String msg = stateMessages.get(entry);
        UiLogger.addGameInfoLabel(State.OPEN + ": " + msg);
    }

    public boolean isCompleted(Object entry) {
        State state = states.get(entry);
        if (state == null || state != State.DONE) {
            return false;
        }
        return true;
    }

    public void complete(Object entry) {
        states.put(entry, State.DONE);
        String msg = stateMessages.get(entry);
        UiLogger.addGameInfoLabel(State.DONE + ": " + msg);
        stateUpdated();
    }

    private void stateUpdated() {
        if (team != null) {
//            Gdxg.clientUi.journalPanel.showFor(team);
        }
    }

    public void fail(Object entry) {
        states.put(entry, State.FAIL);
        String msg = stateMessages.get(entry);
        UiLogger.addGameInfoLabel(State.FAIL + ": " + msg);
        stateUpdated();
    }

    public void completeOpen() {
        for (Object key : states.keys()) {
            State state = states.get(key);
            if (state == State.OPEN) {
                complete(key);
            }
        }

    }

    public enum State {
        UNAVAILABLE, OPEN, FAIL, DONE
    }
}
