package conversion7.game.ui.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import conversion7.engine.custom2d.TextureRegionColoredDrawable;
import conversion7.engine.custom2d.VBox;
import conversion7.game.Assets;
import conversion7.game.stages.world.inventory.items.IronOreItem;
import conversion7.game.stages.world.inventory.items.RadioactiveIsotopeItem;
import conversion7.game.stages.world.inventory.items.StoneItem;
import conversion7.game.stages.world.inventory.items.weapons.StickItem;
import conversion7.game.stages.world.team.Team;
import conversion7.game.ui.ClientUi;

public class TribeResorcesPanel extends VBox {

    public static ProgressBar.ProgressBarStyle BAR_STYLE;
    private static TribeResorcesPanel inst;
    private Stage stageGUI;
    private final ProgressBar evolutionSubProgressBar;
    private Team team;

    static {
        TextureRegionDrawable knobDrawable = new TextureRegionColoredDrawable(Color.GREEN, Assets.pixel);
        TextureRegionDrawable backDrawable = new TextureRegionColoredDrawable(Color.GRAY, Assets.pixel);
        BAR_STYLE = new ProgressBar.ProgressBarStyle(backDrawable, knobDrawable);
        BAR_STYLE.knob.setMinHeight(10);
        BAR_STYLE.knobBefore = BAR_STYLE.knob;
        BAR_STYLE.background.setMinHeight(8);
    }

    public TribeResorcesPanel(Stage stageGUI) {
        this.stageGUI = stageGUI;
        pad(ClientUi.SPACING);
        setBackground(new TextureRegionColoredDrawable(ClientUi.PANEL_COLOR, Assets.pixel));
        evolutionSubProgressBar = new ProgressBar(0, 100, 1, false, BAR_STYLE);
    }


    public void showFor(Team team) {
        this.team = team;
        clearChildren();

        int evo = team.getEvolutionPoints();
        int stick = team.getInventory().getItemQty(StickItem.class);
        int stone = team.getInventory().getItemQty(StoneItem.class);
        int iron = team.getInventory().getItemQty(IronOreItem.class);
        int uran = team.getInventory().getItemQty(RadioactiveIsotopeItem.class);
        addLabel(
                team.getName() + " || evo " + evo
                        + " | stone " + stone + " | stick " + stick
                        + " | iron " + iron + " | isotop " + uran, Assets.labelStyle14white2);

        addSpaceLine();

        stageGUI.addActor(this);
        pack();
        setX(stageGUI.getWidth() - getWidth() - 100);
        setY(stageGUI.getHeight() - getHeight());
    }

    public void refresh() {
        if (team.isHumanActivePlayer()) {
            showFor(team);
        }
    }
}
