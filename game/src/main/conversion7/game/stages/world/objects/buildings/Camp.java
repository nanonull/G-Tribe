package conversion7.game.stages.world.objects.buildings;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import conversion7.engine.Gdxg;
import conversion7.engine.artemis.ui.InWorldPanelsOverlaySystem;
import conversion7.engine.customscene.ModelActor;
import conversion7.engine.customscene.SceneGroup3d;
import conversion7.engine.pools.system.PoolManager;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.adventure.BaalsMainCampaign;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.AreaObjectDetailsButton;
import conversion7.game.stages.world.objects.actions.items.BuildCampAction;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.quest.items.BuildCampQuest;
import conversion7.game.stages.world.team.Team;
import conversion7.game.stages.world.unit.Power2;
import conversion7.game.stages.world.unit.UnitFertilizer2;
import conversion7.game.unit_classes.UnitClassConstants;
import org.slf4j.Logger;

public class Camp extends BuildingObject implements AreaObjectDetailsButton {

    public static final int REQUIRED_CELL_FOOD = 20;
    public static final int REQUIRED_CELL_WATER = 10;
    public static final int MIN_DISTANCE_BTW_CAMPS = World.BASE_VIEW_RADIUS - 1;
    public static final int CAMP_RADIUS = MIN_DISTANCE_BTW_CAMPS - 1;
    public static final int CAMP_CONSTRUCTION_AP_TOTAL = AbstractSquad.START_MOVE_AP;
    private static final Logger LOG = Utils.getLoggerForClass();
    public static String HINT = "Camp: "
            + "\n - fertilization chance in camp: " + UnitFertilizer2.CAMP_FERTILIZE_PERC + "%"
            + "\n - increases healing effect"
            + "\n "
            + "\n - required minimum distance from another camps: " + MIN_DISTANCE_BTW_CAMPS
            + "\n - resources cost: " + ResourceCosts.getCostAsString(Camp.class);
    private static int campIds = 0;
    private final int campId;
    private int constructionProgress;
    private boolean constructionCompleted;
    private CampNet net;
    private int gatheredAmount;

    public Camp(Cell cell, Team team) {
        super(cell, team);
        init();
        campId = ++campIds;
        addToExistingCampNet();
        power = new Power2(this);
        power.updateMaxValue(UnitClassConstants.BASE_POWER * 3);
    }

    public static boolean couldBeBuiltOnCell(Cell cell) {
        if (cell.getCamp() == null) {
            if (!BuildCampAction.hasCellEnoughDistanceFromAnotherTowns(cell)) {
                return false;
            }
        } else {
            if (cell.getCamp().isConstructionCompleted()) {
                return false;
            }
        }

        return cell.isGoodForCamp() && !cell.hasCamp();
    }

    @Override
    public String getDetailsButtonLabel() {
        return "Camp";
    }

    @Override
    public ClickListener getDetailsClickListener() {
        return new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdxg.clientUi.getCellDetailsRootPanel().load((Camp) thiz, false);
            }
        };
    }

    public CampNet getNet() {
        return net;
    }

    public void setNet(CampNet net) {
        this.net = net;
    }

    public int getGatheredAmount() {
        return gatheredAmount;
    }

    public int getConstructionProgress() {
        return constructionProgress;
    }

    public boolean isConstructionCompleted() {
        return constructionCompleted;
    }

    public Array<Cell> getCampCells() {
        return getLastCell().getCellsAroundToRadiusInclusively(Camp.CAMP_RADIUS);
    }

    public int getGatheringWithBonus() {
        int exp = getGatheringPerStep();
        exp = (int) (exp * (1 + net.getBonus()));
        return exp;
    }

    public int getGatheringPerStep() {
        Array<Cell> cellsAround = getCampCells();
        int exp = 0;
        for (Cell cell : cellsAround) {
            exp += cell.getGatheringValue();
        }
        return exp;
    }

    @Override
    public boolean givesCornerDefenceBonus() {
        return true;
    }

    @Override
    protected String buildName() {
        return super.buildName() + campId;
    }

    public void addToExistingCampNet() {
        for (Cell cell : this.getLastCell().getCellsAroundToRadiusInclusively(MIN_DISTANCE_BTW_CAMPS)) {
            if (cell.hasCamp() && cell.camp.team == this.team) {
                if (this.net == null) {
                    cell.camp.createNetIfAbsent();
                    cell.camp.net.addCamp(this);
                } else {
                    if (cell.camp.net == null) {
                        net.addCamp(cell.camp);
                    }
                    if (net != cell.camp.net) {
                        net.addNet(cell.camp.net);
                    }
                }
            }
        }

        createNetIfAbsent();
    }

    private void createNetIfAbsent() {
        if (this.net == null) {
            this.net = new CampNet();
            net.addCamp(this);
        }
    }

    @Override
    public SceneGroup3d buildSceneBody() {
        SceneGroup3d sceneBody = super.buildSceneBody();
        ModelActor modelActor;
        if (constructionCompleted) {
            modelActor = PoolManager.CAMP_FRAGMENT_BUILT_MODEL_POOL.obtain();
        } else {
            modelActor = PoolManager.CAMP_FRAGMENT_MODEL_POOL.obtain();
        }
        sceneBody.addNode(modelActor);
        BoundingBox boundingBox = new BoundingBox();
        modelActor.modelInstance.calculateBoundingBox(boundingBox);
        sceneBody.assignBoundingBox(boundingBox);
        return sceneBody;
    }

    @Override
    public void seizeCell(Cell newCell) {
        super.seizeCell(newCell);
        getLastCell().setCamp(this);
    }

    public void completeConstruction() {
        constructionCompleted = true;
        InWorldPanelsOverlaySystem.createFor(this);
        addSnapshotLog(Camp.class.getSimpleName() + "completeConstruction", "");
        clearBody();

        if (team.isHumanPlayer()) {
            BuildCampQuest buildCampQuest = team.journal.getOrCreate(BuildCampQuest.class);
            if (!buildCampQuest.isCompleted(BuildCampQuest.State.S1)) {
                buildCampQuest.complete(BuildCampQuest.State.S1);
                team.updateEvolutionPointsOn(1, BuildCampQuest.class.getSimpleName());
            }
        }

        validate();
        validateView();

    }

    public void captureBy(Team newTeam) {
        Team prevTeam = this.team;
        prevTeam.removeCamp(this);

        setTeam(newTeam);
        newTeam.addCamp(this);

        net.recalculateCamps();

        getLastCell().addFloatLabel("Camp captured", Color.ORANGE, true);
    }

    public void endStepSimulation() {
        if (cell.hasSquad() && cell.squad.team == team) {
            int exp = getGatheringWithBonus();
            gatheredAmount += exp;
            team.updateEvolutionExp(exp);
            getLastCell().addFloatLabel("Camp gathering: " + exp, Color.YELLOW, true);
        } else {
//            getLastCell().addFloatLabel("No unit in camp", Color.ORANGE, true);
        }

        if (team.isBaals()) {
            BaalsMainCampaign.placeScout(cell);
        }
    }
}
