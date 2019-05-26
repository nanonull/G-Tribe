package conversion7.game.stages.world.inventory.items.types;

import com.badlogic.gdx.utils.ObjectMap;
import conversion7.game.stages.world.inventory.InventoryItemStaticParams;
import conversion7.game.stages.world.inventory.items.weapons.CudgelItem;
import conversion7.game.stages.world.inventory.items.weapons.HammerItem;
import conversion7.game.stages.world.inventory.items.weapons.MaceItem;
import conversion7.game.stages.world.inventory.items.weapons.MammothMaceItem;

public abstract class MeleeWeaponItem extends AbstractInventoryItem {

    public static final ObjectMap<Class, Integer> STUN_CHANCES = new ObjectMap<>();

    static {
        STUN_CHANCES.put(CudgelItem.class, 1);
        STUN_CHANCES.put(HammerItem.class, 3);
        STUN_CHANCES.put(MaceItem.class, 5);
        STUN_CHANCES.put(MammothMaceItem.class, 10);
    }

    public MeleeWeaponItem(InventoryItemStaticParams itemParams) {
        super(itemParams);
    }

    @Override
    public String getDescription() {
        StringBuilder stringBuilder = new StringBuilder(super.getDescription());
        stringBuilder.append("Damage: ").append(params.getMeleeDamage()).append("\n")
                .append("Hit Chance: ").append(params.getHitChancePerc()).append("\n");
        if (canPierce()) {
            stringBuilder.append("Pierce effect").append("\n");
        }
        if (getStunChance() > 0) {
            stringBuilder.append("Stun/push chance: " + getStunChance()).append("\n");
        }

        stringBuilder.append(getRequiredSkillDescriptionLine());
        return stringBuilder.toString();
    }

    @Override
    public int getOrder() {
        return 10;
    }

    @Override
    public boolean isUsable() {
        return false;
    }

    public int getStunChance() {
        Integer val = STUN_CHANCES.get(getClass());
        return val == null ? 0 : val;

    }

    public boolean canPierce() {
        return false;
    }
}
