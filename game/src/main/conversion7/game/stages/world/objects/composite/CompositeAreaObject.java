package conversion7.game.stages.world.objects.composite;

import com.badlogic.gdx.utils.Array;
import conversion7.engine.Gdxg;
import conversion7.engine.ai_new.base.AiEntity;
import conversion7.engine.ai_new.base.AiTask;
import conversion7.game.GdxgConstants;
import conversion7.game.ai.global.CompositeAiEvaluator;
import conversion7.game.stages.StageObject;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.team.Team;

import java.util.ArrayList;
import java.util.List;

public abstract class CompositeAreaObject extends StageObject implements AiEntity {
    public static final int BASE_ACTION_POINTS_FOR_COMPOSITE = 3;
    public Team team;
    public boolean alive = true;
    protected boolean active;
    private Array<AreaObject> parts = new Array<>();
    private int hadAiActAt;
    private List<AiTask> aiTasks = new ArrayList<>();

    public static <C extends CompositeAreaObject> C create(Class<C> cls, Array<AreaObject> parts) {
        C object = null;
        try {
            object = cls.newInstance();
            object.setParts(parts);
            object.init();
            object.validateView();
        } catch (Throwable e) {
            Gdxg.core.addError(e);
        }
        return object;
    }

    @Override
    public String getHint() {
        return "TODO";
    }

    public Array<AreaObject> getParts() {
        return parts;
    }

    public void setParts(Array<AreaObject> parts) {
        this.parts = parts;
    }

    public abstract CompositeAiEvaluator getAiEvaluator();

    public void setTeam(Team team) {
        this.team = team;
    }

    public void validateView() {
        if (isActive()) {
            for (AreaObject part : parts) {
                part.validateView();
            }
        }
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public void init() {

    }

    public void placeAt(Cell startCell) {
        active = true;
    }

    public int getMaxAiAttemptsPerTurn() {
        return BASE_ACTION_POINTS_FOR_COMPOSITE;
    }

    public boolean isAiEnabled() {
        return GdxgConstants.AREA_OBJECT_AI;
    }

    public void hadAiActAt(int step) {
        hadAiActAt = step;
    }

    @Override
    public void addAiTask(AiTask aiTask) {
        aiTasks.add(aiTask);
    }

    @Override
    public List<AiTask> getAiTasks() {
        return aiTasks;
    }

    public abstract void deactivate();

    public boolean isPartActive(AreaObject part) {
        return !part.isRemovedFromWorld();
    }
}
