package conversion7.game.stages.battle_deprecated;

import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectSet;
import conversion7.engine.Gdxg;
import conversion7.engine.customscene.CustomStage;
import conversion7.engine.customscene.DecalActor;
import conversion7.engine.customscene.DecalGroup;
import conversion7.engine.customscene.SceneGroup3d;
import conversion7.engine.customscene.input.CustomInputEvent;
import conversion7.engine.customscene.input.Scene3dInputListener;
import conversion7.engine.geometry.Drawer3d;
import conversion7.engine.geometry.Point2s;
import conversion7.engine.pools.system.PoolManager;
import conversion7.engine.utils.MathUtils;
import conversion7.engine.utils.Utils;
import conversion7.game.Assets;
import conversion7.game.GdxgConstants;
import conversion7.game.stages.GameStage;
import conversion7.game.stages.battle_deprecated.calculation.FigureStepParams;
import conversion7.game.stages.battle_deprecated.calculation.Round;
import conversion7.game.stages.world.inventory.items.SkinItem;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.team.Team;
import conversion7.game.stages.world.team.skills.SkillType;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.stages.world.unit.effects.UnitEffectManager;
import conversion7.game.stages.world.unit.effects.items.ForestDefenceEffect;
import conversion7.game.stages.world.unit.effects.items.FortificationEffect;
import conversion7.game.stages.world.unit.effects.items.HillDefenceEffect;
import conversion7.game.unit_classes.animals.BaseAnimalClass;
import conversion7.game.utils.collections.Comparators;
import org.fest.assertions.api.Fail;
import org.slf4j.Logger;
import org.testng.Assert;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static conversion7.engine.utils.Utils.error;

@Deprecated
public class Battle extends GameStage {

    private static final Logger LOG = Utils.getLoggerForClass();

    private static final float BATTLE_SIZE_MAGIC_N = 3f;
    public static final float ANIM_SPEED = 0.9f;
    public static final float ANIM_TRANSITION = 0.2f;
    public static boolean _HUMAN_PLAYER_COULD_NOT_LOST = false;
    public static boolean SUPPORT_MASSIVE_BATTLES = false;

    /** Only Battlefield sizes (exclude place where Player sets army) */
    private int fieldWidth;
    private int fieldHeight;

    /** Grid out of the battlefield. It's used in place army area definition */
    private int gridRadius;
    private int doubleGridRadius;

    private int totalWidth = fieldWidth + doubleGridRadius;
    private int totalHeight = fieldHeight + doubleGridRadius;

    private float halfOfTotalWidth;
    private float halfOfTotalHeight;


    private CustomStage mainStage;
    public SceneGroup3d mainGroup;
    public DecalGroup decalGroup;

    public conversion7.game.stages.battle_deprecated.calculation.State state = conversion7.game.stages.battle_deprecated.calculation.State.IN_PROGRESS;
    public Round round;

    public ArmyPlaceArea armyPlaceArea;
    public BattleFigure selectedFigure;
    public BattleFigure highlightedFigure;

    private Map<BattleSide, Boolean> mapTeamHasSelectedFigureForRound;

    public Array<BattleFigure> battleFigures = PoolManager.ARRAYS_POOL.obtain();
    public Array<BattleFigure> aliveFiguresAfterBattle;
    TeamSide attackerTeamSide;
    TeamSide defenderTeamSide;
    public Array<TeamSide> teamSides = new Array<>();
    public Array<BattleSide> teamsInBattle = new Array<>();
    public AreaObject humanPlayerArmyLink;
    private boolean autoCalculated;
    public int killedAnimals;
    private Cell origin;
    Cell defenderMainCell;

    public Battle(AbstractSquad attacker, AbstractSquad defender) {
        LOG.info("< create battle");
        Fail.fail("create battle");
        this.autoCalculated = true;
        origin = defender.getLastCell();

        setupTeamSides(attacker, defender);

        round = new Round(this);
        armyPlaceArea = new ArmyPlaceArea(this);

        if (!autoCalculated) {
            initBattleField();
            registerInputProcessors();
            setupUi(attacker, defender);
        }

        setupFigures();
        calculateTeamsInBattle();

        LOG.info("> created");
    }

