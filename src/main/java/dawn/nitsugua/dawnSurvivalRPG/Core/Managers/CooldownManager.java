package dawn.nitsugua.dawnSurvivalRPG.Core.Managers;

import dawn.nitsugua.dawnSurvivalRPG.Core.Registry.DawnRegistry;
import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
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

    public boolean isOnCooldown(Player player, PersistentDataContainer pdc) {
        long lastUsed = pdc.getOrDefault(DawnKeys.LAST_USED, PersistentDataType.LONG, 0L);

        double cooldown = pdc.getOrDefault(DawnKeys.COOLDOWN, PersistentDataType.DOUBLE, 0.0);
        boolean onCooldown = System.currentTimeMillis() - lastUsed < ((long) cooldown * 1000);

        if(onCooldown) {
            player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.FAILURE, "On Cooldown!"));
            return true;
        }
        return false;
    }

    public void setCooldown(ItemStack itemStack) {
        if(!itemStack.hasItemMeta()) return;
        ItemMeta meta = itemStack.getItemMeta();
        meta.getPersistentDataContainer().set(DawnKeys.LAST_USED, PersistentDataType.LONG, System.currentTimeMillis());
        itemStack.setItemMeta(meta);
    }
}
