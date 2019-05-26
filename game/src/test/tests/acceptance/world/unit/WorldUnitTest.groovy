package tests.acceptance.world.unit

import conversion7.engine.utils.Utils
import conversion7.game.GdxgConstants
import conversion7.game.stages.world.inventory.items.ArrowItem
import conversion7.game.stages.world.landscape.Cell
import conversion7.game.stages.world.objects.unit.AbstractSquad
import conversion7.game.stages.world.team.Team
import conversion7.game.stages.world.team.events.UnitDefeatedEvent
import conversion7.game.stages.world.unit.Power2
import conversion7.game.stages.world.unit.Unit
import conversion7.game.stages.world.unit.UnitAge
import conversion7.game.stages.world.unit.UnitParameterType
import conversion7.game.stages.world.unit.effects.items.HungerEffect
import conversion7.game.stages.world.unit.effects.items.InjuryEffect
import conversion7.game.stages.world.unit.effects.items.ThirstEffect
import conversion7.game.unit_classes.humans.theOldest.SahelanthropusTchadensis
import org.slf4j.Logger
import org.testng.Assert
import shared.BaseGdxgSpec
import shared.asserts.WorldAsserts
import spock.lang.Ignore
import tests.NoWorldTest

import static org.fest.assertions.api.Assertions.assertThat

class WorldUnitTest extends BaseGdxgSpec {

    private static final Logger LOG = Utils.getLoggerForClass(WorldUnitTest)

    void 'test create unit on world cell'() {
        when:
        Team team = worldSteps.createHumanTeam()
        def cell = worldSteps.getNextNeighborCell()
//        AbstractSquad squad = WorldServices.createUnit(team, cell, SahelanthropusTchadensis)

        then:
        assert cell.squad == squad
        assert cell.squad.unit == squad.unit
    }

    void 'test NewUnitAddedToTeam'() {
        when:
        Team team = worldSteps.createHumanTeam()
        def cell = worldSteps.getNextNeighborCell()
//        AbstractSquad squad = WorldServices.createUnit(team, cell, SahelanthropusTchadensis)
        def unit = squad.unit

        then:
        assert team.squads.contains(squad)
        assert team.teamClassesManager.allTeamUnits.contains(squad.unit)
        WorldAsserts.assertTeamControllerContainsUnit(true, team.getTeamClassesManager(), unit)
        WorldAsserts.assertTeamClassesManagerContainsInfoAbout(team.getTeamClassesManager(), (Class<Unit>) unit.getClass(), 1)
    }

    void 'test unit defeated'() {
        given:
        lockCore()
        worldSteps.createTeamTempGarantNoZeroTeamsInWorld()

        Team team = worldSteps.createHumanTeam()
        def tchadensis = SahelanthropusTchadensis
        WorldAsserts.assertTeamClassesManagerContainsInfoAbout(team.getTeamClassesManager()
                , tchadensis, null)

        def cell = worldSteps.getNextNeighborCell()
//        AbstractSquad squad = WorldServices.createUnit(team, cell, tchadensis)
        def unit = squad.unit
        releaseCoreAndWaitNextCoreStep()

        when:
        worldSteps.defeatKillUnit(unit)

        then:
        assert !team.teamClassesManager.allTeamUnits.contains(squad.unit)
        WorldAsserts.assertTeamControllerContainsUnit(false, team.getTeamClassesManager(), unit)
        WorldAsserts.assertTeamClassesManagerContainsInfoAbout(team.getTeamClassesManager()
                , (Class<Unit>) unit.getClass(), 0)
        WorldAsserts.assertAreaObjectDefeated(squad, true, false)

        when:
        worldSteps.rewindTeamsToStartNewWorldStep()
        waitForNextCoreStep()

        then:
        WorldAsserts.assertAreaObjectDefeated(squad, true, true)

    }

