package conversion7.game.ui.world;

import conversion7.engine.custom2d.table.Panel;
import conversion7.game.stages.world.team.Team;

public class GodInfoPanel extends Panel {

    public void refresh(Team team) {
        clearChildren();
//        addLabel("(DEPR) Our tribe believes in God: " + (team.myGod == null ? "-no god-" : team.myGod.getNameAndType())
//                , Assets.labelStyle14white2);
    }
}