    public TeamSide getAttackerTeamSide() {
        return attackerTeamSide;
    }

    public TeamSide getDefenderTeamSide() {
        return defenderTeamSide;
    }

    public Array<BattleFigure> getAliveFigures() {
        Array<BattleFigure> array = PoolManager.ARRAYS_POOL.obtain();
        for (TeamSide aliveTeamSide : teamSides) {
            Array<BattleFigure> aliveFigures = aliveTeamSide.getAliveFigures();
            array.addAll(aliveFigures);
            PoolManager.ARRAYS_POOL.free(aliveFigures);
        }
        return array;
    }

    public float getHalfOfTotalHeight() {
        return halfOfTotalHeight;
    }

    public int getFieldWidth() {
        return fieldWidth;
    }

    public int getFieldHeight() {
        return fieldHeight;
    }

    public int getGridRadius() {
        return gridRadius;
    }

    public int getDoubleGridRadius() {
        return doubleGridRadius;
    }

    public int getTotalWidth() {
        return totalWidth;
    }

    public int getTotalHeight() {
        return totalHeight;
    }

    public float getHalfOfTotalWidth() {
        return halfOfTotalWidth;
    }

    public Cell getOrigin() {
        return origin;
    }

    public boolean isCompleted() {
        return state == conversion7.game.stages.battle_deprecated.calculation.State.COMPLETED;
    }

    public void setHumanPlayerArmyLink(AreaObject humanPlayerArmyLink) {
        this.humanPlayerArmyLink = humanPlayerArmyLink;
    }

    private void setupTeamSides(AbstractSquad attacker, AbstractSquad defender) {
        attackerTeamSide = new TeamSide(this, attacker.getTeam(), BattleSide.LEFT_YELLOW);
        teamSides.add(attackerTeamSide);
        attackerTeamSide.addMainArmy(attacker);
        defenderTeamSide = new TeamSide(this, defender.getTeam(), BattleSide.RIGHT_RED);
        teamSides.add(defenderTeamSide);
        defenderTeamSide.addMainArmy(defender);
        defenderMainCell = defender.getLastCell();

        // additional armies, teams:
        if (SUPPORT_MASSIVE_BATTLES) {
            collectArmiesAroundDefender(defender);
        }

        // define enemies
        for (TeamSide curTeamSide : teamSides) {
            for (int i = 0; i < teamSides.size; i++) {
                TeamSide compareToTeamSide = teamSides.get(i);
                if (!curTeamSide.equals(compareToTeamSide)) {
                    // fixme conversion7.game.stages.world.unit.Unit.isEnemyWith() ?
                    if (curTeamSide.getTeam().isEnemyOf(compareToTeamSide.getTeam())) {
                        curTeamSide.addEnemySide(compareToTeamSide);
                    }
                }
            }
        }
    }

    private void initBattleField() {
        calculateDimensions();
        mainStage = new CustomStage(this.getClass().getSimpleName(), Gdxg.graphic.getCamera());
        mainStage.root.setPosition(MathUtils.toEngineCoords(halfOfTotalWidth, halfOfTotalHeight, 0));

        mainGroup = new SceneGroup3d();
        mainStage.addNode(mainGroup);
        mainGroup.setDimensions(totalWidth, totalHeight, 10);
        mainGroup.createBoundingBox();
        mainGroup.setDoNotReturnMeOnTouch(true);

        decalGroup = new DecalGroup(Gdxg.decalBatchCommon);
        mainStage.addNode(decalGroup);
        decalGroup.setDimensions(totalWidth, totalHeight, 1);
        decalGroup.createBoundingBox();

        DecalActor floor = new DecalActor(
                Decal.newDecal(totalWidth, totalHeight, Assets.glow, true),
                Gdxg.decalBatchCommon);
        decalGroup.addNode(floor);
        floor.rotate(0, -90, 0);
        floor.createBoundingBox();

        DecalActor field = new DecalActor(
                Decal.newDecal(fieldWidth, fieldHeight, Assets.grass, true),
                Gdxg.decalBatchCommon);
        decalGroup.addNode(field);
        field.rotate(0, -90, 0);
        field.setY(0.001f);
    }

