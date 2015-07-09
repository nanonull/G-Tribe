package conversion7.game.stages;

import conversion7.game.interfaces.ExecutableVoid;

public abstract class AbstractStageObjectAction implements ExecutableVoid {

    private StageObject object;

    public AbstractStageObjectAction(StageObject stageObject) {
        object = stageObject;
    }

    public StageObject getObject() {
        return object;
    }

}
