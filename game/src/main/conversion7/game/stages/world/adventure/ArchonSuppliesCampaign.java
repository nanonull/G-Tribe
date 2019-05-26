package conversion7.game.stages.world.adventure;

import conversion7.engine.utils.MathUtils;
import conversion7.game.stages.world.World;
import conversion7.game.stages.world.area.Area;
import conversion7.game.stages.world.inventory.items.CampBuildingKit;
import conversion7.game.stages.world.inventory.items.FusionCellItem;
import conversion7.game.stages.world.inventory.items.weapons.FusionBlasterItem;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.objects.SupplyContainer;
import conversion7.game.ui.UiLogger;

public class ArchonSuppliesCampaign {
    private static int suppliesDrop;

    public static void newStep(World world) {
        if (suppliesDrop == 0 && world.step > 0 && MathUtils.random()) {
            WorldAdventure.runAround(world.getSpaceShip().getLastCell(), (int) (Area.WIDTH_IN_CELLS * 0.3f),
                    canUseCell -> canUseCell.hasLandscapeAvailableForMove(),
                    cell -> {
                        SupplyContainer container = AreaObject.create(cell, world.animalTeam, SupplyContainer.class);
                        world.addImportantObj(container);
                        UiLogger.addImportantGameInfoLabel("Your scanner has found supply container");
                        container.inventory.addItem(FusionCellItem.class, 10);
                        container.inventory.addItem(FusionBlasterItem.class, 1);
                        container.inventory.addItem(CampBuildingKit.class, 1);
                        suppliesDrop++;
                    });
        }
    }
}
