package conversion7.game.run;

import conversion7.engine.Gdxg;
import conversion7.engine.utils.Utils;
import org.slf4j.Logger;

import java.util.Arrays;

public class ConsoleCommandExecutor {

    private static final Logger LOG = Utils.getLoggerForClass();

    public enum RunnableConsoleCommand {
        ADD_UNIT(ConsoleCommandsLibrary.addUnitsToActiveObject()),
        CLONE(ConsoleCommandsLibrary.cloneActiveObject()),
        ANIMAL(ConsoleCommandsLibrary.createAnimalHerd()),
        NODE(ConsoleCommandsLibrary.createNodeOnMouseOverCellForSelectedObjectTeam()),
        ADD_EFFECT(ConsoleCommandsLibrary.addEffectToActiveObject()),
        HELP(ConsoleCommandsLibrary.help()),
        TEST(ConsoleCommandsLibrary.runMainTest()),;

        private final ConsoleCommand command;

        RunnableConsoleCommand(ConsoleCommand command) {
            this.command = command;
        }

        public ConsoleCommand getCommand() {
            return command;
        }

        @Override
        public String toString() {
            return command.toString();
        }

        public static RunnableConsoleCommand getCommand(String name) {
            for (RunnableConsoleCommand runnableConsoleCommand : values()) {
                if (runnableConsoleCommand.getCommand().getCommandName().equals(name)) {
                    return runnableConsoleCommand;
                }
            }
            return null;
        }
    }

    public static String args[] = null;

    public static void runCmd(String commandText) {
        String[] strings = commandText.split("\\s", 2); // split by space
        args = null;
        if (strings.length > 1) {
            args = strings[1].split("\\s");
            LOG.info("cmd args: " + Arrays.toString(args));
        }

        String cmdName = strings[0];
        RunnableConsoleCommand command = RunnableConsoleCommand.getCommand(cmdName);
        if (command == null) {
            Gdxg.clientUi.getConsole().logErrorToConsole(" no such command: " + cmdName);
        } else {
            command.getCommand().run();
        }
    }
}
