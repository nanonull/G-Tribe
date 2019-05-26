package conversion7.game.stages.world.unit;

import com.badlogic.gdx.graphics.Color;
import conversion7.engine.AudioPlayer;
import conversion7.engine.artemis.AnimationSystem;
import conversion7.engine.utils.MathUtils;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.objects.actions.items.ChargeAction;
import conversion7.game.stages.world.objects.totem.AttackTotem;
import conversion7.game.stages.world.objects.totem.DefenceTotem;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.objects.unit.AnimalHerd;
import conversion7.game.stages.world.objects.unit.AttackCalculation;
import conversion7.game.stages.world.team.Team;
import conversion7.game.stages.world.team.skills.AbstractSkill;
import conversion7.game.stages.world.team.skills.SkillType;
import conversion7.game.stages.world.team.skills.items.LocomotionSkill;
import conversion7.game.stages.world.team.skills.items.WeaponMasterySkill;
import conversion7.game.stages.world.unit.actions.ActionEvaluation;
import conversion7.game.stages.world.unit.effects.AbstractUnitEffect;
import conversion7.game.stages.world.unit.effects.items.*;
import conversion7.game.stages.world.unit.effects.items.spec.AgileGuyEffect;
import conversion7.game.stages.world.unit.effects.items.spec.FastGuyEffect;
import conversion7.game.unit_classes.UnitClassConstants;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.stream.Stream;

import static com.badlogic.gdx.math.MathUtils.clamp;
import static conversion7.game.stages.world.objects.actions.items.ChargeAction.getNextCellOnDir;

public class Power2 {
    public static final float BASE_DAMAGE_MLT = 0.24f;
    public static final float ANIMAL_POWER_MLT = 1f;
    public static final float ANIMAL_DAMAGE_MLT = 0.65f;
    public static final int MAX_DODGE_CHANCE_RADIUS = 70;
    public static final int MAX_AGI_DIFF_DODGE = UnitClassConstants.BASE_POWER * 2;
    public static final float CORNER_DEFENCE_BONUS = 0.25f;
    public static final float HILL_DEFENCE_BONUS = 0.25f;
    public static final float FOREST_DEFENCE_MLT = 0.5f;
    public static final int EXPERIENCE_PER_DMG = Unit.BASE_EXP_FOR_LEVEL / 30;
    public static final int EXPERIENCE_PER_KILL = Unit.BASE_EXP_FOR_LEVEL / 5;
    public static final int EXPERIENCE_PER_CAPTURE = EXPERIENCE_PER_KILL;
    public static final int EXPERIENCE_PER_FERTILIZE = EXPERIENCE_PER_KILL;
    public static final float MIN_EXP_MLT_ON_POWER_DIFF = 0.05f;
    public static final float MAX_EXP_MLT_ON_POWER_DIFF = 2.5f;
    private static final Logger LOG = Utils.getLoggerForClass();
    private static final int BASE_CHANCE_FOR_NEG_EFFECT = 50;
    private static final int FOREST_DEFENCE = 2;
    public boolean freeNextAttack;
    public boolean madeAttackOnStep;
    public boolean freeNextMove;
    public Integer overrideDodgeChance;
    public Integer overrideHitChance;
    private int currentValue = 1;
    private int maxValue = 1;
    private AreaObject ownerObj;
    private int madeAttacks;

    public Power2(AreaObject ownerObj) {
        this.ownerObj = ownerObj;
    }

    private static void applyWeaponPierceEffects(AbstractSquad attacker, Cell targCell) {
        if (attacker.equipment.hasMeleeWeap()) {
            if (attacker.equipment.getMeleeWeaponItem().canPierce()) {
                Cell nextCellAfterTarget = getNextCellOnDir(attacker.cell, targCell);
                if (attacker.canAttack(nextCellAfterTarget)) {
                    attacker.power.freeNextAttack = true;
                    nextCellAfterTarget.addFloatLabel("Pierce", Color.ORANGE);
                    attacker.initAttack(nextCellAfterTarget)
                            .setAttackAlly(false)
                            .setCustomDamage(attacker.equipment.getMeleeDamage())
                            .start();
                }
            }
        }
    }

