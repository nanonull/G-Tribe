package conversion7.game.ai.global;

import conversion7.engine.utils.ExcelFile;
import conversion7.engine.utils.FastAsserts;
import conversion7.engine.utils.ResourceLoader;
import conversion7.game.ai.AiTaskTypeDto;
import org.apache.commons.beanutils.BeanUtils;

import java.util.List;
import java.util.Map;

public enum AiTaskType {
    ESCAPE, ANIMAL_MIGRATION, BUILD_CAMP, FERTILIZE, MELEE_ATTACK, RANGE_ATTACK,

    MOVE, MOVE_FOR_ATTACK, MOVE_FOR_BUILD_CAMP, MOVE_TO_COMPLETE_CAMP_BUILDING, MOVE_FOR_FERTILIZE,
    MOVE_TO_CONTROLLER, MOVE_TO_BETTER_CELL, MOVE_TO_CAPTURE_CAMP, MOVE_FOR_TRIBE_GOAL, MOVE_TO_CONTACT_TRIBE,
    ;

    public int priority;

    public static void init() {
        try {
            List<Map<String, String>> tables =
                    ExcelFile.readXlsx(ResourceLoader.getResourceAsStream("unit_ai_tasks.xlsx"));
            for (Map<String, String> table : tables) {
                AiTaskTypeDto taskTypeDto = new AiTaskTypeDto();
                BeanUtils.populate(taskTypeDto, table);
                AiTaskType aiTaskType = AiTaskType.valueOf(taskTypeDto.getType());
                aiTaskType.priority = taskTypeDto.getPriority();
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

        FastAsserts.assertMoreThan(MOVE_FOR_BUILD_CAMP.priority, MOVE.priority);
    }
}
