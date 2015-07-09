package conversion7.game.stages.battle;

import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import conversion7.engine.ClientCore;
import conversion7.engine.Gdxg;
import conversion7.engine.customscene.CustomStage;
import conversion7.engine.customscene.DecalActor;
import conversion7.engine.customscene.DecalGroup;
import conversion7.engine.customscene.SceneGroup3d;
import conversion7.engine.customscene.input.CustomInputEvent;
import conversion7.engine.customscene.input.CustomInputListener;
import conversion7.engine.geometry.Drawer3d;
import conversion7.engine.geometry.Point2s;
import conversion7.engine.pools.system.PoolManager;
import conversion7.engine.utils.MathUtils;
import conversion7.engine.utils.Utils;
import conversion7.game.Assets;
import conversion7.game.classes.animals.AbstractAnimalUnit;
import conversion7.game.stages.GameStage;
import conversion7.game.stages.battle.calculation.FigureStepParams;
import conversion7.game.stages.battle.calculation.Round;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.inventory.items.SkinItem;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.team.Team;
import conversion7.game.stages.world.team.skills.FlayingSkill;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.utils.collections.Comparators;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static conversion7.engine.utils.Utils.error;

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
    private int doubleGridRadius = gridRadius * 2;

    private int totalWidth = fieldWidth + doubleGridRadius;
    private int totalHeight = fieldHeight + doubleGridRadius;

    private float halfOfTotalWidth = totalWidth / 2f;
    private float halfOfTotalHeight = totalHeight / 2f;


    private CustomStage mainStage = new CustomStage(this.getClass().getSimpleName(), Gdxg.graphic.getCamera());
    public SceneGroup3d mainGroup;
    public DecalGroup decalGroup;

    public conversion7.game.stages.battle.calculation.State state = conversion7.game.stages.battle.calculation.State.IN_PROGRESS;
    public Round round;

    public ArmyPlaceArea armyPlaceArea;
    public BattleFigure selectedFigure;
    public BattleFigure highlightedFigure;

    private final Map<BattleSide, Boolean> mapTeamHasSelectedFigureForRound = new HashMap<>();

    public Array<BattleFigure> battleFigures = PoolManager.ARRAYS_POOL.obtain();
    public Array<BattleFigure> aliveFiguresAfterBattle;
    TeamSide attackerTeamSide;
    TeamSide defenderTeamSide;
    public Array<TeamSide> aliveTeamSides = new Array<>();
    public Array<TeamSide> deadTeamSides = new Array<>();
    public Array<BattleSide> teamsInBattle = new Array<>();
    public AreaObject humanPlayerArmyLink;
    private boolean autoCalculated;
    public int killedAnimals;

    public Battle(AreaObject attacker, AreaObject defender, boolean autoCalculated) {
        LOG.info("< create battle");
        this.autoCalculated = autoCalculated;

        setupTeamSides(attacker, defender);
        calculateDimensions();

        round = new Round(this);
        armyPlaceArea = new ArmyPlaceArea(this);

        if (!this.autoCalculated) {
            setupUi(attacker, defender);
        }

        registerInputProcessors();

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

        setupFigures();
        calculateTeamsInBattle();

        mainStage.addListener(new CustomInputListener() {
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

        LOG.info("> created");
    }

    public void setHumanPlayerArmyLink(AreaObject humanPlayerArmyLink) {
        this.humanPlayerArmyLink = humanPlayerArmyLink;
    }

    public TeamSide getAttackerTeamSide() {
        return attackerTeamSide;
    }

    public TeamSide getDefenderTeamSide() {
        return defenderTeamSide;
    }

    public Array<BattleFigure> getAliveFigures() {
        Array<BattleFigure> array = PoolManager.ARRAYS_POOL.obtain();
        for (TeamSide aliveTeamSide : aliveTeamSides) {
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

    private void calculateDimensions() {
        int biggestSideSize = 0;
        for (TeamSide teamSide : aliveTeamSides) {
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

    private void highlightCell(conversion7.game.stages.battle.calculation.Cell highlightCell) {
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

    private void pressOnCell(conversion7.game.stages.battle.calculation.Cell pressedCell) {
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

    @Override
    public void registerInputProcessors() {
        inputProcessors.add(mainStage);
    }

    public void setupUi(AreaObject attacker, AreaObject defender) {
        // TODO flexible UI window:
        Team playerTeam = World.getPlayerTeam();
        if (playerTeam.equals(attackerTeamSide.getTeam())) {
            humanPlayerArmyLink = attacker;
        } else if (playerTeam.equals(defenderTeamSide.getTeam())) {
            humanPlayerArmyLink = defender;
        } else {
            error("Battle must contain playerTeam to be shown!");
        }
    }

    private void setupTeamSides(AreaObject attacker, AreaObject defender) {
        attackerTeamSide = new TeamSide(this, attacker.getTeam(), BattleSide.LEFT);
        aliveTeamSides.add(attackerTeamSide);
        attackerTeamSide.addMainArmy(attacker);
        defenderTeamSide = new TeamSide(this, defender.getTeam(), BattleSide.RIGHT);
        aliveTeamSides.add(defenderTeamSide);
        defenderTeamSide.addMainArmy(defender);

        // additional armies, teams:
        if (SUPPORT_MASSIVE_BATTLES) {
            collectArmiesAroundDefender(defender);
        }

        // define enemies
        for (TeamSide curTeamSide : aliveTeamSides) {
            for (int i = 0; i < aliveTeamSides.size; i++) {
                TeamSide compareToTeamSide = aliveTeamSides.get(i);
                if (!curTeamSide.equals(compareToTeamSide)) {
                    if (curTeamSide.getTeam().isEnemyOf(compareToTeamSide.getTeam())) {
                        curTeamSide.addEnemySide(compareToTeamSide);
                    }
                }
            }
        }
    }

    private void collectArmiesAroundDefender(AreaObject defender) {
        for (Cell cellAround : defender.getCell().getNeighborCells()) {
            if (cellAround.isSeized()) {
                AreaObject armyNearDefender = cellAround.getSeizedBy();

                boolean armyAddedToExistingSide = false;
                for (TeamSide teamSide : aliveTeamSides) {
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

    private void setupFigures() {
        attackerTeamSide.setupFigures();
        defenderTeamSide.setupFigures();
        // TODO setupFigures for 3rd teams

        for (BattleFigure battleFigure : battleFigures) {
            battleFigure.initVisual();
            // TODO load saved position from last battle (should be saved on unit?)
            battleFigure.activate();
        }
    }

    public BattleFigure getFigure(Unit byUnit) {
        for (BattleFigure battleFigure : battleFigures) {
            if (battleFigure.representsUnit.equals(byUnit)) {
                return battleFigure;
            }
        }
        error("Figure was not found by: " + byUnit);
        return null;
    }

    private void recalculateArmies() {
        for (TeamSide deadTeamSide : deadTeamSides) {
            // TODO remove on prod
            if (deadTeamSide.aliveArmies.size > 0) {
                Utils.error("deadTeamSide.aliveArmies.size > 0");
            }

            for (BattleArmy deadArmy : deadTeamSide.deadArmies) {
                deadArmy.defeatAreaObject("deadTeamSide > deadArmy");
            }
        }

        for (TeamSide aliveTeamSide : aliveTeamSides) {
            for (BattleArmy aliveArmy : aliveTeamSide.aliveArmies) {
                aliveArmy.getAreaObject().validate();
                aliveArmy.getAreaObject().addSnapshotLog("alive after battle");
            }

            for (BattleArmy deadArmy : aliveTeamSide.deadArmies) {
                // TODO remove on prod
                if (deadArmy.getAreaObject().getUnits().size > 0) {
                    Utils.error("deadArmy.getAreaObject().units.size > 0");
                }
                deadArmy.defeatAreaObject("aliveTeamSide > deadArmy");
            }
        }
    }

    @Override
    public void onShow() {
        Gdxg.graphic.getCameraController().setCamera2dPosition(0, 0);
        Gdxg.clientUi.getBattleWindowManageArmyForRound().refresh();
    }

    @Override
    public void onHide() {

    }

    public void startAuto() {
        LOG.info("Auto start");
        autoCalculated = true;
        AutoBattle autoBattle = new AutoBattle(this);
        autoBattle.start();
    }

    public void updateTeamSides() {
        Iterator<TeamSide> iterator = aliveTeamSides.iterator();
        while (iterator.hasNext()) {
            TeamSide teamSide = iterator.next();
            teamSide.updateArmies();
            if (teamSide.isDefeated()) {
                deadTeamSides.add(teamSide);
                iterator.remove();
            }
        }
    }

    public boolean hasWinner() {
        if (aliveTeamSides.size == 0) {
            Utils.error("no winner - not implemented!");
        } else if (aliveTeamSides.size == 1) {
            return true;
        }
        return false;
    }

    public void playRound() {
        round.state = Round.State.ACTION_IN_PROCESS;
    }

    public boolean isCompleted() {
        return state == conversion7.game.stages.battle.calculation.State.COMPLETED;
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

    public void calculateRound() {
        LOG.info("Manual calculateRound");
        if (state.equals(conversion7.game.stages.battle.calculation.State.COMPLETION_IN_PROGRESS) || isCompleted()) {
            Utils.error("attempt to calculateRound round when battle is completed");
        }

        if (couldBeStarted()) {
            round.start();
        } else {
            Utils.logErrorWithCurrentStacktrace("battle could not be started!");
        }
    }

    public void finish() {
        LOG.info("# BATTLE FINISHED!");
        state = conversion7.game.stages.battle.calculation.State.COMPLETION_IN_PROGRESS;
        Cell defenderMainCell = defenderTeamSide.getMainArmy().getAreaObject().getCell();

        aliveFiguresAfterBattle = PoolManager.ARRAYS_POOL.obtain();
        for (BattleFigure battleFigure : battleFigures) {
            battleFigure.applyBattleAffectOnWorldUnit();
        }
        recalculateArmies();
        takeFoodFromKilledAnimals();
        takeSkinFromKilledAnimals();

        if (defenderTeamSide.isDefeated()
                && !attackerTeamSide.isDefeated() && !attackerTeamSide.getMainArmy().isDefeated()) {
            attackerTeamSide.getMainArmy().getAreaObject().moveOn(defenderMainCell);
        }

        if (!autoCalculated) {
            Gdxg.clientUi.getBattleBar().hide();
            Gdxg.clientUi.getBattleEndWindow().show(ClientCore.core.battle);
        }
        state = conversion7.game.stages.battle.calculation.State.COMPLETED;
    }

    private void takeFoodFromKilledAnimals() {
        int totalFoodFromAnimals = killedAnimals * AbstractAnimalUnit.FOOD_FROM_ONE_UNIT_TOTAL;

        ObjectSet<AreaObject> objectWithUpdatedFood = PoolManager.OBJECT_SET_POOL.obtain();

        while (totalFoodFromAnimals > 0) {
            aliveFiguresAfterBattle.shuffle();
            for (BattleFigure battleFigure : aliveFiguresAfterBattle) {
                totalFoodFromAnimals -= 2;
                AreaObject object = battleFigure.representsUnit.getAreaObject();
                objectWithUpdatedFood.add(object);

                int takeFood;
                if (object.getTeam().getTeamSkillsManager().getStoneWorkSkill().isLearned()) {
                    takeFood = 2;
                } else {
                    takeFood = 1;
                }
                object.getFoodStorage().updateFoodOnValue(+takeFood);

                if (totalFoodFromAnimals <= 0) {
                    break;
                }
            }
        }

        for (AreaObject areaObject : objectWithUpdatedFood) {
            areaObject.getFoodStorage().validateDependencies();
        }

        PoolManager.OBJECT_SET_POOL.free(objectWithUpdatedFood);
    }

    private void takeSkinFromKilledAnimals() {
        Array<Unit> unitsWithFlayingSkill = PoolManager.ARRAYS_POOL.obtain();

        // collect skilled units
        for (BattleFigure battleFigure : aliveFiguresAfterBattle) {
            if (battleFigure.representsUnit.getAreaObject().getTeam().getTeamSkillsManager()
                    .getSkill(FlayingSkill.class).isLearnStarted()) {
                unitsWithFlayingSkill.add(battleFigure.representsUnit);
            }
        }

        if (unitsWithFlayingSkill.size > 0) {
            BattleThreadLocalSort.instance().sort(unitsWithFlayingSkill, Comparators.UNIT_POWER_COMPARATOR);
            // spread skins
            Iterator<Unit> iterator = unitsWithFlayingSkill.iterator();
            for (int i = 0; i < killedAnimals; i++) {
                Unit unit = iterator.next();
                unit.getAreaObject().getMainInventory().addItem(SkinItem.class, 1);
                if (!iterator.hasNext()) {
                    iterator = unitsWithFlayingSkill.iterator();
                }
            }
        }

        PoolManager.ARRAYS_POOL.free(unitsWithFlayingSkill);
    }

}
