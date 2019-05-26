package conversion7.game.stages.world.objects.actions;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import conversion7.engine.Gdxg;
import conversion7.engine.artemis.ui.UnitSelectionUiSystem;
import conversion7.game.interfaces.AreaViewerInputResolver;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.unit.AbstractSquad;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractWorldTargetableAction extends AbstractSquadAction implements AreaViewerInputResolver {

    private static final String HINT = "Press LEFT mouse to ACT\nRIGHT mouse - CANCEL";

    public AbstractWorldTargetableAction(Group group) {
        super(group);
    }

    public abstract String getActionWorldHint();

    protected Array<Cell> getAcceptableCells() {
        return getSquad().getLastCell().getCellsAround(1, getDistance(), new Array<>());
    }

    @Override
    public boolean isTwoStepCompletion() {
        return true;
    }

    protected int getMaxPossibleDistance() {
        return getSquad().team.world.widthInCells;
    }

    public abstract int getDistance();

    private String getActionWorldHint2() {
        return getClass().getSimpleName();
    }

    @Override
    public boolean hasAcceptableDistanceTo(Cell toCell) {
        return getSquad().getLastCell().distanceIntTo(toCell) <= getDistance();
    }

    @Override
    public boolean couldAcceptInput(Cell input) {
        return actionEvaluation.testOwnerVsTargetCell.apply(getSquad(), input);
    }

    @Override
    public void run() {
        begin();
        UnitSelectionUiSystem unitSelectionUiSystem = Gdxg.core.artemis.getSystem(UnitSelectionUiSystem.class);
        Array<AbstractWorldTargetableAction> actionSelectionScheduled = unitSelectionUiSystem
                .newActionSelectionScheduled;
        actionSelectionScheduled.clear();
        actionSelectionScheduled.add(this);
    }

    @Override
    public void begin() {
        Gdxg.getAreaViewer().startInputResolving(this);
        Gdxg.clientUi.getWorldHintPanel().showHint(getActionWorldHint2(), HINT);
    }

    @Override
    public void beforeInputHandle() {

    }

    @Override
    public void afterInputHandled() {
        Gdxg.core.artemis.getSystem(UnitSelectionUiSystem.class).scheduleReselectionUnitAction();
        end();
    }

    @Override
    public void cancel() {
        super.cancel();
        Gdxg.core.artemis.getSystem(UnitSelectionUiSystem.class).scheduleReselectionUnitAction();
        Gdxg.getAreaViewer().unhideSelection();
    }

    protected abstract Color getTargetCellSelectionColor(Cell cellAround);

    public List<Cell> calculateAcceptableCells() {
        AbstractSquad squad = getSquad();
        if (!actionEvaluation.evaluateOwner(squad)) {
            return new ArrayList<>();
        }

        return Stream.concat(Stream.of(squad.getLastCell()),
                Stream.of(getAcceptableCells().toArray()))
                .filter(cellAround -> {
                    if (actionEvaluation.testOwnerVsTargetCell.apply(squad, cellAround)) {
                        cellAround.selectionColor = getTargetCellSelectionColor(cellAround);
                        return true;
                    }
                    return false;
                }).collect(Collectors.toList());
    }
}
