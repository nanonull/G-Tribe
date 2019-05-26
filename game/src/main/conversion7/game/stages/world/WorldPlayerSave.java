package conversion7.game.stages.world;

import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.team.Team;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;

public class WorldPlayerSave {
    private static final Logger LOG = Utils.getLoggerForClass();

    public static int getPlayerTeamProgress() {
        try {
            String fileToString = FileUtils.readFileToString(new File("save/ep.txt"));
             return Integer.valueOf(fileToString);
        } catch (IOException e) {
            LOG.info("1st game");
        }
        return 0;
    }
}
