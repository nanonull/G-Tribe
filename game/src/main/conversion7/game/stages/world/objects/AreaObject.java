package conversion7.game.stages.world.objects;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;
import conversion7.engine.Gdxg;
import conversion7.engine.artemis.AnimationSystem;
import conversion7.engine.artemis.ui.UnitInWorldHintPanelsSystem;
import conversion7.engine.artemis.ui.float_lbl.UnitFloatingStatusBatch;
import conversion7.engine.customscene.SceneGroup3d;
import conversion7.engine.geometry.Point2s;
import conversion7.engine.pools.system.PoolManager;
import conversion7.engine.tween.Node3dAccessor;
import conversion7.engine.utils.MathUtils;
import conversion7.engine.utils.Utils;
import conversion7.engine.validators.NodeValidator;
import conversion7.game.GameError;
import conversion7.game.GdxgConstants;
import conversion7.game.stages.StageObject;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.area.Area;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.actions.AbstractAreaObjectAction;
import conversion7.game.stages.world.objects.buildings.BuildingObject;
import conversion7.game.stages.world.objects.buildings.Camp;
import conversion7.game.stages.world.objects.composite.CompositeAreaObject;
import conversion7.game.stages.world.objects.controllers.ActionsController;
import conversion7.game.stages.world.objects.controllers.UnitParametersValidator;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.objects.unit.AnimalHerd;
import conversion7.game.stages.world.objects.unit.WorldSquad;
import conversion7.game.stages.world.team.Team;
import conversion7.game.stages.world.team.TribeRelationType;
import conversion7.game.stages.world.unit.Power2;
import conversion7.game.stages.world.unit.effects.items.DiscordEffect;
import conversion7.game.stages.world.unit.effects.items.SleptEffect;
import conversion7.game.stages.world.view.AreaViewer;
import conversion7.game.stages.world.view.AreaViewerAnimationsHelper;
import conversion7.game.ui.ClientUi;
import conversion7.game.ui.world.UnitInWorldHintPanel;
import conversion7.game.unit_classes.humans.BaseHumanClass;
import org.slf4j.Logger;

import java.util.Iterator;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

public abstract class AreaObject extends StageObject {

    private static final Logger LOG = Utils.getLoggerForClass();
    public final AreaObject thiz;
    public SceneGroup3d sceneBody;
    public Team team;
    public Cell cell;
    public Array<AreaObject> visibleObjects = PoolManager.ARRAYS_POOL.obtain();
    /** It's possible that this will not know about another sees it... */
    public Array<AreaObject> visibleForObjects = PoolManager.ARRAYS_POOL.obtain();
    public long validatedOnFrame;
    public Power2 power;
    public boolean destroyed = false;
    public UnitFloatingStatusBatch batchFloatingStatusLines = new UnitFloatingStatusBatch(this);
    public UnitInWorldHintPanel unitInWorldHintPanel;
    public boolean initialized;
    public AreaObject killedBy;
    public AreaObject createdBy;
    public boolean removedTemporary;
    protected Cell previousCell;
    protected Cell removedOnCell;
    protected Array<Cell> visibleCellsAround = new Array<>();
    protected Array<Cell> visibleCellsWithMyCell = new Array<>();
    protected NodeValidator rootValidator;
    protected ActionsController actionsController;
    protected UnitParametersValidator unitParametersValidator;
    protected Direction direction;
    private Array<String> _snapshotLog = new Array<>();
    private OrderedMap<Class<? extends AbstractAreaObjectAction>, AbstractAreaObjectAction> actions = new OrderedMap<>();
    private Array<Consumer<AreaObject>> deathListeners = new Array<>();
    private CompositeAreaObject parentCompositeObject;

    public AreaObject(Cell cell, Team team) {
        super();
        thiz = this;
        this.cell = cell;
        this.team = team;
    }

    public static <C extends AreaObject> C create(Cell cell, AreaObject owner, Class<C> objClass) {
        C c = create(cell, owner.team, objClass);
        c.createdBy = owner;
        return c;
    }

    public static <C extends AreaObject> C create(Cell cell, Team team, Class<C> objClass) {
        try {
            AreaObject object = objClass.getDeclaredConstructor(Cell.class, Team.class).newInstance(cell, team);
            if (!object.initialized) {
                object.init();
                object.validateView();
            }
            return (C) object;
        } catch (Throwable e) {
            Gdxg.core.addError(e);
        }
        return null;
    }

