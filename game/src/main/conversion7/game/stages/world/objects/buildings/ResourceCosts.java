package conversion7.game.stages.world.objects.buildings;

import com.badlogic.gdx.utils.ObjectMap;
import conversion7.game.stages.world.inventory.items.AppleItem;
import conversion7.game.stages.world.inventory.items.ArrowItem;
import conversion7.game.stages.world.inventory.items.IronOreItem;
import conversion7.game.stages.world.inventory.items.RadioactiveIsotopeItem;
import conversion7.game.stages.world.inventory.items.StoneItem;
import conversion7.game.stages.world.inventory.items.StringItem;
import conversion7.game.stages.world.inventory.items.types.AbstractInventoryItem;
import conversion7.game.stages.world.inventory.items.weapons.JavelinItem;
import conversion7.game.stages.world.inventory.items.weapons.StickItem;
import conversion7.game.stages.world.objects.BallistaObject;
import conversion7.game.stages.world.objects.MountainDebris;
import conversion7.game.stages.world.objects.ScorpionObject;
import conversion7.game.stages.world.objects.actions.items.AppleUranusBombAction;
import conversion7.game.stages.world.objects.actions.items.BallistaJavelinShotAction;
import conversion7.game.stages.world.objects.actions.items.BallistaShotAction;
import conversion7.game.stages.world.objects.actions.items.BallistaVolleyShotAction;
import conversion7.game.stages.world.objects.actions.items.ScorpionShotAction;
import conversion7.game.stages.world.objects.actions.items.ThrowStoneAction;

public class ResourceCosts {
    static final ObjectMap<Class,
            ObjectMap<Class<? extends AbstractInventoryItem>, Integer>> COSTS = new ObjectMap<>();

    static {
        ObjectMap<Class<? extends AbstractInventoryItem>, Integer> cost;

        cost = new ObjectMap<>();
        cost.put(StoneItem.class, 1);
        ResourceCosts.COSTS.put(MountainDebris.class, cost);

        cost = new ObjectMap<>();
        cost.put(StoneItem.class, 5);
        cost.put(StickItem.class, 5);
        ResourceCosts.COSTS.put(Camp.class, cost);

        cost = new ObjectMap<>();
        cost.put(StoneItem.class, 10);
        cost.put(StickItem.class, 10);
        ResourceCosts.COSTS.put(IronFactory.class, cost);

        cost = new ObjectMap<>();
        cost.put(StoneItem.class, 10);
        cost.put(StickItem.class, 10);
        cost.put(IronOreItem.class, 10);
        ResourceCosts.COSTS.put(UranusFactory.class, cost);

        cost = new ObjectMap<>();
        cost.put(IronOreItem.class, 10);
        cost.put(RadioactiveIsotopeItem.class, 10);
        ResourceCosts.COSTS.put(CommunicationSatellite.class, cost);

        cost = new ObjectMap<>();
        cost.put(AppleItem.class, 10);
        cost.put(RadioactiveIsotopeItem.class, 1);
        ResourceCosts.COSTS.put(AppleUranusBombAction.class, cost);

        cost = new ObjectMap<>();
        cost.put(StoneItem.class, 1);
        ResourceCosts.COSTS.put(ThrowStoneAction.class, cost);

        cost = new ObjectMap<>();
        cost.put(StickItem.class, 5);
        cost.put(StoneItem.class, 5);
        cost.put(StringItem.class, 1);
        ResourceCosts.COSTS.put(ScorpionObject.class, cost);

        cost = new ObjectMap<>();
        cost.put(StickItem.class, 10);
        cost.put(StoneItem.class, 10);
        cost.put(StringItem.class, 5);
        ResourceCosts.COSTS.put(BallistaObject.class, cost);

        cost = new ObjectMap<>();
        cost.put(ArrowItem.class, 1);
        ResourceCosts.COSTS.put(ScorpionShotAction.class, cost);

        cost = new ObjectMap<>();
        cost.put(StoneItem.class, 1);
        ResourceCosts.COSTS.put(BallistaShotAction.class, cost);

        cost = new ObjectMap<>();
        cost.put(StoneItem.class, BallistaVolleyShotAction.SHOTS);
        ResourceCosts.COSTS.put(BallistaVolleyShotAction.class, cost);

        cost = new ObjectMap<>();
        cost.put(JavelinItem.class, 1);
        ResourceCosts.COSTS.put(BallistaJavelinShotAction.class, cost);
    }

    public static ObjectMap.Entries<Class<? extends AbstractInventoryItem>, Integer>
    getCost(Class buildClass) {
        return new ObjectMap.Entries<>(COSTS.get(buildClass));
    }

    public static String getCostAsString(Class cls) {
        ObjectMap.Entries<Class<? extends AbstractInventoryItem>, Integer> cost = getCost(cls);
        StringBuilder stringBuilder = new StringBuilder();
        for (ObjectMap.Entry<Class<? extends AbstractInventoryItem>, Integer> entry : cost) {
            stringBuilder.append(entry.key.getSimpleName()).append(":").append(entry.value).append(" ");
        }
        return stringBuilder.toString();
    }

}
