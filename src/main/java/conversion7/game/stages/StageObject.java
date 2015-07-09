package conversion7.game.stages;

import conversion7.engine.utils.Utils;
import conversion7.game.interfaces.HintProvider;

public abstract class StageObject implements HintProvider {

    private int id = -1;
    protected String name;

    public StageObject(int id) {
        this.id = id;
    }

    public StageObject() {
        this(Utils.getNextId());
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name == null ? getClass().getSimpleName() : name;
    }

    public int getId() {
        return id;
    }

}
