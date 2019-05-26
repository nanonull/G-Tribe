package conversion7.game.stages.world.adventure;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import conversion7.engine.Gdxg;
import conversion7.engine.utils.MathUtils;
import conversion7.game.GdxgConstants;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.inventory.items.ArrowItem;
import conversion7.game.stages.world.inventory.items.weapons.BowItem;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.objects.unit.WorldSquad;
import conversion7.game.stages.world.quest.items.FindAndKillIlluminatiDadQuest;
import conversion7.game.stages.world.quest.items.IlluminatiCampaignQuest;
import conversion7.game.stages.world.team.Team;
import conversion7.game.stages.world.team.TribeRelationType;
import conversion7.game.stages.world.team.goals.FindAndAttackTribeGoal;
import conversion7.game.stages.world.team.goals.GroupUpTribeGoal;
import conversion7.game.stages.world.unit.effects.items.VengeanceHitEffect;
import conversion7.game.unit_classes.humans.theOldest.Propliopithecus;
import conversion7.game.unit_classes.ufo.Illuminat;

import java.util.Iterator;

@Deprecated
public class IlluminatiCampaign {
    public static final String TEAM_NAME = "Illuminati";
    public static boolean worldWarStarted;
    private static final int ARRIVE_AT_STEP = World.DAY_LENGTH * 2;
    static Team illumTeam;
    public static boolean campaignFailed;
    public static boolean arrived;
    public static boolean gotAway;
    public static boolean metPlayer;
    public static Team targTeam;

    public static void newStep(World world) {
        if (gotAway) {
            return;
        }
        if (targTeam == null || targTeam.isDefeated()) {
            targTeam = Gdxg.core.world.getRndPlayerTeam();
        }
        if (targTeam == null) {
            return;
        }
        int step = world.step;
        if (step == ARRIVE_AT_STEP
                && campaignTargetAlive(world)
                && !GdxgConstants.DEVELOPER_MODE) {
            arrive(world);
            return;
        }

        if (arrived && campaignFailed) {
            teamGetAway();
        }
    }

    private static boolean campaignTargetAlive(World world) {
        return !targTeam.isDefeated()
                && targTeam.getUnitControlsTribe() != null
                && targTeam.getUnitControlsTribe().squad.isArchon()
                && targTeam.getUnitControlsTribe().squad.isAlive()
                ;
    }

    private static void arrive(World world) {
        arrived = true;
        targTeam.addEventMainUiNotification(IlluminatiCampaign.TEAM_NAME + " arrived");
        illumTeam = new Team(false, IlluminatiCampaign.TEAM_NAME, world);
        illumTeam.setIlluminati(true);
        world.addTeam2(illumTeam);
        illumTeam.setTeamOrder(1);

        Cell cellOrigin = targTeam.getSquads().get(0).getLastCell();
        Array<Cell> cellsAroundOnRadius = cellOrigin.
                getCellsAroundOnRadius(world.widthInCells / 5, new Array<>());

        int unitsToPlace = (int) (cellsAroundOnRadius.size / 5f);
        Iterator<Cell> iterator = cellsAroundOnRadius.iterator();
        while ((iterator.hasNext())) {
            if (!iterator.next().canBeSeized()) {
                iterator.remove();
            }
        }

        unitsToPlace = Math.min(unitsToPlace, cellsAroundOnRadius.size);
        int unitsWithBows = unitsToPlace / 2;
        illumTeam.getInventory().addItem(ArrowItem.class, unitsWithBows * 3);

        cellsAroundOnRadius.shuffle();
        for (int i = 0; i < unitsToPlace; i++) {
            Cell cellToPlace = cellsAroundOnRadius.get(i);
            WorldSquad squad = WorldSquad.create(Illuminat.class, illumTeam, cellToPlace);
            if (i == 0) {
                illumTeam.illumDad = squad;
            }
            if (unitsWithBows > 0) {
                unitsWithBows--;
                squad.equipment.equipRangeWeaponItem(BowItem.class);
            }
            squad.effectManager.getOrCreate(VengeanceHitEffect.class);
        }

        illumTeam.addGoal(new GroupUpTribeGoal(illumTeam));
    }

    public static void startWorldWar() {
        if (worldWarStarted) {
            return;
        }
        worldWarStarted = true;

        World world = Gdxg.core.world;
        targTeam.startQuest(FindAndKillIlluminatiDadQuest.class);

        for (Team team : new Array.ArrayIterable<>(world.teams)) {
            if (team != illumTeam && team.isHumanRace() && team.getSquads().size > 0) {
                illumTeam.world.addRelationType(TribeRelationType.MAIN_ENEMY, team, illumTeam);
            }
        }

        illumTeam.addGoal(new FindAndAttackTribeGoal(illumTeam, targTeam));
        targTeam.addEventMainUiNotification("World war started");
        cloneUnits(illumTeam);
    }

    public static void cloneUnits(Team team) {
        for (AbstractSquad squad : new Array<>(team.getSquads())) {
            if (squad.isIlluminat()) {
                squad.cloneUnit();
            }
        }
    }

    public static WorldSquad giftOthers(Team illumTeam) {
        Array<AbstractSquad> illumTeamSquads = illumTeam.getSquads();
        AbstractSquad squad = illumTeamSquads.get(MathUtils.random(0, illumTeamSquads.size - 1));
        Array<AreaObject> areaObjects = new Array<>(squad.visibleObjects);
        areaObjects.shuffle();
        for (AreaObject areaObject : areaObjects) {
            if (areaObject.isSquad() && areaObject.team.isHumanRace()) {
                if (!areaObject.toSquad().team.isEnemyOf(illumTeam)) {
                    Cell couldBeSeizedNeighborCell = areaObject.cell.getCouldBeSeizedNeighborCell();
                    if (couldBeSeizedNeighborCell != null) {
                        WorldSquad worldSquad = WorldSquad.create(Propliopithecus.class, areaObject.team, couldBeSeizedNeighborCell);
                        worldSquad.batchFloatingStatusLines.addImportantLine(IlluminatiCampaign.TEAM_NAME + " gift");
                        return worldSquad;
                    }
                }
                return null;
            }
        }
        return null;
    }

    public static void teamGetAway() {
        gotAway = true;
        for (AbstractSquad squad : new Array.ArrayIterable<>(illumTeam.getSquads())) {
            squad.cell.addFloatLabel("Teleport", Color.WHITE);
            WorldSquad.killUnit(squad);
        }
        if (!illumTeam.isDefeated()) {
            illumTeam.world.defeat(illumTeam);
        }
        targTeam.addEventMainUiNotification(TEAM_NAME + " left the planet");
    }

    public static void failed(World world) {
        if (targTeam != null) {
            targTeam.journal.getOrCreate(IlluminatiCampaignQuest.class)
                    .fail(IlluminatiCampaignQuest.State.S1);
            campaignFailed = true;
        }
    }
}
