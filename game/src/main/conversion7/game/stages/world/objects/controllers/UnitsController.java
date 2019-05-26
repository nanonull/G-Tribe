package conversion7.game.stages.world.objects.controllers;

import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.ui.world.UnitInWorldHintPanel;
import org.junit.Assert;
import org.slf4j.Logger;

// contains excessive logic from old army type
@Deprecated
public class UnitsController extends AbstractObjectController {

    private static final Logger LOG = Utils.getLoggerForClass();

    private AbstractSquad owner;

    public UnitsController(AbstractSquad owner) {
        super(owner);
        this.owner = owner;
    }

    public void setUnitAndValidate(Unit unit) {
        mainAddUnit(unit);
        owner.validate();
    }

    @Override
    public void validate() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("validate " + owner);
        }
        owner.getActionsController().invalidate();
        owner.getUnitParametersValidator().invalidate();
        owner.getStealthValidator().invalidate();
    }

    @Deprecated
    public void addUnit(Unit unit) {
        mainAddUnit(unit);
    }

    private void mainAddUnit(Unit unit) {
        Assert.assertTrue(isOwnerUnitChanged(unit));
        unit.squad.initParams(unit.getStartingParams());
        owner.unit = unit;
        if (owner.unitInWorldHintPanel == null) {
            owner.unitInWorldHintPanel = new UnitInWorldHintPanel(owner);
        }
        unit.assignToSquad(owner);
        Assert.assertNotNull(unit.squad);
        if (unit.squad.getAge() == null) {
            unit.squad.setAgeStep(0);
        }
        invalidate();
        owner.unitCellValidator.invalidate();
    }

    private boolean isOwnerUnitChanged(Unit unit) {
        return owner.unit == null || owner.unit == unit;
    }

    /** Make sure validate is called after */
    private void mainRemoveUnit(Unit unit) {
        invalidate();
    }

    public void remove(Unit unit) {
        mainRemoveUnit(unit);
    }
}
