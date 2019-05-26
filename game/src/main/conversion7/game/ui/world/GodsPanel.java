package conversion7.game.ui.world;

import com.badlogic.gdx.scenes.scene2d.Stage;
import conversion7.engine.custom2d.TextureRegionColoredDrawable;
import conversion7.engine.custom2d.VBox;
import conversion7.game.Assets;
import conversion7.game.stages.world.gods.AbstractGod;
import conversion7.game.stages.world.team.Team;
import conversion7.game.ui.ClientUi;

public class GodsPanel extends VBox {

    private Stage stageGUI;
    GodInfoPanel godInfoPanel;

    public GodsPanel(Stage stageGUI) {
        this.stageGUI = stageGUI;
        pad(ClientUi.SPACING);
        setBackground(new TextureRegionColoredDrawable(ClientUi.PANEL_COLOR, Assets.pixel));
        godInfoPanel = new GodInfoPanel();
    }

    public void showFor(Team team) {
        clearChildren();
        addSmallCloseButton();
        addLabel("Gods:", Assets.labelStyle14white2);
        add().height(ClientUi.SPACING);
        team.world.godsGlobalStats.validatePercents();
        for (AbstractGod god : team.world.godsGlobalStats.gods.values()) {
            String line = " * " + god.getName()
                    + ", " + god.expPercent + "% power with " + team.world.godsGlobalStats.getMyPeople(god) + " followers";
            addLabel(line, Assets.labelStyle14orange);
        }


        add().height(ClientUi.SPACING);
        add(godInfoPanel);
        godInfoPanel.refresh(team);

        stageGUI.addActor(this);
        pack();
        setY(stageGUI.getHeight() - getHeight());
    }
}
