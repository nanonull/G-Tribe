package conversion7.game.stages.world.adventure;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Predicate;
import conversion7.engine.Gdxg;
import conversion7.engine.geometry.Point2s;
import conversion7.engine.utils.FastAsserts;
import conversion7.engine.utils.MathUtils;
import conversion7.engine.utils.Utils;
import conversion7.game.services.WorldServices;
import conversion7.game.stages.world.FindPath;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.area.Area;
import conversion7.game.stages.world.inventory.items.FusionCellItem;
import conversion7.game.stages.world.inventory.items.RadioactiveIsotopeItem;
import conversion7.game.stages.world.inventory.items.types.RangeWeaponItem;
import conversion7.game.stages.world.inventory.items.weapons.AtomicBlasterItem;
import conversion7.game.stages.world.inventory.items.weapons.FusionBlasterItem;
import conversion7.game.stages.world.inventory.items.weapons.PowerFistItem;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.IronResourceObject;
import conversion7.game.stages.world.objects.MountainDebris;
import conversion7.game.stages.world.objects.PrimalExperienceJewel;
import conversion7.game.stages.world.objects.UranusResourceObject;
import conversion7.game.stages.world.objects.totem.ExperienceTotem;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.objects.unit.WorldSquad;
import conversion7.game.stages.world.team.Team;
import conversion7.game.stages.world.team.TribeBehaviourTag;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.stages.world.unit.UnitAge;
import conversion7.game.stages.world.unit.actions.ActionEvaluation;
import conversion7.game.stages.world.unit.actions.UnitSkills;
import conversion7.game.unit_classes.animals.oligocene.Mastodon;
import conversion7.game.unit_classes.ufo.Archon;
import conversion7.game.unit_classes.ufo.ArchonSoldier;
import conversion7.game.unit_classes.ufo.BaalBoss;
import org.slf4j.Logger;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// all fun here
public class WorldAdventure {

    public static final int START_UNITS_AMOUNT_PER_TEAM = 4;
    public static final int START_AGE_RND_SCOPE = UnitAge.YOUNG.getEndsAtAgeStep();
    @Deprecated
    public static final int PLAYER_TEAM_SIZE = 4;
    private static final Logger LOG = Utils.getLoggerForClass();
    public static Array<Cell> goodCells = new Array<>();
    public static Array<Cell> cellsWip = new Array<>();
    public static Array<Cell> runAroundCellWip = new Array<>();

    public static boolean placeAiTeamGoodCellsCheckEnabled = true;

    private static Point2s getRndSearchDir() {
        Point2s searchDir = new Point2s();
        if (MathUtils.random()) {
            searchDir.x = 1;
        } else {
            searchDir.y = 1;
        }

        if (MathUtils.random()) {
            searchDir.multiply(-1);
        }
        return searchDir;
    }

    public static WorldSquad placeRndAnimalUnit(Cell cell) {
        Class<? extends Unit> clazz = cell.getArea().world.getSpawnableRndAnimalClass();
        Team team = cell.getArea().world.animalTeam;
        WorldSquad squad = WorldSquad.create(clazz, team, cell);
        LOG.info("spawnUnit: " + squad);
        return squad;
    }

    public static boolean canPlacePlayerTeam(Cell aroundCell) {
        List<Cell> goodCells = getGoodCells(aroundCell);
        int playerTeamSize = PLAYER_TEAM_SIZE;
        return goodCells.size() >= playerTeamSize;
    }

    public static void placeSecondarySquad(World world) {
        final Cell[] canSetSquadCell = new Cell[1];
        Team team = world.lastActivePlayerTeam;
        runAround(team, (int) (Area.WIDTH_IN_CELLS * 2f),
                Cell.Filters.CAN_SET_SQUAD, cell -> {
                    canSetSquadCell[0] = cell;
                });


        WorldSquad squad = WorldSquad.create(ArchonSoldier.class, team, canSetSquadCell[0]);
        squad.cell.addFloatLabel("We need to find our " + Archon.class.getSimpleName(), Color.ORANGE, true);

        int createdSquads = 1;
        Array<Cell> cellsWip = new Array<>();
        int radius = 0;
        main:
        while (radius < world.maxIterationRadius) {
            radius++;
            cellsWip.clear();
            squad.cell.getCellsAroundOnRadius(radius, cellsWip);
            cellsWip.shuffle();
            for (Cell cell : cellsWip) {
                if (cell.canBeSeized()) {
                    squad = WorldSquad.create(ArchonSoldier.class, team, cell);
                    squad.equipment.equipMeleeWeaponItem(new PowerFistItem());
                    createdSquads++;
                    if (createdSquads >= 3) {
                        break main;
                    }
                }
            }

        }
    }

    public static List<Cell> getGoodCells(Cell aroundCell) {
        Array<Cell> allCells = aroundCell.getCellsAroundFromToRadiusInclusively(0, 2);
        List<Cell> goodCells = Stream.of(allCells.toArray())
                .filter(Cell::canBeSeized)
                .collect(Collectors.toList());
        return goodCells;
    }

