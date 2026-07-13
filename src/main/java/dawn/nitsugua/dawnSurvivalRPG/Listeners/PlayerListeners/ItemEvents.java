package dawn.nitsugua.dawnSurvivalRPG.Listeners.PlayerListeners;

import dawn.nitsugua.dawnSurvivalRPG.Core.Managers.CooldownManager;
import dawn.nitsugua.dawnSurvivalRPG.Core.Managers.ManaManager;
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
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class ItemEvents implements Listener {
    private final DawnSurvivalRPG plugin;
    private final ManaManager manaManager;
    private final CooldownManager cooldownManager;

    public ItemEvents(DawnSurvivalRPG plugin) {
        this.plugin = plugin;
        this.manaManager = new ManaManager(plugin);
        this.cooldownManager = new CooldownManager(plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onItemInteraction(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if(!DawnKeys.isDawnObject(item)) return;


        //player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.MESSAGE, "Checking if item is updated..."));

        //check item versions here
    }

    @EventHandler
    public void onSkillUse(PlayerInteractEvent e) {
        SkillManager skillManager = new SkillManager(plugin);

        Player player = e.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if(!DawnKeys.isDawnObject(item)) return;
        e.setCancelled(true); //for future stuff, change it because of consumables

        // Ensure we are only firing skills on right clicks
        Action action = e.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemMeta itemMeta = item.getItemMeta();
        PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();

        if(!pdc.has(DawnKeys.USES)) {return;}

        if(pdc.has(DawnKeys.MANA) && pdc.has(DawnKeys.COOLDOWN)) {
            // --> THE FIX: Stop vanilla Minecraft from continuing the block interaction
            //e.setCancelled(true);

            if(!manaManager.hasMana(player, pdc)) return;
            if(cooldownManager.isOnCooldown(player, pdc)) return;

            skillManager.onUse(player, item, pdc);
            cooldownManager.setCooldown(item);
        }
    }

    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent e) {
        Player player = e.getPlayer();

        int currentSlot = e.getNewSlot();
        int previousSlot = e.getPreviousSlot();
        ItemStack item = player.getInventory().getItem(currentSlot);
        ItemStack previousItem = player.getInventory().getItem(previousSlot);

        if(item == null && previousItem == null) {return;}

        manaManager.calculateMaxIntelligenceFromItem(player, item, previousItem);
    }
}
