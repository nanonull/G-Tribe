package conversion7.engine.ai_new.base;

import java.util.List;

public interface AiEntity {

    void addAiTask(AiTask aiTask);

    List<AiTask> getAiTasks();
}
