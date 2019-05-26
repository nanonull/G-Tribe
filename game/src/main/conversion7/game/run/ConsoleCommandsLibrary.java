package conversion7.game.run;

import com.badlogic.gdx.utils.Array;
import conversion7.engine.Gdxg;
import conversion7.engine.pools.system.PoolManager;
import conversion7.game.PackageReflectedConstants;
import conversion7.game.stages.world.ai_deprecated.AiTeamControllerOld;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.ui.Console;
import conversion7.tests_standalone.RuntimeTestInvoker;
import conversion7.tests_standalone.misc.TestsCustomScene3d;
import org.testng.Assert;

public class ConsoleCommandsLibrary {

    private static final String THERE_IS_NO_ACTIVE_OBJECT = "There is no active object!";

    public static ConsoleCommand runMainTest() {
        return new ConsoleCommand("dev_test", "run currently mapped dev test") {
            @Override
            public boolean body() {
                RuntimeTestInvoker.run();
                return true;
            }
        };
    }

    public static ConsoleCommand switchTestObject() {
        return new ConsoleCommand("test_object", "switchTestObject") {
            @Override
            public boolean body() {
                TestsCustomScene3d.switchTestObject(Integer.parseInt(ConsoleCommandExecutor.args[0]));
                return true;
            }
        };
    }

    public static ConsoleCommand switchTestMode() {
        return new ConsoleCommand("test_mode", "switchTestMode") {
            @Override
            public boolean body() {
                TestsCustomScene3d.switchTestMode(Integer.parseInt(ConsoleCommandExecutor.args[0]));
                return true;
            }
        };
    }

    // add 10 male pans: add_unit Pan 10 m
    public static ConsoleCommand addUnitsToActiveObject() {
        return new ConsoleCommand("add_unit", "add unit(s) to selected object", "<class> <amount> <gender>") {
            @Override
            public boolean body() {
                AreaObject selectedObject = Gdxg.getAreaViewer().getSelectedSquad();
                if (!(selectedObject instanceof AbstractSquad)) {
                    Gdxg.clientUi.getConsole().logErrorToConsole("No squad selected!");
                }
                AbstractSquad squad = (AbstractSquad) selectedObject;
                if (squad == null) {
                    Gdxg.clientUi.getConsole().logErrorToConsole(THERE_IS_NO_ACTIVE_OBJECT);
                    return false;
                }

                if (ConsoleCommandExecutor.args == null || ConsoleCommandExecutor.args.length < 3) {
                    logWrongFormatError();
                    return false;
                }

                String genderString = ConsoleCommandExecutor.args[2];
                boolean maleGender;
                if (genderString.equalsIgnoreCase("m")) {
                    maleGender = true;
                } else if (genderString.equalsIgnoreCase("f")) {
                    maleGender = false;
                } else {
                    Gdxg.clientUi.getConsole().logErrorToConsole("gender must be 'm' or 'f'. Actual: " + genderString);
                    return false;
                }

                int amount;
                try {
                    amount = Integer.parseInt(ConsoleCommandExecutor.args[1]);
                } catch (NumberFormatException e) {
                    Gdxg.clientUi.getConsole().logErrorToConsole("Amount should be number!");
                    return false;
                }
                if (amount < 1) {
                    Gdxg.clientUi.getConsole().logErrorToConsole("Amount should be >= 1. Actual: " + amount);
                    return false;
                }

                Class<? extends Unit> className = PackageReflectedConstants.getWorldUnitClass(ConsoleCommandExecutor.args[0]);
                if (className == null) {
                    Gdxg.clientUi.getConsole().logErrorToConsole("unitClass was not found by: " + ConsoleCommandExecutor.args[0]);
                    return false;
                }

                Array<Unit> newUnits = PoolManager.ARRAYS_POOL.obtain();
                for (int i = 0; i < amount; i++) {
//                    Unit unit = UnitFertilizer2.createStandardUnit(className, maleGender);
//                    newUnits.add(unit);
                }

                squad.getUnitsController().setUnitAndValidate(newUnits.get(0));
                PoolManager.ARRAYS_POOL.free(newUnits);
                return true;
            }
        };
    }

