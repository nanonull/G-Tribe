package conversion7.game.stages.world.objects.unit;

import com.badlogic.gdx.utils.ObjectSet;
import conversion7.engine.AudioPlayer;
import conversion7.engine.geometry.Point2s;
import conversion7.engine.utils.MathUtils;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.unit.Power2;
import org.slf4j.Logger;

public class AttackCalculation {
    private static final Logger LOG = Utils.getLoggerForClass();
    private final AbstractSquad attacker;
    private final Cell targCell;
    ObjectSet<AreaObject> targObjects = new ObjectSet<>();
    public boolean meleeAttack = true;
    /** not melee not range (bow) */
    private Integer customDamage;
    private boolean hasRangeWeap;
    boolean hasMeleeWeap;
    public boolean missOrDodge;
    private Boolean flanked;
    private boolean attackAlly;
    @Deprecated
    public boolean concentrationCounterAttack;
    public boolean miss;
    public boolean dodge;
    public boolean attackerKilledByCounter;
    public boolean attackWasCancelled;
    private boolean counterAttack;
    public boolean targetInCamp;

    public AttackCalculation(AbstractSquad attacker, Cell targCell) {
        this.attacker = attacker;
        this.targCell = targCell;
    }

    public Integer getCustomDamage() {
        return customDamage;
    }

    public boolean isCounterAttack() {
        return counterAttack;
    }

    public AttackCalculation setCustomDamage(int customDamage) {
        this.customDamage = customDamage;
        return this;
    }

    public Boolean getFlanked() {
        return flanked;
    }

    public void setFlanked(boolean flanked) {
        this.flanked = flanked;
    }

    public boolean isRangeAttack() {
        return customDamage == null && !meleeAttack;
    }

    public boolean isMeleeAttack() {
        return customDamage == null && meleeAttack;
    }

    public AttackCalculation setMeleeAttack(boolean meleeAttack) {
        this.meleeAttack = meleeAttack;
        return this;
    }

    public boolean isWeaponUsed() {
        return hasMeleeWeap || hasRangeWeap;
    }

    public int getCritChance() {
        return attacker.power.getCriticalDamageChancePercent();
    }

    public AttackCalculation setAttackAlly(boolean attackAlly) {
        this.attackAlly = attackAlly;
        return this;
    }

    public AttackCalculation setCounterAttack(boolean counterAttack) {
        this.counterAttack = counterAttack;
        return this;
    }

    public void start() {
        if (targCell.hasCamp()) {
            targObjects.add(targCell.camp);
        } else {
            if (targCell.hasSquad()) {
                targObjects.add(targCell.squad);
            } else {
                for (AreaObject areaObject : targCell.getObjectsOnCell()) {
                    if (areaObject.hasPower()) {
                        targObjects.add(areaObject);
                    }
                }
            }
        }

        for (AreaObject targObject : targObjects) {
            boolean canAttack = false;
            if (targObject.isSquad()) {
                if (attackAlly || attacker.canAttack(targObject.toSquad())) {
                    canAttack = true;
                }
            } else {
                if (targObject.hasPower()) {
                    canAttack = true;
                }
            }

            if (canAttack) {
                hit(targObject);
            }
        }

        if (targObjects.size > 0) {
            if (MathUtils.random()) {
                AudioPlayer.play("fx\\wooh7.mp3");
            } else {
                AudioPlayer.play("fx\\2\\tic.mp3").setVolume(0.5f);
            }
        }
    }

    private void hit(AreaObject targObject) {
        if (targObject.hasPower()) {
            calcFlanked(attacker.cell, targObject);
            attacker.power.hit(targObject.power, this);
        }
    }

    public int getBaseDmg(Power2 power) {
        if (customDamage == null) {
            if (meleeAttack) {
                hasMeleeWeap = power.getOwnerObj().toSquad().equipment.hasMeleeWeap();
                return power.getMeleeDamage();
            } else {
                hasRangeWeap = power.getOwnerObj().toSquad().equipment.hasRangeWeap();
                return power.getRangeDamage();
            }
        } else {
            return customDamage;
        }
    }

    public boolean canMissOrDodge() {
        return getCustomDamage() == null;
    }

    public boolean testCritChance() {
        return MathUtils.testPercentChance(getCritChance());
    }

    public AttackCalculation calcFlanked(Cell from, AreaObject target) {
        flanked = false;
        if (target.hasDirection()) {
            Point2s diffWithCell = from.getDiffWithCell(target.cell);
            diffWithCell.trim(1);
            flanked = target.getDirection().isFlankedBy(diffWithCell);
        }
        return this;
    }

    public boolean hasCustomDamage() {
        return customDamage != null && customDamage > 0;
    }
}
