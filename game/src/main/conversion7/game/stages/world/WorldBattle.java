package conversion7.game.stages.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import conversion7.engine.Gdxg;
import conversion7.engine.artemis.BattleAiSystem;
import conversion7.engine.artemis.engine.time.SchedulingSystem;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.Direction;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.team.Team;
import org.slf4j.Logger;

import java.util.Iterator;

public class WorldBattle {
    public static final int FIGHT_RADIUS = 3;
    public static final Logger LOGGER = Utils.getLoggerForClass();
    private static final int BATTLE_TURNS = 4;
    private static final int AUTO_BATTLE_TURNS = BATTLE_TURNS - 1;
    public boolean isAuto;
    Array<Cell> cells;
    private World world;
    private Team attacker;
    private AbstractSquad target;
    private Cell originCell;
    private Array<AbstractSquad> squadsInFight = new Array<>();
    private Array<AbstractSquad> squadsOutOfFight = new Array<>();
    private AbstractSquad activeSquad;
    private int turn;

    public WorldBattle(World world, Team attacker, AbstractSquad target) {
        this.world = world;
        this.attacker = attacker;
        this.target = target;
        this.originCell = target.cell;
        cells = getBattleCells(originCell);
        cells.shuffle();
        originCell.floatingStatusBatch.addLine("- New battle -");
        originCell.floatingStatusBatch.flush(Color.CYAN);
        buildUnitsQueue();
    }

    public static Array<Cell> getBattleCells(Cell originCell) {
        return originCell.getCellsAround(0, FIGHT_RADIUS, new Array<>());
    }

    public static void processPostponedBattles(World world) {
        if (world.postponedBattles.size > 0) {
            AbstractSquad targetFound = null;
            AbstractSquad attackerFound = null;
            findBattles:
            for (PostponedBattle postponedBattle : world.postponedBattles) {
                if (!postponedBattle.attackerTeam.isDefeated()
                        && !postponedBattle.playerTeam.isDefeated()) {

                    targetFound = null;
                    attackerFound = null;
                    Array<Cell> battleCells = getBattleCells(postponedBattle.battleAt);

                    findTarget:
                    for (Cell cell : battleCells) {
                        if (cell.hasSquad() && cell.squad.team == postponedBattle.playerTeam) {
                            targetFound = cell.squad;
                            break;
                        }
                    }

                    findAttacker:
                    for (Cell cell : battleCells) {
                        if (cell.hasSquad() && cell.squad.team == postponedBattle.attackerTeam) {
                            attackerFound = cell.squad;
                            break;
                        }
                    }
                }

                if (targetFound != null && attackerFound != null) {
                    world.startBattle(attackerFound.team, targetFound);
                    break findBattles;
                }

            }

            world.postponedBattles.clear();
        }
    }

    public Cell getOriginCell() {
        return originCell;
    }

    public AbstractSquad getActiveSquad() {
        return activeSquad;
    }

    private boolean isWinnerKnown() {
        Team oneTeam = null;
        for (AbstractSquad squad : squadsInFight) {
            if (oneTeam == null) {
                oneTeam = squad.team;
            } else {
                if (squad.team != oneTeam) {
                    return false;
                }
            }
        }

        return true;
    }

    public void buildUnitsQueue() {
        for (Cell cell : cells) {
            if (cell.hasSquad()) {
                AbstractSquad squad = cell.getSquad();
                boolean doesTeamParticipateInBattle = squad.team == attacker || squad.team == target.team;
                if (doesTeamParticipateInBattle && squad.hasAttackAp()) {
                    squadsInFight.add(squad);
                } else {
                    squadsOutOfFight.add(squad);
                }
            }
        }
    }

    public void prepareBattleField() {
        for (AbstractSquad squad : squadsInFight) {
            squad.savedStats = new SavedUnitStats();
            squad.savedStats.attackAp = squad.getAttackAp();
            squad.savedStats.moveAp = squad.getMoveAp();
            squad.savedStats.direction = squad.getDirection();
            squad.savedStats.cell = squad.getLastCell();
        }

        for (AbstractSquad squad : squadsOutOfFight) {
            squad.removedTemporary = true;
            squad.removeFromWorld();
        }
    }

    public void startNewTurn() {
        turn++;
        if (isWinnerKnown() || !hasStepsLeft()) {
            restorePreFightStates();
            originCell.floatingStatusBatch.addImportantLine("End of battle");
            originCell.floatingStatusBatch.flush(Color.ORANGE);
            end();
        } else {
            if (turn > 1) {
                originCell.floatingStatusBatch.addLine("Battle turn " + turn);
            }
            activateSquad(squadsInFight.get(0));
//            originCell.floatingStatusBatch.addImportantLine("New turn");
//            originCell.floatingStatusBatch.flush(Color.ORANGE);
        }
    }

