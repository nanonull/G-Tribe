package conversion7.game.stages.world.objects.controllers;

import com.badlogic.gdx.utils.Array;
import conversion7.AAATest;
import conversion7.AbstractTests;
import conversion7.game.services.WorldServices;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.objects.HumanSquad;
import conversion7.game.stages.world.unit.Unit;
import conversion7.test_steps.WorldSteps;
import org.testng.annotations.Test;

import static conversion7.game.services.WorldServices.createSomeHumanUnit;
import static conversion7.test_steps.WorldSteps.addUnitToAreaObject;
import static conversion7.test_steps.WorldSteps.addUnitsToAreaObject;
import static conversion7.test_steps.WorldSteps.removeAndValidateUnitFromAreaObject;
import static conversion7.test_steps.WorldSteps.removeUnitFromAreaObject;
import static conversion7.test_steps.asserts.WorldAsserts.assertAreaObjectContainsUnit;
import static conversion7.test_steps.asserts.WorldAsserts.assertAreaObjectContainsUnits;
import static conversion7.test_steps.asserts.WorldAsserts.assertAreaObjectDoesntContainUnit;
import static conversion7.test_steps.asserts.WorldAsserts.assertControllerIsNotValidated;
import static conversion7.test_steps.asserts.WorldAsserts.assertControllerIsValidated;

public class UnitsControllerTest extends AbstractTests {

    @Test
    public void testAddUnit() throws Exception {
        new AAATest() {
            @Override
            public void body() {
                HumanSquad humanSquad = WorldServices.createHumanSquadWithSomeUnit(World.createHumanTeam(false), WorldSteps.getNextStandaloneCell());
                Unit someUnit = createSomeHumanUnit();
                addUnitToAreaObject(someUnit, humanSquad);

                assertAreaObjectContainsUnit(humanSquad, someUnit);
                assertControllerIsValidated(humanSquad.getUnitsController());
            }
        }.run();
    }

    @Test
    public void testAddUnits() throws Exception {
        new AAATest() {
            @Override
            public void body() {
                HumanSquad humanSquad = WorldServices.createHumanSquadWithSomeUnit(World.createHumanTeam(false), WorldSteps.getNextStandaloneCell());

                Array<Unit> units = WorldServices.createSomeHumanUnits(2);
                addUnitsToAreaObject(units, humanSquad);

                assertAreaObjectContainsUnits(humanSquad, units);
                assertControllerIsValidated(humanSquad.getUnitsController());
            }
        }.run();
    }

    @Test
    public void testRemoveAndValidate() throws Exception {
        new AAATest() {

            @Override
            public void body() {
                HumanSquad humanSquad = WorldServices.createHumanSquadWithSomeUnit(World.createHumanTeam(false), WorldSteps.getNextStandaloneCell());
                Unit someUnit = createSomeHumanUnit();
                addUnitToAreaObject(someUnit, humanSquad);

                removeAndValidateUnitFromAreaObject(someUnit, humanSquad);
                assertAreaObjectDoesntContainUnit(humanSquad, someUnit);
                assertControllerIsValidated(humanSquad.getUnitsController());
            }
        }.run();
    }

    @Test
    public void testRemove() throws Exception {
        new AAATest() {

            @Override
            public void body() {
                HumanSquad humanSquad = WorldServices.createHumanSquadWithSomeUnit(World.createHumanTeam(false), WorldSteps.getNextStandaloneCell());
                Unit someUnit = createSomeHumanUnit();
                addUnitToAreaObject(someUnit, humanSquad);

                removeUnitFromAreaObject(someUnit, humanSquad);
                assertAreaObjectDoesntContainUnit(humanSquad, someUnit);
                assertControllerIsNotValidated(humanSquad.getUnitsController());
            }
        }.run();
    }
}