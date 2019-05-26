package conversion7.game.stages.world.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import conversion7.engine.AudioPlayer;
import conversion7.engine.Gdxg;
import conversion7.engine.customscene.ModelActor;
import conversion7.engine.customscene.SceneGroup3d;
import conversion7.engine.geometry.Modeler;
import conversion7.engine.utils.MathUtils;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.WorldTrend;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.objects.unit.AiGoalHelper;
import conversion7.game.stages.world.objects.unit.WorldSquad;
import conversion7.game.stages.world.team.Team;
import conversion7.game.stages.world.unit.AnimalHunting;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.unit_classes.ClassStandard;
import conversion7.game.unit_classes.UnitClassConstants;
import org.slf4j.Logger;
import org.testng.Assert;

import java.util.Iterator;

public class AnimalSpawn extends AreaObject implements AreaObjectTickable {
    private static final Logger LOG = Utils.getLoggerForClass();
    private static final int BASE_SPAWN_CHANCE = 20;
    public static final int MAX_SPAWN_CHANCE = 100;
    public static final Vector3 SHIFT_POS = MathUtils.toEngineCoords(new Vector3(-0.3f, 0.3f, 0));
    public static final int CONTROL_EXP = Unit.BASE_EXP_FOR_LEVEL / 5;
    public static final int CAPTURE_EXP = Unit.BASE_EXP_FOR_LEVEL / 3;
    public static final int MAX_SPAWNED_UNITS_ALIVE = 3;
    public static int BASE_SPAWN_EVERY = 2;
    @Deprecated
    private int lastSpawnStepsAgo;
    private int spawnChance;
    private int hordeSpawnCounter;
    private boolean alive = true;
    private boolean migration = false;
    private boolean quest;
    private int spawnEvery = World.HALF_DAY_LENGTH;
    private int tick;
    private Array<AbstractSquad> mySpawnedUnits = new Array<>();
    private boolean captured;
    private float spawnedPowerMlt = 1f;

    public AnimalSpawn(Cell cell, Team team) {
        super(cell, team);
        spawnChance = BASE_SPAWN_CHANCE;
        cell.getArea().world.animalSpawns.add(this);
        init();
    }

    public int getSpawnChance() {
        return spawnChance;
    }

    private boolean isControlledByHuman() {
        return cell.hasSquad() && cell.squad.isHuman();
    }

    @Override
    public String getShortHint() {
        boolean visited = false;
        if (Gdxg.core.world.activeTeam != null) {
            if (Gdxg.core.world.activeTeam.visitedSpawns.contains(this)) {
                visited = true;
            }
        }
        return super.getShortHint() + (visited ? " (visited)" : "");
    }

    public void setMigration(boolean migration) {
        this.migration = migration;
    }

    public void setQuest(boolean quest) {
        this.quest = quest;
    }

    public void setSpawnEvery(int spawnEvery) {
        this.spawnEvery = spawnEvery;
    }

    public void setSpawnedPowerMlt(float spawnedPowerMlt) {
        this.spawnedPowerMlt = spawnedPowerMlt;
    }

    public static boolean canSpawnAnimalOn(Cell cell) {
        return cell.canBeSeized();
    }

    @Override
    public boolean givesExpOnHurt() {
        return false;
    }

    @Override
    public SceneGroup3d buildSceneBody() {
        SceneGroup3d sceneBody = super.buildSceneBody();
        ModelActor modelActor;
        modelActor = new ModelActor("spawn",
                Modeler.buildAnimalSpawn(Color.GREEN), Gdxg.modelBatch);
        sceneBody.addNode(modelActor);
        modelActor.setPosition(ResourceObject.SHIFT_POS);

        return sceneBody;
    }

    public void trySpawnSingleUnit() {
        if (mySpawnedUnits.size >= MAX_SPAWNED_UNITS_ALIVE) {
            showSpawnStagnationMsg();
            return;
        }

        if (team.isAnimals()) {
            // TODO dead branch
            if (isControlledByHuman()) {
                cell.squad.updateExperience(CONTROL_EXP, "Control spawn exp");
                return;
            }
        } else {
            if (cell.hasSquad() && cell.squad.team == this.team && cell.squad.canHunt()) {
                AnimalHunting.hunted(cell.squad, cell.getArea().world.getSpawnableRndAnimalClass());
                return;
            }
        }

        spawnSingleUnit();
    }

    private void showSpawnStagnationMsg() {
        cell.addFloatLabel("Spawn stagnation", Color.WHITE);
    }

