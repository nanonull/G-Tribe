package conversion7.game.stages.world.inventory.items;

import conversion7.engine.AudioPlayer;
import conversion7.engine.Gdxg;
import conversion7.game.stages.world.inventory.InventoryItemStaticParams;
import conversion7.game.stages.world.inventory.items.types.ResourceItem;
import conversion7.game.stages.world.objects.actions.items.BuildCampAction;
import conversion7.game.stages.world.objects.buildings.Camp;
import conversion7.game.stages.world.unit.Unit;
import conversion7.game.ui.UiLogger;
import conversion7.game.ui.dialogs.InfoDialog;
import conversion7.game.unit_classes.ufo.Archon;

public class CampBuildingKit extends ResourceItem {
    public CampBuildingKit() {
        super(InventoryItemStaticParams.CAMP_BUILDING_KIT);
    }

    @Override
    public boolean isUsable() {
        return true;
    }

    @Override
    public void useBy(Unit unit) {
        if (unit.getGameClass() == Archon.class) {
            if (Camp.couldBeBuiltOnCell(unit.squad.getLastCell())) {
                BuildCampAction.buildCamp(unit.squad);
                super.useBy(unit);
            } else {
                Gdxg.clientUi.getInfoDialog().show("Warn", "Can't set camp here");
                AudioPlayer.playFail();
            }
        } else {
            Gdxg.clientUi.getInfoDialog().show("Can't use item", "Only Archon knows how to use it");
            AudioPlayer.playFail();
        }

    }

}
