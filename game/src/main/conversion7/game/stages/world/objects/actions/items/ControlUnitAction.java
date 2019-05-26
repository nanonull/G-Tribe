package conversion7.game.stages.world.objects.actions.items;

import com.badlogic.gdx.graphics.Color;
import conversion7.engine.artemis.ui.UnitInWorldHintPanelsSystem;
import conversion7.engine.artemis.ui.UnitUnderControlIndicatorSystem;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.actions.AbstractWorldTargetableAction;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.quest.items.FertilizeAnimalsQuest;
import conversion7.game.stages.world.team.Team;
import conversion7.game.stages.world.team.TribeRelationType;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.stages.world.unit.effects.UnitEffectManager;
import conversion7.game.stages.world.unit.effects.items.ControlUnitsEffect;
import conversion7.game.stages.world.unit.effects.items.UnderControlEffect;
import conversion7.game.ui.world.UnitIconWithInfoPanel;
import org.testng.Assert;

// use capture
@Deprecated
public class ControlUnitAction extends AbstractWorldTargetableAction {

    public static final float TARGET_POWER_CHECK_MOD = 1.2f;
    private static final Color UNDER_CONTROL_COLOR = UnitIconWithInfoPanel.BASE_BORDER_COLOR;

    public ControlUnitAction() {
        super(Group.ATTACK);
    }

    @Override
    public String getShortName() {
        return "CtrlUnit";
    }

    @Override
    public String getActionWorldHint() {
        return "control unit";
    }

    public int getDistance() {
        return 1;
    }

    public static boolean doesControl(Unit controller, Unit underCtrl) {
        ControlUnitsEffect controlUnitsEffect = controller.squad.getEffectManager().getEffect(ControlUnitsEffect.class);
        return controlUnitsEffect != null && controlUnitsEffect.underControl.contains(underCtrl, true);
    }

    public static void takeControl(Unit controllerUnit, Unit underCtrl) {
        UnitEffectManager effectManager = controllerUnit.squad.getEffectManager();
        ControlUnitsEffect controlUnitsEffect = effectManager.getEffect(ControlUnitsEffect.class);
        if (controlUnitsEffect == null) {
            controlUnitsEffect = new ControlUnitsEffect();
            effectManager.addEffect(controlUnitsEffect);
        }
        Assert.assertFalse(doesControl(controllerUnit, underCtrl));
        controlUnitsEffect.underControl.add(underCtrl);

        markUnitIsUnderControl(controllerUnit, underCtrl);
        Team prevTeam = underCtrl.squad.getPrevTeam();
        prevTeam.world.addRelationType(TribeRelationType.UNIT_CAPTURED,
                controllerUnit.getSquad().team, prevTeam);
        underCtrl.squad.validate(true);
        underCtrl.squad.refreshUiPanelInWorld();
        underCtrl.squad.validateView();
    }

    public static void markUnitIsUnderControl(Unit controllerUnit, Unit unitUnderCtrl) {
        UnderControlEffect underControlEffect = unitUnderCtrl.squad.getEffectManager().getEffect(UnderControlEffect.class);
        if (underControlEffect == null) {
            underControlEffect = new UnderControlEffect();
            unitUnderCtrl.squad.getEffectManager().addEffect(underControlEffect);
        } else {
            releaseControlByAnotherUnit(underControlEffect);
        }
        underControlEffect.controller = controllerUnit;
        underControlEffect.previousTeam = unitUnderCtrl.squad.team;
        controllerUnit.squad.team.joinSquad(unitUnderCtrl.squad);
        if (controllerUnit.squad.team.isHumanPlayer()) {
            UnitInWorldHintPanelsSystem.getOrCreate(unitUnderCtrl.squad)
                    .init(unitUnderCtrl.squad, false, true, UNDER_CONTROL_COLOR);
        }
        UnitUnderControlIndicatorSystem.components.create(unitUnderCtrl.squad.entityId).squad = unitUnderCtrl.squad;

        if (unitUnderCtrl.squad.isAnimal()) {
            FertilizeAnimalsQuest.addedAnimal(controllerUnit.squad.team);
        }
    }

    public static void releaseControlByAnotherUnit(UnderControlEffect releaseOwnerOfEffect) {
        AbstractSquad underControl = releaseOwnerOfEffect.getOwner();
        Team previousTeam = underControl.getEffectManager().getEffect(UnderControlEffect.class).previousTeam;
        previousTeam.joinSquad(underControl);

        ControlUnitsEffect controlUnitsEffect = releaseOwnerOfEffect.controller.squad.getEffectManager().getEffect(ControlUnitsEffect.class);
        boolean removedControl = controlUnitsEffect.underControl.removeValue(underControl.unit, true);
        Assert.assertTrue(removedControl, "removedControl fail");
        controlUnitsEffect.validate();
        if (releaseOwnerOfEffect.controller.squad.team.isHumanPlayer()) {
            UnitInWorldHintPanelsSystem.getOrCreate(underControl)
                    .init(underControl, false, true, null);
        }

        releaseOwnerOfEffect.controller = null;
        releaseOwnerOfEffect.remove();
        UnitUnderControlIndicatorSystem.components.create(underControl.entityId).squad = underControl;
    }

    @Override
    protected String buildDescription() {
//        return getName() + "\n \nTake control on another Unit.\n" +
//                "Requires 'unit power' to be more than 'target power * " + TARGET_POWER_CHECK_MOD + "'.\n" +
//                "'Unit power' is multiplied on " + ShamanUnitEffect.CONTROL_UNIT_MLT + " for Shaman unit.\n" +
//                ScaredEffect.SCARE_TO_CONTROL_HINT;
        return "";
    }

    @Override
    protected Color getTargetCellSelectionColor(Cell cellAround) {
        return Color.SCARLET;
    }

    @Override
    public void handleAcceptedInput(Cell input) {
        Unit underCtrl = input.squad.unit;
        Unit controllerUnit = getSquad().unit;
        takeControl(controllerUnit, underCtrl);
    }
}