    private void end() {
        world.activeBattle = null;
        Gdxg.core.areaViewer.deselectCell();
    }

    private boolean hasStepsLeft() {
        if (turn > BATTLE_TURNS) {
            originCell.floatingStatusBatch.addLine("No battle turns left");
            return false;
        }
        return true;
    }

    private void restorePreFightStates() {
        // prepare to restore initial places
        for (AbstractSquad squad : squadsInFight) {
            squad.removedTemporary = true;
            squad.removeFromWorld();
        }

        for (AbstractSquad squad : squadsInFight) {
            SavedUnitStats savedStats = squad.savedStats;
            squad.power.freeNextMove = true;
            squad.returnToWorld(savedStats.cell);
            squad.getDirection().setValue(savedStats.direction.getValue());
            squad.setMoveAp(savedStats.moveAp);
            int newAttackAp = savedStats.attackAp - 1;
            squad.updateAttackAp(newAttackAp - squad.getAttackAp());
        }

        for (AbstractSquad squad : squadsOutOfFight) {
            squad.returnToWorld(squad.getRemovedOnCell());
        }
    }

    public void activateNextSquad() {
        SchedulingSystem.schedule("next unit", 1, () -> {
            validateDefeated();
            int nextIndex = squadsInFight.indexOf(activeSquad, true) + 1;
            if (squadsInFight.size > nextIndex) {
                activateSquad(squadsInFight.get(nextIndex));
            } else {
                startNewTurn();
            }
        });
    }

    private void validateDefeated() {
        Iterator<AbstractSquad> iterator = squadsInFight.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().destroyed) {
                iterator.remove();
            }
        }
    }

    private void activateSquad(AbstractSquad squad) {
        Gdxg.core.areaViewer.hideSelection();
        activeSquad = squad;
//        activeSquad.cell.floatingStatusBatch.addLine("GOOO");
        activeSquad.resetActionPoints();
        activeSquad.refreshUiPanelInWorld();
        if (activeSquad.team.isHumanPlayer()) {
            Gdxg.core.areaViewer.selectCell(activeSquad.cell);
        } else {
//            activeSquad.cell.floatingStatusBatch.flush(Color.CYAN);
            BattleAiSystem battleAiSystem = Gdxg.core.artemis.getSystem(BattleAiSystem.class);
            battleAiSystem.activateUnitAi(this);
        }
    }

    public boolean hasMember(AbstractSquad squad) {
        return squadsInFight.contains(squad, true);
    }

    public void autoCalc() {
        isAuto = true;
        BattleSides battleSides = new BattleSides(squadsInFight);
        Array<AbstractSquad> squadsInFightWip = new Array<>(squadsInFight);
        Iterator<AbstractSquad> squadsInFightWipIterator = squadsInFightWip.iterator();

        rounds:
        for (int round = 0; round < AUTO_BATTLE_TURNS; round++) {
            round:
            while (squadsInFightWipIterator.hasNext()) {
                AbstractSquad squadOnTurn = squadsInFightWipIterator.next();
                if (squadOnTurn.isRemovedFromWorld()) {
                    squadsInFightWipIterator.remove();
                } else {
                    Array<AbstractSquad> enemies = battleSides.teamsToEnemies.get(squadOnTurn.team);
                    enemies.shuffle();
                    Iterator<AbstractSquad> enemiesIterator = enemies.iterator();

                    attackAliveEnemy:
                    while (enemiesIterator.hasNext()) {
                        AbstractSquad target = enemiesIterator.next();
                        if (target.isRemovedFromWorld()) {
                            enemiesIterator.remove();
                            continue attackAliveEnemy;
                        }

                        try {
                            squadOnTurn.meleeAttack(target);
                        } catch (Exception e) {
                            LOGGER.error(e.getMessage(), e);
                        }
                        if (target.isRemovedFromWorld()) {
                            enemiesIterator.remove();
                        }
                        break attackAliveEnemy;
                    }
                }
            }
            if (!battleSides.checkStillHasEnemies()) {
                break rounds;
            }
        }

        end();

    }

    public static class PostponedBattle {

        public final Team attackerTeam;
        public final Cell battleAt;
        public Team playerTeam;

        public PostponedBattle(Team attackerTeam, AbstractSquad humanPlayerAsTarget) {
            this.attackerTeam = attackerTeam;
            this.battleAt = humanPlayerAsTarget.getLastCell();
            playerTeam = humanPlayerAsTarget.team;
        }
    }

    public class SavedUnitStats {
        public int moveAp;
        public int attackAp;
        public Cell cell;
        public Direction direction;

    }
}
