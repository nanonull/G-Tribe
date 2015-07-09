package conversion7.game.ui.world.split_units;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.objects.HumanSquad;
import conversion7.game.stages.world.team.Team;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.ui.UiLogger;

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

    public void reset() {
        initiatorTeam = null;
        leftSide.reset();
        rightSide.reset();
    }

    public void executeTransfer() {
        // create areaObject for one side from two if need
        boolean leftSideNewObjectCreated = false;
        if (leftSide.getAreaObject() == null) {
            leftSideNewObjectCreated = true;
            HumanSquad newObject = initiatorTeam.createHumanSquad(leftSide.getCell());
            leftSide.wrapAreaObject(newObject);
        }
        if (rightSide.getAreaObject() == null) {
            if (leftSideNewObjectCreated) {
                // TODO clear on iteration end
                Utils.error("2 sides with empty objects!");
            }
            HumanSquad newObject = initiatorTeam.createHumanSquad(rightSide.getCell());
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
            unit.moveInto(rightSide.getAreaObject());
        }
        leftSide.getTransferList().clear();

        // transfer right side to left
        rightSide.getTransferTable().clearChildren();
        for (UnitRow unitRow : rightSide.getTransferList()) {
            leftSide.addUnitRowToMainTable(unitRow);
            Unit unit = unitRow.getUnit();
            unit.moveInto(leftSide.getAreaObject());
        }
        rightSide.getTransferList().clear();


        // validate model
        if (rightSide.getAreaObject().couldBeDefeated()) {
            leftSide.getAreaObject().getInventoryController().mergeInventoriesFrom(rightSide.getAreaObject());
            rightSide.getAreaObject().defeat();
            rightSide.setAreaObject(null);
        } else {
            rightSide.getAreaObject().validate();
        }

        if (leftSide.getAreaObject().couldBeDefeated()) {
            rightSide.getAreaObject().getInventoryController().mergeInventoriesFrom(leftSide.getAreaObject());
            leftSide.getAreaObject().defeat();
            leftSide.setAreaObject(null);
        } else {
            leftSide.getAreaObject().validate();
        }
    }

    /** limit-error could not be reached if any side had a null object before transfer start (no object on target cell) */
    private boolean isTransferPossible() {
        int leftSideUnitsAmountBeforeGetNew = leftSide.getAreaObject().getUnits().size - leftSide.getTransferList().size;
        int rightSideUnitsAmountBeforeGetNew = rightSide.getAreaObject().getUnits().size - rightSide.getTransferList().size;

        boolean leftSideLimitError = false;
        if (leftSideUnitsAmountBeforeGetNew + rightSide.getTransferList().size > AreaObject.UNITS_AMOUNT_LIMIT) {
            UiLogger.addInfoLabel(LEFT_SIDE_LIMIT_ERROR);
            leftSideLimitError = true;
        }

        boolean rightSideLimitError = false;
        if (rightSideUnitsAmountBeforeGetNew + leftSide.getTransferList().size > AreaObject.UNITS_AMOUNT_LIMIT) {
            UiLogger.addInfoLabel(RIGHT_SIDE_LIMIT_ERROR);
            rightSideLimitError = true;
        }

        return !(leftSideLimitError || rightSideLimitError);
    }

    public void setInitiatorTeam(Team initiatorTeam) {
        this.initiatorTeam = initiatorTeam;
    }

    public static class SplitMergeSide {
        private final Table initialTable;
        private final Table transferTable;
        private final Array<UnitRow> transferList = new Array<>();
        private AreaObject areaObject;
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

        public AreaObject getAreaObject() {
            return areaObject;
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
            areaObject = null;
            cell = null;
        }

        public void setAreaObject(AreaObject areaObject) {
            this.areaObject = areaObject;
        }

        public void wrapAreaObject(AreaObject areaObject) {
            setAreaObject(areaObject);
            this.cell = areaObject.getCell();
        }

        public void wrapCell(Cell cell) {
            this.cell = cell;
        }
    }


}
