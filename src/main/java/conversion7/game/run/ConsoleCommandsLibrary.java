package conversion7.game.run;

import com.badlogic.gdx.utils.Array;
import conversion7.engine.Gdxg;
import conversion7.engine.pools.system.PoolManager;
import conversion7.game.PackageReflectedConstants;
import conversion7.game.services.WorldServices;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.ai.AiTeamController;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.AnimalHerd;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.objects.effects.AbstractObjectEffect;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.stages.world.unit.UnitFertilizer;
import conversion7.game.ui.Console;
import conversion7.game.utils.collections.IterationRegistrators;
import conversion7.tests_standalone.RuntimeTestInvoker;
import conversion7.tests_standalone.misc.TestsCustomScene3d;

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
                AreaObject areaObject = World.getAreaViewer().selectedObject;
                if (areaObject == null) {
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
                    Unit unit = UnitFertilizer.createStandardUnit(className, maleGender);
                    newUnits.add(unit);
                }

                areaObject.getUnitsController().addUnitsAndValidate(newUnits);
                PoolManager.ARRAYS_POOL.free(newUnits);
                return true;
            }
        };
    }

    public static ConsoleCommand cloneActiveObject() {
        return new ConsoleCommand("clone", "clone selected object") {
            @Override
            public boolean body() {
                AreaObject selectedObject = World.getAreaViewer().selectedObject;
                if (selectedObject == null) {
                    Gdxg.clientUi.getConsole().logErrorToConsole(THERE_IS_NO_ACTIVE_OBJECT);
                    return false;
                }

                Cell neighborCell = selectedObject.getCell().getCouldBeSeizedNeighborCell();
                if (neighborCell == null) {
                    Gdxg.clientUi.getConsole().logErrorToConsole("there is no CouldBeSeizedNeighborCell!");
                    return false;
                }

                AreaObject clonedAreaObject;
                if (selectedObject.isSquad()) {
                    clonedAreaObject = selectedObject.getTeam().createHumanSquad(neighborCell);
                } else if (selectedObject.isTownFragment()) {
                    clonedAreaObject = selectedObject.getTeam().createTown(neighborCell);
                } else {
                    Gdxg.clientUi.getConsole().logErrorToConsole("unknown object type: " + selectedObject);
                    return false;
                }

                Array<Unit> newUnits = PoolManager.ARRAYS_POOL.obtain();
                IterationRegistrators.UNITS_ITERATION_REGISTRATOR.start();
                for (int i = 0; i < selectedObject.getUnits().size; i++) {
                    Unit unit = selectedObject.getUnits().get(i);
                    Unit clonedUnit = UnitFertilizer.createStandardUnit(unit.getClass(), unit.getGender());
                    newUnits.add(clonedUnit);
                }
                IterationRegistrators.UNITS_ITERATION_REGISTRATOR.end();

                clonedAreaObject.getUnitsController().addUnitsAndValidate(newUnits);
                PoolManager.ARRAYS_POOL.free(newUnits);
                return true;
            }
        };
    }

    public static ConsoleCommand createNodeOnMouseOverCellForSelectedObjectTeam() {
        return new ConsoleCommand("node", "create AI node on hovered cell for team of selected object") {
            @Override
            public boolean body() {
                Cell mouseOverCell = World.getAreaViewer().mouseOverCell;

                if (mouseOverCell == null) {
                    Gdxg.clientUi.getConsole().logErrorToConsole("mouseOverCell null");
                    return false;
                }

                AreaObject selectedObject = World.getAreaViewer().selectedObject;
                if (selectedObject == null) {
                    Gdxg.clientUi.getConsole().logErrorToConsole("selectedObject null");
                    return false;
                }

                AiTeamController aiTeamController = selectedObject.getTeam().getAiTeamController();
                if (aiTeamController == null) {
                    Gdxg.clientUi.getConsole().logErrorToConsole("This team doesn't support AI!");
                    return false;
                }

                aiTeamController.addAiNode(mouseOverCell);
                return true;
            }
        };
    }

    public static ConsoleCommand createAnimalHerd() {
        return new ConsoleCommand("animal", "create animal herd on hovered cell") {
            @Override
            public boolean body() {
                Cell mouseOverCell = World.getAreaViewer().mouseOverCell;

                if (mouseOverCell == null) {
                    Gdxg.clientUi.getConsole().logErrorToConsole("mouseOverCell null");
                    return false;
                }

                if (mouseOverCell.isSeized()) {
                    Gdxg.clientUi.getConsole().logErrorToConsole("cell is seized");
                    return false;
                }

                AnimalHerd animalHerdOnCell = WorldServices.createAnimalHerdWithRandomUnits(mouseOverCell);
                return true;
            }
        };
    }

    public static ConsoleCommand addEffectToActiveObject() {
        return new ConsoleCommand("add_effect", "add effect to selected object", "<class>") {
            @Override
            public boolean body() {
                AreaObject selectedObject = World.getAreaViewer().selectedObject;
                if (selectedObject == null) {
                    Gdxg.clientUi.getConsole().logErrorToConsole(THERE_IS_NO_ACTIVE_OBJECT);
                    return false;
                }

                if (ConsoleCommandExecutor.args == null) {
                    logWrongFormatError();
                    return false;
                }

                String classString = ConsoleCommandExecutor.args[0];
                Class<? extends AbstractObjectEffect> effectClassFound =
                        PackageReflectedConstants.getAreaObjectEffectClass(classString);
                if (effectClassFound == null) {
                    Gdxg.clientUi.getConsole().logErrorToConsole("Effect class was not found by: " + classString);
                    return false;
                }

                WorldServices.addEffectToAreaObject(effectClassFound, selectedObject);
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
