package conversion7.game.stages.world.objects;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import conversion7.engine.Gdxg;
import conversion7.engine.customscene.ModelActor;
import conversion7.engine.customscene.SceneGroup3d;
import conversion7.engine.customscene.SceneNode3d;
import conversion7.engine.geometry.Point2s;
import conversion7.engine.pools.system.PoolManager;
import conversion7.engine.tween.Node3dAccessor;
import conversion7.engine.utils.MathUtils;
import conversion7.engine.utils.Utils;
import conversion7.game.GdxgConstants;
import conversion7.game.stages.StageObject;
import conversion7.game.stages.battle.Battle;
import conversion7.game.stages.world.Area;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.WorldThreadLocalSort;
import conversion7.game.stages.world.ai.ArtificialIntelligence;
import conversion7.game.stages.world.ai.tasks.single.AbstractAreaObjectTaskSingle;
import conversion7.game.stages.world.inventory.CraftInventory;
import conversion7.game.stages.world.inventory.MainInventory;
import conversion7.game.stages.world.inventory.MilitaryInventory;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.actions.AbstractAreaObjectAction;
import conversion7.game.stages.world.objects.actions.ArmyOverviewAction;
import conversion7.game.stages.world.objects.actions.OpenInventoryAction;
import conversion7.game.stages.world.objects.actions.SplitMergeUnitsAction;
import conversion7.game.stages.world.objects.controllers.ActionsController;
import conversion7.game.stages.world.objects.controllers.AreaObjectEffectsController;
import conversion7.game.stages.world.objects.controllers.InventoryController;
import conversion7.game.stages.world.objects.controllers.ReadyRangeUnitsController;
import conversion7.game.stages.world.objects.controllers.StepEndController;
import conversion7.game.stages.world.objects.controllers.UnitsController;
import conversion7.game.stages.world.objects.effects.AbstractObjectEffect;
import conversion7.game.stages.world.objects.effects.IncreaseBattleParametersEffect;
import conversion7.game.stages.world.objects.effects.IncreaseFertilizingChanceEffect;
import conversion7.game.stages.world.objects.effects.IncreaseViewRadiusEffect;
import conversion7.game.stages.world.objects.effects.ScareBeastEffect;
import conversion7.game.stages.world.team.Team;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.stages.world.unit.effects.items.Cold;
import conversion7.game.stages.world.unit.effects.items.Healing;
import conversion7.game.stages.world.unit.effects.items.Hunger;
import conversion7.game.stages.world.unit.effects.items.Thirst;
import conversion7.game.stages.world.view.AreaViewer;
import conversion7.game.ui.UiLogger;
import conversion7.game.utils.collections.Comparators;
import conversion7.game.utils.collections.IterationRegistrators;
import org.slf4j.Logger;
import org.testng.Assert;

import java.lang.reflect.InvocationTargetException;