    public static Team placeAiTeam(Cell aroundCell) {
        World world = aroundCell.getArea().world;
        Team humanTeam = world.createHumanTeam(false);
        if (MathUtils.RANDOM.nextBoolean()) {
            humanTeam.tags.add(TribeBehaviourTag.AGGRESSIVE);
        }

        if (MathUtils.RANDOM.nextBoolean()) {
            if (MathUtils.RANDOM.nextBoolean()) {
                humanTeam.tags.add(TribeBehaviourTag.HATE_MALE);
            } else {
                humanTeam.tags.add(TribeBehaviourTag.RESPECT_MALE);
            }
        }

        if (MathUtils.RANDOM.nextBoolean()) {
            if (MathUtils.RANDOM.nextBoolean()) {
                humanTeam.tags.add(TribeBehaviourTag.RESPECT_FEMALE);
            } else {
                humanTeam.tags.add(TribeBehaviourTag.HATE_FEMALE);
            }
        }

        int maxRad = (int) Math.ceil(Area.WIDTH_IN_CELLS / 2f);

        goodCells.clear();
        main:
        for (int radius = 1; radius < maxRad; radius++) {
            cellsWip.clear();
            aroundCell.getCellsAroundOnRadius(radius, cellsWip);
            for (Cell cell : cellsWip) {
                if (cell.canBeSeized()) {
                    goodCells.add(cell);
                    if (goodCells.size == START_UNITS_AMOUNT_PER_TEAM) {
                        break main;
                    }
                }
            }
        }

        for (Cell cell : goodCells) {
            AbstractSquad squad = WorldServices.createWorldInitialClassUnit(humanTeam, cell);
            squad.setAgeStep(MathUtils.RANDOM.nextInt(START_AGE_RND_SCOPE));
        }

        return humanTeam;
    }

    public static Unit placeBaalUfo(Cell cell, Team tribe) {
        WorldServices.nextUnitGender = MathUtils.random();
        WorldSquad squad = WorldSquad.create(BaalBoss.class, tribe, cell);
        squad.setAgeStep(1);
        squad.getInventory().addItem(AtomicBlasterItem.class, 1);
        squad.getInventory().addItem(RadioactiveIsotopeItem.class, 30);
        squad.setInspirationPoints(Unit.INSPIRATION_POINTS_MAX);
        return squad.unit;
    }

    public static Team findGoodFreeTribe() {
        List<Team> tribes = Stream.of(Gdxg.core.world.teams.toArray())
                .filter(team -> !team.isUfo()
                        && team.isHumanRace()
                        && team.isAiPlayer()
                        && !team.isDefeated()
                        && !isVisibleByPlayerTeam(team))
                .collect(Collectors.toList());

        if (tribes.size() == 0) {
            return null;
        }

        Cell searchNearCell = Gdxg.core.world.lastActivePlayerTeam.getSquads().get(0).getLastCell();
        for (Team tribe : tribes) {
            if (!tribe.isDefeated() && tribe.getSquads().size > 0) {
                AbstractSquad rndSquad = tribe.getSquads().get(0);
                float dstToTargetLocation = searchNearCell.distanceTo(rndSquad.getLastCell());
                tribe.wipTribeValue = tribe.getObjectsAmount() / dstToTargetLocation;
            }
        }

        tribes.sort(Team.WIP_TRIBE_VALUE_COMPARATOR);
        return tribes.get(0);
    }

    public static boolean isVisibleByPlayerTeam(Team team) {
        Team playerTeam = Gdxg.core.world.lastActivePlayerTeam;
        for (AbstractSquad squad : team.getSquads()) {
            if (squad.getLastCell() != null && playerTeam.getVisibleCellsPlayerTribeOnly().contains(squad.getLastCell())) {
                return true;
            }
        }

        return false;
    }

    public static Cell runAround(Team team, int dstFromRndUnit, Predicate<Cell> canUseCell
            , Consumer<Cell> useCell) {
        Array<AbstractSquad> squads = team.getSquads();
        AbstractSquad rndSquad = squads.get(MathUtils.random(0, squads.size - 1));
        Cell startFrom = rndSquad.getLastCell();
        return runAround(startFrom, dstFromRndUnit, canUseCell, useCell);
    }

    public static Cell runAround(Cell aroundCell, int fromDistance, int maxDistance,
                                 Predicate<Cell> canUseCell, Consumer<Cell> useCell, boolean testPathToTarget) {
        FastAsserts.assertMoreThan(maxDistance, fromDistance);
        for (int r = fromDistance; r < maxDistance; r++) {
            runAroundCellWip.clear();
            aroundCell.getCellsAroundOnRadius(r, runAroundCellWip);
            runAroundCellWip.shuffle();
            for (Cell cell : runAroundCellWip) {
                if (canUseCell.evaluate(cell)) {
                    boolean accept = false;
                    if (testPathToTarget) {
                        if (cell == aroundCell ||
                                FindPath.getPath(aroundCell, cell) != null) {
                            accept = true;
                        }
                    } else {
                        accept = true;
                    }

                    if (accept) {
                        useCell.accept(cell);
                        return cell;
                    }
                }
            }
        }

        LOG.warn("not able to run adventure after " + (maxDistance - fromDistance) + " iterations");
        return null;
    }

