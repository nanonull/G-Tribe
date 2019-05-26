package conversion7.game.ui.world;

import conversion7.engine.custom2d.TextureRegionColoredDrawable;
import conversion7.engine.custom2d.VBox;
import conversion7.game.Assets;
import conversion7.game.stages.world.objects.buildings.Camp;
import conversion7.game.ui.ClientUi;

public class CampDetailsPanel extends VBox {
    VBox rootInfo;
    private Camp activeCamp;

    public CampDetailsPanel() {
        pad(ClientUi.SPACING);
        defaults().space(2);
        setBackground(new TextureRegionColoredDrawable(ClientUi.PANEL_COLOR, Assets.pixel));

        addSmallCloseButton();
        rootInfo = new VBox();
        add(rootInfo).grow();
    }

    public void load(Camp object) {
        refreshContent(object);
    }

    public void refreshContent(final Camp camp) {
        activeCamp = camp;
        rootInfo.clearChildren();
        rootInfo.addLabel(camp.getName() + " " + camp.team.getName(), Assets.labelStyle14blackWithBackground);
        if (!camp.isConstructionCompleted()) {
            rootInfo.addLabel("Construction progress: " + camp.getConstructionProgress() + "/"
                            + Camp.CAMP_CONSTRUCTION_AP_TOTAL,
                    Assets.labelStyle14white2);

        }

        rootInfo.addLabel("Gathering (round): " + camp.getGatheringPerStep(), Assets.labelStyle14white2);
        rootInfo.addLabel("Net progress: " + camp.getNet().getCampsUiHint(), Assets.labelStyle14white2);
        rootInfo.addLabel("Net bonus: " + camp.getNet().getBonus(), Assets.labelStyle14white2);
        rootInfo.addLabel("Gathering with bonus(round): " + camp.getGatheringWithBonus(), Assets.labelStyle14white2);
        rootInfo.addLabel("Total gathered: " + camp.getGatheredAmount(), Assets.labelStyle14white2);

        pack();
    }

//    public void loadIfActiveAndVisible(Camp camp) {
//        if (isVisible() && camp == activeCamp) {
//            load(camp);
//        }
//    }
//
//    public void loadIfActive(Camp camp) {
//        if (isVisible()) {
//            load(camp);
//        }
//    }

    public boolean isLoaded(Camp camp) {
        return activeCamp == camp;
    }
}