    @Override
    public void registerInputProcessors() {
        inputProcessors.add(mainStage);
    }

    public void setupUi(AreaObject attacker, AreaObject defender) {
        mapTeamHasSelectedFigureForRound = new HashMap<>();

        // TODO flexible UI window:
        Team playerTeam = Gdxg.core.world.lastActivePlayerTeam;
        if (playerTeam.equals(attackerTeamSide.getTeam())) {
            humanPlayerArmyLink = attacker;
        } else if (playerTeam.equals(defenderTeamSide.getTeam())) {
            humanPlayerArmyLink = defender;
        } else {
            error("Battle must contain playerTeam to be shown!");
        }

        mainStage.addListener(new Scene3dInputListener() {
            Point2s touchDownCoord;
            Point2s touchUpCoord;
            Point2s mouseMovedCoord;
            Point2s newMouseMovedCoord;

            @Override
            public boolean touchDown(CustomInputEvent event, Vector3 touchPoint, int pointer, int button) {
                touchDownCoord = MathUtils.toGameCoords2d(touchPoint);
                if (LOG.isDebugEnabled())
                    LOG.debug("Got touchDown in Battle! x/y = " + touchDownCoord.x + "/" + touchDownCoord.y);
                return false;
            }

            @Override
            public boolean mouseMoved(CustomInputEvent event, Vector3 touchPoint) {
                newMouseMovedCoord = MathUtils.toGameCoords2d(touchPoint);
                if (!newMouseMovedCoord.equals(mouseMovedCoord)) {
                    if (LOG.isDebugEnabled())
                        LOG.debug("mouseMoved in Battle! x/y = " + newMouseMovedCoord.x + "/" + newMouseMovedCoord.y);
                    mouseMovedCoord = newMouseMovedCoord;
                    highlightCell(round.startStep.getCell(mouseMovedCoord));
                }

                return true;
            }

            @Override
            public void touchUp(CustomInputEvent event, Vector3 touchPoint, int pointer, int button) {
                if (round.state.equals(Round.State.SET_ARMY)) {
                    touchUpCoord = MathUtils.toGameCoords2d(touchPoint);
                    LOG.info("Got touchUp in Battle! x/y = " + touchUpCoord.x + "/" + touchUpCoord.y);
                    if (touchUpCoord.equals(touchDownCoord)) {
                        pressOnCell(round.startStep.getCell(touchUpCoord));
                    }
                }
            }
        });
    }

    private void setupFigures() {
        attackerTeamSide.setupFigures();
        defenderTeamSide.setupFigures();
        switchAllUnitsEffects(true);
        switchDefenderUnitsEffects(true);
        // TODO setupFigures for 3rd teams

        if (!autoCalculated) {
            for (BattleFigure battleFigure : battleFigures) {
                battleFigure.initVisual();
                // TODO load saved position from last battle (should be saved on unit?)
                battleFigure.activate();
            }
        }
    }

    public void calculateTeamsInBattle() {
        teamsInBattle.clear();
        for (BattleFigure battleFigure : battleFigures) {
            BattleSide battleSide = battleFigure.getBattleSide();
            if (!teamsInBattle.contains(battleSide, false)) {
                teamsInBattle.add(battleSide);
            }
        }
    }

    private void collectArmiesAroundDefender(AbstractSquad defender) {
        for (Cell cellAround : defender.getLastCell().getCellsAround()) {
            if (cellAround.hasSquad()) {
                AbstractSquad armyNearDefender = cellAround.getSquad();

                boolean armyAddedToExistingSide = false;
                for (TeamSide teamSide : teamSides) {
                    if (armyNearDefender.getTeam().equals(teamSide.getTeam())
                            || armyNearDefender.getTeam().isAllyOf(teamSide.getTeam())) {
                        teamSide.addArmy(armyNearDefender);
                        armyAddedToExistingSide = true;
                        break;
                    }
                }

                if (!armyAddedToExistingSide) {
                    // TODO new teamSide in battle for 3rd teams
                }
            }
        }
    }

