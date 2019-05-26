package conversion7.game.stages.battle_deprecated2;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import conversion7.engine.pools.system.PoolManager;
import conversion7.engine.utils.FastAsserts;
import conversion7.engine.utils.MathUtils;
import conversion7.engine.utils.Utils;
import conversion7.game.GdxgConstants;
import conversion7.game.stages.battle_deprecated.BattleThreadLocalSort;
import conversion7.game.stages.world.inventory.items.SkinItem;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.objects.unit.WorldSquad;
import conversion7.game.stages.world.team.Team;
import conversion7.game.stages.world.team.skills.SkillType;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.stages.world.unit.effects.UnitEffectManager;
import conversion7.game.stages.world.unit.effects.items.ForestDefenceEffect;
import conversion7.game.stages.world.unit.effects.items.FortificationEffect;
import conversion7.game.stages.world.unit.effects.items.HillDefenceEffect;
import conversion7.game.stages.world.unit.effects.items.IncreaseBattleParamsEffect;
import conversion7.game.unit_classes.animals.BaseAnimalClass;
import conversion7.game.utils.collections.Comparators;
import org.slf4j.Logger;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class BattleField {

    private static final Logger LOG = Utils.getLoggerForClass();
    private List<BattleCell> activeCells = new ArrayList<>();
    private BattleCell[][] battleCells = new BattleCell[3][3];
    private int id = Utils.getNextId();

    List<RoundStep> roundStepsWip = new ArrayList<>();
    private int roundSteps;
    private int killedAnimals;
    private Cell defenderCell;
    private boolean completed;
    /** Use attackerBattleCell */
    @Deprecated
    AbstractSquad attackerSquad;
    @Deprecated
    AbstractSquad defenderSquad;
    private BattleCell attackerBattleCell;
    private Team winnerTeam;

    public BattleField(List<Unit> attackerUnits, Cell defenderCell) {
        LOG.info("=== CREATE BATTLE {} ===", id);
        Assert.assertTrue(false, "old stuff...");
        setupCells(attackerUnits, defenderCell);
        setupSides();
        if (attackerSquad.isHumanlike() && defenderSquad.isHumanlike()) {
//            Gdxg.core.world.updateTotalTribesSeparationValue(GdxgConstants.INCREASE_TRIBES_SEPARATION_PER_BATTLE);
        }

        prepareRound();
    }

    public List<BattleCell> getActiveCells() {
        return activeCells;
    }

    public int getRoundSteps() {
        return roundSteps;
    }

    public boolean isCompleted() {
        return completed;
    }

    public BattleCell getAttackerBattleCell() {
        return attackerBattleCell;
    }

    public Team getWinnerTeam() {
        return winnerTeam;
    }

    public boolean hasWinner() {
        return winnerTeam != null;
    }

    private void setupCells(List<Unit> attackerUnits, Cell defenderCell) {
        LOG.info("attackerUnits: {} ", attackerUnits.size());
        this.defenderCell = defenderCell;
        defenderSquad = defenderCell.getSquad();
        attackerSquad = attackerUnits.get(0).getSquad();
        Assert.assertNotNull(defenderSquad);
        Assert.assertNotNull(attackerSquad);
        LOG.info("attack initiator: {} \n target: {}", attackerSquad, defenderCell);

        for (int x = 0; x < 3; x++) {
            BattleCell[] battleCellsRow = battleCells[x];
            for (int y = 0; y < 3; y++) {
                Cell cell = defenderCell.getArea().getCell(defenderCell, x - 1, y - 1);
                BattleCell battleCell;
                if (cell == attackerSquad.getLastCell()) {
                    battleCell = new BattleCell(this, cell, attackerUnits, BattleCell.Type.ATTACKER);
                    attackerBattleCell = battleCell;
                } else if (defenderCell == cell) {
                    battleCell = new BattleCell(this, cell, null, BattleCell.Type.DEFENDER);
                } else {
                    battleCell = new BattleCell(this, cell, null, BattleCell.Type.NEIGHBOR);
                }
                battleCellsRow[y] = battleCell;

                // FIXME temp simple design for 2 teams in battle
                if (battleCell.getType().equals(BattleCell.Type.ATTACKER)
                        || battleCell.getType().equals(BattleCell.Type.DEFENDER)) {
                    activeCells.add(battleCell);
                }
            }
        }
    }

    private void setupSides() {
        FastAsserts.assertMoreThan(activeCells.size(), 1);
        for (BattleCell activeCell : activeCells) {
            for (BattleCell otherCell : activeCells) {
                if (activeCell != otherCell
                        && activeCell.getSquad().isEnemyWith(otherCell.getSquad().unit)) {
                    activeCell.addEnemy(otherCell);
                }
            }
        }
    }

    private void switchAllUnitsEffects(List<Unit> units, boolean active) {
        for (Unit unit : units) {
            UnitEffectManager unitEffectManager = unit.squad.getEffectManager();
            IncreaseBattleParamsEffect battleParamsUnitEffect =
                    unitEffectManager.getEffect(IncreaseBattleParamsEffect.class);
            if (battleParamsUnitEffect != null) {
                battleParamsUnitEffect.setEnabled(active);
            }
        }
    }

    private void switchDefenderUnitsEffects(List<Unit> units, boolean active) {
        boolean hasHill = defenderCell.getLandscape().hasHill();
        boolean hasForest = defenderCell.getLandscape().hasForest();
        for (Unit unit : units) {
            if (unit.getSquad() != defenderSquad) {
                continue;
            }

            UnitEffectManager unitEffectManager = unit.squad.getEffectManager();
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

    public void start() {
        LOG.info("=== START BATTLE {} ===", id);
        // battle disabled for now
        if (true) return;
        startRound();
        finishBattle();
    }

    private void finishBattle() {
        LOG.info("=== FINISH BATTLE {} ===", id);
        if (hasWinner()) {
            LOG.info("Winner: {}", winnerTeam);
        } else {
            LOG.info("There are no final winner in battle.");
        }
        if (GdxgConstants.isAlwaysDontResurrectUnitsInBattle()) {
            LOG.warn("isAlwaysDontResurrectUnitsInBattle ACTIVE!");
        }
        if (GdxgConstants.isResurrectUnitInBattleIfResistFailed()) {
            LOG.warn("isResurrectUnitInBattleIfResistFailed ACTIVE!");
        }

        validateAndDefeatArmies();

        List<Unit> aliveUnits = collectAliveWorldUnits();
        switchAllUnitsEffects(aliveUnits, false);
        switchDefenderUnitsEffects(aliveUnits, false);
        spreadFoodFromKilledAnimals(aliveUnits);
        spreadSkinFromKilledAnimals(aliveUnits);

        completeBattleForAttacker();

        completed = true;
        LOG.info("=== BATTLE COMPLETED {} ===", id);
    }

    private void completeBattleForAttacker() {
        if (!attackerSquad.isRemovedFromWorld()) {
            List<Unit> units = new ArrayList<>();
            attackerBattleCell.getActiveUnitsMap().entrySet().forEach(unitBattleUnitEntry -> {
                if (unitBattleUnitEntry.getKey().squad.isAlive()) {
                    units.add(unitBattleUnitEntry.getKey());
                }
            });

            if (defenderSquad.isRemovedFromWorld()) {
                Assert.assertTrue(false);
//                MoveAction.completeMovement(units, defenderCell);
            } else {
                for (Unit unit : units) {
//                    unit.squad.updateActionPoints(-ActionPoints.MELEE_ATTACK);
                }
                attackerSquad.validate();
            }
        }
    }

    private List<Unit> collectAliveWorldUnits() {
        ArrayList<Unit> units = new ArrayList<>();
        for (BattleCell activeCell : activeCells) {
            if (!activeCell.isAlive()) {
                continue;
            }

            Assert.assertTrue(activeCell.getSquad().unit.squad.isAlive());
            Unit unit = activeCell.getSquad().unit;
            Assert.assertTrue(unit.squad.isAlive());
            units.add(unit);

        }
        Assert.assertFalse(units.isEmpty());
        return units;
    }

    private void spreadFoodFromKilledAnimals(List<Unit> aliveUnits) {
        int totalFoodFromAnimals = killedAnimals * BaseAnimalClass.FOOD_FROM_ONE_UNIT_TOTAL;

        ObjectSet<AreaObject> objectWithUpdatedFood = PoolManager.OBJECT_SET_POOL.obtain();
        while (totalFoodFromAnimals > 0) {
            Collections.shuffle(aliveUnits, MathUtils.RANDOM);
            for (Unit aliveUnit : aliveUnits) {
                totalFoodFromAnimals -= 2;
                AbstractSquad object = aliveUnit.getSquad();
                objectWithUpdatedFood.add(object);

//                int takeFood = Team.howManyFoodCouldGrab(object.getTeam(), 2);
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

    private void spreadSkinFromKilledAnimals(List<Unit> aliveUnits) {
        Array<Unit> unitsWithFlayingSkill = PoolManager.ARRAYS_POOL.obtain();

        // collect skilled units
        for (Unit aliveUnit : aliveUnits) {
            if (aliveUnit.getSquad().getTeam().getTeamSkillsManager()
                    .getSkill(SkillType.HUNTING).isLearnStarted()) {
                unitsWithFlayingSkill.add(aliveUnit);
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

    private void validateAndDefeatArmies() {
        for (int i = activeCells.size() - 1; i >= 0; i--) {
            BattleCell activeCell = activeCells.get(i);
            activeCell.validateUnits();
            WorldSquad.killUnit(activeCell.getSquad());
        }
    }

    /** Steps in {@link #prepareRound()} */
    private void startRound() {
        for (RoundStep roundStep : roundStepsWip) {
            roundSteps++;
            LOG.info("roundStep {}", roundSteps);
            BattleUnit winner = roundStep.execute();
            if (winner != null) {
                this.winnerTeam = winner.getUnit().getSquad().getTeam();
                return;
            }
        }
    }

    private void prepareRound() {
        List<Unit> units = collectAliveWorldUnits();
        switchAllUnitsEffects(units, true);
        switchDefenderUnitsEffects(units, true);

        roundStepsWip.clear();
        roundStepsWip.add(new RoundStep(this, RoundStep.Type.RANGE));
        roundStepsWip.add(new RoundStep(this, RoundStep.Type.RANGE));
        roundStepsWip.add(new RoundStep(this, RoundStep.Type.MIXED));
        roundStepsWip.add(new RoundStep(this, RoundStep.Type.MIXED));
    }

    protected void updateKilledAnimals(int plus) {
        this.killedAnimals += plus;
    }
}