import static java.lang.String.format;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public abstract class AreaObject extends StageObject implements ArtificialIntelligence {

    private static final Logger LOG = Utils.getLoggerForClass();
    private static final int BASE_VIEW_RADIUS = 3;
    public static final int UNITS_AMOUNT_LIMIT = 50;

    private Team team;
    protected Array<Unit> units = PoolManager.ARRAYS_POOL.obtain();
    private Cell cell;
    private Cell removedOnCell;

    private ModelActor modelActor;
    protected SceneGroup3d sceneGroup;

    private AbstractAreaObjectTaskSingle activeTask = null;
    protected Array<AbstractAreaObjectTaskSingle> tasksSet = new Array<>(false, 4);
    protected Array<AbstractAreaObjectTaskSingle> _proceededTasksSet = new Array<>();
    protected Array<String> _snapshotLog = new Array<>();
    protected Array<AbstractAreaObjectAction> actions = PoolManager.ARRAYS_POOL.obtain();

    private FoodStorage foodStorage;
    private boolean chasingCancelled = false;

    private Array<AbstractObjectEffect> effects = PoolManager.ARRAYS_POOL.obtain();
    private MainInventory mainInventory = new MainInventory(this);
    private MilitaryInventory militaryInventory = new MilitaryInventory(this);
    private CraftInventory craftInventory = new CraftInventory(this);

    private StepEndController stepEndController = new StepEndController(this);
    private UnitsController unitsController = new UnitsController(this);
    private InventoryController inventoryController = new InventoryController(this);
    private ActionsController actionsController = new ActionsController(this);
    private ReadyRangeUnitsController readyRangeUnitsController = new ReadyRangeUnitsController(this);
    private AreaObjectEffectsController areaObjectEffectsController = new AreaObjectEffectsController(this);
    private boolean validateEnabled = true;

    public AreaObject(Cell cell, Team team) {
        super();
        if (LOG.isDebugEnabled()) LOG.debug("< create AreaObject");

        this.team = team;
        foodStorage = new FoodStorage(this);

        if (!cell.hasLandscapeAvailableForMove()) {
            cell.getLandscapeController().regenerateLandscapeForAreaObject();
        }
        if (!cell.couldBeSeized()) {
            Utils.error(format("could not seize %s, it is seized by %s ", cell, cell.getSeizedBy()));
        }

        if (LOG.isDebugEnabled()) LOG.debug(" at cell " + cell);
        cell.getArea().addObject(this);
        seizeCell(cell);

        sceneGroup = new SceneGroup3d();
        sceneGroup.setName("sceneGroup " + toString());
        sceneGroup.frustrumRadius = 1f;
        buildModelActor();

        placeSceneNodeOnCell(sceneGroup);
        cell.getArea().getSceneGroup().addNode(sceneGroup);

        addSnapshotLog("placeObject " + cell);

        if (LOG.isDebugEnabled()) LOG.debug("> AreaObject created");
    }

    public Array<AbstractAreaObjectAction> getActions() {
        return actions;
    }

    private void placeSceneNodeOnCell(SceneNode3d sceneNode3d) {
        Assert.assertNull(sceneNode3d.getParent());
        sceneNode3d.setPosition(cell.x + 0.5f - Area.WIDTH_IN_CELLS / 2f,
                cell.getLandscape().getTerrainVertexData().getHeight(),
                -(cell.y + 0.5f) + Area.HEIGHT_IN_CELLS / 2f);
    }

    public AreaObjectEffectsController getAreaObjectEffectsController() {
        return areaObjectEffectsController;
    }

    public ReadyRangeUnitsController getReadyRangeUnitsController() {
        return readyRangeUnitsController;
    }

    public ActionsController getActionsController() {
        return actionsController;
    }

    public CraftInventory getCraftInventory() {
        return craftInventory;
    }

    public InventoryController getInventoryController() {
        return inventoryController;
    }

    public MilitaryInventory getMilitaryInventory() {
        return militaryInventory;
    }

    public MainInventory getMainInventory() {
        return mainInventory;
    }

    public void setModelActor(ModelActor modelActor) {
        this.modelActor = modelActor;
    }

    public void setCell(Cell cell) {
        this.cell = cell;
    }

    public Array<AbstractObjectEffect> getEffects() {
        return effects;
    }

    public UnitsController getUnitsController() {
        return unitsController;
    }

    public StepEndController getStepEndController() {
        return stepEndController;
    }

    public Team getTeam() {
        return team;
    }

    public Array<Unit> getUnits() {
        return units;
    }

    public Cell getCell() {
        return cell;
    }

    public ModelActor getModelActor() {
        return modelActor;
    }

    public SceneGroup3d getSceneGroup() {
        return sceneGroup;
    }

    public AbstractAreaObjectTaskSingle getActiveTask() {
        return activeTask;
    }

    public Area getArea() {
        return cell.getArea();
    }

    public FoodStorage getFoodStorage() {
        return foodStorage;
    }

    public int getViewRadius() {
        if (hasEffect(IncreaseViewRadiusEffect.class)) {
            return BASE_VIEW_RADIUS + 1;
        } else {
            return BASE_VIEW_RADIUS;
        }
    }

    public Array<Cell> getVisibleCellsAroundOnly() {
        return cell.getCellsAroundToRadiusInclusively(getViewRadius());
    }

    public Array<Cell> getVisibleCellsAndMe() {
        Array<Cell> cells = getVisibleCellsAroundOnly();
        cells.add(cell);
        return cells;
    }

    public float getPower() {
        float powerSum = 0;
        IterationRegistrators.UNITS_ITERATION_REGISTRATOR.start();
        for (int i = 0; i < units.size; i++) {
            Unit unit = units.get(i);
            powerSum += unit.getCalculatedPower();
        }
        IterationRegistrators.UNITS_ITERATION_REGISTRATOR.end();
        return powerSum;
    }

    public float getPowerWithNeighborSupport() {
        float totalPower = getPower();
        for (Cell neighborCell : cell.getNeighborCells()) {
            if (neighborCell.isSeized() && neighborCell.isSeizedByTeam(team)) {
                totalPower += neighborCell.getSeizedBy().getPower();
            }
        }
        return totalPower;
    }

    public int getAvailableFood() {
        return cell.getFood() + foodStorage.getFood();
    }

    public static float getObjectsPower(Array<? extends AreaObject> objects) {
        float totalPower = 0;
        for (AreaObject object : objects) {
            totalPower += object.getPower();
        }
        return totalPower;
    }

    @Override
    public String getHint() {
        StringBuilder sb = new StringBuilder(getName()).append(" ")
                .append("id: ").append(getId()).append(GdxgConstants.HINT_SPLITTER)
                .append("team: ").append(team.getTeamId()).append(GdxgConstants.HINT_SPLITTER)
                .append(cell).append(GdxgConstants.HINT_SPLITTER);
        return sb.toString();
    }

    public void decreaseActionPoints() {
        IterationRegistrators.UNITS_ITERATION_REGISTRATOR.start();
        for (int i = 0; i < units.size; i++) {
            Unit unit = units.get(i);
            unit.updateActionPoints(-1);
        }
        IterationRegistrators.UNITS_ITERATION_REGISTRATOR.end();
    }

    public int getAmountUnitsWithoutActionPoints() {
        int amount = 0;
        IterationRegistrators.UNITS_ITERATION_REGISTRATOR.start();
        for (int i = 0; i < units.size; i++) {
            Unit unit = units.get(i);
            if (unit.getActionPoints() <= 0) {
                amount++;
            }
        }
        IterationRegistrators.UNITS_ITERATION_REGISTRATOR.end();
        return amount;
    }

    public void resetActionPoints() {
        IterationRegistrators.UNITS_ITERATION_REGISTRATOR.start();
        for (int i = 0; i < units.size; i++) {
            Unit unit = units.get(i);
            unit.resetActionPoints();
        }
        IterationRegistrators.UNITS_ITERATION_REGISTRATOR.end();
    }

    public boolean couldMove() {
        return GdxgConstants.DEVELOPER_MODE || getAmountUnitsWithoutActionPoints() == 0;
    }

    public boolean couldPartiallyMove() {
        return GdxgConstants.DEVELOPER_MODE || getAmountUnitsWithoutActionPoints() < units.size;
    }

    @Override
    public String toString() {
        return getHint();
    }

    protected void initActions() {
        actions.add(new ArmyOverviewAction(this));
        actions.add(new SplitMergeUnitsAction(this));
        actions.add(new OpenInventoryAction(this));
    }

    public void validateActions() {

    }

    public AbstractAreaObjectAction getAction(Class<? extends AbstractAreaObjectAction> actionClass) {
        for (AbstractAreaObjectAction action : actions) {
            if (action.getClass().equals(actionClass)) {
                return action;
            }
        }
        return null;
    }

    public void addActionIfAbsent(Class<? extends AbstractAreaObjectAction> actionClass) {
        AbstractAreaObjectAction action = getAction(actionClass);
        if (action == null) {
            try {
                action = actionClass.getConstructor(AreaObject.class).newInstance(this);
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                throw new GdxRuntimeException(e.getMessage() + " \nduring creation new action of class: " + actionClass, e);
            }
            actions.add(action);
        }
    }

    public void removeActionIfExist(Class<? extends AbstractAreaObjectAction> actionClass) {
        AbstractAreaObjectAction action = getAction(actionClass);
        if (action != null) {
            actions.removeValue(action, true);
        }
    }

    public AbstractObjectEffect getEffect(Class<? extends AbstractObjectEffect> effectClass) {
        for (AbstractObjectEffect effect : effects) {
            if (effectClass.equals(effect.getClass())) {
                return effect;
            }
        }
        return null;
    }

    public boolean hasEffect(Class<? extends AbstractObjectEffect> effectClass) {
        return getEffect(effectClass) != null;
    }

    public void addEffectIfAbsentOtherwiseProlong(Class<? extends AbstractObjectEffect> effectClass) {
        AbstractObjectEffect effect = getEffect(effectClass);
        if (effect == null) {
            if (effectClass.equals(IncreaseBattleParametersEffect.class)) {
                effect = new IncreaseBattleParametersEffect(this);
            } else if (effectClass.equals(IncreaseFertilizingChanceEffect.class)) {
                effect = new IncreaseFertilizingChanceEffect(this);
            } else if (effectClass.equals(IncreaseViewRadiusEffect.class)) {
                effect = new IncreaseViewRadiusEffect(this);
            } else if (effectClass.equals(ScareBeastEffect.class)) {
                effect = new ScareBeastEffect(this);
            } else {
                Utils.error("Unsupported effectClass: " + effectClass);
            }
            effects.add(effect);
        } else {
            effect.prolong();
        }
    }

    public void validateEffects() {
        IterationRegistrators.UNITS_ITERATION_REGISTRATOR.assertNotStarted();
        WorldThreadLocalSort.instance().sort(units, Comparators.UNIT_FOOD_WATER_PRIORITY_COMPARATOR);
        calculateTemperatureHungerThirstEffects();
    }

    public void recalculateDueToChangedCell() {
        foodStorage.trimCollectedFoodByUnitsLimit();
        calculateTemperatureHungerThirstEffects();
        inventoryController.setLeftInventoryActive();
    }

    private void calculateTemperatureHungerThirstEffects() {
        int availableFood = getAvailableFood();
        IterationRegistrators.UNITS_ITERATION_REGISTRATOR.start();
        for (int curUnitIndex = 0; curUnitIndex < units.size; curUnitIndex++) {
            Unit unit = units.get(curUnitIndex);

            if (unit.getTemperature() + unit.getTemperatureStepBalance() < Unit.HEALTHY_TEMPERATURE_MIN - unit.getEquipment().getHeat()) {
                if (!unit.getEffectManager().containsEffect(Cold.class)) {
                    unit.getEffectManager().addEffect(new Cold());
                }
            } else {
                unit.getEffectManager().removeEffect(Cold.class);
            }

            if (availableFood > curUnitIndex) {
                unit.getEffectManager().removeEffect(Hunger.class);
            } else {
                if (unit.getFood() < 2) {
                    if (!unit.getEffectManager().containsEffect(Hunger.class)) {
                        unit.getEffectManager().addEffect(new Hunger());
                    }
                } else {
                    unit.getEffectManager().removeEffect(Hunger.class);
                }
            }

            if (cell.getWater() > curUnitIndex) {
                unit.getEffectManager().removeEffect(Thirst.class);
            } else {
                if (unit.getWater() < 2) {
                    if (!unit.getEffectManager().containsEffect(Thirst.class)) {
                        unit.getEffectManager().addEffect(new Thirst());
                    }
                } else {
                    unit.getEffectManager().removeEffect(Thirst.class);
                }
            }


            if (unit.hasPerfectConditions()) {
                if (!unit.getEffectManager().containsEffect(Healing.class)) {
                    unit.getEffectManager().addEffect(new Healing());
                }
            } else {
                unit.getEffectManager().removeEffect(Healing.class);
            }
        }
        IterationRegistrators.UNITS_ITERATION_REGISTRATOR.end();
    }

    private boolean isVisibleOnView() {
        return this.cell.isVisibleOnView();
    }

    public void moveOn(Cell targetCell) {
        if (LOG.isDebugEnabled()) LOG.debug("moveOn " + targetCell);

        Cell oldCell = this.cell;
        seizeCell(targetCell);
        decreaseActionPoints();

        // move to new area
        if (!oldCell.getArea().equals(targetCell.getArea())) {
            sceneGroup.removeFromParent();
            cell.getArea().getSceneGroup().addNode(sceneGroup);

            Point2s areaDiff = oldCell.getArea().diffWithArea(targetCell.getArea());
            sceneGroup.translate(-areaDiff.x * Area.WIDTH_IN_CELLS,
                    0,
                    areaDiff.y * Area.HEIGHT_IN_CELLS);

            // not visual
            oldCell.getArea().removeObject(this);
            targetCell.getArea().addObject(this);
        }

        recalculateDueToChangedCell();
        Tween.to(sceneGroup, Node3dAccessor.POSITION_XYZ, 1f)
                .target(targetCell.x + 0.5f - Area.WIDTH_IN_CELLS_HALF,
                        getCell().getLandscape().getTerrainVertexData().getHeight(),
                        -targetCell.y - 0.5f + Area.HEIGHT_IN_CELLS_HALF)
                .setCallback(new MoveCompletedCallback(oldCell, this))
                .setCallbackTriggers(TweenCallback.COMPLETE)
                .start(Gdxg.tweenManager);
    }


    public void seizeCell(Cell newCell) {
        if (this.cell != null) {
            this.cell.free();
        }
        newCell.seizeBy(this);
    }


    public boolean isNeighborOf(AreaObject target) {
        return cell.isNeighborOf(target.cell);
    }

    public void attack(AreaObject target) {
        LOG.info("attack initiator: " + this + "; target: " + target);
        if (target.isTownFragment() && target.units.size == 0) {
            Cell townCell = target.cell;
            target.defeat();
            moveOn(townCell);
            return;
        }

        boolean autoCalculated = true;
        if (GdxgConstants.AUTO_BATTLE_FOR_PLAYER) {
            autoCalculated = true;
        } else if (target.team.isHumanPlayer() || this.team.isHumanPlayer()) {
            autoCalculated = false;
        } else if (Battle.SUPPORT_MASSIVE_BATTLES) {
            for (Cell neighborCell : target.cell.getNeighborCells()) {
                if (neighborCell.isSeized() && neighborCell.getSeizedBy().team.isHumanPlayer()) {
                    autoCalculated = false;
                    break;
                }
            }
        }
        World.startBattle(this, target, autoCalculated);
    }

    public boolean isTownFragment() {
        return this.getClass().equals(TownFragment.class);
    }

    public boolean isSquad() {
        return this instanceof AbstractSquad;
    }

    public boolean isHumanSquad() {
        return this.getClass().equals(HumanSquad.class);
    }

    public boolean isAnimalHerd() {
        return this.getClass().equals(AnimalHerd.class);
    }

    public boolean isRemovedFromWorld() {
        return cell == null;
    }

    public void removeFromWorld() {
        getInventoryController().moveAllItemsToCellInventory();
        getArea().removeObject(this);
        removedOnCell = cell;
        cell.free();
        if (World.getAreaViewer().selectedObject == this) {
            World.getAreaViewer().selectedObject = null;
            AreaViewer.selectionGroup.setVisible(true);
        }
        assertTrue(sceneGroup.removeFromParent());
        addSnapshotLog("completeRemoveFromWorld");
        addSnapshotLog("removeFromWorld");
        validateView();
    }

    public void defeat() {
        LOG.info("defeated: " + this);
        removeFromWorld();
        team.validateIsDefeated();
    }

    public boolean validateAndDefeat() {
        if (couldBeDefeated()) {
            defeat();
            return true;
        } else {
            validate();
            return false;
        }
    }

    public void setValidateEnabled(boolean enabled) {
        this.validateEnabled = enabled;
    }

    /**
     * Goals are: <br>
     * - collect all validate calls for different components here;<br>
     * - avoid validate call inside another validate call if both calls are about AreaObject class. <br>
     * Use invalidate inside validate and call AreaObject#validate on the highest possible level of logic.
     */
    public void validate() {
        if (validateEnabled) {
            LOG.info("validate " + this);
            unitsController.validate();
            inventoryController.validate();
            areaObjectEffectsController.validate();
            readyRangeUnitsController.validate();
        } else {
            if (LOG.isDebugEnabled()) LOG.debug("validateEnabled: " + validateEnabled);
        }
    }

    public boolean couldBeDefeated() {
        return units.size == 0 && isSquad();
    }

    public boolean hasEnoughFood() {
        return cell.hasEnoughFoodFor(this);
    }

    public boolean hasEnoughWater() {
        return cell.hasEnoughWaterFor(this);
    }

    public void setCurrentObjectTask(AbstractAreaObjectTaskSingle currentObjectTask) {
        this.activeTask = currentObjectTask;
        if (LOG.isDebugEnabled()) LOG.debug("new task set: " + currentObjectTask);
    }

    public void addTaskToWipSet(AbstractAreaObjectTaskSingle objectTask) {
        tasksSet.add(objectTask);
    }

    public void executeTask() {
        if (isRemovedFromWorld()) {
            return;
        }
        if (activeTask != null) {
            activeTask.execute();
        }
    }

    public abstract void buildModelActor();

    @Override
    public void ai() {
        applyNewTaskFromWipSet();
        customObjectAi();
    }

    public abstract void customObjectAi();

    protected void applyNewTaskFromWipSet() {
        AbstractAreaObjectTaskSingle theHighestPriorityNewTask = proceedTaskQueueAndGetTheHighestPriorityTask();
        if (theHighestPriorityNewTask != null) { // got new tasks
            if (activeTask == null) {
                // and has no task
                setCurrentObjectTask(theHighestPriorityNewTask);
            } else if (activeTask.priority > theHighestPriorityNewTask.priority) {
                // and has task, but new one has more priority
                activeTask.cancel();
                setCurrentObjectTask(theHighestPriorityNewTask);
            }
        }
    }

    private AbstractAreaObjectTaskSingle proceedTaskQueueAndGetTheHighestPriorityTask() {
        if (tasksSet.size > 0) {
            WorldThreadLocalSort.instance().sort(tasksSet, AbstractAreaObjectTaskSingle.TASK_PRIORITY);
            AbstractAreaObjectTaskSingle theHighestTask = tasksSet.get(0);
//            _proceededTasksSet.addAll(tasksSet);
            tasksSet.clear();
            return theHighestTask;
        } else {
            return null;
        }
    }

    public void setChasingCancelled(boolean b) {
        this.chasingCancelled = b;
    }

    public boolean isChasingCancelled() {
        return chasingCancelled;
    }

    public void mergeMeInto(AreaObject mergeInto) {
        mergeInto.unitsController.addUnitsAndValidate(units);
        mergeInto.inventoryController.mergeInventoriesFrom(this);
        removeFromWorld();
    }

    public void addSnapshotLog() {
        addSnapshotLog("empty");
    }

    public void addSnapshotLog(String logName) {
        StringBuilder stringBuilder = new StringBuilder("[").append(logName).append("] ")
                .append("units.size=").append(units.size);
        _snapshotLog.add(stringBuilder.toString());
    }

    public void validateView() {
        team.recalculateVisibleCells();
        if (World.getAreaViewer() != null) {
            if (isRemovedFromWorld()) {
                assertNotNull(removedOnCell);
                World.getAreaViewer().addCellToBeRefreshedInView(removedOnCell);
                World.getAreaViewer().addCellsToBeRefreshedInView(removedOnCell.getCellsAroundToRadiusInclusively(getViewRadius()));
            } else {
                World.getAreaViewer().addCellsToBeRefreshedInView(getVisibleCellsAndMe());
            }
            World.getAreaViewer().refreshView();
        }
    }

    public void validateBodyInView() {
        if (cell.getDiscovered() == null || cell.getDiscovered().equals(Cell.Discovered.NOT_VISIBLE)) {
            sceneGroup.setVisible(false);
        } else {
            sceneGroup.setVisible(true);
        }
    }

    public void updateMoral(int onValue) {
        UiLogger.addInfoLabel("Moral " + MathUtils.formatNumber(onValue));
        IterationRegistrators.UNITS_ITERATION_REGISTRATOR.start();
        for (int i = 0; i < units.size; i++) {
            Unit unit = units.get(i);
            unit.getParams().updateMoral(onValue);
        }
        IterationRegistrators.UNITS_ITERATION_REGISTRATOR.end();
    }

    public boolean areUnitsWithHealthyTemperature() {
        IterationRegistrators.UNITS_ITERATION_REGISTRATOR.start();
        boolean healthy = true;
        for (int i = 0; i < units.size; i++) {
            Unit unit = units.get(i);
            if (!unit.hasHealthyTemperature()) {
                healthy = false;
                break;
            }
        }
        IterationRegistrators.UNITS_ITERATION_REGISTRATOR.end();
        return healthy;
    }

    /**
     * Use this method in way: I want compare my power to other's power, <br>
     * because in such way AnimalHerd will be correctly compared with HumanSquad + ScareBeastEffect<br>
     * AND HumanSquad with ScareBeastEffect will not attack AnimalHerd which actually could be more powerful...
     */
    public boolean hasMorePowerThan(AreaObject otherObject) {
        return getPower() > otherObject.getPower();
    }

    public void validateReadyRangeUnits() {
        throw new GdxRuntimeException("Not supported!");
    }

    public abstract boolean couldJoinToTeam(AreaObject targetToBeJoined);

    public void setTeam(Team team) {
        this.team = team;
    }

    private class MoveCompletedCallback implements TweenCallback {

        private Cell oldCell;
        private AreaObject areaObject;

        public MoveCompletedCallback(Cell oldCell, AreaObject areaObject) {
            this.oldCell = oldCell;
            this.areaObject = areaObject;
        }

        @Override
        public void onEvent(int i, BaseTween<?> baseTween) {
            World.getAreaViewer().addCellsToBeRefreshedInView(oldCell.getCellsAroundToRadiusInclusively(getViewRadius()));
            World.getAreaViewer().addCellToBeRefreshedInView(oldCell);

            areaObject.validateView();
        }
    }
}