    public Cell getRemovedOnCell() {
        return removedOnCell;
    }

    public boolean isCellMainSlotObject() {
        return false;
    }

    public UnitParametersValidator getUnitParametersValidator() {
        return unitParametersValidator;
    }

    @Deprecated
    public int getMaxHealth() {
        return power.getMaxValue();
    }

    @Deprecated
    public float getEquipPriority() {
        return 0;
    }

    /**
     * Goals are: <br>
     * - collect all validate calls for different components here;<br>
     * - avoid validate call inside another validate call. <br>
     * Use invalidate inside child#validate and call rootValidator#validate on the highest possible level of logic.
     */
    public NodeValidator getRootValidator() {
        return rootValidator;
    }

    public OrderedMap<Class<? extends AbstractAreaObjectAction>, AbstractAreaObjectAction> getActions() {

        return actions;
    }

    public ActionsController getActionsController() {
        return actionsController;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public CompositeAreaObject getParentCompositeObject() {
        return parentCompositeObject;
    }

    public void setParentCompositeObject(CompositeAreaObject parentCompositeObject) {
        this.parentCompositeObject = parentCompositeObject;
    }

    public Cell getPreviousCell() {
        return previousCell;
    }

    public Direction getDirection() {
        return direction;
    }

    public String getSnapshotLogLines() {
        StringBuilder builder = new StringBuilder();
        for (String line : _snapshotLog) {
            builder.append(line).append("\n");
        }
        return builder.toString();
    }

    public Array<Cell> getVisibleCellsAround() {
        return visibleCellsAround;
    }

    public Array<Cell> getVisibleCellsWithMyCell() {
        return visibleCellsWithMyCell;
    }

    public Cell getLastCell() {
        Cell res = cell == null ? removedOnCell : cell;
        if (res == null) {
            throw new GameError("getCell >> null").setObjectWithError(this);
        }
        return res;
    }

    private boolean isVisibleOnView() {
        return cell.isVisibleOnView();
    }

    public boolean isTownFragment() {
        return this.getClass().equals(Camp.class);
    }

    public boolean isSquad() {
        return this instanceof AbstractSquad;
    }

    public boolean isHuman() {
        return isHumanlike() && ((AbstractSquad) this).unit instanceof BaseHumanClass;
    }

    public boolean isHumanlike() {
        return isSquad();
    }

    public boolean isAnimal() {
        return
//                isSquad() ? toSquad().unit.isAnimal() :
                this instanceof AnimalHerd;
    }

    public Area getArea() {
        return getLastCell().getArea();
    }

    /** is same as AbstractSquad#isAlive() */
    public boolean isRemovedFromWorld() {
        return cell == null;
    }

    public String getFullName() {
        return getName();
    }

    public SceneGroup3d getSceneBody() {
        if (sceneBody == null) {
            buildSceneBody();
        }
        return sceneBody;
    }

    public String getShortHint() {
        if (hasPower()) {
            return getName()
                    + " " + power.getCurrentValue() + "/" + power.getMaxValue()
                    + (team == null ? "" : " " + team.getName());
        } else {
            return getName() + (team == null ? "" : " " + team.getName());
        }
    }

    public String getHint() {
        StringBuilder sb = new StringBuilder(getName()).append(" ")
                .append("removed=").append(isRemovedFromWorld()).append(GdxgConstants.HINT_SPLITTER)
                .append("id: ").append(getId()).append(GdxgConstants.HINT_SPLITTER)
                .append(getTeamHint())
                .append(getLastCell()).append(GdxgConstants.HINT_SPLITTER);
        return sb.toString();
    }

    private String getTeamHint() {
        if (team == null) {
            return "";
        }
        return "team: " + team.getTeamId() + GdxgConstants.HINT_SPLITTER;
    }

    public boolean isAlive() {
        return !destroyed || (hasPower() && power.isAlive());
    }

    public Array<AbstractSquad> getSquadsAround() {
        return getLastCell().getObjectsAround(AbstractSquad.class);
    }

    @Deprecated
    public float getBaseMaxPower() {
        return getMaxPower();
    }

    @Deprecated
    public float getBaseActualPower() {
        return getCurrentPower();
    }

    public int getMaxPower() {
        return power.getMaxValue();
    }

    public int getCurrentPower() {
        return power.getCurrentValue();
    }

    public String getName() {
        return name;
    }

    public boolean isUiInfoDisplayed() {
        return unitInWorldHintPanel != null;
    }

    public Team getKilledByTeam() {
        if (killedBy != null && killedBy.team != null) {
            return killedBy.team;
        }
        return null;
    }

    public int getViewRadius(boolean dayNightEffect) {
        if (team == null || !dayNightEffect) {
            return World.BASE_VIEW_RADIUS;
        }
        return team.world.getDayNightViewRadius();
    }

    public int getViewRadius() {
        return getViewRadius(true);
    }

    public boolean isBuilding() {
        return BuildingObject.class.isAssignableFrom(getClass());
    }

    public void refreshUiPanelInWorld() {
        if (shouldDisplayUiInfo()) {
            ClientUi.UNIT_IN_WORLD_PANELS_MAIN.addActor(unitInWorldHintPanel);
            UnitInWorldHintPanelsSystem.getOrCreate(this).updateMainPanelData = true;
        }
    }

    public boolean shouldDisplayUiInfo() {
        return isUiInfoDisplayed() && isAlive();
    }

    @Override
    public void init() {
        if (LOG.isDebugEnabled()) LOG.debug("< create AreaObject");
        name = buildName();
        rootValidator = new NodeValidator() {
            @Override
            public void validate() {
            }
        };
        actionsController = new ActionsController(this);
        rootValidator.registerChildValidator(actionsController);
        unitParametersValidator = new UnitParametersValidator(this);
        rootValidator.registerChildValidator(unitParametersValidator);

        seizeCell(cell);

        if (LOG.isDebugEnabled()) LOG.debug(" at cell " + cell);
        addSnapshotLog("placeObject " + cell, null);

        if (LOG.isDebugEnabled()) LOG.debug("> AreaObject created");


        initialized = true;
    }

    public boolean givesCornerDefenceBonus() {
        return false;
    }

    @Deprecated
    public boolean canFortify() {
        return false;
//        return actionPoints >= ActionPoints.FORTIFY
//                && effectManager.getEffectRaw(FortificationEffect.class) == null;
    }

    public Camp getCampAround(Team ofTeam) {
        Optional<Cell> first = Stream.of(getLastCell().getCellsAround().toArray()).filter(cell -> {
            return cell.hasCamp() && cell.camp.team == ofTeam;
        }).findFirst();
        return first.map(cell -> cell.camp).orElse(null);
    }

    public void moveBody(Cell oldCell, Cell targetCell) {
        SceneGroup3d sceneBody = getSceneBody();

        // move to new area
        if (!oldCell.getArea().equals(targetCell.getArea())) {
            sceneBody.removeFromParent();
            getLastCell().getArea().getSceneGroup().addNode(sceneBody);

            Point2s areaDiff = oldCell.getArea().diffWithArea(targetCell.getArea());
            sceneBody.translate(-areaDiff.x * Area.WIDTH_IN_CELLS,
                    0,
                    areaDiff.y * Area.HEIGHT_IN_CELLS);

            // not visual
            oldCell.getArea().removeSquad(this);
            targetCell.getArea().addSquad(this);
        }

        if (AreaViewerAnimationsHelper.shouldShowAnimation(oldCell, targetCell)) {
            AreaViewerAnimationsHelper.setAnimationStartedOn(this, true);
            Tween.to(sceneBody, Node3dAccessor.POSITION_XYZ, AnimationSystem.ANIM_DURATION)
                    .target(targetCell.x + 0.5f - Area.WIDTH_IN_CELLS_HALF,
                            getLastCell().getLandscape().getTerrainVertexData().getHeight(),
                            -targetCell.y - 0.5f + Area.HEIGHT_IN_CELLS_HALF)
                    .setCallback(new MoveCompletedCallback(oldCell, this))
                    .setCallbackTriggers(TweenCallback.COMPLETE)
                    .start(Gdxg.tweenManager);
        } else {
            sceneBody.setPosition(targetCell.x + 0.5f - Area.WIDTH_IN_CELLS_HALF,
                    getLastCell().getLandscape().getTerrainVertexData().getHeight(),
                    -targetCell.y - 0.5f + Area.HEIGHT_IN_CELLS_HALF);
            MoveCompletedCallback.refreshAfterMove(oldCell, this);
        }
    }

    protected void calculateObjectsAround() {
    }

    public void seizeCell(Cell newCell) {
        if (newCell == null) {
            previousCell = cell;
        }
        if (previousCell != null) {
            previousCell.removeFromObjectsOnCell(this);
        }
        previousCell = newCell;
        cell = newCell;
        if (previousCell == null) {
            previousCell = cell;
        }
        if (newCell != null) {
            if (!newCell.getObjectsOnCell().contains(this, true)) {
                newCell.addObject(this);
            }

            TrapObject trapObject = newCell.getObject(TrapObject.class);
            if (trapObject != null && isSquad() && trapObject.team.isEnemyOf(this.team)) {
                trapObject.act((AbstractSquad) this);
            }

            AnimalSpawn animalSpawn = newCell.getObject(AnimalSpawn.class);
            if (animalSpawn != null && isSquad()) {
                AbstractSquad squad = toSquad();
                animalSpawn.captureBy(squad);
                if (squad.team.visitedSpawns.add(animalSpawn)) {
                    squad.updateExperience(AnimalSpawn.CONTROL_EXP, "New spawn found exp");
                }
            }
            calculateObjectsAround();
        }
    }

    public void addSnapshotLog(String logMain, String logAdditional) {
        StringBuilder stringBuilder = new StringBuilder("[").append(logMain).append("] ").append(logAdditional);
        _snapshotLog.add(stringBuilder.toString());
    }

    @Deprecated
    protected void initActions() {
    }

    public boolean isNeighborOf(AreaObject target) {
        return getLastCell().isNeighborOf(target.getLastCell());
    }

    public void returnToWorld(Cell cell) {
        removedOnCell = null;
        seizeCell(cell);
        validateView();
        refreshUiPanelInWorld();
    }

    public void removeFromWorld() {
        if (removedOnCell == null) {
            removeObjectFromCell();
            validateView();
        }
    }

    public void removeObjectFromCell() {
        removedOnCell = cell;
        cell.removeFromObjectsOnCell(this);
//        if (!removedTemporary) {
        clearBody();
//        }
        addSnapshotLog("removeFromWorld", getClass().getSimpleName());
    }

    public void clearBody() {
        if (sceneBody != null) {
            sceneBody.removeFromParent();
            sceneBody = null;
        }
    }

    /** Call this after object is created at least */
    public void validateView() {
        getLastCell().setRefreshedInView(false);
        getLastCell().refreshViewer();
    }

    public void validateBodyInView() {
        Team playerTeam = Gdxg.core.world.lastActivePlayerTeam;
        if (GdxgConstants.DEVELOPER_MODE || playerTeam == null) {
            showBody();
            return;
        }

        if (getLastCell().getDiscovered() == null || getLastCell().getDiscovered().equals(Cell.Discovered.NOT_VISIBLE)) {
            hideBody();
        } else if (team != playerTeam && !playerTeam.canSeeObject(this)) {
            hideBody();
        } else {
            showBody();
        }
    }

    public void showBody() {
        getSceneBody().setVisible(true);
    }

    public void hideBody() {
        if (sceneBody != null) {
            sceneBody.setVisible(false);
        }
    }

    public SceneGroup3d buildSceneBody() {
        sceneBody = new SceneGroup3d();
        if (cell != null) {
            AreaViewer.placeBodyOnCell(cell, sceneBody);
            cell.getArea().getSceneGroup().addNode(sceneBody);
        }
        sceneBody.setName("AreaObject sceneBody " + getShortHint());
        sceneBody.frustrumRadius = 1f;
        return sceneBody;
    }

    @Override
    public String toString() {
        return getHint();
    }

    public void validate(boolean revalidateAll) {
        if (!isRemovedFromWorld()) {
            if (revalidateAll) {
                rootValidator.invalidateChildRecursive();
            }
            rootValidator.runTreeValidation();
            validatedOnFrame = Gdxg.core.frameId;
        }
    }

    public void disableValidations() {
        rootValidator.disableChildRecursive();

    }

    public void validate() {
        validate(false);
    }

    protected void clearVisibleAndVisibleBy() {
        // clear whom I saw
        Iterator<AreaObject> iteratorISee = visibleObjects.iterator();
        while (iteratorISee.hasNext()) {
            AreaObject iSawObj = iteratorISee.next();
            iteratorISee.remove();
            iSawObj.visibleForObjects.removeValue(this, true);
        }

        // clear who saw me
        Iterator<AreaObject> iteratorVisibleBy = visibleForObjects.iterator();
        while (iteratorVisibleBy.hasNext()) {
            AreaObject objSawMe = iteratorVisibleBy.next();
//            if (!squadSawMe.getVisibleCellsAround().contains(this.cell, true)) {
            objSawMe.visibleObjects.removeValue(this, true);
            iteratorVisibleBy.remove();
//            }
        }
    }

    public boolean hasPower() {
        return power != null;
    }

    /** Returns true if unit was killed by given damage - death could be triggered for this unit. */
    public boolean hurtBy(int damage, AreaObject hurtBy) {
        int wipDmg = damage;
        if (wipDmg < 0) {
            LOG.error("wipDmg < 0");
            wipDmg = 0;
        }

        tellTeammatesAroundAboutHurt(hurtBy);

        if (isSquad() && toSquad().getEffectManager().containsEffect(DiscordEffect.class)) {
            int newDmg = (int) (wipDmg * DiscordEffect.MLT);
            int add = newDmg - wipDmg;
            add = Math.max(1, add);
            wipDmg += add;
            batchFloatingStatusLines.addDebugLine(DiscordEffect.class.getSimpleName() + " dmg: +" + add);
        }

        if (wipDmg > 0) {
            power.setCurrentValue(power.getCurrentValue() - wipDmg);
            refreshUiPanelInWorld();
            batchFloatingStatusLines.addImportantLine(wipDmg + " dmg");
            batchFloatingStatusLines.flush(Color.ORANGE);
        }

        boolean defeated = checkDefeated();
        if (defeated) {
            killedBy = hurtBy;
        } else {
            tellAllTeammatesAboutHurt(hurtBy);
            if (isSquad()) {
                toSquad().effectManager.removeEffectIfExist(SleptEffect.class);
            }
        }
        return defeated;
    }

    private void tellAllTeammatesAboutHurt(AreaObject hurtBy) {
        if (hurtBy != null && hurtBy.team != null && this.team != null) {
            if (!checkDisabled()) {
                team.world.addRelationType(TribeRelationType.ATTACK, team, hurtBy.team);
            }
        }
    }

    private boolean checkDisabled() {
        boolean hurtNotify = true;
        if (isSquad() && toSquad().isDisabled()) {
            hurtNotify = false;
        }
        return hurtNotify;
    }

    private void tellTeammatesAroundAboutHurt(AreaObject hurtBy) {
        if (hurtBy == null || team == null || hurtBy.team == null) {
            return;
        }

        if (!checkDisabled()) {
            for (AbstractSquad visibleBySquad : cell.visibleBySquads) {
                if (visibleBySquad != this && visibleBySquad.team == this.team) {
                    team.world.addRelationType(TribeRelationType.ATTACK, team, hurtBy.team);
                }
            }
        }
    }

    protected boolean checkDefeated() {
        if (hasPower()) {
            boolean mortalWound = power.getCurrentValue() <= 0;
            if (mortalWound) {
                destroyObject();
            }
            return mortalWound;
        }
        return false;
    }

    public AbstractSquad toSquad() {
        return (AbstractSquad) this;
    }

    public void destroyObject() {
        LOG.info("destroyObject: {} ", thiz);
        if (isSquad()) {
            WorldSquad.killUnit(thiz.toSquad());
        } else {
            removeFromWorld();
        }

        for (Consumer<AreaObject> listener : deathListeners) {
            listener.accept(this);
        }
    }

    public void addDeathListener(Consumer<AreaObject> runnable) {
        deathListeners.add(runnable);
    }

    public boolean canDodge() {
        return false;
    }

    public boolean hasDirection() {
        return direction != null;
    }

    public abstract boolean givesExpOnHurt();

    @Deprecated
    public enum UnitSpecialization {
        MELEE, RANGE;

        public static UnitSpecialization getRandom() {
            return MathUtils.RANDOM.nextBoolean() ? UnitSpecialization.MELEE : UnitSpecialization.RANGE;
        }
    }

    public static class MoveCompletedCallback implements TweenCallback {

        private Cell oldCell;
        private AreaObject squad;

        public MoveCompletedCallback(Cell oldCell, AreaObject squad) {
            this.oldCell = oldCell;
            this.squad = squad;
        }

        public static void refreshAfterMove(Cell oldCell, AreaObject object) {
            AreaViewerAnimationsHelper.setAnimationCompletedOn(object);

            oldCell.setRefreshedInView(false);
            for (Cell cell : oldCell.getCellsAroundToRadiusInclusively(object.getViewRadius())) {
                cell.setRefreshedInView(false);
            }

            object.validateView();
        }

        @Override
        public void onEvent(int i, BaseTween<?> baseTween) {
            refreshAfterMove(oldCell, squad);
        }
    }
}
