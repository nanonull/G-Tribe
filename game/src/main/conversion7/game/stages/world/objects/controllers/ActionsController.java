package conversion7.game.stages.world.objects.controllers;

import com.badlogic.gdx.utils.GdxRuntimeException;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.objects.actions.AbstractAreaObjectAction;
import conversion7.game.stages.world.unit.actions.ActionEvaluation;

import java.lang.reflect.InvocationTargetException;

public class ActionsController extends AbstractObjectController {

    public ActionsController(AreaObject areaObject) {
        super(areaObject);
        if (areaObject.isSquad()) {
//            AbstractSquad squad = areaObject.toSquad();
//            Array<ActionEvaluation> allActions = ActionEvaluation.UnitActionMappings.
//                    ACTIONS_BY_UNIT_CLASS.get(squad.unit.getGameClass());
            for (ActionEvaluation evaluation : ActionEvaluation.values()) {
                activateAction(evaluation.classImpl);
                deactivateAction(evaluation.classImpl);
            }
        }
    }

    public boolean isActionActive(final Class<? extends AbstractAreaObjectAction> aClass) {
        AbstractAreaObjectAction action = getAction(aClass);
        return action != null || action.active;
    }

    public void activateAction(final Class<? extends AbstractAreaObjectAction> aClass) {
        AbstractAreaObjectAction action = getAction(aClass);
        if (action == null) {
            try {
                action = aClass.getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                throw new GdxRuntimeException(e.getMessage() + " \nduring creation new action of class: " + aClass, e);
            }
            areaObject.getActions().put(aClass, action);
            action.setObject(areaObject);
        }
        action.active = true;
        action.setCancelled(false);
    }

    public <T extends AbstractAreaObjectAction> T getAction(final Class<T> aClass) {
        AbstractAreaObjectAction objectAction = areaObject.getActions().get(aClass);
        return objectAction == null ? null : (T) objectAction;
    }

    public void deactivateAction(final Class<? extends AbstractAreaObjectAction> aClass) {
        areaObject.getActions().remove(aClass);
//        AbstractAreaObjectAction action = getAction(aClass);
//        if (action != null) {
//            action.active = false;
//        }
    }

    @Override
    public void validate() {

    }
}
