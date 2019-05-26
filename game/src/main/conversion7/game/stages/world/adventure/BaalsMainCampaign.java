package conversion7.game.stages.world.adventure;

import conversion7.engine.Gdxg;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.inventory.items.weapons.PowerFistItem;
import conversion7.game.stages.world.landscape.Biom;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.buildings.Camp;
import conversion7.game.stages.world.objects.unit.AiGoalHelper;
import conversion7.game.stages.world.objects.unit.WorldSquad;
import conversion7.game.stages.world.quest.BaseQuest;
import conversion7.game.stages.world.quest.items.AttackBaalsCamp;
import conversion7.game.stages.world.quest.items.BaalsCampaingQuest;
import conversion7.game.stages.world.team.Team;
import conversion7.game.stages.world.team.TribeBehaviourTag;
import conversion7.game.stages.world.team.TribeRelationType;
import conversion7.game.stages.world.team.goals.FindAndAttackTribeGoal;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.unit_classes.ufo.BaalScout;

import java.util.concurrent.atomic.AtomicReference;

public class BaalsMainCampaign {

    public static final String TEAM_NAME = "Baals";
    public static final String QUEST_TEXT_0 = BaalsMainCampaign.TEAM_NAME + " can receive scout message at any time...";
    public static final String QUEST_TEXT_1 = BaalsMainCampaign.TEAM_NAME + " have located galaxy!";
    public static final String QUEST_TEXT_2 = BaalsMainCampaign.TEAM_NAME + " have located solar system!";
    public static final String QUEST_TEXT_3 = BaalsMainCampaign.TEAM_NAME + " have located planet!";
    public static final String QUEST_TEXT_4 = BaalsMainCampaign.TEAM_NAME + " have been landed on the Earth!";
    public static final int STEP_1 = 1;
    private static final int STEP_LENGTH = 3;
    private static final int STEP_2 = STEP_1 + STEP_LENGTH;
    private static final int STEP_3 = STEP_2 + STEP_LENGTH;
    private static final int STEP_4 = STEP_3 + STEP_LENGTH;
    private static final int STEP_5 = STEP_4 + STEP_LENGTH;
    private static final int START_CAMP_UNITS = 8;
    public static Team targTeam;
    private static boolean landed;

    public static void newStep(World world) {
        if (world.lastActivePlayerTeam == null) {
            return;
        }

        int step = world.step;
        if (step == STEP_1) {
//            createTeam(world);
        } else if (step == STEP_2) {
//            theyFoundGalaxy();
        } else if (step == STEP_3) {
//            theyFoundSolar();
        } else if (step == STEP_4) {
//            theyFoundPlanet();
        } else if (step >= STEP_5 && !landed) {
//            theyLand();
        }
    }

    private static void theyFoundGalaxy() {
        BaalsCampaingQuest campaingQuest = Gdxg.core.world.lastActivePlayerTeam.journal.getOrCreate(BaalsCampaingQuest.class);
        campaingQuest.completeOpen();
        campaingQuest.initEntry(BaalsCampaingQuest.State.S1, QUEST_TEXT_1);
    }

    private static void theyFoundSolar() {
        BaalsCampaingQuest campaingQuest = Gdxg.core.world.lastActivePlayerTeam.journal.getOrCreate(BaalsCampaingQuest.class);
        campaingQuest.completeOpen();
        campaingQuest.initEntry(BaalsCampaingQuest.State.S2, QUEST_TEXT_2);
    }

    private static void theyFoundPlanet() {
        BaalsCampaingQuest campaingQuest = Gdxg.core.world.lastActivePlayerTeam.journal.getOrCreate(BaalsCampaingQuest.class);
        campaingQuest.completeOpen();
        campaingQuest.initEntry(BaalsCampaingQuest.State.S3, QUEST_TEXT_3);
    }

