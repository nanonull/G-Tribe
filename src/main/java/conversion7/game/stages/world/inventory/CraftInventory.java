package conversion7.game.stages.world.inventory;

import com.badlogic.gdx.utils.Array;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.inventory.items.types.AbstractInventoryItem;
import conversion7.game.stages.world.objects.AreaObject;
import org.slf4j.Logger;
import org.testng.Assert;

public class CraftInventory extends BasicInventory {

    private static final Logger LOG = Utils.getLoggerForClass();
    public final static Array<CraftRecipe> RECIPES_LIST = new Array<>();

    private AreaObject areaObject;

    public CraftInventory(AreaObject areaObject) {
        super();
        this.areaObject = areaObject;
    }

    public void update() {
        clearItems();

        for (CraftRecipe craftRecipe : RECIPES_LIST) {
            AbstractInventoryItem possibleCraftItem = craftRecipe.getPossibleCraftItemWithin(areaObject.getMainInventory());
            if (possibleCraftItem != null && areaObject.getTeam().getTeamSkillsManager().getSkill(
                    possibleCraftItem.getParams().getRequiredSkillLearned()).isLearnStarted()) {
                addItem(possibleCraftItem);
            }
        }
    }

    @Override
    public void addItem(AbstractInventoryItem inventoryItem) {
        if (LOG.isDebugEnabled()) Utils.debug(LOG, "addItem: %s", inventoryItem);
        Assert.assertNull(items.put(inventoryItem.getClass(), inventoryItem));
    }

    public void craft(Class<? extends AbstractInventoryItem> itemClassToCraft, int executeRecipesQty) {
        AbstractInventoryItem itemOfCraftedClass = getItem(itemClassToCraft);
        AbstractInventoryItem craftedItem = itemOfCraftedClass.split(
                executeRecipesQty * itemOfCraftedClass.getParams().getCraftRecipe().getFinalItemQuantityPerCraft());
        craft(craftedItem);
    }

    public void craft(Class<? extends AbstractInventoryItem> itemClassToCraft) {
        AbstractInventoryItem craftedItem = getItem(itemClassToCraft);
        craft(craftedItem);
    }

    private void craft(AbstractInventoryItem craftedItem) {
        Assert.assertNotNull(craftedItem);
        MainInventory mainInventory = areaObject.getMainInventory();
        mainInventory.startBatch();
        mainInventory.addItem(craftedItem);
        craftedItem.getParams().getCraftRecipe().useConsumables(mainInventory, craftedItem.getQuantity());
        mainInventory.endBatch();
    }

    public static void addRecipe(CraftRecipe craftRecipe) {
        RECIPES_LIST.add(craftRecipe);
    }

    public static CraftRecipe getRecipeForItem(Class<? extends AbstractInventoryItem> itemClass) {
        for (CraftRecipe craftRecipe : RECIPES_LIST) {
            if (craftRecipe.getFinalItemClass().equals(itemClass)) {
                return craftRecipe;
            }
        }
        return null;
    }
}