    public static Cell runAround(Cell aroundCell, int fromDistance, int maxDistance,
                                 Predicate<Cell> canUseCell, Consumer<Cell> useCell) {
        return runAround(aroundCell, fromDistance, maxDistance, canUseCell, useCell, true);
    }

    public static Cell runAround(Cell aroundCell, int fromDistance, Predicate<Cell> canUseCell
            , Consumer<Cell> useCell) {
        World world = aroundCell.getArea().world;
        return runAround(aroundCell, fromDistance, world.minRadiusInCells, canUseCell, useCell);
    }

    public static void placePlayerUnits(Team team, Cell aroundCell) {
        List<Cell> goodCells = getGoodCells(aroundCell);

        WorldServices.nextUnitGender = true;
        AbstractSquad squad1 = WorldSquad.create(ArchonSoldier.class, team, goodCells.get(0));
        squad1.setAgeStep(UnitAge.YOUNG.getEndsAtAgeStep());
        UnitSkills.SKILLS_BY_ACTION_OR_EFFECT.get(ActionEvaluation.CHARGE).learn(squad1);
        UnitSkills.SKILLS_BY_ACTION_OR_EFFECT.get(ActionEvaluation.DISCORD).learn(squad1);
        if (team.playerUnitProgress > 0) {
            squad1.nextExpToTeamExp = false;
            squad1.updateExperience(team.playerUnitProgress);
            squad1.setInspirationPoints(0);
        }

        WorldServices.nextUnitGender = false;
        AbstractSquad squad2 = WorldSquad.create(ArchonSoldier.class, team, goodCells.get(1));
        squad2.setAgeStep(UnitAge.YOUNG.getEndsAtAgeStep());
        UnitSkills.SKILLS_BY_ACTION_OR_EFFECT.get(ActionEvaluation.MELEE_SWING).learn(squad2);
        UnitSkills.SKILLS_BY_ACTION_OR_EFFECT.get(ActionEvaluation.HOOK).learn(squad2);

        AbstractSquad squad3 = WorldSquad.create(ArchonSoldier.class, team, goodCells.get(2));
        squad3.setAgeStep(UnitAge.YOUNG.getEndsAtAgeStep());
        UnitSkills.SKILLS_BY_ACTION_OR_EFFECT.get(ActionEvaluation.PROVOKE).learn(squad1);
        UnitSkills.SKILLS_BY_ACTION_OR_EFFECT.get(ActionEvaluation.SLEEP).learn(squad1);

        WorldServices.nextUnitGender = MathUtils.random();
        AbstractSquad archon = WorldSquad.create(Archon.class, team, goodCells.get(3));
        UnitSkills.SKILLS_BY_ACTION_OR_EFFECT.get(ActionEvaluation.HEALING).learn(archon);
        team.setUnitControlsTribe(archon.unit);
        archon.equipment.equipRangeWeaponItem((RangeWeaponItem) new FusionBlasterItem().setQuantity(1));
        archon.setInspirationPoints(Unit.INSPIRATION_POINTS_MAX);
        int startIso = 1 + aroundCell.getArea().world.settings.resBalance;
        if (startIso > 0) {
            archon.getInventory().addItem(FusionCellItem.class, 5);
        }
    }

    public static class Events {

        public static final Consumer<Cell> PLACE_DEBRIS = cell -> new MountainDebris(cell);
        public static final Consumer<Cell> PLACE_EXP_TOTEM = cell -> new ExperienceTotem(cell);
        public static final Consumer<Cell> PLACE_Q_MAMMOTH = cell -> {
            WorldSquad squad = WorldSquad.create(Mastodon.class, Gdxg.core.world.animalTeam, cell);
            squad.setQuestUnit();
        };
        public static final Consumer<Cell> PLACE_IRON = cell -> {
            Gdxg.core.world.addImportantObj(new IronResourceObject(cell, Gdxg.core.world.animalTeam));
        };
        public static final Consumer<Cell> PLACE_URANUS = cell -> {
            Gdxg.core.world.addImportantObj(new UranusResourceObject(cell, Gdxg.core.world.animalTeam));
        };
        public static final Consumer<Cell> PLACE_AI_TRIBE = cell -> {
            WorldAdventure.placeAiTeamGoodCellsCheckEnabled = false;
            WorldAdventure.placeAiTeam(cell);
        };
        public static final Consumer<Cell> PLACE_JEWEL_EXP = cell -> new PrimalExperienceJewel(cell);
    }

}