    void 'test UnitDiesDueToHunger'() {
        given:
        GdxgConstants.AREA_OBJECT_AI = false

        Unit unit = null
        def humanSquad1 = null
        commonCoreStep {
            humanSquad1 = worldSteps.createUnit(
                    worldSteps.createHumanTeam(),
                    worldSteps.getNextStandaloneCell())

            worldSteps.createTeamTempGarantNoZeroTeamsInWorld()

            Cell cell = humanSquad1.getLastCell()
            worldSteps.makePerfectConditionsOnCell(cell)
            cell.setFood(0)
            unit = humanSquad1.unit
            unit.getBaseParams().put(UnitParameterType.HEALTH, 2)
        }

        and:
        unit.updateFood(-1)

        when:
        worldSteps.rewindTeamsToStartNewWorldStep()

        then:
        WorldAsserts.assertUnitHasNoEffect(unit, HungerEffect.class)

        when: 'no food'
//        unit.updateFood(-unit.getFood())
        worldSteps.rewindTeamsToStartNewWorldStep()

        then:
        assert unit.getTotalParam(UnitParameterType.HEALTH) == 1
        WorldAsserts.assertUnitAlive(unit)
        WorldAsserts.assertUnitHasEffect(unit, HungerEffect.class)

        when:
        worldSteps.rewindTeamsToStartNewWorldStep()

        then:
        WorldAsserts.assertUnitDead(unit)
        WorldAsserts.assertAreaObjectDefeated(humanSquad1, true, true)
    }

    void 'test UnitDiesDueToThirst'() {
        given:
        GdxgConstants.AREA_OBJECT_AI = false

        Unit unit = null
        def humanSquad1 = null
        commonCoreStep {
            humanSquad1 = worldSteps.createUnit(
                    worldSteps.createHumanTeam(),
                    worldSteps.getNextStandaloneCell())

            worldSteps.createTeamTempGarantNoZeroTeamsInWorld()

            Cell cell = humanSquad1.getLastCell()
            worldSteps.makePerfectConditionsOnCell(cell)
            cell.setWater(0)
            unit = humanSquad1.unit
            unit.getBaseParams().put(UnitParameterType.HEALTH, 2)
        }

        when:
        unit.updateWater(-1)
        worldSteps.rewindTeamsToStartNewWorldStep()

        then:
        assert unit.getTotalParam(UnitParameterType.HEALTH) == 2
        WorldAsserts.assertUnitHasNoEffect(unit, ThirstEffect.class)

        when:
//        unit.updateWater(-unit.getWater())
        worldSteps.rewindTeamsToStartNewWorldStep()

        then:
        assert unit.getTotalParam(UnitParameterType.HEALTH) == 1
        WorldAsserts.assertUnitAlive(unit)
        WorldAsserts.assertUnitHasEffect(unit, ThirstEffect.class)

        when:
        worldSteps.rewindTeamsToStartNewWorldStep()

        then:
        WorldAsserts.assertUnitDead(unit)
        WorldAsserts.assertAreaObjectDefeated(humanSquad1, true, true)
    }

    @Ignore
    void test_UnitResistsToDeath() {
        given:
        AbstractSquad squad = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextStandaloneCell())
        Unit unit = squad.unit
        int preStrength = unit.getTotalParam(UnitParameterType.STRENGTH)
        int preAgility = unit.getTotalParam(UnitParameterType.AGILITY)

        when:
        worldSteps.defeatKillUnit(unit)

        // old
        Assert.assertTrue(false, "review")
        unit.diesInBattle()
        battleSteps.setResurrectUnitInBattleIfResistFailed(true)
//        battleSteps.startBattle(squad, humanSquad2, false);

