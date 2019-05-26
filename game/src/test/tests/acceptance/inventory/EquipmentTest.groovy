package tests.acceptance.inventory

import conversion7.engine.utils.Utils
import conversion7.game.stages.world.inventory.InventoryItemStaticParams
import conversion7.game.stages.world.inventory.items.SkinItem
import conversion7.game.stages.world.inventory.items.weapons.StickItem
import conversion7.game.stages.world.unit.Unit
import conversion7.game.stages.world.unit.UnitParameterType
import org.fest.assertions.api.Assertions
import org.slf4j.Logger
import shared.BaseGdxgSpec

public class EquipmentTest extends BaseGdxgSpec {

    private static final Logger LOG = Utils.getLoggerForClass();

    public void 'test MeleeWeaponDamage'() {
        given:
        def squad = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextNeighborCell());

        Unit unit1 = squad.unit;

        int damage1 = unit1.getMeleeDamage();
        assert damage1 > 0

        when:
        unit1.getEquipment().equipMeleeWeaponItem(new StickItem());
        int damage2Act = unit1.getMeleeDamage();
        int damage2Exp = Unit.calculateMeleeDamage(InventoryItemStaticParams.STICK.meleeDamage,
                unit1.getTotalParam(UnitParameterType.STRENGTH), 1f)

        then:
        assert damage2Act == damage2Exp
    }

    public void 'test ClothesArmor'() {
        given:
        def squad = worldSteps.createUnit(
                worldSteps.createHumanTeam(),
                worldSteps.getNextNeighborCell());
        Unit unit1 = squad.unit;

        int armorNoClothes = unit1.getArmor();
        Assertions.assertThat(armorNoClothes).isGreaterThanOrEqualTo(0);

        when:
        unit1.getEquipment().equipClothesItem(new SkinItem());
        int armorWithClothesAct = unit1.getArmor();

        then:
        assert armorWithClothesAct == Unit.calculateArmor(
                InventoryItemStaticParams.SKIN.armor, unit1.getTotalParam(UnitParameterType.VITALITY))
    }

}
