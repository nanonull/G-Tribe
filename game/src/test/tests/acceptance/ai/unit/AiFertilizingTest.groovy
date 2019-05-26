package tests.acceptance.ai.unit

import shared.BaseGdxgSpec
import spock.lang.Ignore

@Ignore
class AiFertilizingTest extends BaseGdxgSpec {
    def unitAiEvaluatorStart

//    @Override
//    def setup() {
//        GdxgConstants.AREA_OBJECT_AI = true
//        unitAiEvaluatorStart = UnitAiEvaluator.instance
//        UnitAiEvaluator.instance = new UnitAiEvaluator() {
//            @Override
//            protected void preProcessTasks(List<AiTask> aiTasks) {
//                Iterator<AiTask> taskIterator = aiTasks.iterator()
//                while (taskIterator.hasNext()) {
//                    def task = taskIterator.next()
//                    if ((task.getClass() == MoveTask && task.priority == AiTaskType.MOVE_FOR_FERTILIZE.priority)
//                            || task.getClass() == FertilizeTask) {
//                        println('target task found')
//                    } else {
//                        taskIterator.remove()
//                    }
//                }
//            }
//        }
//    }
//
//    @Override
//    def cleanup() {
//        UnitAiEvaluator.instance = unitAiEvaluatorStart
//    }
//
//    void 'test move and fertilize'() {
//        given:
//        assert (ActionPoints.UNIT_START_ACTION_POINTS - ActionPoints.MOVEMENT) >= ActionPoints.FERTILIZE
//
//        lockCore()
//        worldSteps.createHumanTeam()
//
//        def cell1 = worldSteps.nextNeighborCell
//        WorldServices.nextUnitGender = true
//        def squad = worldSteps.createUnit(
//                worldSteps.createHumanTeam(false),
//                cell1)
//        worldSteps.setAgeLevel(squad.unit, FertilizeAction.AGE_FROM)
//        Team team = squad.getTeam()
//
//        def cell2 = worldSteps.nextNeighborCell
//        def cell3 = worldSteps.nextNeighborCell
//        def cell4 = worldSteps.nextNeighborCell
//
//        WorldServices.nextUnitGender = false
//        def squad2 = worldSteps.createUnit(
//                team,
//                cell4)
//        worldSteps.setAgeLevel(squad2.unit, FertilizeAction.AGE_FROM)
//        releaseCoreAndWaitNextCoreStep()
//
//        assert squad.unit.canFertilize()
//        assert squad2.unit.canBeFertilized()
//        assert squad.visibleCellsAround.containsAll(cell2, cell3, cell4)
//
//        when: "1st turn"
//        worldSteps.rewindTeamsToStartNewWorldStep()
//        waitForNextCoreStep()
//
//        then: "s1 moves to s2"
//        assert squad.cell == cell2
//
//        when: "2nd turn"
//        UnitFertilizer.overrideNextFertilizationChance = 100
//        UnitFertilizer.ignoreConditionsOnNextFertilization = true
//        worldSteps.rewindTeamsToStartNewWorldStep()
//        waitForNextCoreStep()
//
//        then: "move and fertilize"
//        assert squad.cell == cell3
//        assert squad2.unit.effectManager.getEffect(ChildbearingEffect)
//        and: 'no one around to fertilize'
//        assert !squad.actionsController.getAction(FertilizeAction)
//    }

}
