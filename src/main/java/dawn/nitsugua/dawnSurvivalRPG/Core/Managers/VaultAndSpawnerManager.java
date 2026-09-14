package dawn.nitsugua.dawnSurvivalRPG.Core.Managers;

import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.Utils.InventoryUtil;
import dawn.nitsugua.dawnSurvivalRPG.Utils.RNGManager;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class VaultAndSpawnerManager {
    private final DawnSurvivalRPG plugin;

    public VaultAndSpawnerManager(DawnSurvivalRPG plugin) {
        this.plugin = plugin;
    }

    public void manageVaultLoot(List<ItemStack> loot) {
        //chance to drop elite key shards and epic shards
        if(RNGManager.roll(0.1)) {
            ItemStack shard = InventoryUtil.resolveItem(plugin, "ELITE_KEY_SHARD", RNGManager.rand(1, 10));
            if(shard != null) loot.add(shard);
        }
    }

    public void manageTrialSpawnerLoot(List<ItemStack> loot) {
        //chance to drop elite key shards
        if(RNGManager.roll(0.05)) {
            ItemStack shard = InventoryUtil.resolveItem(plugin, "ELITE_KEY_SHARD", RNGManager.rand(1, 5));
            if(shard != null) loot.add(shard);
        }
    }

    public void manageSpawnerSpawnedMob(Entity e) {
        if(e instanceof Player) return;
        if(!(e instanceof Creature creature)) return;

        AttributeInstance health = creature.getAttribute(Attribute.MAX_HEALTH);
        if(health != null) {
            double newHealth = health.getBaseValue() * 1.5;
            health.setBaseValue(newHealth);
            creature.setHealth(newHealth);
            //plugin.getLog().info("Set new health: " + newHealth + ", from: " + health.getBaseValue());
        }

        AttributeInstance damage = creature.getAttribute(Attribute.ATTACK_DAMAGE);
        if(damage != null) {
            double newDamage = damage.getBaseValue() * 1.5;
            damage.setBaseValue(newDamage);
            //plugin.getLog().info("Set new damage: " + newDamage + ", from: " + damage.getBaseValue());
        }
    }


}