    private static void applyWeaponShockEffects(AbstractSquad attacker, AreaObject targetObj) {
        if (targetObj.isSquad() && attacker.equipment.hasMeleeWeap()) {
            AbstractSquad targSq = targetObj.toSquad();

            int stunChance = attacker.equipment.getMeleeWeaponItem().getStunChance();
            if (MathUtils.testPercentChance(stunChance)) {
                ChargeAction.tryToPush(attacker, targSq);
            }
            if (MathUtils.testPercentChance(stunChance)) {
                targSq.effectManager.getOrCreate(StunnedEffect.class).resetTickCounter();
            }
        }
    }

    public static boolean isTargetContactMoreAllies(AreaObject target, Cell targetCell,
                                                    AbstractSquad me, Cell myCell) {
        if (target.team == null) {
            return false;
        }
        for (Cell adjCell : targetCell.getCellsAround()) {
            if (adjCell != myCell && adjCell.hasSquad()
                    && !adjCell.isNeighborOf(myCell)
                    && adjCell.getSquad().team.isAllyOf(me.team)) {
                return true;
            }
        }

        return false;
    }

    public int getBaseDamage() {
        int baseDmg;
        float mlt;
        if (ownerObj.isSquad() && ownerObj.toSquad().isAnimal()) {
            mlt = ANIMAL_DAMAGE_MLT;
        } else {
            mlt = BASE_DAMAGE_MLT;
        }
        baseDmg = Math.round((getCurrentValue() + getMaxValue()) / 2 * mlt);

        if (ownerObj.isSquad()) {
            AbstractSquad squad = ownerObj.toSquad();
            if (squad.effectManager.containsEffect(AgileGuyEffect.class)) {
                baseDmg = (int) (baseDmg * AgileGuyEffect.BOOST_MLT);
            }

            if (squad.effectManager.containsEffect(FastGuyEffect.class)) {
                baseDmg = (int) (baseDmg * FastGuyEffect.BOOST_MLT);
            }
        }
        return baseDmg;
    }

    public int getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(int currentValue) {
        this.currentValue = currentValue;
    }

    public AreaObject getOwnerObj() {
        return ownerObj;
    }