        then:
        WorldAsserts.assertAreaObjectDefeated(squad, false)
        WorldAsserts.assertUnitHasHealth(unit, Unit.HEALTH_AFTER_BACK_TO_LIFE)
        WorldAsserts.assertUnitHasEffect(unit, InjuryEffect.class)
        assertThat(unit.getTotalParam(UnitParameterType.STRENGTH)).isEqualTo(preStrength - InjuryEffect.DECREASE_PARAMS_BY_VALUE)
        assertThat(unit.getTotalParam(UnitParameterType.AGILITY)).isEqualTo(preAgility - InjuryEffect.DECREASE_PARAMS_BY_VALUE)
    }

    @Ignore
    @NoWorldTest
    def 'test hit chance'() {
        given:
        AbstractSquad squad = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextStandaloneCell())
        Unit unit = squad.unit
        unit.equipment.equipRangeBulletsItem(new ArrowItem())

        println unit.hitChanceByAgility
        println unit.rangeWeaponHitChance
        unit.baseParams.update(UnitParameterType.AGILITY, unit.baseParams.get(UnitParameterType.AGILITY))
        println unit.hitChanceByAgility
        println unit.rangeWeaponHitChance

        assert 0: "ignored"
    }

    @NoWorldTest
    def 'test dodge chance'() {
        given:
        AbstractSquad squad = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextStandaloneCell())
        Unit unit = squad.unit
        unit.equipment.equipRangeBulletsItem(new ArrowItem())

        AbstractSquad squad2 = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextStandaloneCell())
        Unit unit2 = squad2.unit

        and: "// diff < 0 ---> dodge chance < BASE_DODGE_CHANCE"
        unit2.baseParams.put(UnitParameterType.AGILITY, unit.baseParams.get(UnitParameterType.AGILITY) + 2)
        assert unit.dodgeChance(unit2) < Power2.BASE_DODGE_CHANCE

        unit2.baseParams.put(UnitParameterType.AGILITY, unit.baseParams.get(UnitParameterType.AGILITY) + Power2.MAX_AGI_DIFF_DODGE)
        println unit.dodgeChance(unit2)
        assert unit.dodgeChance(unit2) < Power2.BASE_DODGE_CHANCE

        and: "// diff = 0 ---> dodge chance = BASE_DODGE_CHANCE"
        unit2.baseParams.put(UnitParameterType.AGILITY, unit.baseParams.get(UnitParameterType.AGILITY))
        println unit.dodgeChance(unit2)
        assert unit.dodgeChance(unit2) == Power2.BASE_DODGE_CHANCE

        and: "// diff > 0 ---> dodge chance > BASE_DODGE_CHANCE"
        unit2.baseParams.put(UnitParameterType.AGILITY, unit.baseParams.get(UnitParameterType.AGILITY) - 2)
        println unit.dodgeChance(unit2)
        assert unit.dodgeChance(unit2) > Power2.BASE_DODGE_CHANCE

        unit2.baseParams.put(UnitParameterType.AGILITY, unit.baseParams.get(UnitParameterType.AGILITY) - 5)
        println unit.dodgeChance(unit2)
        assert unit.dodgeChance(unit2) > Power2.BASE_DODGE_CHANCE

        unit2.baseParams.put(UnitParameterType.AGILITY, unit.baseParams.get(UnitParameterType.AGILITY) - Power2.MAX_AGI_DIFF_DODGE)
        println unit.dodgeChance(unit2)
        assert unit.dodgeChance(unit2) > Power2.BASE_DODGE_CHANCE
    }

    def 'test exp from dmg'() {
        given:
        AbstractSquad squad = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextStandaloneCell())
        Unit unit = squad.unit
        unit.equipment.equipRangeBulletsItem(new ArrowItem())

        AbstractSquad squad2 = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextStandaloneCell())
        Unit unit2 = squad2.unit

        when:
        def inExp = unit.experience
        worldSteps.prepareForSuccessfulHit(unit.squad, unit2.squad)
        unit.hit(unit2, true)

        then:
        assert unit.experience > inExp
    }

    def 'test level steps'() {
        given:
        AbstractSquad squad = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextNeighborCell())

        def expFor2ndLevel = Unit.BASE_EXP_FOR_LEVEL

        assert squad.unit.expLevel == 0
        assert squad.unit.expLevelUi == 1
        assert squad.unit.getExperienceOnNextLevel() == expFor2ndLevel
        assert squad.unit.getExperienceForNextLevel() == expFor2ndLevel
        assert squad.unit.getCurrentLevelExperience() == 0
        assert squad.unit.getExperienceForNextLevelLeft() == expFor2ndLevel
        assert squad.unit.getExpLevelProgressPercent() == 0

        when:
        int halfOf2ndLvl = expFor2ndLevel / 2
        squad.unit.updateExperience(halfOf2ndLvl)
        then:
        assert squad.unit.expLevel == 0
        assert squad.unit.expLevelUi == 1
        assert squad.unit.getExperienceOnNextLevel() == expFor2ndLevel
        assert squad.unit.getExperienceForNextLevel() == expFor2ndLevel
        assert squad.unit.getCurrentLevelExperience() == halfOf2ndLvl
        assert squad.unit.getExperienceForNextLevelLeft() == halfOf2ndLvl
        assert squad.unit.getExpLevelProgressPercent() == 50

        when:
        squad.unit.updateExperience(halfOf2ndLvl)
        def expOn3rdLevel = Unit.BASE_EXP_FOR_LEVEL * 3
        then: 'got 2nd lvl'
        assert squad.unit.expLevel == 1
        assert squad.unit.expLevelUi == 2
        assert squad.unit.experience == expFor2ndLevel
        assert squad.unit.getExperienceOnNextLevel() == expOn3rdLevel
        assert squad.unit.getExperienceForNextLevel() == expOn3rdLevel - expFor2ndLevel
        assert squad.unit.getCurrentLevelExperience() == 0
        assert squad.unit.getExperienceForNextLevelLeft() == expOn3rdLevel - expFor2ndLevel
        assert squad.unit.getExpLevelProgressPercent() == 0

        when:
        def halfOf3rdLevel = 1000
        squad.unit.updateExperience(halfOf3rdLevel)
        then: 'got half of 3rd lvl'
        assert squad.unit.expLevel == 1
        assert squad.unit.expLevelUi == 2
        assert squad.unit.getExperienceOnNextLevel() == expOn3rdLevel
        assert squad.unit.getExperienceForNextLevel() == expOn3rdLevel - expFor2ndLevel
        assert squad.unit.getCurrentLevelExperience() == halfOf3rdLevel
        assert squad.unit.getExperienceForNextLevelLeft() == halfOf3rdLevel
        assert squad.unit.getExpLevelProgressPercent() == 50

        when:
        def expFor4thLvl = Unit.BASE_EXP_FOR_LEVEL * 6
        squad.unit.updateExperience(halfOf3rdLevel)
        then: 'got 3rd lvl'
        assert squad.unit.expLevel == 2
        assert squad.unit.expLevelUi == 3
        assert squad.unit.getExperienceOnNextLevel() == expFor4thLvl
        assert squad.unit.getExperienceForNextLevel() == expFor4thLvl - expOn3rdLevel
        assert squad.unit.getCurrentLevelExperience() == 0
        assert squad.unit.getExperienceForNextLevelLeft() == expFor4thLvl - expOn3rdLevel
        assert squad.unit.getExpLevelProgressPercent() == 0

        when:
        def halfOf4thLevel = 1500
        squad.unit.updateExperience(halfOf4thLevel)
        then: 'got half of 3rd lvl'
        assert squad.unit.expLevel == 2
        assert squad.unit.expLevelUi == 3
        assert squad.unit.getExperienceOnNextLevel() == expFor4thLvl
        assert squad.unit.getExperienceForNextLevel() == expFor4thLvl - expOn3rdLevel
        assert squad.unit.getCurrentLevelExperience() == halfOf4thLevel
        assert squad.unit.getExperienceForNextLevelLeft() == halfOf4thLevel
        assert squad.unit.getExpLevelProgressPercent() == 50

        when:
        worldSteps.rewindTeamsToStartNewWorldStep()
        then:
        assert squad.unit.experienceOnStep == 0
    }

    def 'test unit Killed By Age'() {
        given:
        def team = worldSteps.createHumanTeam()
        AbstractSquad squad = worldSteps.createUnit(
                team,
                worldSteps.getNextNeighborCell())
        AbstractSquad squad2AnotherSquad = worldSteps.createUnit(
                team,
                worldSteps.getNextNeighborCell())
        assert !squad.unit.willDieOfAge

        AbstractSquad squad3AnotherTeam = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextStandaloneCell())
        when:
        squad.unit.setAgeStep(Unit.DIES_AT_AGE_STEP - 1)
        then:
        assert squad.unit.age == UnitAge.OLD
        assert !squad.unit.willDieOfAge

        when:
        worldSteps.rewindTeamsToStartNewWorldStep()
        then:
        assert squad.unit.age == UnitAge.OLD
        assert squad.unit.willDieOfAge
        assert squad.unit.alive
        assert !squad.isRemovedFromWorld()

        when:
        worldSteps.rewindTeamsToStartNewWorldStep()
        def event = worldSteps.getEvent(UnitDefeatedEvent, squad.team)

        then:
        assert !squad.unit.alive
        assert squad.isRemovedFromWorld()
        assert event
    }


}
