package conversion7.game.stages.world.creator;

import conversion7.engine.utils.Utils;

public class AreaFaunaCreationResults {
    private int curTeamSquadsPoints;

    public void reset() {
        curTeamSquadsPoints = 0;
    }

    public boolean shouldCreateNewTeam() {
        if (curTeamSquadsPoints < 1) {
            curTeamSquadsPoints = 100;
            return true;
        } else {
            curTeamSquadsPoints -= Utils.RANDOM.nextInt(50) + 15;
            return false;
        }
    }


}
