package dawn.nitsugua.dawnSurvivalRPG.Core.Managers;

import dawn.nitsugua.dawnSurvivalRPG.Core.Registry.DawnRegistry;
import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.Items.CustomItem;
import dawn.nitsugua.dawnSurvivalRPG.Utils.DawnKeys;
import dawn.nitsugua.dawnSurvivalRPG.Utils.MessagesUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class CooldownManager {
    DawnRegistry dawnRegistry = DawnRegistry.getInstance();
    DawnSurvivalRPG plugin;

    public CooldownManager(DawnSurvivalRPG plugin) {
        this.plugin = plugin;
    }

    public boolean isOnCooldown(Player player, ItemStack item) {
        if(!item.hasItemMeta()) return false;

        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        String itemId = pdc.get(DawnKeys.ITEM_ID, PersistentDataType.STRING);
        if(itemId == null) return false;

        double cooldownSeconds = pdc.getOrDefault(DawnKeys.COOLDOWN, PersistentDataType.DOUBLE, 0.0);
        Long lastUsed = dawnRegistry.getCooldown(player.getUniqueId(), itemId);

        if(lastUsed == null) return false; //if items has never been used

        long timePassed = System.currentTimeMillis() - lastUsed;
        long cooldownMs = (long) (cooldownSeconds * 1000);

        if(timePassed < cooldownMs) {
            double timeLeftSeconds = (cooldownMs - timePassed) / 1000.0;
            String formattedTime = String.format("%.1f", timeLeftSeconds);

            player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.FAILURE,
                    "On Cooldown! Wait " + formattedTime + "s."));
            return true;
        }
        return false;
    }

    public void setCooldown(Player player, ItemStack item) {
        if(!item.hasItemMeta()) return;

        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        String itemId = pdc.get(DawnKeys.ITEM_ID, PersistentDataType.STRING);
        if(itemId != null) {
            dawnRegistry.setCooldown(player.getUniqueId(), itemId, System.currentTimeMillis());
        }
    }
}
