package conversion7.game.stages.battle.calculation;

import com.badlogic.gdx.utils.Array;
import conversion7.engine.pools.system.PoolManager;
import conversion7.engine.utils.Utils;
import conversion7.game.interfaces.Progressive;
import conversion7.game.stages.battle.Battle;
import conversion7.game.stages.battle.BattleFigure;
import conversion7.game.stages.battle.FigureStepParamSpeedComparator;
import org.slf4j.Logger;

import java.awt.*;

import static java.lang.String.format;
import static org.fest.assertions.api.Assertions.assertThat;

public class Step implements Progressive {

    private static final Logger LOG = Utils.getLoggerForClass();

    private static int versionCounter = 0;
    public int version = -1;
    public int round = -1;

    public boolean targetFound;
    public boolean noActiveFigures = false;

    public float progress = 0;
    public boolean completed = false;
    public boolean halfCompleted = false;

    Step initialStep = null;

    Cell[][] cells;
    public Array<FigureStepParams> figuresParamsList = PoolManager.ARRAYS_POOL.obtain();
    public State calculationState = State.DEFINED;
    public Battle battle;

    /**
     * Initial step for battle
     */
    public Step(Battle battle) {
        LOG.info("create initial step for round");
        this.battle = battle;
        init();
        LOG.info("step created " + this);
    }

    /**
     * Step based on existing step (next steps)
     */
    public Step(Step previousStep) {
        if (LOG.isDebugEnabled()) LOG.debug("< create step based on existing " + previousStep);
        assertThat(previousStep).isNotNull();
        assertThat(previousStep.noActiveFigures)
                .as("Attempt to create new step based on step without active figures").isFalse();

        previousStep.nextStep = this;
        this.battle = previousStep.battle;
        this.round = previousStep.round;

        init();

        this.initialStep = previousStep;

        // figuresParamsList
        Array<FigureStepParams> initialFiguresParams = PoolManager.ARRAYS_POOL.obtain();
        initialFiguresParams.addAll(previousStep.figuresParamsList);

        for (FigureStepParams prevStepFigureParams : initialFiguresParams) {
            if (!prevStepFigureParams.killed) {
                figuresParamsList.add(new FigureStepParams(prevStepFigureParams, this));
            }
        }

        if (LOG.isDebugEnabled()) LOG.debug(" > step created ");
    }

    private void init() {
        if (LOG.isDebugEnabled()) LOG.debug("...init started");
        version = Step.versionCounter++;
        if (LOG.isDebugEnabled()) LOG.debug(format("version = %d; round = %d", version, round));

        cells = new Cell[battle.getTotalWidth()][battle.getTotalHeight()];
        // map
        for (int x = 0; x < battle.getTotalWidth(); x++) {
            for (int y = 0; y < battle.getTotalHeight(); y++) {
                cells[x][y] = new Cell(x, y, this);
            }
        }
    }

    @Override
    public String toString() {
        return "STEP [version = " + version + ";" +
                " round = " + round + ";" +
                " progress = " + progress + ";" +
                " noActiveFigures = " + noActiveFigures +
                "]";
    }

    public Cell getCell(Point point) {
        return getCell(point.x, point.y);
    }


    public Cell getCell(int x, int y) {
        if (x >= 0 && x < battle.getTotalWidth() && y >= 0 && y < battle.getTotalHeight()) return cells[x][y];
        else return null;
    }

    public boolean hasFigureAssigned(BattleFigure battleFigure) {
        for (FigureStepParams figureStepParams : figuresParamsList) {
            if (figureStepParams.battleFigure.equals(battleFigure)) {
                return true;
            }
        }
        return false;
    }

    //** Is used for 1st step in round*/
    public void addFigure(BattleFigure battleFigure, Cell cell) {
        if (hasFigureAssigned(battleFigure)) {
            Utils.error("such figure has been already assigned: " + battleFigure);
        }
        figuresParamsList.add(battleFigure.params);
        battleFigure.params.step = this;

        if (cell.isSeized()) {
            throw new RuntimeException("Impossible place " + battleFigure + " at " + cell + " because another figure exists there: " + cell.seizedBy.battleFigure + "!");
        } else {
            if (battleFigure.params.cell != null) {
                battleFigure.params.cell.free();
            }
            cell.seize(battleFigure.params);
            battleFigure.savedMirrorPosition = battle.armyPlaceArea.
                    getMirrorPositionByBattleFieldPosition(battleFigure.params.cell, battleFigure.getBattleSide());
        }
    }

    public void removeFigure(BattleFigure battleFigure) {
        if (!figuresParamsList.removeValue(battleFigure.params, false)) {
            Utils.error("such figure was not assigned: " + battleFigure);
        }
        battleFigure.params.step = null;
        battleFigure.params.cell.free();
    }

    //
    // CALCULATION PART
    //

    public void calculateActions() {

        if (LOG.isDebugEnabled()) LOG.debug("< calculateActions");
        calculationState = State.IN_PROGRESS;

        // temp speed randomization
        for (int i = 0; i < figuresParamsList.size; i++) {
            figuresParamsList.get(i).speed = Utils.RANDOM.nextInt(100);
        }

        FigureStepParamSpeedComparator.sort(figuresParamsList);

        int activeFigures = 0;
        for (FigureStepParams fgp : figuresParamsList) {
            if (fgp.killed) {
                if (LOG.isDebugEnabled()) LOG.debug(" will-be-killed figure will act: " + fgp);
            }
            activeFigures += BattleAi.calculateFigureAction(fgp);
        }

        if (activeFigures == 0) {
            noActiveFigures = true;
            if (LOG.isDebugEnabled()) LOG.debug("> calculateActions - no active figuresParamsList " + this);
        } else {
            if (LOG.isDebugEnabled()) LOG.debug("> calculateActions " + this);
        }

        calculationState = State.COMPLETED;
    }

    private Step nextStep;


    @Override
    public void act(float delta) {
        Utils.error("is not supported");
    }

    //
    // VISUAL PART
    //

    @Override
    public void start() {
        if (LOG.isDebugEnabled()) LOG.debug("< start " + this);
        for (FigureStepParams fsParams : this.figuresParamsList) {
            fsParams.battleFigure.start();
        }
        if (LOG.isDebugEnabled()) LOG.debug("> start step");
    }

    @Override
    public void completeHalf() {
        if (LOG.isDebugEnabled()) LOG.debug("< completeHalf " + this);

        this.halfCompleted = true;

        for (FigureStepParams fgp : this.figuresParamsList) {
            fgp.battleFigure.completeHalf();
        }
        if (LOG.isDebugEnabled()) LOG.debug("> completeHalf step");
    }

    @Override
    public void complete() {
        completed = true;

        if (LOG.isDebugEnabled()) LOG.debug("< complete: " + this);

        for (FigureStepParams fgp : this.figuresParamsList) {
            fgp.battleFigure.complete();
        }

        switchFiguresToNextStep();

        if (LOG.isDebugEnabled()) LOG.debug("> complete step");
    }

    private void switchFiguresToNextStep() {
        for (FigureStepParams nextStepFigureParams : nextStep.figuresParamsList) {
            nextStepFigureParams.battleFigure.params = nextStepFigureParams;
        }
    }

    public boolean isLastStep() {
        return noActiveFigures;
    }
}