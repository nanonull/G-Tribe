package tests.acceptance.world.team.skills

import conversion7.game.stages.world.inventory.items.SkinItem
import conversion7.game.stages.world.objects.unit.AbstractSquad
import conversion7.game.stages.world.team.Team
import conversion7.game.stages.world.team.skills.items.FlayingSkill
import conversion7.game.stages.world.unit.Unit
import conversion7.game.unit_classes.animals.BaseAnimalClass
import shared.BaseGdxgSpec
import shared.asserts.WorldAsserts

class FlayingSkillTest extends BaseGdxgSpec {

    public void test_FlayingSkillInBattle_noSkill() {
        given:
        Team team1 = worldSteps.createHumanTeam();
        Team team2 = worldSteps.createAnimalTeam();
        AbstractSquad army1 = worldSteps.createUnit(team1, worldSteps.getNextNeighborCell());
        Unit unit1 = army1.unit;
        worldSteps.makeUnitInvincible(unit1);

        AbstractSquad animalHerd = worldSteps.createUnit(team2, worldSteps.getNextNeighborCell());

        when:
        worldSteps.prepareForSuccessfulHit(army1, animalHerd)
        battleSteps.startBattle(army1, animalHerd);

        then:
        WorldAsserts.assertWorldIsActiveStage();
        WorldAsserts.assertAreaObjectDefeated(animalHerd);
        WorldAsserts.assertInventoryDoesntContainItem(army1.getInventory(), SkinItem.class);
    }

    public void test_FlayingSkillInBattle_withSkill() {
        given:
        Team team1 = worldSteps.createHumanTeam();
        Team team2 = worldSteps.createAnimalTeam();

        AbstractSquad army1 = worldSteps.createUnit(team1, worldSteps.getNextNeighborCell());
        Unit unit1 = army1.unit;
        worldSteps.makeUnitInvincible(unit1);
        worldSteps.teamLearnsSkill(team1, FlayingSkill.class);

        AbstractSquad animalHerd = worldSteps.createUnit(team2, worldSteps.getNextNeighborCell());

        when:
        battleSteps.startBattle(army1, animalHerd);

        then:
        WorldAsserts.assertWorldIsActiveStage();
        WorldAsserts.assertAreaObjectDefeated(animalHerd);
        WorldAsserts.assertInventoryContainsItem(army1.getInventory(),
                SkinItem.class, BaseAnimalClass.SKIN_FROM_ONE_UNIT);
    }

    public void test_FlayingSkill_equipment() {
        given:
        lockCore()
        Team humanTeam = worldSteps.createHumanTeam();
        def humanSquad = worldSteps.createUnit(
                humanTeam,
                worldSteps.getNextNeighborCell());

        Unit unit1 = humanSquad.unit
        assert !unit1.equipment.couldEquip(new SkinItem())
        releaseCoreAndWaitNextCoreStep()

        when:
        worldSteps.teamLearnsSkill(humanTeam, FlayingSkill.class);
        then:
        assert unit1.equipment.couldEquip(new SkinItem())
    }

}