    public static ConsoleCommand cloneActiveObject() {
        return new ConsoleCommand("clone", "clone selected object") {
            @Override
            public boolean body() {
                AreaObject selectedObject = Gdxg.getAreaViewer().getSelectedSquad();
                if (!(selectedObject instanceof AbstractSquad)) {
                    Gdxg.clientUi.getConsole().logErrorToConsole("Clone supported for suqad only!");
                }

                AbstractSquad squad = (AbstractSquad) selectedObject;
                if (squad == null) {
                    Gdxg.clientUi.getConsole().logErrorToConsole(THERE_IS_NO_ACTIVE_OBJECT);
                    return false;
                }

                Cell neighborCell = squad.getLastCell().getCouldBeSeizedNeighborCell();
                if (neighborCell == null) {
                    Gdxg.clientUi.getConsole().logErrorToConsole("there is no CouldBeSeizedNeighborCell!");
                    return false;
                }

                AbstractSquad clonedAreaObject;
                if (squad.isSquad()) {
                    Assert.assertTrue(false, "review createWorldSquad");
                    clonedAreaObject = squad.getTeam().createWorldSquad(neighborCell, null);
                } else {
                    Gdxg.clientUi.getConsole().logErrorToConsole("Unknown object type: " + squad);
                    return false;
                }

                if (true) throw new RuntimeException("fixme");
//                Array<Unit> newUnits = PoolManager.ARRAYS_POOL.obtain();
//                for (int i = 0; i < squad.getUnits().size; i++) {
//                    Unit unit = squad.getUnits().get(i);
//                    Unit clonedUnit = UnitFertilizer.createStandardUnit(unit.getClass(), unit.getGender());
//                    newUnits.add(clonedUnit);
//                }
//
//                clonedAreaObject.getUnitsController().addUnitsAndValidate(newUnits);
//                PoolManager.ARRAYS_POOL.free(newUnits);
                return true;
            }
        };
    }

    public static ConsoleCommand createNodeOnMouseOverCellForSelectedObjectTeam() {
        return new ConsoleCommand("node", "create AI node on hovered cell for team of selected object") {
            @Override
            public boolean body() {
                Cell mouseOverCell = Gdxg.getAreaViewer().mouseOverCell;

                if (mouseOverCell == null) {
                    Gdxg.clientUi.getConsole().logErrorToConsole("mouseOverCell null");
                    return false;
                }

                AreaObject selectedObject = Gdxg.getAreaViewer().getSelectedSquad();
                if (selectedObject == null) {
                    Gdxg.clientUi.getConsole().logErrorToConsole("selectedObject null");
                    return false;
                }

                AiTeamControllerOld aiTeamControllerOld = selectedObject.getTeam().getAiTeamControllerOld();
                if (aiTeamControllerOld == null) {
                    Gdxg.clientUi.getConsole().logErrorToConsole("This team doesn't support AI!");
                    return false;
                }

                aiTeamControllerOld.addAiNode(mouseOverCell);
                return true;
            }
        };
    }

    public static ConsoleCommand createAnimalHerd() {
        return new ConsoleCommand("animal", "create animal herd on hovered cell") {
            @Override
            public boolean body() {
                Cell mouseOverCell = Gdxg.getAreaViewer().mouseOverCell;

                if (mouseOverCell == null) {
                    Gdxg.clientUi.getConsole().logErrorToConsole("mouseOverCell null");
                    return false;
                }

                if (mouseOverCell.hasSquad()) {
                    Gdxg.clientUi.getConsole().logErrorToConsole("cell is seized");
                    return false;
                }

//                WorldServices.createUnit(Gdxg.core.world.animalTeam
//                        , mouseOverCell
//                        , WorldServices.ANIMAL_FIRST_CLASS);
                return true;
            }
        };
    }

    public static ConsoleCommand help() {
        return new ConsoleCommand("help", "show console help") {
            @Override
            public boolean body() {
                Console console = Gdxg.clientUi.getConsole();
                console.logInfoToConsole("--- Console help ---");
                for (ConsoleCommandExecutor.RunnableConsoleCommand runnableConsoleCommand : ConsoleCommandExecutor.RunnableConsoleCommand.values()) {
                    console.logInfoToConsole(runnableConsoleCommand.toString());
                }
                return true;
            }
        };
    }
}
