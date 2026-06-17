package dawn.nitsugua.dawnSurvivalRPG.Listeners.PlayerListeners;

import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.Utils.DawnKeys;
import dawn.nitsugua.dawnSurvivalRPG.Utils.MessagesUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ItemEvents implements Listener {
    private final DawnSurvivalRPG plugin;

    public ItemEvents(DawnSurvivalRPG plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onItemInteraction(PlayerInteractEvent e) {
        ItemStack item = e.getItem();
        if(item == null || !(DawnKeys.isDawnObject(item.getItemMeta().getPersistentDataContainer()))) return;

        Player player = e.getPlayer();
        player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.MESSAGE, "Hey, that's a Dawn Custom Item!"));
    }
}
