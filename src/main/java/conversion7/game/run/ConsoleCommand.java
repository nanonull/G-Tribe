package conversion7.game.run;

import conversion7.engine.Gdxg;
import conversion7.engine.utils.Utils;
import org.slf4j.Logger;

public abstract class ConsoleCommand implements Runnable {

    private static final Logger LOG = Utils.getLoggerForClass();

    private final String commandName;
    private final String description;
    private String format;

    public ConsoleCommand(String commandName, String description) {
        this(commandName, description, null);
    }

    public ConsoleCommand(String commandName, String description, String format) {
        this.commandName = commandName;
        this.description = description;
        this.format = format;
    }

    public String getCommandName() {
        return commandName;
    }

    public String getDescription() {
        return description;
    }

    public String getFormat() {
        return format;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(commandName).append(" ");
        if (format != null) {
            builder.append(format);
        }
        builder.append("\n      ").append(description);
        return builder.toString();
    }

    protected void logWrongFormatError() {
        Gdxg.clientUi.getConsole().logErrorToConsole("Expected format: " + format);
    }

    @Override
    public void run() {
        LOG.info("Run command: " + commandName);
        if (body()) {
            Gdxg.clientUi.getConsole().logInfoToConsole("    Success");
        } else {
            LOG.info("command was not executed!");
        }
    }

    /** Returns true if command executed successfully */
    public abstract boolean body();
}