    private void calculateDimensions() {
        int biggestSideSize = 0;
        for (TeamSide teamSide : teamSides) {
            int unitAmount = teamSide.getUnitAmount();
            if (unitAmount > biggestSideSize) {
                biggestSideSize = unitAmount;
            }
        }

        /** If side will have unit amount == BATTLE_SIZE_MAGIC_N
         * then calculated value will be == BATTLE_SIZE_MAGIC_N*/
        int magicSize = (int) (biggestSideSize / Math.sqrt(biggestSideSize / BATTLE_SIZE_MAGIC_N));
        LOG.info(String.format("magicSize=%s by biggestSideSize=%s", magicSize, biggestSideSize));
        gridRadius = magicSize;
        fieldWidth = gridRadius;
        fieldHeight = fieldWidth;
        doubleGridRadius = gridRadius * 2;
        totalWidth = fieldWidth + doubleGridRadius;
        totalHeight = fieldHeight + doubleGridRadius;
        halfOfTotalWidth = totalWidth / 2f;
        halfOfTotalHeight = totalHeight / 2f;
    }

    private void highlightCell(conversion7.game.stages.battle_deprecated.calculation.Cell highlightCell) {
        if (highlightedFigure != null && !highlightedFigure.equals(selectedFigure)) {
            highlightedFigure.resetHighlight();
        }

        if (highlightCell.isSeized()) {
            highlightedFigure = highlightCell.seizedBy.battleFigure;
            LOG.info("highlight figure: " + highlightedFigure);
            if (!highlightedFigure.equals(selectedFigure)) {
                highlightedFigure.highlight();
            }
        } else {
            highlightedFigure = null;
        }
    }

    private void pressOnCell(conversion7.game.stages.battle_deprecated.calculation.Cell pressedCell) {
        if (LOG.isDebugEnabled()) LOG.debug("\n pressOnCell: " + pressedCell);
        if (pressedCell.isSeized()) {
            if (selectedFigure != null) {
                selectedFigure.resetSelections();
            }
            selectedFigure = pressedCell.seizedBy.battleFigure;
            LOG.info("press on figure: " + selectedFigure);
            selectedFigure.select();
        } else {
            if (selectedFigure != null) {
                LOG.info("place figure: " + selectedFigure);
                selectedFigure.deactivate();
                selectedFigure.savedMirrorPosition = armyPlaceArea.
                        getMirrorPositionByBattleFieldPosition(pressedCell, selectedFigure.getBattleSide());
                selectedFigure.activate();
                selectedFigure.resetSelections();
                highlightedFigure = selectedFigure;
                highlightedFigure.highlight();
                selectedFigure = null;
            }
        }
    }

    private void switchAllUnitsEffects(boolean active) {
        for (BattleFigure battleFigure : battleFigures) {
//            UnitEffectManager unitEffectManager = battleFigure.getWorldUnit().getEffectManager();
//            IncreaseBattleParamsEffect battleParamsUnitEffect =
//                    unitEffectManager.getEffect(IncreaseBattleParamsEffect.class);
//            if (battleParamsUnitEffect != null) {
//                battleParamsUnitEffect.setEnabled(active);
//            }
        }
    }

