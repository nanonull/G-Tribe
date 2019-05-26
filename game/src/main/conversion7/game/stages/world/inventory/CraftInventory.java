package conversion7.game.stages.world.inventory;

import com.badlogic.gdx.utils.Array;
import conversion7.engine.utils.Utils;
import conversion7.game.stages.world.inventory.items.types.AbstractInventoryItem;
import conversion7.game.stages.world.objects.AreaObject;
import conversion7.game.stages.world.objects.unit.AbstractSquad;
import org.slf4j.Logger;
import org.testng.Assert;

@Deprecated
public class CraftInventory extends BasicInventory {

    private static final Logger LOG = Utils.getLoggerForClass();
    @Deprecated
    public final static Array<CraftRecipe> RECIPES_LIST = new Array<>();

    private AbstractSquad squad;

    public CraftInventory(AreaObject object) {
        super();
        this.squad = (AbstractSquad) object;
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

    public void update() {
        clearItems();

        for (CraftRecipe craftRecipe : RECIPES_LIST) {
            AbstractInventoryItem possibleCraftItem = craftRecipe.getPossibleCraftItemWithin(squad.getInventory());
            if (possibleCraftItem != null && squad.getTeam().getTeamSkillsManager().getSkill(
                    possibleCraftItem.getParams().requiredSkill).isLearnStarted()) {
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

    private void craft(AbstractInventoryItem craftedItem) {
        Assert.assertNotNull(craftedItem);
        BasicInventory inventory = squad.getInventory();
        inventory.startBatch();
        inventory.addItem(craftedItem);
        craftedItem.getParams().getCraftRecipe().useConsumables(inventory, craftedItem.getQuantity());
        inventory.endBatch();
    }
}
