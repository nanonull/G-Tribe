package conversion7.game.stages.world.inventory.items;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import conversion7.game.Assets;
import conversion7.game.stages.world.inventory.InventoryItemStaticParams;
import conversion7.game.stages.world.inventory.items.types.FoodItem;

public class AppleItem extends FoodItem {
    public AppleItem() {
        super(InventoryItemStaticParams.APPLE);
    }

    @Override
    public TextureRegion getIconTexture() {
        return Assets.apple;
    }
}