    private void switchDefenderUnitsEffects(boolean active) {
        BattleArmy battleArmy = defenderTeamSide.getMainArmy();

        Assert.assertEquals(defenderMainCell, battleArmy.getSquad().getLastCell(),
                "Cell should not change, because at least cell effects depends on it!");

        boolean hasHill = battleArmy.getSquad().getLastCell().getLandscape().hasHill();
        boolean hasForest = battleArmy.getSquad().getLastCell().getLandscape().hasForest();
        for (BattleFigure battleFigure : battleArmy.getBattleFigures()) {
            UnitEffectManager unitEffectManager = battleFigure.getWorldUnit().squad.getEffectManager();
            FortificationEffect fortificationEffect = unitEffectManager.getEffect(FortificationEffect.class);
            if (fortificationEffect != null) {
                fortificationEffect.setEnabled(active);
            }
            if (active) {
                if (hasHill) {
                    unitEffectManager.addEffect(new HillDefenceEffect());
                }
                if (hasForest) {
                    unitEffectManager.addEffect(new ForestDefenceEffect());
                }
            } else {
                if (hasHill) {
                    HillDefenceEffect effect = unitEffectManager.getEffect(HillDefenceEffect.class);
                    unitEffectManager.removeEffect(effect);
                }
                if (hasForest) {
                    ForestDefenceEffect effect = unitEffectManager.getEffect(ForestDefenceEffect.class);
                    unitEffectManager.removeEffect(effect);
                }
            }
        }
    }

    @Override
    public void draw() {
        Drawer3d.grid(totalWidth, totalHeight);
        mainStage.draw();
    }

    @Override
    public void act(float delta) {
        mainStage.act(delta);
        round.act(delta);
    }

    public BattleFigure getFigure(Unit byUnit) {
        for (BattleFigure battleFigure : battleFigures) {
            if (battleFigure.getWorldUnit().equals(byUnit)) {
                return battleFigure;
            }
        }
        error("Figure was not found by: " + byUnit);
        return null;
    }

    @Override
    public void onShow() {
        Gdxg.graphic.getCameraController().setCamera2dPosition(0, 0);
        Gdxg.clientUi.getBattleWindowManageArmyForRound().refresh();
    }

    @Override
    public void onHide() {

    }

    @Override
    public void dispose() {

    }

    /** Old entry point to battle */
    @Deprecated
    public void start() {
        LOG.info("Auto start");
        AutoBattle autoBattle = new AutoBattle(this);
        autoBattle.start();
    }

    public void calculateRound() {
        LOG.info("Manual calculateRound");
        if (state.equals(conversion7.game.stages.battle_deprecated.calculation.State.COMPLETION_IN_PROGRESS) || isCompleted()) {
            throw new GdxRuntimeException("Attempt to calculate round when battle is completed");
        }

        if (couldBeStarted()) {
            round.start();
        } else {
            Utils.printErrorWithCurrentStacktrace("Battle could not be started!");
        }
    }

    public boolean couldBeStarted() {
        LOG.info("check each team has selected at least 1 figure for round");
        mapTeamHasSelectedFigureForRound.clear();
        for (BattleSide battleSide : teamsInBattle) {
            mapTeamHasSelectedFigureForRound.put(battleSide, false);
        }

        for (FigureStepParams figureStepParams : round.startStep.figuresParamsList) {
            mapTeamHasSelectedFigureForRound.put(figureStepParams.battleFigure.getBattleSide(), true);
        }

        for (Map.Entry<BattleSide, Boolean> sideIntegerEntry : mapTeamHasSelectedFigureForRound.entrySet()) {
            if (!sideIntegerEntry.getValue()) {
                return false;
            }
        }

        return true;
    }

    public void playRound() {
        round.state = Round.State.ACTION_IN_PROCESS;
    }

