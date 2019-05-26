package conversion7.game.ui.world;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import conversion7.engine.custom2d.TextureRegionColoredDrawable;
import conversion7.engine.custom2d.VBox;
import conversion7.game.Assets;
import conversion7.game.stages.world.landscape.Cell;
import conversion7.game.stages.world.objects.BurningForest;
import conversion7.game.stages.world.objects.BurntForest;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import conversion7.game.ui.ClientUi;
import conversion7.game.ui.hint.PopupHintPanel;

public class CellDetailsPanel extends VBox {
    VBox rootInfo;

    public CellDetailsPanel() {
        pad(ClientUi.SPACING);
        setBackground(new TextureRegionColoredDrawable(ClientUi.PANEL_COLOR, Assets.pixel));

        addSmallCloseButton();
        rootInfo = new VBox();
        rootInfo.defaults().space(2);
        add(rootInfo).grow();
    }

    public void load(Cell object) {
        refreshContent(object);
    }

    public void refreshContent(final Cell object) {
        rootInfo.clearChildren();

        rootInfo.addLabel("Cell " + object.getWorldPosInCells(), Assets.labelStyle14blackWithBackground);
        Label gatheringLabel = rootInfo.addLabel("Gathering value: " + object.getGatheringValue(),
                Assets.labelStyle14yellow).getActor();
        PopupHintPanel.assignHintTo(gatheringLabel, "It means how many experience can be gathered from cell");
        if (object.getLandscape().hasForest()) {
            rootInfo.addLabel("Forest - defending unit has more power",
                    Assets.labelStyle14white2);
            rootInfo.addLabel("       - increases gathering value",
                    Assets.labelStyle14white2);
        }
        if (object.getLandscape().hasHill()) {
            rootInfo.addLabel("Hill - defending unit has more power",
                    Assets.labelStyle14white2);
        }
        if (object.getLandscape().hasBog()) {
            rootInfo.addLabel("Bog - decreases unit AP to " + AbstractSquad.BOG_MOVE_AP + " on next step",
                    Assets.labelStyle14white2);
        }
        if (object.getObject(BurningForest.class) != null) {
            rootInfo.addLabel("BurningForest - hurts units by " + BurningForest.HURT_HP + " HP",
                    Assets.labelStyle14white2);
            rootInfo.addLabel("              - fires adjacent forests",
                    Assets.labelStyle14white2);
            rootInfo.addLabel("              - decreases gathering value by multiplying on " + BurningForest.FIRE_EFFECT_FOOD_MLT,
                    Assets.labelStyle14white2);
        }
        if (object.getObject(BurntForest.class) != null) {
            rootInfo.addLabel("BurntForest - decreases gathering value by multiplying on " + BurningForest.FIRE_EFFECT_FOOD_MLT,
                    Assets.labelStyle14white2);
        }
        pack();
    }

}