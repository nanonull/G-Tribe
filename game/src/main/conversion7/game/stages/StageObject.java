package conversion7.game.stages;

import conversion7.engine.Gdxg;
import conversion7.engine.utils.Utils;
import conversion7.game.interfaces.HintProvider;

public abstract class StageObject implements HintProvider {

    private int id = -1;
    protected String name;
    public int entityId;

    public StageObject(int id) {
        this.id = id;
        entityId = Gdxg.core.nextEntityId();
    }

    public StageObject() {
        this(Utils.getNextId());
    }

    public String getName() {
        return name == null ? buildName() : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public abstract void init();

    protected String buildName() {
        return getClass().getSimpleName() + "-" + getId();
    }

}
