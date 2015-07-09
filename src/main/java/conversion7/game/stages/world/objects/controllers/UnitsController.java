package conversion7.game.stages.world.objects.controllers;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import conversion7.engine.utils.Utils;
import conversion7.game.interfaces.Validatable;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.unit.Unit;
import org.slf4j.Logger;

public class UnitsController extends AbstractObjectController implements Validatable {

    private static final Logger LOG = Utils.getLoggerForClass();

    private AreaObject areaObject;

    public UnitsController(AreaObject areaObject) {
        super(areaObject);
        this.areaObject = areaObject;
    }

    public boolean isValidated() {
        return valid;
    }

    @Override
    public void invalidate() {
        valid = false;
    }

    @Override
    public void validate() {
        if (!valid) {
            valid = true;
            if (LOG.isDebugEnabled()) {
                LOG.debug("validate " + areaObject);
                LOG.debug("units.size: " + areaObject.getUnits().size);
            }
            areaObject.getAreaObjectEffectsController().invalidate();
            areaObject.getReadyRangeUnitsController().invalidate();
        }
    }

    private void mainAddUnit(Unit unit) {
        areaObject.getUnits().add(unit);
        unit.assignToAreaObject(areaObject);
        invalidate();
        areaObject.getTeam().getTeamClassesManager().addUnitIfNewcomerInTeam(unit);
    }

    public void addUnit(Unit unit) {
        mainAddUnit(unit);
    }

    public void addUnitsAndValidate(Array<Unit> addUnits) {
        for (Unit unit : addUnits) {
            mainAddUnit(unit);
        }
        areaObject.validate();
    }

    /** Use method without validation if possible */
    public void addUnitAndValidate(Unit unit) {
        mainAddUnit(unit);
        areaObject.validate();
    }

    /** Make sure validate is called after */
    private void mainRemoveUnit(Unit unit) {
        if (!areaObject.getUnits().removeValue(unit, true)) {
            throw new GdxRuntimeException(String.format("mainRemoveUnit: AreaObject doesn't contain %s %n%s", unit, areaObject));
        }
        invalidate();
        afterUnitRemoved(unit);
    }

    public void removeAndValidate(Unit unit) {
        mainRemoveUnit(unit);
        areaObject.validate();
    }

    public void remove(Unit unit) {
        mainRemoveUnit(unit);
    }

    public void afterUnitRemoved(Unit unit) {
        areaObject.getTeam().getTeamClassesManager().removeUnit(unit);
    }
}
