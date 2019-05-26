package conversion7.game.ui.world.split_units;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.objects.unit.WorldSquad;
import conversion7.game.stages.world.team.Team;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.ui.UiLogger;
import org.testng.Assert;

@Deprecated
public class SplitMergeUnitsController {

    private static final String LEFT_SIDE_LIMIT_ERROR = "Total-units-amount limit reached on Left side of transfer!";
    private static final String RIGHT_SIDE_LIMIT_ERROR = "Total-units-amount limit reached on Right side of transfer!";
    private SplitMergeUnitsWindow splitMergeUnitsWindow;
    private SplitMergeSide leftSide;
    private SplitMergeSide rightSide;
    private Team initiatorTeam;

    public SplitMergeUnitsController(SplitMergeUnitsWindow splitMergeUnitsWindow) {
        this.splitMergeUnitsWindow = splitMergeUnitsWindow;
        leftSide = new SplitMergeSide(
                splitMergeUnitsWindow.getLeftSideUnitsTable(), splitMergeUnitsWindow.getLeftSideUnitsForTransferTable());
        rightSide = new SplitMergeSide(
                splitMergeUnitsWindow.getRightSideUnitsTable(), splitMergeUnitsWindow.getRightSideUnitsForTransferTable());
    }

    public SplitMergeSide getLeftSide() {
        return leftSide;
    }

    public SplitMergeSide getRightSide() {
        return rightSide;
    }

    /** limit-error could not be reached if any side had a null object before transfer start (no object on target cell) */
    private boolean isTransferPossible() {
        Assert.assertTrue(false, "review");

        int leftSideUnitsAmountBeforeGetNew = 0;
//        int leftSideUnitsAmountBeforeGetNew = leftSide.getSquad().getUnits().size - leftSide.getTransferList().size;
        int rightSideUnitsAmountBeforeGetNew = 0;
//        int rightSideUnitsAmountBeforeGetNew = rightSide.getSquad().getUnits().size - rightSide.getTransferList().size;

        boolean leftSideLimitError = false;
        if (leftSideUnitsAmountBeforeGetNew + rightSide.getTransferList().size > AbstractSquad.UNITS_AMOUNT_LIMIT) {
            UiLogger.addErrorLabel(LEFT_SIDE_LIMIT_ERROR);
            leftSideLimitError = true;
        }

        boolean rightSideLimitError = false;
        if (rightSideUnitsAmountBeforeGetNew + leftSide.getTransferList().size > AbstractSquad.UNITS_AMOUNT_LIMIT) {
            UiLogger.addErrorLabel(RIGHT_SIDE_LIMIT_ERROR);
            rightSideLimitError = true;
        }

        return !(leftSideLimitError || rightSideLimitError);
    }

    public void setInitiatorTeam(Team initiatorTeam) {
        this.initiatorTeam = initiatorTeam;
    }

    public void reset() {
        initiatorTeam = null;
        leftSide.reset();
        rightSide.reset();
    }

    public void executeTransfer() {
        // create squad for one side from two if need
        boolean leftSideNewObjectCreated = false;
        if (leftSide.getSquad() == null) {
            leftSideNewObjectCreated = true;
            Assert.assertTrue(false, "review createWorldSquad");
            WorldSquad newObject = initiatorTeam.createWorldSquad(leftSide.getCell(), null);
            leftSide.wrapAreaObject(newObject);
        }
        if (rightSide.getSquad() == null) {
            if (leftSideNewObjectCreated) {
                // TODO clear on iteration end
                Utils.error("2 sides with empty objects!");
            }
            Assert.assertTrue(false, "review createWorldSquad");
            WorldSquad newObject = initiatorTeam.createWorldSquad(rightSide.getCell(), null);
            rightSide.wrapAreaObject(newObject);
        }

        if (!isTransferPossible()) {
            return;
        }


        // transfer left side to right
        leftSide.getTransferTable().clearChildren();
        for (UnitRow unitRow : leftSide.getTransferList()) {
            rightSide.addUnitRowToMainTable(unitRow);
            Unit unit = unitRow.getUnit();
//            unit.moveInto(rightSide.getSquad());
        }
        leftSide.getTransferList().clear();

        // transfer right side to left
        rightSide.getTransferTable().clearChildren();
        for (UnitRow unitRow : rightSide.getTransferList()) {
            leftSide.addUnitRowToMainTable(unitRow);
            Unit unit = unitRow.getUnit();
//            unit.moveInto(leftSide.getSquad());
        }
        rightSide.getTransferList().clear();


        // validate model
        if (rightSide.getSquad().hasMortalWound()) {
            leftSide.getSquad().getInventoryController().mergeInventoriesFrom(rightSide.getSquad());
            Assert.assertTrue(false);
//            rightSide.getSquad().defeat();
            rightSide.setSquad(null);
        } else {
            rightSide.getSquad().validate();
        }

        if (leftSide.getSquad().hasMortalWound()) {
            rightSide.getSquad().getInventoryController().mergeInventoriesFrom(leftSide.getSquad());
            Assert.assertTrue(false);
//            leftSide.getSquad().defeat();
            leftSide.setSquad(null);
        } else {
            leftSide.getSquad().validate();
        }
    }

    public static class SplitMergeSide {
        private final Table initialTable;
        private final Table transferTable;
        private final Array<UnitRow> transferList = new Array<>();
        private AbstractSquad squad;
        private Cell cell;

        public SplitMergeSide(Table initialTable, Table transferTable) {
            this.initialTable = initialTable;
            this.transferTable = transferTable;
        }

        public Table getTransferTable() {
            return transferTable;
        }

        public Array<UnitRow> getTransferList() {
            return transferList;
        }

        public AbstractSquad getSquad() {
            return squad;
        }

        public void setSquad(AbstractSquad squad) {
            this.squad = squad;
        }

        public Cell getCell() {
            return cell;
        }

        public void shiftUnitRow(UnitRow unitRow) {
            boolean wasSetOnTransfer;
            if (initialTable.getCell(unitRow) == null) {
                if (transferTable.getCell(unitRow) == null) {
                    Utils.error("unitRow was not found in both Side tables");
                }
                wasSetOnTransfer = true;
            } else {
                wasSetOnTransfer = false;
            }

            if (wasSetOnTransfer) {
                if (!transferList.removeValue(unitRow, true)) {
                    Utils.error("Was not removed from transfer due to absence!");
                }
                transferTable.removeActor(unitRow);
                addUnitRowToMainTable(unitRow);
            } else {
                if (transferList.contains(unitRow, true)) {
                    Utils.error("Already in transfer!");
                }
                transferList.add(unitRow);
                initialTable.removeActor(unitRow);
                addUnitRowToTransferTable(unitRow);
            }
        }

        public void addUnitRowToMainTable(UnitRow unitRow) {
            unitRow.setSide(this);
            initialTable.add(unitRow);
            initialTable.row();
        }

        public void addUnitRowToTransferTable(UnitRow unitRow) {
            transferTable.add(unitRow);
            transferTable.row();
        }

        public void reset() {
            transferList.clear();
            squad = null;
            cell = null;
        }

        public void wrapAreaObject(AbstractSquad areaObject) {
            setSquad(areaObject);
            this.cell = areaObject.getLastCell();
        }

        public void wrapCell(Cell cell) {
            this.cell = cell;
        }
    }


}
