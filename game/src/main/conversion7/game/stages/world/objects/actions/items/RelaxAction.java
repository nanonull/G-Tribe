package conversion7.game.stages.world.objects.actions.items;

import conversion7.engine.Gdxg;
import conversion7.game.stages.world.objects.actions.AbstractSquadAction;
import conversion7.game.stages.world.unit.effects.items.ConcentrationEffect;

public class RelaxAction extends AbstractSquadAction {
    public static final String DESC = "Deactivate unit for current turn (but save APs until the end of turn)." +
            "\nUnit gets " + ConcentrationEffect.class.getSimpleName() + " if he ends turn with Attack AP > 0" +
            "\n \n" + ConcentrationEffect.class.getSimpleName() + ": " + ConcentrationEffect.HINT;

    public RelaxAction() {
        super(Group.COMMON);
    }

    @Override
    public String getShortName() {
        return "Relax";
    }

    @Override
    public void begin() {
        getSquad().skipTurn = true;
        getSquad().getActionsController().forceTreeValidationFromThisNode();
        Gdxg.clientUi.getWorldMainWindow().teamControlsPanel.tryActivateNext(getSquad().team);
    }

    @Override
    protected String buildDescription() {
        return toString() + "\n \n" + DESC;
    }
}