    public Team getTeam() {
        return ownerObj.getTeam();
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public boolean isAlive() {
        return currentValue > 0;
    }

    public int getRangeDamage() {
        if (ownerObj.isSquad()) {
            AbstractSquad squad = ownerObj.toSquad();
            if (squad.canRangeAttack()) {
                return squad.equipment.getRangeDamage();
            }
        }

        return 0;
    }

    public int getCriticalDamageChancePercent() {
        if (ownerObj.isSquad()) {
            AbstractSquad mySquad = ownerObj.toSquad();
            int critBoost = 0;
            if (Stream.of(mySquad.getSquadsAround().toArray())
                    .anyMatch(squad -> mySquad.team == squad.team
                            && squad.getEffectManager().containsEffect(MaturityAuraEffect.class))) {
                critBoost = MaturityAuraEffect.CRIT_BOOST_PERCENT;
            }

            CriticalDamageChanceBoostEffect criticalDamageChanceBoostEffect =
                    mySquad.getEffectManager().getEffect(CriticalDamageChanceBoostEffect.class);
            if (criticalDamageChanceBoostEffect != null) {
                critBoost = Math.max(criticalDamageChanceBoostEffect.chanceBoostPercent, critBoost);
            }

            PostFertilizationMaleEffect postFertilizationMaleEffect = mySquad.getEffectManager().getEffect(PostFertilizationMaleEffect.class);
            if (postFertilizationMaleEffect != null) {
                critBoost = Math.max(postFertilizationMaleEffect.critBoostPercent, critBoost);
            }

            Optional<PostFertilizationMaleEffect> postFertilizationMaleEffectAround = Stream.of(mySquad.getSquadsAround().toArray())
                    .filter(squad -> squad.team == squad.team
                            && squad.getEffectManager().containsEffect(PostFertilizationMaleEffect.class))
                    .map(squad -> squad.getEffectManager().getEffect(PostFertilizationMaleEffect.class))
                    .sorted()
                    .findFirst();
            if (postFertilizationMaleEffectAround.isPresent()) {
                critBoost = Math.max(postFertilizationMaleEffectAround.get().critBoostPercent, critBoost);
            }

            return critBoost;
        } else {
            return 0;
        }
    }

    private float getHeroUnitDmgMod() {
        HeroUnitEffect effect = ownerObj.toSquad().effectManager.getEffect(HeroUnitEffect.class);
        if (effect == null) {
            return 1;
        } else {
            return effect.getEffectMultiplier();
        }
    }

    public int getDefence() {
        return Math.round(
                (ownerObj.toSquad().getTotalParam(UnitParameterType.AGILITY) / 2f)
        );
    }

    public int getMeleeDamage() {
        return ownerObj.toSquad().equipment.getMeleeDamage()
                + ownerObj.power.getBaseDamage();
    }

    /** 0-1 */
    public float getValueRatio() {
        return currentValue / (float) maxValue;
    }

    public void hit(Power2 targetPower, AttackCalculation attackCalculation) {
        AbstractSquad ownerSquad = ownerObj.isSquad() ? ownerObj.toSquad() : null;
        AreaObject targetObj = targetPower.ownerObj;
        Runnable attackLogic = () -> {
            LOG.info("\nATTACK (melee={}) \nattacker: {}\ntargetPower: {}", attackCalculation.meleeAttack
                    , this.ownerObj, targetPower.ownerObj);
            if (!targetPower.isAlive()) {
                LOG.warn("executeAttack targetPower.unit is not alive!");
            }

            targetObj.batchFloatingStatusLines.start();
            if (freeNextAttack) {
                freeNextAttack = false;
            } else {
                madeAttackOnStep = true;
            }

            if (attackCalculation.canMissOrDodge()) {
                if (willMiss(attackCalculation)) {
                    ownerObj.batchFloatingStatusLines.addImportantLine("Miss");
                    attackCalculation.missOrDodge = true;
                    attackCalculation.miss = true;
                }

                if (!attackCalculation.missOrDodge && targetPower.tryDodgeAttack(this, attackCalculation)) {
                    targetObj.batchFloatingStatusLines.addImportantLine("Dodge");
                    attackCalculation.missOrDodge = true;
                    attackCalculation.dodge = true;
                }
            }

            if (attackCalculation.miss || attackCalculation.dodge) {
                boolean ownerHuman = ownerSquad != null && ownerSquad.team.isHumanPlayer();
                boolean targeHuman = targetObj.team != null && targetObj.team.isHumanPlayer();
                if (ownerHuman || targeHuman) {
                    AudioPlayer.play("fx\\click2.mp3");
                }
            } else {
                //                if (attackCalculation.meleeAttack
//                        && attackCalculation.concentrationCounterAttack
//                        && targetObj.isSquad()) {
//                    targetObj.power.freeNextAttack = true;
//                    AbstractSquad targetSq = targetObj.toSquad();
//                    targetSq.batchFloatingStatusLines.addLine("Concentration: counter attack");
//                    targetSq.getMeleeAttackCalc(ownerSquad).setCounterAttack(true).start();
//                    if (!isAlive()) {
//                        targetSq.batchFloatingStatusLines.addLine("Counter kill!");
//                        attackCalculation.attackerKilledByCounter = true;
//                    }
//                }
                if (attackCalculation.attackerKilledByCounter) {
                    attackCalculation.attackWasCancelled = true;
                } else {
                    applyDamage(targetObj, attackCalculation);
                }
            }

            // 2019-04-27 counter attack 2
//            if (attackCalculation.meleeAttack && !attackCalculation.isCounterAttack()
//                    && isAlive()
//                    && targetObj.isSquad() && targetObj.isAlive()) {
//                targetObj.power.freeNextAttack = true;
//                AbstractSquad targetSq = targetObj.toSquad();
//                targetSq.batchFloatingStatusLines.start();
//                targetSq.getMeleeAttackCalc(ownerSquad).setCounterAttack(true).start();
//                if (!isAlive()) {
//                    targetSq.batchFloatingStatusLines.addLine("Counter kill!");
//                    attackCalculation.attackerKilledByCounter = true;
//                }
//            }

            if (ownerSquad != null && attackCalculation.isRangeAttack()) {
                ownerSquad.equipment.spendRangeBullet(targetObj.getLastCell().getInventory());
                ownerSquad.getActionsController().invalidate();
            }

            targetObj.batchFloatingStatusLines.flush(Color.ORANGE);
            madeAttacks++;

            // POST
            if (attackCalculation.attackWasCancelled || attackCalculation.attackerKilledByCounter) {
                LOG.info("attackWasCancelled");
            } else {
                Cell targCell = targetObj.getLastCell();
                Team targTeam = targetPower.ownerObj.team;
                boolean targKilled = false;
                boolean attackRevealedOld = false;

                if (attackCalculation.isMeleeAttack()) {
                    applyWeaponPierceEffects(ownerSquad, targCell);
                    if (targetPower.isAlive()) {
                        attackRevealedOld = true;
                        if (ownerSquad != null) {
                            ownerSquad.updateActionPointsAfterAttack(-ActionEvaluation.MELEE_ATTACK.getAttackApCost());
                            AnimalHerd.applyAnimalScaring(ownerSquad, targetObj);
                            applyWeaponShockEffects(ownerSquad, targetObj);
                        }
                    } else {
                        targKilled = true;
                        if (ownerSquad != null) {
                            if (targTeam != null) {
                                if (targTeam.isEnemyOf(ownerSquad.team)) {
                                    attackRevealedOld = true;
                                } else {
                                    if (isVisibleToTargTeam(ownerSquad, targTeam)) {
                                        attackRevealedOld = true;
                                    }
                                }
                            }

                            ownerSquad.updateAttackAp(-1);

                            if (ownerObj.isSquad() && !ownerObj.isAnimal()
                                    && targetObj.isSquad() && targetObj.isAnimal()) {
                                AnimalHunting.hunted(ownerObj.toSquad(), targetObj.toSquad());
                            }
                        }
                    }

                } else if (attackCalculation.isRangeAttack()) {
                    if (ownerSquad != null) {
                        ownerSquad.updateActionPointsAfterAttack(-ActionEvaluation.RANGE_ATTACK.getAttackApCost());
                    }
                } else if (attackCalculation.hasCustomDamage()) {
                    LOG.info("attackCalculation.hasCustomDamage() " + attackCalculation.getCustomDamage());
                } else {
                    LOG.error("some other attack?");
                }
            }

            if (isAlive()) {
                ownerObj.validate();
            }
            ownerObj.batchFloatingStatusLines.flush(Color.ORANGE);
            targetObj.batchFloatingStatusLines.flush(Color.ORANGE);
        };

        attackLogic.run();
        World world = ownerObj.team.world;
        if (!world.isBattleActive() || !world.getActiveBattle().isAuto) {
            AnimationSystem.vectorAnimation("Attack", ownerObj.getLastCell(), targetObj.getLastCell());
        }
//        AreaViewerAnimationsHelper.highlightAnimation(this.ownerObj, ownerObj.getLastCell(), targetObj.getLastCell()
//                , "Attack", attackLogic);
    }

    private boolean isVisibleToTargTeam(AbstractSquad ownerSquad, Team targTeam) {
        Cell ownerSquadCell = ownerSquad.getLastCell();
        for (AbstractSquad visibleBySquad : ownerSquadCell.visibleBySquads) {
            if (visibleBySquad.team == targTeam && visibleBySquad.isAlive()) {
                return true;
            }
        }
        return false;
    }

    public void applyDamage(AreaObject target, AttackCalculation attackCalculation) {
        Cell targetCell = target.getLastCell();
        Cell attackerCell = this.ownerObj.getLastCell();
        AbstractSquad attacker = ownerObj.toSquad();
        int damageWip = 0;

        if (targetCell.hasCamp()) {
            attackCalculation.targetInCamp = true;
            target.cell.addFloatLabel("! Camp under attack", Color.ORANGE);
        }

        if (attackCalculation.getCustomDamage() == null) {
            damageWip = attackCalculation.getBaseDmg(this);
        } else {
            damageWip += attackCalculation.getCustomDamage();
        }
        int baseDmg = damageWip;
        LOG.info("baseDmg: {} ", baseDmg);

        // attack
        if (attackCalculation.testCritChance()) {
            target.batchFloatingStatusLines.addDebugLine("Critical hit at " + attackCalculation.getCritChance() + "%");
            int dmgAdd = 2;
            target.batchFloatingStatusLines.addDebugLine("Crit dmg +" + dmgAdd);
            damageWip += dmgAdd;
        }

        if (attackCalculation.getFlanked()) {
            int add = 2;
            target.batchFloatingStatusLines.addDebugLine("Flanked: dmg +" + add);
            damageWip += add;
        }

        LocomotionSkill locom = (LocomotionSkill) attacker.team.getTeamSkillsManager().getSkill(SkillType.LOCOMOTION);
        if (locom.isLearnStarted()) {
            int dmgAdd = locom.getDmgAdd();
            target.batchFloatingStatusLines.addDebugLine("Locomotion: dmg +" + dmgAdd);
            damageWip += dmgAdd;
        }

        WeaponMasterySkill masterySkill = (WeaponMasterySkill) attacker.team.
                getTeamSkillsManager().getSkill(SkillType.WEAPON_MASTERY);
        if (masterySkill.isLearnStarted() && attackCalculation.isWeaponUsed()) {
            int dmgAdd = masterySkill.getDmgAdd();
            target.batchFloatingStatusLines.addDebugLine("Weapon mastery: dmg +" + dmgAdd);
            damageWip += dmgAdd;
        }

        if (target.isSquad() &&
                attacker.elementType.doesAttack(target.toSquad().elementType)) {
            int add = 1;
            target.batchFloatingStatusLines.addDebugLine("Elemental dmg +" + add);
            damageWip += add;
        }

//        if (isTargetContactMoreAllies(target, targetCell, myCell)) {
//            int add = (int) Math.ceil(damageWip * 0.25f);
//            target.batchFloatingStatusLines.addImportantLine("Attention dmg +" + add);
//            damageWip += add;
//        }

        if (attacker.getLastCell().getEffectiveTotem(AttackTotem.class, attacker.team) != null) {
            damageWip += AttackTotem.BOOST;
            target.batchFloatingStatusLines.addDebugLine(AttackTotem.BOOST_HINT);
        }

        // defence
        if (targetCell.getLandscape().hasForest()) {
            int forestAbsorb = FOREST_DEFENCE;
            target.batchFloatingStatusLines.addDebugLine("Forest defence: dmg -" + forestAbsorb);
            damageWip -= forestAbsorb;
        }

        if (attacker.effectManager.containsEffect(WeakeningEffect.class)) {
            int weakEff = WeakeningEffect.DMG_MINUS;
            target.batchFloatingStatusLines.addDebugLine("Weakening: dmg " + weakEff);
            damageWip += weakEff;
        }

        if (targetCell.getLandscape().hasHill()) {
            int hillAbsorb = 1;
            target.batchFloatingStatusLines.addDebugLine("Hill defence: dmg -" + hillAbsorb);
            damageWip -= hillAbsorb;
        }

        if (hasCornerDefenceBonus(target, targetCell, attackerCell)) {
            int absorbDmg = 1;
            target.batchFloatingStatusLines.addDebugLine("Corner defence: dmg -" + absorbDmg);
            damageWip -= absorbDmg;
        }


        if (targetCell.getEffectiveTotem(DefenceTotem.class, target.team) != null) {
            damageWip -= DefenceTotem.DEF_AMOUNT;
            target.batchFloatingStatusLines.addDebugLine(DefenceTotem.BOOST_HINT);
        }

        if (target.isSquad()) {
            AbstractSquad targSq = target.toSquad();
            if (damageWip > 0) {
                int fortArmor = targSq.getFortArmor();
                if (fortArmor > 0) {
                    int dmgToArmor = Math.min(fortArmor, damageWip);
                    targSq.batchFloatingStatusLines.addDebugLine("Fortification: dmg -" + dmgToArmor);
                    targSq.updateFortArmor(-dmgToArmor);
                    damageWip -= dmgToArmor;
                }
            }

            if (damageWip > 0) {
                int equipArmor = targSq.getEquipArmor();
                if (equipArmor > 0) {
                    int dmgToArmor = Math.min(equipArmor, damageWip);
                    targSq.batchFloatingStatusLines.addDebugLine("Equipment absorbs dmg -" + dmgToArmor);
                    targSq.damageEquip(dmgToArmor);
                    damageWip -= dmgToArmor;
                }
            }
        }


        LOG.info("final damage: {} ", damageWip);
        if (damageWip <= 0) {
            target.batchFloatingStatusLines.addDebugLine("Damage absorbed");
            return;
        }

        float myPow = getMaxValue();
        float targMaxPow = target.power.getMaxValue();
        float myPowToTarget = targMaxPow / myPow;
        myPowToTarget = clamp(myPowToTarget, MIN_EXP_MLT_ON_POWER_DIFF, MAX_EXP_MLT_ON_POWER_DIFF);

        int targPowerBefore = target.power.getCurrentValue();
        int newExp = 0;
        if (target.hurtBy(damageWip, ownerObj)) {
            newExp = (int) (EXPERIENCE_PER_KILL * myPowToTarget);
        }
        int targPowerAfter = target.power.getCurrentValue();

        int tookDmg = targPowerBefore - targPowerAfter;
        newExp += (int) (EXPERIENCE_PER_DMG * tookDmg * myPowToTarget);

        if (attackCalculation.isMeleeAttack() && target.isSquad()) {
            AbstractSquad targSquad = target.toSquad();
            if (targSquad.isAlive() && MathUtils.testPercentChance(BASE_CHANCE_FOR_NEG_EFFECT)) {
                targSquad.effectManager.getOrCreate(WeakeningEffect.class).resetTickCounter();
            }
        }

        if (ownerObj.isSquad()) {
            if (newExp > 0 && target.givesExpOnHurt()) {
                attacker.updateExperience(newExp);
            }

            VengeanceHitEffect vengeanceHitEffect = attacker.effectManager.getEffect(VengeanceHitEffect.class);
            if (vengeanceHitEffect != null) {
                if (target.isAlive()) {
                    vengeanceHitEffect.hurt(target);
                } else {
                    vengeanceHitEffect.actOnHurtUnits();
                }
            }

            if (attacker.isAnimal() && target.isAlive() && target.isSquad()) {
                AbstractSquad targSq = target.toSquad();
                if (MathUtils.testPercentChance(BASE_CHANCE_FOR_NEG_EFFECT)) {
                    AnimalHerd.ATTACK_EFFECTS.shuffle();
                    Class<? extends AbstractUnitEffect> aClass = AnimalHerd.ATTACK_EFFECTS.get(0);
                    targSq.effectManager.getOrCreate(aClass).resetTickCounter();
                }
            }
        }
    }

    private boolean hasCornerDefenceBonus(AreaObject target, Cell targetCell, Cell myCell) {
        if (targetCell.isNeighborOf(myCell)
                && targetCell.isCornerCellTo(myCell)) {
            for (Cell adjCell : myCell.getCellsAround()) {
                if (targetCell.getCellsAround().contains(adjCell, true)
                        && adjCell.squad != target
                        && adjCell.squad != ownerObj) {

                    if (adjCell.canProvideCornerDefence()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean willMiss(AttackCalculation attackCalculation) {
        boolean isMelee = attackCalculation.meleeAttack;
        int hitChance;
        if (isMelee) {
            hitChance = ownerObj.toSquad().getMeleeWeaponHitChance();
        } else {
            hitChance = ownerObj.toSquad().getRangeWeaponHitChance();
        }
        if (attackCalculation.isCounterAttack()) {
            ownerObj.batchFloatingStatusLines.addDebugLine("Counter attack: hit chance x" + ConcentrationEffect.HIT_CHANCE_MLT_FOR_COUNTER_HIT);
            hitChance *= ConcentrationEffect.HIT_CHANCE_MLT_FOR_COUNTER_HIT;
        }
        ownerObj.batchFloatingStatusLines.addDebugLine("Hit chance: " + hitChance);
        return !MathUtils.testPercentChance(hitChance);
    }

    public boolean tryDodgeAttack(Power2 dodgeFrom, AttackCalculation attackCalculation) {
        if (ownerObj.canDodge()) {
            int dodgeChance;
            Integer overrideDodgeChance = this.overrideDodgeChance;
            if (overrideDodgeChance == null) {
                dodgeChance = AbstractSquad.BASE_DODGE_PERC;
                if (ownerObj.isSquad()) {
                    AbstractSquad targetSq = ownerObj.toSquad();
                    if (targetSq.relax > 0) {
                        int add = targetSq.relax * AbstractSquad.RELAX_DODGE_PERC;
                        dodgeChance += add;
                        targetSq.batchFloatingStatusLines.addDebugLine("Relax: dodge +" + add);
                        targetSq.relax = 0;
                    }
                    if (targetSq.effectManager.containsEffect(ConcentrationEffect.class)) {
                        targetSq.effectManager.removeEffectIfExist(ConcentrationEffect.class);
                        int add = ConcentrationEffect.DODGE_PERC;
                        dodgeChance += add;
                        targetSq.batchFloatingStatusLines.addDebugLine("Concentration: dodge +" + add);
                        attackCalculation.concentrationCounterAttack = true;
                    }
                    if (targetSq.effectManager.containsEffect(EvadeEffect.class)) {
                        int add = EvadeEffect.DODGE_PERC;
                        dodgeChance += add;
                        targetSq.batchFloatingStatusLines.addDebugLine("Evade: dodge +" + add);
                    }

                    if (dodgeFrom.ownerObj.isSquad()) {
                        AbstractSquad dodgeFromSq = dodgeFrom.ownerObj.toSquad();

                        if (targetSq.team != null) {
                            AbstractSkill skill = targetSq.team.getTeamSkillsManager()
                                    .getSkill(SkillType.LOCOMOTION);
                            boolean learnStarted = skill.isLearnStarted();
                            if (learnStarted) {
                                int add = skill.getCurrentLevel() * LocomotionSkill.DODGE_PERC_PER_LVL;
                                dodgeChance += add;
                                targetSq.batchFloatingStatusLines.addDebugLine("Locomotion: dodge +" + add);
                            }

                        }
                        if (isTargetContactMoreAllies(targetSq, targetSq.cell,
                                dodgeFromSq, dodgeFrom.ownerObj.cell)) {
                            targetSq.batchFloatingStatusLines.addDebugLine(
                                    "Surrounded: dodge -" + AbstractSquad.SURROUNDED_DODGE_PERC);
                            int add = AbstractSquad.SURROUNDED_DODGE_PERC;
                            dodgeChance -= add;
                        }
                    }
                }
            } else {
                dodgeChance = overrideDodgeChance;
            }

            if (dodgeChance < 0) {
                dodgeChance = 0;
            }
            ownerObj.batchFloatingStatusLines.addDebugLine("Dodge chance: " + dodgeChance + "%");
            return MathUtils.testPercentChance(dodgeChance);
        }
        return false;
    }


    public void updateMaxValue(int onVal) {
        int newMaxV = getMaxValue() + onVal;
        if (newMaxV <= 0) {
            newMaxV = 1;
        }
        setMaxValue(newMaxV);

        int newActV = getCurrentValue() + onVal;
        if (newActV <= 0) {
            newActV = 1;
        }
        setCurrentValue(newActV);
    }
}
