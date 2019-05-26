package conversion7.game.stages;

import conversion7.game.stages.world.actions.AbstractAction;

public abstract class AbstractStageObjectAction extends AbstractAction {

    private StageObject object;

    public StageObject getObject() {
        return object;
    }

    public void setObject(StageObject object) {
        this.object = object;
    }

    public abstract void begin();

    public abstract void end();

    public void run() {
        begin();
        end();
    }


}
