package conversion7.game.stages.world.objects.actions;

import com.badlogic.gdx.utils.Array;
import conversion7.engine.utils.Utils;
import conversion7.engine.validators.NodeAppendedValidator;
import conversion7.game.stages.world.objects.controllers.ActionsController;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.stages.world.unit.actions.ActionEvaluation;
import conversion7.game.ui.world.main_panel.WorldMainWindowSystem;
import org.slf4j.Logger;

public class UnitActionsValidator extends NodeAppendedValidator {

    private static final Logger LOG = Utils.getLoggerForClass();
    private final ActionsController actionsController;
    private final AbstractSquad squad;
    private Array<ActionEvaluation> actionsToActivateWip = new Array<>();

    public UnitActionsValidator(ActionsController actionsController, AbstractSquad squad) {
        this.actionsController = actionsController;
        this.squad = squad;
    }

    @Override
    public void validation() {
        Unit unit = squad.unit;
        if (squad.isAlive() && squad.cell != null) {
            for (ActionEvaluation actionEvaluation : ActionEvaluation.valuesOrdered) {
                if (actionEvaluation.evaluateOwner(squad)) {
                    actionsController.activateAction(actionEvaluation.classImpl);
                } else {
                    actionsController.deactivateAction(actionEvaluation.classImpl);
                }
            }

            //        squad.getActions().clear();
//        for (ActionEvaluation actionEvaluation : actionsToActivateWip) {
//            actionsController.activateAction(actionEvaluation.classImpl);
//        }
//        actionsToActivateWip.clear();

            WorldMainWindowSystem.updatedObjects.add(unit);

        }
    }

}