    public void spawnSingleUnit() {
        lastSpawnStepsAgo++;
        tick = 0;

        Cell canUseCell;
        if (canSpawnAnimalOn(cell)) {
            canUseCell = cell;
        } else {
            canUseCell = cell.getCouldBeSeizedNeighborCell();

        }

        if (canUseCell != null && canSpawnAnimalOn(canUseCell)) {
            spawnUnit(canUseCell, canUseCell.getArea().world.getSpawnableRndAnimalClass());
        } else {
            showSpawnStagnationMsg();
        }
    }

    public AbstractSquad spawnUnit(Cell cell, Class<? extends Unit> clazz) {
        Assert.assertTrue(alive);
        lastSpawnStepsAgo = 0;
        World world = cell.getArea().world;
        AbstractSquad squad = WorldSquad.create(clazz, team, cell);

        int powerUp = 0;
        if (world.trends.contains(WorldTrend.ANIMAL_POWER_PLUS)) {
            int maxValue = squad.power.getMaxValue();
            maxValue *= WorldTrend.Mappings.ANIMAL_POWER_PLUS_MLT;
            int diff = maxValue - squad.power.getMaxValue();
            powerUp += diff;
        }

        if (world.trends.contains(WorldTrend.ANIMAL_POWER_MINUS)) {
            int maxValue = squad.power.getMaxValue();
            maxValue *= WorldTrend.Mappings.ANIMAL_POWER_MINUS_MLT;
            int diff = maxValue - squad.power.getMaxValue();
            powerUp += diff;
        }

        if (quest && team.isAnimals()) {
            squad.addAiGoal(AiGoalHelper.moveToAndAttackTribe(world.getRndPlayerTeam()));
        }

        if (MathUtils.testPercentChance(1)) {
            squad.setBossUnit();
        }

        powerUp = Math.round(powerUp * spawnedPowerMlt);
        squad.power.updateMaxValue(powerUp);

        LOG.info("spawnUnit: " + squad);
        mySpawnedUnits.add(squad);
        return squad;
    }

    public void setMaxAnimalSpawnChance() {
        spawnChance = MAX_SPAWN_CHANCE;
    }

    public void migrate() {
        if (migration) {
            seizeCell(cell.getCell(MathUtils.random(-1, 1), MathUtils.random(-1, 1)));
        }
    }

    public void destroy() {
        cell.getArea().world.animalSpawns.removeValue(this, true);
        alive = false;
    }

    @Override
    public void tick() {
        tick++;
        validateMyUnits();
        if (tick >= spawnEvery) {
            trySpawnSingleUnit();
        }
    }

    private void validateMyUnits() {
        Iterator<AbstractSquad> iterator = mySpawnedUnits.iterator();
        while (iterator.hasNext()) {
            AbstractSquad squad = iterator.next();
            if (!squad.isAlive()) {
                iterator.remove();
            }
        }
    }

    public void spawnHorde() {
        int animalLvl = getLastCell().getArea().world.getSpawnableRndAnimalLevel();
        int spawnPoints = animalLvl * 3 + MathUtils.random(0, animalLvl * 2);
        Class<? extends Unit> aClass = UnitClassConstants.getAnimalClassByLevel(animalLvl);
        ClassStandard classStandard = UnitClassConstants.CLASS_STANDARDS.get(aClass);

        int unitSpawnCost = classStandard.level;
        int unitsToSpawn = spawnPoints / unitSpawnCost;
        LOG.info("unitsToSpawn: " + unitsToSpawn);
        LOG.info("unitSpawnCost: " + classStandard.level);

        int unitsSpawned = 0;
        Array<Cell> cellsAround = getLastCell().getCellsAround(0, 2, new Array<>());
        cellsAround.shuffle();
        for (Cell spawnOn : cellsAround) {
            if (canSpawnAnimalOn(spawnOn)) {
                spawnUnit(spawnOn, classStandard.unitClass);
                unitsSpawned++;
                if (unitsSpawned >= unitsToSpawn) {
                    break;
                }
            }
        }
    }

    public void captureBy(AbstractSquad squad) {
        if (squad.team != null && team != squad.team) {
            if (!captured) {
                squad.updateExperience(AnimalSpawn.CAPTURE_EXP, "Spawn captured exp");
            } else {
                squad.batchFloatingStatusLines.addLine("Spawn recaptured");
            }
            captured = true;
            setTeam(squad.team);
            if (team.isHumanPlayer()){
                AudioPlayer.playSingleSnare();
            }
        }
    }
}