    public void finish() {
        state = conversion7.game.stages.battle_deprecated.calculation.State.COMPLETION_IN_PROGRESS;
        LOG.info("# BATTLE {}", state);

        if (GdxgConstants.isAlwaysDontResurrectUnitsInBattle()) {
            LOG.warn("isAlwaysDontResurrectUnitsInBattle ACTIVE!");
        }
        if (GdxgConstants.isResurrectUnitInBattleIfResistFailed()) {
            LOG.warn("isResurrectUnitInBattleIfResistFailed ACTIVE!");
        }

        switchAllUnitsEffects(false);
        switchDefenderUnitsEffects(false);
        aliveFiguresAfterBattle = PoolManager.ARRAYS_POOL.obtain();
        for (BattleFigure battleFigure : battleFigures) {
            battleFigure.applyBattleAffectOnWorldUnit();
        }
        validateAndDefeatArmies();
        spreadFoodFromKilledAnimals();
        spreadSkinFromKilledAnimals();

        if (defenderTeamSide.isDefeated()
                && !attackerTeamSide.isDefeated() && !attackerTeamSide.getMainArmy().isDefeated()) {
            attackerTeamSide.getMainArmy().getSquad().moveOn(defenderMainCell);
        }

        if (!autoCalculated) {
            Gdxg.clientUi.getBattleBar().hide();
            Gdxg.clientUi.getBattleEndWindow().show(this);
        }

        state = conversion7.game.stages.battle_deprecated.calculation.State.COMPLETED;
        LOG.info("# BATTLE {}!", state);
    }

    private void validateAndDefeatArmies() {
        boolean hasWinner = validateWinner();
        if (!hasWinner) {
            LOG.info("Battle finished with more than one alive side!");
        }

        for (TeamSide teamSide : teamSides) {
            for (BattleArmy battleArmy : teamSide.getArmies()) {

//                battleArmy.getSquad().validateAndDefeat();
            }
        }
    }

    private void spreadFoodFromKilledAnimals() {
        int totalFoodFromAnimals = killedAnimals * BaseAnimalClass.FOOD_FROM_ONE_UNIT_TOTAL;

        ObjectSet<AreaObject> objectWithUpdatedFood = PoolManager.OBJECT_SET_POOL.obtain();

        while (totalFoodFromAnimals > 0) {
            aliveFiguresAfterBattle.shuffle();
            for (BattleFigure battleFigure : aliveFiguresAfterBattle) {
                totalFoodFromAnimals -= 2;
                AbstractSquad object = battleFigure.getWorldUnit().getSquad();
                objectWithUpdatedFood.add(object);

//                int takeFood;
//                if (object.getTeam().getTeamSkillsManager().getSkill(SkillType.STONE_WORK).isFullyLearned()) {
//                    takeFood = 2;
//                } else {
//                    takeFood = 1;
//                }
//                object.unit.updateFood(+takeFood);

                if (totalFoodFromAnimals <= 0) {
                    break;
                }
            }
        }

        for (AreaObject areaObject : objectWithUpdatedFood) {
            areaObject.validate();
        }

        PoolManager.OBJECT_SET_POOL.free(objectWithUpdatedFood);
    }

    private void spreadSkinFromKilledAnimals() {
        Array<Unit> unitsWithFlayingSkill = PoolManager.ARRAYS_POOL.obtain();

        // collect skilled units
        for (BattleFigure battleFigure : aliveFiguresAfterBattle) {
            if (battleFigure.getWorldUnit().getSquad().getTeam().getTeamSkillsManager()
                    .getSkill(SkillType.HUNTING).isLearnStarted()) {
                unitsWithFlayingSkill.add(battleFigure.getWorldUnit());
            }
        }

        if (unitsWithFlayingSkill.size > 0) {
            BattleThreadLocalSort.instance().sort(unitsWithFlayingSkill, Comparators.UNIT_POWER_COMPARATOR);
            // spread skins
            Iterator<Unit> iterator = unitsWithFlayingSkill.iterator();
            for (int i = 0; i < killedAnimals; i++) {
                Unit unit = iterator.next();
                unit.getSquad().getInventory().addItem(SkinItem.class, 1);
                if (!iterator.hasNext()) {
                    iterator = unitsWithFlayingSkill.iterator();
                }
            }
        }

        PoolManager.ARRAYS_POOL.free(unitsWithFlayingSkill);
    }

    public boolean validateWinner() {
        int aliveSides = 0;
        for (TeamSide teamSide : teamSides) {
            teamSide.updateArmies();
            if (!teamSide.isDefeated()) {
                aliveSides++;
            }
        }

        if (aliveSides == 0) {
            LOG.warn("Battle has 0 alive sides!");
        }
        return aliveSides <= 1;
    }
}