    public static void theyLand() {
        if (campaignTargetAlive(Gdxg.core.world)
                && baalsCaptureRandomTribe() != null) {
            BaalsCampaingQuest campaingQuest = Gdxg.core.world.lastActivePlayerTeam.journal.getOrCreate(BaalsCampaingQuest.class);
            campaingQuest.completeOpen();
            campaingQuest.initEntry(BaalsCampaingQuest.State.S4, BaalsMainCampaign.QUEST_TEXT_4);
            landed = true;
        }
    }

    public static void createTeam(World world) {
        Team team = new Team(false, BaalsMainCampaign.TEAM_NAME, world);
        team.setBaalsRace(true);
        world.addTeam2(team);
        team.setTeamOrder(1);
        team.tags.add(TribeBehaviourTag.AGGRESSIVE);
        for (Team player : world.humanPlayers) {
            player.addRelation(TribeRelationType.MAIN_ENEMY, team);
            player.startQuest(BaalsCampaingQuest.class);
        }
        targTeam = world.humanPlayers.get(0);
    }

    public static boolean campaignTargetAlive(World world) {
        return targTeam != null && !targTeam.isDefeated()
//                && world.playerTeam.getUnitControlsTribe() != null
//                && world.playerTeam.getUnitControlsTribe().squad.isArchon()
//                && world.playerTeam.getUnitControlsTribe().squad.isAlive()
                ;
    }

    public static Team baalsCaptureRandomTribe() {
        Team baalsTeam = Gdxg.core.world.getBaalsTeam();

        // find tribe
        Team tribe = WorldAdventure.findGoodFreeTribe();
        // create ufo
        if (tribe != null) {
            World world = tribe.world;
            tribe.getSquads().shuffle();
            Cell neighborCell = tribe.getSquads().get(0).getLastCell().getCouldBeSeizedNeighborCell();
            if (neighborCell != null) {
                Unit baalUfo = WorldAdventure.placeBaalUfo(neighborCell, tribe);
                tribe.setUnitControlsTribe(baalUfo);
                tribe.goals.add(new FindAndAttackTribeGoal(tribe, world.lastActivePlayerTeam));
                world.addImportantObj(baalUfo.squad);
                world.addRelationType(TribeRelationType.MAIN_ENEMY, tribe, world.lastActivePlayerTeam);
                return tribe;
            }
        }
        return null;
    }

    public static void placeBaalCamp1(World world) {
        Biom biom = world.baalsCampStart;
        Cell startCell = biom.startCell;
        world.goodBioms.removeValue(biom, true);
        Team playerTeam1 = world.humanPlayers.get(0);

        WorldAdventure.runAround(startCell, 0, startCell.getArea().world.widthInCells,
                Cell.Filters.CAN_SET_MAIN_SLOT_OBJ, cell -> {
                    Camp camp = world.getBaalsTeam().createCamp(cell);
                    AttackBaalsCamp attackBaalsCamp = BaseQuest.startQuest(playerTeam1, AttackBaalsCamp.class);
                    attackBaalsCamp.stateCellTargets.put(AttackBaalsCamp.State.S1, cell);
                });

        for (int i = 0; i < START_CAMP_UNITS; i++) {
            placeScout(startCell);
        }
    }

    public static void placeScout(Cell aroundCell) {
        AtomicReference<WorldSquad> squad = new AtomicReference<>();
        WorldAdventure.runAround(aroundCell, 0, aroundCell.getArea().world.widthInCells,
                Cell.Filters.CAN_SET_MAIN_SLOT_OBJ, cell -> {
                    Team team = aroundCell.getArea().world.getBaalsTeam();
                    squad.set(WorldSquad.create(BaalScout.class, team, cell));
                    squad.get().equipment.equipMeleeWeaponItem(new PowerFistItem());
                }, false);
        squad.get().addAiGoal(AiGoalHelper.moveToAndAttackTribe(aroundCell.getArea().world.humanPlayers.get(0)));

    }
}
