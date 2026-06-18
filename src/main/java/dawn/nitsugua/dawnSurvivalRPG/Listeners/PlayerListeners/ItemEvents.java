package dawn.nitsugua.dawnSurvivalRPG.Listeners.PlayerListeners;

import dawn.nitsugua.dawnSurvivalRPG.Core.Managers.SkillManager;
import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.Utils.DawnKeys;
import dawn.nitsugua.dawnSurvivalRPG.Utils.MessagesUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

public class ItemEvents implements Listener {
    private final DawnSurvivalRPG plugin;

    public ItemEvents(DawnSurvivalRPG plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onItemInteraction(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if(item.getType() == Material.AIR || !(DawnKeys.isDawnObject(item.getItemMeta().getPersistentDataContainer()))) return;


        //player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.MESSAGE, "Checking if item is updated..."));

        //check item versions here
    }

    @EventHandler
    public void onSkillUse(PlayerInteractEvent e) {
        SkillManager skillManager = new SkillManager(plugin);

        Player player = e.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if(item.getType() == Material.AIR || !(DawnKeys.isDawnObject(item.getItemMeta().getPersistentDataContainer()))) return;

        // Ensure we are only firing skills on right clicks
        Action action = e.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemMeta itemMeta = item.getItemMeta();
        PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();

        if(!pdc.has(DawnKeys.USES)) {
            //player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.FAILURE, "This DawnItem doesn't have a skill attached!"));
            return;
        }

        if(pdc.has(DawnKeys.MANA) && pdc.has(DawnKeys.COOLDOWN)) {
            // --> THE FIX: Stop vanilla Minecraft from continuing the block interaction
            //e.setCancelled(true);

           skillManager.onUse(player, item, pdc);
        }
    }
}
